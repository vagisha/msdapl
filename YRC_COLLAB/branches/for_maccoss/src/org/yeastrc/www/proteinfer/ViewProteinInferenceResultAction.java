package org.yeastrc.www.proteinfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;
import edu.uwpr.protinfer.util.TimeUtils;

public class ViewProteinInferenceResultAction extends Action {

    private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // form for filtering and display options
        ProteinInferFilterForm filterForm = (ProteinInferFilterForm)form;
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // if this is a newly created form the id will be 0.  In this case
        // look for the pinferId in the request parameters
        if(pinferId == 0) {
            try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
            catch(NumberFormatException e){};
        }
        
        // if we still do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Get a list of projects for this protein inference run.  If the user making the request to view this
        // protein inference run is not affiliated with the projects, they should not be able to edit any of 
        // the editable fields
        List<Integer> searchIds = ProteinferDAOFactory.instance().getProteinferRunDao().loadSearchIdsForProteinferRun(pinferId);
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        ProjectExperimentDAO projExptDao = ProjectExperimentDAO.instance();
        List<Integer> projectIds = new ArrayList<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            int experimentId = search.getExperimentId();
            int projectId = projExptDao.getProjectIdForExperiment(experimentId);
            if(projectId > 0)
                projectIds.add(projectId);
        }
        boolean writeAccess = false;
        ProjectDAO projDao = ProjectDAO.instance();
        for(int projectId: projectIds) {
            Project project = projDao.load(projectId);
            if(project.checkAccess(user.getResearcher())) {
                writeAccess = true;
                break;
            }
        }
        request.setAttribute("writeAccess", writeAccess);
        
        
        long s = System.currentTimeMillis();
        
        request.setAttribute("pinferId", pinferId);
        filterForm.setPinferId(pinferId);
        
        // Get the peptide definition
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // update the form with the parameters that were used to run protein inference
        filterForm.setMinPeptides(String.valueOf(idpParams.getMinPeptides()));
        filterForm.setMinUniquePeptides(String.valueOf(idpParams.getMinUniquePeptides()));
        filterForm.setMinCoverage(String.valueOf(idpParams.getMinCoverage()));
        
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverageDouble());
        filterCriteria.setMaxCoverage(filterForm.getMaxCoverageDouble());
        filterCriteria.setMinMolecularWt(filterForm.getMinMolecularWtDouble());
        filterCriteria.setMaxMolecularWt(filterForm.getMaxMolecularWtDouble());
        filterCriteria.setMinPi(filterForm.getMinPiDouble());
        filterCriteria.setMaxPi(filterForm.getMaxPiDouble());
        filterCriteria.setNumPeptides(filterForm.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(filterForm.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(filterForm.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(filterForm.getMaxSpectrumMatchesInteger());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(SORT_BY.defaultSortBy());
        filterCriteria.setSortOrder(SORT_ORDER.defaultSortOrder());
        filterCriteria.setExcludeIndistinGroups(filterForm.isExcludeIndistinProteinGroups());
        filterCriteria.setGroupProteins(filterForm.isJoinGroupProteins());
        filterCriteria.setShowParsimonious(!filterForm.isShowAllProteins());
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
        request.getSession().setAttribute("proteinIds", proteinIds);
        request.getSession().setAttribute("pinferId", pinferId);
        request.getSession().setAttribute("pinferFilterCriteria", filterCriteria);
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIdsPage = ResultsPager.instance().page(proteinIds, pageNum,
                filterCriteria.getSortOrder() == SORT_ORDER.DESC);
        
        // get the protein groups 
        List<WIdPickerProteinGroup> proteinGroups = null;
        proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIdsPage, proteinIds, filterCriteria.isGroupProteins(), peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Run summary
        IdPickerRun idpickerRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        request.setAttribute("idpickerRun", idpickerRun);
        
        // Input summary
        List<WIdPickerInputSummary> inputSummary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
        request.setAttribute("inputSummary", inputSummary);
        int totalDecoyHits = 0;
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            totalDecoyHits += input.getInput().getNumDecoyHits();
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        request.setAttribute("totalDecoyHits", totalDecoyHits);
        request.setAttribute("totalTargetHits", totalTargetHits);
        request.setAttribute("filteredTargetHits", filteredTargetHits);
        request.setAttribute("filteredPercent", round(filteredTargetHits*100.0/(double)totalTargetHits));
        request.setAttribute("filteredUniquePeptideCount", IdPickerResultsLoader.getUniquePeptideCount(pinferId));
        
        // Results summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
//        request.setAttribute("unfilteredProteinCount", summary.getUnfilteredProteinCount());
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        request.setAttribute("showGOForm", isSpeciesYeast(pinferId));
        
        long e = System.currentTimeMillis();
        log.info("Total time (ViewProteinInferenceResultAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private boolean isSpeciesYeast(int pinferId) throws Exception {
        
        
        Set<Integer> notYeastExpts = new HashSet<Integer>();
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
        if(searchIds != null) {
            for(int searchId: searchIds) {

                MsSearch search = searchDao.loadSearch(searchId);

                if(notYeastExpts.contains(search.getExperimentId())) // if we have already seen this and it is not yeast go on looking
                    continue;

                MSJob job = MSJobFactory.getInstance().getJobForExperiment(search.getExperimentId());

                if(job.getTargetSpecies() == 4932) {
                    return true;
                }
                else 
                    notYeastExpts.add(search.getExperimentId());
            }
        }
        return false;
    }
    
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }

}
