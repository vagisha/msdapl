/**
 * ViewProteinProphetResults.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

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
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewProteinProphetResultAction extends Action {

private static final Logger log = Logger.getLogger(ViewProteinProphetResultAction.class);
    
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
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        request.setAttribute("proteinProphetFilterForm", filterForm);
        
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
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        
        
        // Get the filtering criteria
        ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria();
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
        filterCriteria.setMinGroupProbability(filterForm.getMinGroupProbabilityDouble());
        filterCriteria.setMaxGroupProbability(filterForm.getMaxGroupProbabilityDouble());
        filterCriteria.setMinProteinProbability(filterForm.getMinProteinProbabilityDouble());
        filterCriteria.setMaxProteinProbability(filterForm.getMaxProteinProbabilityDouble());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(ProteinProphetFilterCriteria.defaultSortBy());
        filterCriteria.setSortOrder(ProteinProphetFilterCriteria.defaultSortOrder());
        filterCriteria.setGroupProteins(filterForm.isJoinProphetGroupProteins());
        if(!filterForm.isShowAllProteins())
            filterCriteria.setParsimoniousOnly();
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
        request.getSession().setAttribute("proteinIds", proteinIds);
        request.getSession().setAttribute("pinferId", pinferId);
        request.getSession().setAttribute("proteinProphetFilterCriteria", filterCriteria);
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIdsPage = ResultsPager.instance().page(proteinIds, pageNum,
                filterCriteria.getSortOrder() == SORT_ORDER.DESC);
        
        // get the protein groups 
        List<WProteinProphetProteinGroup> proteinGroups = null;
        if(filterCriteria.isGroupProteins())
            proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIdsPage, peptideDef);
        else
            proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIdsPage, false, peptideDef);
        
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
        ProteinProphetRun proteinProphetRun = ProteinferDAOFactory.instance().getProteinProphetRunDao().loadProteinferRun(pinferId);
        request.setAttribute("proteinProphetRun", proteinProphetRun);
        
        // Input summary
        request.setAttribute("filteredUniquePeptideCount", ProteinProphetResultsLoader.getUniquePeptideCount(pinferId));
        
        // Results summary
        WProteinProphetResultSummary summary = ProteinProphetResultsLoader.getProteinProphetResultSummary(pinferId, proteinIds);
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        // ROC
        ProteinProphetROC rocSummary = proteinProphetRun.getRoc();
        request.setAttribute("rocSummary", rocSummary);
        
        request.setAttribute("speciesIsYeast", isSpeciesYeast(pinferId));
        
        long e = System.currentTimeMillis();
        log.info("Total time (ViewProteinProphetResultAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private boolean isSpeciesYeast(int pinferId) throws Exception {
        
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
        if(searchIds != null) {
            for(int searchId: searchIds) {

                MsSearch search = searchDao.loadSearch(searchId);

                MSJob job = MSJobFactory.getInstance().getJobForExperiment(search.getExperimentId());

                if(!(job.getTargetSpecies() == 4932)) {
                    return false;
                }
            }
        }
        return true;
    }
}
