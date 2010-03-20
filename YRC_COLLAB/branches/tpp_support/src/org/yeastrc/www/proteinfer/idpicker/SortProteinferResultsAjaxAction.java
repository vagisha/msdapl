/**
 * SortProteinferResultsAction.java
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
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SortProteinferResultsAjaxAction extends Action{

    private static final Logger log = Logger.getLogger(SortProteinferResultsAjaxAction.class);

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

        // get the protein inference ID from the request
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("inferId"));}
        catch(NumberFormatException e){};
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Invalid protein inference ID: "+pinferId);
            return null;
        }

        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        // Check if we already have information in the session
        ProteinFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForIdPicker(request, pinferId);
        List<Integer> storedProteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        // If we don't have a filtering criteria in the session we are starting from scratch
        // Get the protein Ids that fulfill the criteria.
        if(filterCriteria_session == null || storedProteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale protein inference ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }

        // Protein filter criteria from the session
        PeptideDefinition peptideDef = filterCriteria_session.getPeptideDefinition();


        // How are we displaying the results (grouped by protein group or individually)
        boolean group = filterCriteria_session.isGroupProteins();
        request.setAttribute("groupProteins", group);



        SORT_BY sortBy_session = filterCriteria_session.getSortBy();

        String sortBy_request = (String) request.getParameter("sortBy");

        String sortOrder_request = (String) request.getParameter("sortOrder");
        if(sortOrder_request != null) {
            SORT_ORDER sortOrder_r = SORT_ORDER.getSortByForString(sortOrder_request);
            filterCriteria_session.setSortOrder(sortOrder_r);
        }


        long s = System.currentTimeMillis();
        log.info("Sorting results for protein inference: "+pinferId+"; sort by: "+sortBy_request+"; sort order: "+sortOrder_request);

        if(sortBy_request != null){
            SORT_BY sortBy_r = SORT_BY.getSortByForString(sortBy_request);

            if(sortBy_r != sortBy_session) {
                log.info("Sorting results by "+sortBy_r.name()+"; order: "+filterCriteria_session.getSortOrder().name());


                List<Integer> newOrderIds = null;

                // resort  the results based on the given criteria
                newOrderIds = IdPickerResultsLoader.getSortedProteinIds(pinferId, 
                        peptideDef, 
                        storedProteinIds, 
                        sortBy_r, 
                        group);

                filterCriteria_session.setSortBy(sortBy_r);
                sessionManager.putForIdPicker(request, pinferId, filterCriteria_session, newOrderIds);
                storedProteinIds = newOrderIds;
            }
        }

        // page number is now 1
        int pageNum = 1;


        // determine the list of proteins we will be displaying
        ResultsPager pager = ResultsPager.instance();
        List<Integer> proteinIds = pager.page(storedProteinIds, pageNum, 
                filterCriteria_session.getSortOrder() == SORT_ORDER.DESC);

        // get the protein groups
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIds, 
                group, peptideDef);
        request.setAttribute("proteinGroups", proteinGroups);


        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(storedProteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(storedProteinIds.size(), pageNum);

        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pageNum == pages.get(pages.size() - 1)));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);

        request.setAttribute("sortBy", filterCriteria_session.getSortBy());
        request.setAttribute("sortOrder", filterCriteria_session.getSortOrder());

        long e = System.currentTimeMillis();
        log.info("Total time (SortProteinInferenceResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));

        return mapping.findForward("Success");

    }

}
