/**
 * UpdateProteinInferenceResultAjaxAction.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class UpdateProteinProphetResultAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(UpdateProteinProphetResultAjaxAction.class);
    
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
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        request.setAttribute("proteinProphetFilterForm", filterForm);
        
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
        ProteinProphetFilterCriteria filterCritSession = (ProteinProphetFilterCriteria) request.getSession().getAttribute("proteinProphetFilterCriteria");
        
        // Get the filtering criteria from the request
        PeptideDefinition peptideDef = filterCritSession.getPeptideDefinition();
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
        filterCriteria.setMinProbability(filterForm.getMinProbabilityDouble());
        filterCriteria.setMaxProbability(filterForm.getMaxProbabilityDouble());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(filterCritSession == null ? 
                ProteinProphetFilterCriteria.defaultSortBy() : 
                filterCritSession.getSortBy());
        filterCriteria.setSortOrder(filterCritSession == null ? 
                ProteinProphetFilterCriteria.defaultSortOrder() : 
                filterCritSession.getSortOrder());
        filterCriteria.setGroupProteins(filterForm.isJoinProphetGroupProteins());
        if(!filterForm.isShowAllProteins())
            filterCriteria.setParsimoniousOnly();
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        
        
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
                    filterCriteria.setSortBy(ProteinProphetFilterCriteria.defaultSortBy());
                filterCriteria.setSortOrder(ProteinProphetFilterCriteria.defaultSortOrder());
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
            storedProteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria);
        }
        
        if(resort) {
            // resorted the filtered protein IDs
            storedProteinIds = ProteinProphetResultsLoader.getSortedProteinIds(pinferId, 
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
        List<WProteinProphetProteinGroup> proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIds, 
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
        
        
        // Results summary
        WProteinProphetResultSummary summary = ProteinProphetResultsLoader.getProteinProphetResultSummary(pinferId, storedProteinIds);
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (UpdateProteinProphetResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }

    private boolean matchFilterCriteria(ProteinProphetFilterCriteria filterCritSession,  ProteinProphetFilterCriteria filterCriteria) {
        return filterCritSession.equals(filterCriteria);
    }
}
