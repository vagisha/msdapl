/**
 * ViewPercolatorResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
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
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.experiment.TabularPercolatorResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewPercolatorResults extends Action {

    private static final Logger log = Logger.getLogger(ViewPercolatorResults.class.getName());
    
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


        int runSearchAnalysisId = 0;
        try {
            String strID = request.getParameter("ID");

            if (strID == null || strID.equals("")) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }

            runSearchAnalysisId = Integer.parseInt(strID);

        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        int pageNum = 1;
        try {
            String pnum = request.getParameter("page");
            if(pnum != null)
                pageNum = Integer.parseInt(pnum);
        }
        catch(NumberFormatException e) {
            log.error("Invalid page number in request.  Returning page number 1");
            pageNum = 1;
        }
        
        String sortBy = request.getParameter("sortBy");
        
        // TODO Does the user have access to look at these results? 
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        List<Integer> resultIds = presDao.loadResultIdsForRunSearchAnalysis(runSearchAnalysisId);
        Collections.sort(resultIds);
        
        
        int numResultsPerPage = 50;
        ResultsPager pager = ResultsPager.instance();
        List<Integer> forPage = pager.page(resultIds, pageNum, numResultsPerPage, false);
        
        List<PercolatorResultPlus> results = new ArrayList<PercolatorResultPlus>(numResultsPerPage);
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        for(Integer resultId: forPage) {
            PercolatorResult result = presDao.load(resultId);
            MsScan scan = scanDao.loadScanLite(result.getScanId());
            results.add(new PercolatorResultPlus(result, scan));
        }

        TabularPercolatorResults tabResults = new TabularPercolatorResults(results);
        tabResults.setCurrentPage(pageNum);
        int pageCount = pager.getPageCount(resultIds.size(), numResultsPerPage);
        tabResults.setLastPage(pageCount);
        List<Integer> pageList = pager.getPageList(resultIds.size(), pageNum, numResultsPerPage);
        tabResults.setDisplayPageNumbers(pageList);
        

        request.setAttribute("results", tabResults);
        request.setAttribute("runSearchAnalysisId", runSearchAnalysisId);


//      Forward them on to the happy success view page!
        return mapping.findForward("Success");


    }
}
