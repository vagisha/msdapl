package org.yeastrc.www.proteinfer;

import java.util.Collections;
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
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
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
        ProteinInferFilterForm filterForm = null;
        if(form != null) {
            filterForm = (ProteinInferFilterForm)form;
        }
        else {
            filterForm = new ProteinInferFilterForm();
            System.out.println("Creating a new filter form!!"); // this should never happen??
        }
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // if this is a newly created form the id will be 0.  In this case
        // look for the pinferId in the request parameters
        if(pinferId == 0) {
            try {pinferId = Integer.parseInt(request.getParameter("inferId"));}
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
        
        long s = System.currentTimeMillis();
        
        request.setAttribute("pinferId", pinferId);
        filterForm.setPinferId(pinferId);
        
        
        // Get the filtering criteria
        PeptideDefinition peptideDef = new PeptideDefinition(filterForm.isPeptideDef_useMods(), filterForm.isPeptideDef_useCharge());
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverage());
        filterCriteria.setNumPeptides(filterForm.getMinPeptides());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptides());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatches());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(SORT_BY.GROUP_ID);
        filterCriteria.setGroupProteins(filterForm.isJoinGroupProteins());
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
        request.getSession().setAttribute("proteinIds", proteinIds);
        request.getSession().setAttribute("pinferId", pinferId);
        request.getSession().setAttribute("pinferFilterCriteria", filterCriteria);
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIdsPage = ProteinferResultsPager.instance().page(proteinIds, pageNum, true);
        
        // get the protein groups 
        List<WIdPickerProteinGroup> proteinGroups = null;
        if(filterCriteria.isGroupProteins())
            proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIdsPage, peptideDef);
        else
            proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIdsPage, false, peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ProteinferResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ProteinferResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pageNum == pages.get(pages.size() - 1)));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        

        // Cluster IDs in this set
        List<Integer> clusterIdList = IdPickerResultsLoader.getClusterIds(pinferId);
        Collections.sort(clusterIdList);
        request.setAttribute("clusterIds", clusterIdList);
        
        
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
        
        // Results summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
//        request.setAttribute("unfilteredProteinCount", summary.getUnfilteredProteinCount());
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (ViewProteinInferenceResultAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }

}
