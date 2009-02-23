/**
 * PageProteinferResultsAction.java
 * @author Vagisha Sharma
 * Jan 8, 2009
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
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class PageProteinferResultsAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);
    
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
        
        
        // make sure protein inference ID in the request matches the ID for results stored in the session
        Integer pinferId_session = (Integer)request.getSession().getAttribute("pinferId");
        if(pinferId_session == null || pinferId_session != pinferId) {
            // redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            ActionForward newResults = mapping.findForward( "ViewNewResults" ) ;
            newResults = new ActionForward( newResults.getPath() + "inferId="+pinferId, newResults.getRedirect() ) ;
            return newResults;
        }
        
        
        // Peptide definition from the session
        ProteinFilterCriteria filterCriteria = (ProteinFilterCriteria) request.getSession().getAttribute("pinferFilterCriteria");
        if(filterCriteria == null)  filterCriteria = new ProteinFilterCriteria();
        PeptideDefinition peptideDef = filterCriteria.getPeptideDefinition();

        // How are we displaying the results (grouped by protein group or individually)
        boolean group = filterCriteria.isGroupProteins();
        request.setAttribute("groupProteins", group);
        
        
        // Get the list of filtered and sorted protein IDs stored in the session
        List<Integer> storedProteinIds = (List<Integer>) request.getSession().getAttribute("proteinIds");
        if(storedProteinIds == null || storedProteinIds.size() == 0) {
         // redirect to the /viewProteinInferenceResult action if no proteinIds are stored in the session
            ActionForward newResults = mapping.findForward( "ViewNewResults" ) ;
            newResults = new ActionForward( newResults.getPath() + "inferId="+pinferId, newResults.getRedirect() ) ;
            return newResults;
        }
        
        // get the page number from the request
        int pageNum = 1;
        try {pageNum = Integer.parseInt(request.getParameter("pageNum"));}
        catch(NumberFormatException e){ pageNum = 1;}
        request.setAttribute("pageNum", pageNum);
        
        long s = System.currentTimeMillis();
        
        log.info("Paging results for protein inference: "+pinferId+"; page num: "+pageNum+"; sort order: "+filterCriteria.getSortOrder() );
        
        
        // determine the list of proteins we will be displaying
        ProteinferResultsPager pager = ProteinferResultsPager.instance();
        List<Integer> proteinIds = pager.page(storedProteinIds, pageNum, 
                filterCriteria.getSortOrder() == SORT_ORDER.DESC);
        
        // get the protein groups
        boolean fullLookup = filterCriteria.doExhaustiveCommonNameLookup();
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIds, 
                group, peptideDef, fullLookup);
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ProteinferResultsPager.instance().getPageCount(storedProteinIds.size());
        List<Integer> pages = ProteinferResultsPager.instance().getPageList(storedProteinIds.size(), pageNum);
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pageNum == pages.get(pages.size() - 1)));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        long e = System.currentTimeMillis();
        log.info("Total time (PageProteinInferenceResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        return mapping.findForward("Success");
        
    }
}
