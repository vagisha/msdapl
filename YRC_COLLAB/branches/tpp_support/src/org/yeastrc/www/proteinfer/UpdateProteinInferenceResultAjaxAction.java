/**
 * UpdateProteinInferenceResultAjaxAction.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class UpdateProteinInferenceResultAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(UpdateProteinInferenceResultAjaxAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // form for filtering and display options
        ProteinInferFilterForm filterForm = (ProteinInferFilterForm)form;
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // if we do not have a valid protein inference run id return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Invalid protein inference ID: "+pinferId);
            return null;
        }
        
        // make sure protein inference ID in the request matches the ID for results stored in the session
        Integer pinferId_session = (Integer)request.getSession().getAttribute("pinferId");
        if(pinferId_session == null || pinferId_session != pinferId) {
            // redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale protein inference ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }
        
        long s = System.currentTimeMillis();
        
        
        // Check if there a filtering criteria in the session
        ProteinFilterCriteria filterCritSession = (ProteinFilterCriteria) request.getSession().getAttribute("pinferFilterCriteria");
        
        // Get the filtering criteria from the request
        PeptideDefinition peptideDef = filterCritSession.getPeptideDefinition();
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverageDouble());
        filterCriteria.setMaxCoverage(filterForm.getMaxCoverageDouble());
        filterCriteria.setNumPeptides(filterForm.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(filterForm.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(filterForm.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(filterForm.getMaxSpectrumMatchesInteger());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(filterCritSession == null ? 
                ProteinFilterCriteria.defaultSortBy() : 
                filterCritSession.getSortBy());
        filterCriteria.setSortOrder(filterCritSession == null ? 
                ProteinFilterCriteria.defaultSortOrder() : 
                filterCritSession.getSortOrder());
        filterCriteria.setGroupProteins(filterForm.isJoinGroupProteins());
        if(!filterForm.isShowAllProteins())
            filterCriteria.setParsimoniousOnly();
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        filterCriteria.setPeptide(filterForm.getPeptide());
        filterCriteria.setExactPeptideMatch(filterForm.getExactPeptideMatch());
        
        
        // Get the protein IDs from the session
        List<Integer> storedProteinIds = (List<Integer>) request.getSession().getAttribute("proteinIds");
        System.out.println("stored protein ids: "+storedProteinIds.size());

        // check if the protein grouping has changed. If so we may have to resort the proteins. 
        boolean resort = false;
        if(filterCritSession.isGroupProteins() != filterCriteria.isGroupProteins()) {
            // If the filter criteria has proteins GROUPED and the sort_by is
            // on one of the protein specific columns change it to group_id
            if(filterCriteria.isGroupProteins()) {
                SORT_BY sortby = filterCriteria.getSortBy();
                if(sortby == SORT_BY.ACCESSION || sortby == SORT_BY.VALIDATION_STATUS)
                    filterCriteria.setSortBy(ProteinFilterCriteria.defaultSortBy());
                filterCriteria.setSortOrder(ProteinFilterCriteria.defaultSortOrder());
            }
            resort = true; // if the grouping has changed we will resort proteins (UNLESS the filtering criteria has also changed).
        }
        
        // Match this filtering criteria with the one in the request
        boolean match = false;
        if(filterCritSession != null) {
            match = matchFilterCriteria(filterCritSession, filterCriteria);
        }
        
        // if the filtering criteria has changed we need to filter the results again
        if(!match)  {
            
            resort = false; // no need to re-sort.  The method below will take that into account.
            // Get a list of filtered and sorted proteins
            storedProteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        }
        
        if(resort) {
            // resorted the filtered protein IDs
            storedProteinIds = IdPickerResultsLoader.getSortedProteinIds(pinferId, 
                    peptideDef, 
                    storedProteinIds, 
                    filterCriteria.getSortBy(), 
                    filterCriteria.isGroupProteins());
        }
        
        
        // update the list of filtered and sorted protein IDs in the session, along with the filter criteria
        request.getSession().setAttribute("proteinIds", storedProteinIds);
        request.getSession().setAttribute("pinferFilterCriteria", filterCriteria);
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIds = ResultsPager.instance().page(storedProteinIds, pageNum, 
                filterCriteria.getSortOrder() == SORT_ORDER.DESC);
        
        // get the protein groups 
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIds, 
                                                        filterCriteria.isGroupProteins(), peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(storedProteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(storedProteinIds.size(), pageNum);
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", ( pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Get some summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, storedProteinIds);
//        request.setAttribute("unfilteredProteinCount", summary.getUnfilteredProteinCount());
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (UpdateProteinInferenceResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }

    private boolean matchFilterCriteria(ProteinFilterCriteria filterCritSession,  ProteinFilterCriteria filterCriteria) {
        return filterCritSession.equals(filterCriteria);
    }
}
