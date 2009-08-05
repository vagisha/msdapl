/**
 * ViewPeptideProphetResults.java
 * @author Vagisha Sharma
 * Aug 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.PeptideProphetResultPlus;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.TabularPeptideProphetResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewPeptideProphetResults extends Action {

    private static final Logger log = Logger.getLogger(ViewPeptideProphetResults.class.getName());

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
        PeptideProphetFilterResultsForm myForm = (PeptideProphetFilterResultsForm)form;

        int searchAnalysisId = myForm.getSearchAnalysisId();
        if(searchAnalysisId == 0) {
            try {
                String strID = request.getParameter("ID");
                if(strID != null)
                    searchAnalysisId = Integer.parseInt(strID);


            } catch (NumberFormatException nfe) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "PeptideProphet analysis"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
        }
        // If we still don't have a valid id, return an error
        if(searchAnalysisId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "PeptideProphet analysis"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        // If this is a brand new form
        if(myForm.getSearchAnalysisId() == 0) {
            myForm.setSearchAnalysisId(searchAnalysisId);
            myForm.setShowModified(true);
            myForm.setShowUnmodified(true);
            myForm.setExactPeptideMatch(true);
            myForm.setMinProbability("0.05");
            myForm.setSortBy(SORT_BY.QVAL);
            myForm.setSortOrder(SORT_ORDER.ASC);
        }


        // TODO Does the user have access to look at these results? 

        // GET THE SUMMARY 
        List<Integer> projectIds = new ArrayList<Integer>();
        List<Integer> experimentIds = new ArrayList<Integer>();
        String program = null;
        int numResults = 0;
        int numResultsFiltered = 0;


        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(searchAnalysisId);
        program = analysis.getAnalysisProgram()+" "+analysis.getAnalysisProgramVersion();

        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(searchAnalysisId);
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();


        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            experimentIds.add(search.getExperimentId());
        }
        if(experimentIds.size() > 0) {
            // Get the projects for these experiments
            ProjectExperimentDAO projExpDao = ProjectExperimentDAO.instance();
            projectIds = projExpDao.getProjectIdsForExperiments(experimentIds);
        }



        // Get ALL the filtered and sorted resultIds
        PeptideProphetResultDAO ppRes = DAOFactory.instance().getPeptideProphetResultDAO();
        numResults = ppRes.numAnalysisResults(searchAnalysisId);
        List<Integer> resultIds = null;
        if(myForm.isPeptidesView()) {
            resultIds = ppRes.loadResultIdsForSearchAnalysisUniqPeptide(searchAnalysisId, myForm.getFilterCriteria(), myForm.getSortCriteria());
        }
        else {
            resultIds = ppRes.loadResultIdsForSearchAnalysis(searchAnalysisId, myForm.getFilterCriteria(), myForm.getSortCriteria());
        }
        numResultsFiltered = resultIds.size();



        // Extract the ones we will display
        int numResultsPerPage = 50;
        int pageNum = myForm.getPageNum();
        if(pageNum <= 0) {
            pageNum = 1;
            myForm.setPageNum(pageNum);
        }
        ResultsPager pager = ResultsPager.instance();
        boolean desc = false;
        if(myForm.getSortOrder() != null)
            desc = myForm.getSortOrder() == SORT_ORDER.DESC ? true : false;
        // TODO if the pageNum is out of range .....
        List<Integer> forPage = pager.page(resultIds, pageNum, numResultsPerPage, desc);



        // Get details for the result we will display
        Map<Integer, String> filenameMap = getFileNames(searchAnalysisId);
        List<PeptideProphetResultPlus> results = new ArrayList<PeptideProphetResultPlus>(numResultsPerPage);

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        for(Integer resultId: forPage) {
            PeptideProphetResult result = ppRes.load(resultId);
            MsScan scan = scanDao.loadScanLite(result.getScanId());
            PeptideProphetResultPlus resPlus = new PeptideProphetResultPlus(result, scan);
            resPlus.setFilename(filenameMap.get(result.getRunSearchAnalysisId()));
            resPlus.setSequestData(seqResDao.load(resultId).getSequestResultData());
            results.add(resPlus);
        }

        
        // Set up for tabular display
        TabularPeptideProphetResults tabResults = new TabularPeptideProphetResults(results);
        tabResults.setCurrentPage(pageNum);
        int pageCount = pager.getPageCount(resultIds.size(), numResultsPerPage);
        tabResults.setLastPage(pageCount);
        List<Integer> pageList = pager.getPageList(resultIds.size(), pageNum, numResultsPerPage);
        tabResults.setDisplayPageNumbers(pageList);
        tabResults.setSortedColumn(myForm.getSortBy());
        tabResults.setSortOrder(myForm.getSortOrder());


        // required attributes in the request
        request.setAttribute("projectIds", projectIds);
        request.setAttribute("experimentIds", experimentIds);
        request.setAttribute("program", program);
        request.setAttribute("numResults", numResults);
        request.setAttribute("numResultsFiltered", numResultsFiltered);

        request.setAttribute("filterForm", myForm);
        request.setAttribute("results", tabResults);
        request.setAttribute("searchAnalysisId", searchAnalysisId);


        // Forward them on to the happy success view page!
        return mapping.findForward("Success");


    }

    private Map<Integer, String> getFileNames(int searchAnalysisId) {

        MsRunSearchAnalysisDAO saDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
        List<Integer> runSearchAnalysisIds = saDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);

        Map<Integer, String> filenameMap = new HashMap<Integer, String>(runSearchAnalysisIds.size()*2);
        for(int runSearchAnalysisId: runSearchAnalysisIds) {
            String filename = saDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
            filenameMap.put(runSearchAnalysisId, filename);
        }
        return filenameMap;

    }
}
