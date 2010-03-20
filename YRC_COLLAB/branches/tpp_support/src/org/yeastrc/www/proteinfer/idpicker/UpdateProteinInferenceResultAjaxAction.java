/**
 * UpdateProteinInferenceResultAjaxAction.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

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
        
        
        // form for filtering and display options
        IdPickerFilterForm filterForm = (IdPickerFilterForm)form;
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // Get the peptide definition
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // filtering criteria from teh request
        ProteinFilterCriteria filterCriteria_request = filterForm.getFilterCriteria(peptideDef);
        
        // protein Ids
        List<Integer> proteinIds = null;
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        
        long s = System.currentTimeMillis();
        
        // Check if we already have information in the session
        ProteinFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForIdPicker(request, pinferId);
        proteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        
        // If we don't have a filtering criteria in the session we are starting from scratch
        // Get the protein Ids that fulfill the criteria.
        if(filterCriteria_session == null || proteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale protein inference ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }
        
        else {
        	
        	log.info("Found information in session for: "+pinferId);
        	System.out.println("stored protein ids: "+proteinIds.size());
        	 
        	// compare the filtering criteria in the session with what we have in the form
        	filterCriteria_request.setSortBy(filterCriteria_session.getSortBy());
        	filterCriteria_request.setSortOrder(filterCriteria_session.getSortOrder());
        	
        	boolean match = matchFilterCriteria(filterCriteria_session, filterCriteria_request);
        	
        	// check if the protein grouping has changed. If so we may have to resort the proteins. 
            boolean resort = false;
            if(filterCriteria_session.isGroupProteins() != filterCriteria_request.isGroupProteins()) {
                // If the filter criteria has proteins GROUPED and the sort_by is
                // on one of the protein specific columns change it to group_id
                if(filterCriteria_request.isGroupProteins()) {
                    SORT_BY sortby = filterCriteria_request.getSortBy();
                    if(sortby == SORT_BY.ACCESSION || sortby == SORT_BY.VALIDATION_STATUS)
                        filterCriteria_request.setSortBy(ProteinFilterCriteria.defaultSortBy());
                    filterCriteria_request.setSortOrder(ProteinFilterCriteria.defaultSortOrder());
                }
                resort = true; // if the grouping has changed we will resort proteins (UNLESS the filtering criteria has also changed).
            }
            
            // if the filtering criteria has changed we need to filter the results again
            if(!match)  {
                
                resort = false; // no need to re-sort.  The method below will take that into account.
                // Get a list of filtered and sorted proteins
                proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria_request);
            }
            
            if(resort) {
                // resort the filtered protein IDs
                proteinIds = IdPickerResultsLoader.getSortedProteinIds(pinferId, 
                        peptideDef, 
                        proteinIds, 
                        filterCriteria_request.getSortBy(), 
                        filterCriteria_request.isGroupProteins());
            }
        }
        
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
    	sessionManager.putForIdPicker(request, pinferId, filterCriteria_request, proteinIds);
        request.setAttribute("sortBy", filterCriteria_request.getSortBy());
        request.setAttribute("sortOrder", filterCriteria_request.getSortOrder());
        
        
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> pageProteinIds = ResultsPager.instance().page(proteinIds, pageNum, 
                filterCriteria_request.getSortOrder() == SORT_ORDER.DESC);
        
        // get the protein groups 
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, pageProteinIds, 
                                                        filterCriteria_request.isGroupProteins(), peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", ( pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Get some summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
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
