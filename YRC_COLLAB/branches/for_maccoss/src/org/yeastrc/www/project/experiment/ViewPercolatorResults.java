/**
 * ViewPercolatorResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

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
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.experiment.TabularPercolatorResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_ORDER;
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

        // Get the form
        PercolatorFilterResultsForm myForm = (PercolatorFilterResultsForm)form;
        
        int runSearchAnalysisId = myForm.getRunSearchAnalysisId();
        if(runSearchAnalysisId == 0) {
            try {
                String strID = request.getParameter("ID");
                if(strID != null)
                    runSearchAnalysisId = Integer.parseInt(strID);
                

            } catch (NumberFormatException nfe) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
        }
        // If we still don't have a valid id, return an error
        if(runSearchAnalysisId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // If this is a brand new form
        if(myForm.getRunSearchAnalysisId() == 0) {
            myForm.setRunSearchAnalysisId(runSearchAnalysisId);
            myForm.setShowModified(true);
            myForm.setShowUnmodified(true);
            myForm.setExactPeptideMatch(true);
        }
        

        // TODO Does the user have access to look at these results? 
        
        // First get the experiment and Percolator level details
        String filename = DAOFactory.instance().getMsRunSearchAnalysisDAO().loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
        myForm.setFilename(filename);
        
        MsRunSearchAnalysis rsAnalysis = DAOFactory.instance().getMsRunSearchAnalysisDAO().load(runSearchAnalysisId);
        int analysisId = rsAnalysis.getAnalysisId();
        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
        myForm.setProgram(analysis.getAnalysisProgram()+" "+analysis.getAnalysisProgramVersion());
        
       List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
       MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
       String exptIds = "";
       for(int searchId: searchIds) {
           MsSearch search = searchDao.loadSearch(searchId);
           exptIds += ", "+search.getExperimentId();
       }
       if(exptIds.length() > 0)     exptIds = exptIds.substring(1); // remove first comma
       myForm.setExperimentId(exptIds);
        
        
        int pageNum = myForm.getPageNum();
        if(pageNum <= 0) {
            pageNum = 1;
            myForm.setPageNum(pageNum);
        }
        
        int numResultsPerPage = 50;
        
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        int totalResults = presDao.numResults(runSearchAnalysisId);
        myForm.setNumResults(totalResults);
        
        List<Integer> resultIds = presDao.loadResultIdsForRunSearchAnalysis(runSearchAnalysisId,
                myForm.getFilterCriteria(), myForm.getSortCriteria());
        myForm.setNumResultsFiltered(resultIds.size());
        
        
        ResultsPager pager = ResultsPager.instance();
        boolean desc = false;
        if(myForm.getSortOrder() != null)
            desc = myForm.getSortOrder() == SORT_ORDER.DESC ? true : false;
        // TODO if the pageNum is out of range .....
        List<Integer> forPage = pager.page(resultIds, pageNum, numResultsPerPage, desc);
        
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
        
//        if(myForm.getSortBy() == null) {
//            myForm.setSortBy(SORT_BY.SCAN);
//            myForm.setSortOrder(SORT_ORDER.ASC);
//        }
        
        tabResults.setSortedColumn(myForm.getSortBy());
        tabResults.setSortOrder(myForm.getSortOrder());
        
        

        request.setAttribute("filterForm", myForm);
        request.setAttribute("results", tabResults);
        request.setAttribute("runSearchAnalysisId", runSearchAnalysisId);


//      Forward them on to the happy success view page!
        return mapping.findForward("Success");


    }
}
