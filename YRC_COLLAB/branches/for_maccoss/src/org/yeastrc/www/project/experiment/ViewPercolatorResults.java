/**
 * ViewPercolatorResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
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
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.TabularPercolatorResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_BY;
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

        int searchAnalysisId = myForm.getSearchAnalysisId();
        if(searchAnalysisId == 0) {
            try {
                String strID = request.getParameter("ID");
                if(strID != null)
                    searchAnalysisId = Integer.parseInt(strID);


            } catch (NumberFormatException nfe) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
        }
        // If we still don't have a valid id, return an error
        if(searchAnalysisId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        // If this is a brand new form
        if(myForm.getSearchAnalysisId() == 0) {
            myForm.setSearchAnalysisId(searchAnalysisId);
            myForm.setShowModified(true);
            myForm.setShowUnmodified(true);
            myForm.setExactPeptideMatch(true);
            myForm.setMaxQValue("0.05");
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
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        numResults = presDao.numAnalysisResults(searchAnalysisId);
        List<Integer> resultIds = null;
        if(myForm.isPeptidesView()) {
            resultIds = presDao.loadResultIdsForSearchAnalysisUniqPeptide(searchAnalysisId, myForm.getFilterCriteria(), myForm.getSortCriteria());
        }
        else {
            resultIds = presDao.loadResultIdsForSearchAnalysis(searchAnalysisId, myForm.getFilterCriteria(), myForm.getSortCriteria());
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

        
        // Do we have Bullseye results for the searched files
        boolean hasBullsEyeArea = false;
        MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
        MS2RunDAO runDao = DAOFactory.instance().getMS2FileRunDAO();
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchIds.get(0));
        for(int runSearchId: runSearchIds) {
            int runId = rsDao.loadRunSearch(runSearchId).getRunId();
            if(runDao.isGeneratedByBullseye(runId)) {
                hasBullsEyeArea = true;
                break;
            }
        }
        

        // Get details for the result we will display
        Map<Integer, String> filenameMap = getFileNames(searchAnalysisId);
        List<PercolatorResultPlus> results = new ArrayList<PercolatorResultPlus>(numResultsPerPage);

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        MS2ScanDAO ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        for(Integer resultId: forPage) {
            PercolatorResult result = presDao.load(resultId);
            PercolatorResultPlus resPlus = null;
            
            if(hasBullsEyeArea) {
                MS2Scan scan = ms2ScanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }
            else {
                MsScan scan = scanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }
            
            resPlus.setFilename(filenameMap.get(result.getRunSearchAnalysisId()));
            resPlus.setSequestData(seqResDao.load(resultId).getSequestResultData());
            results.add(resPlus);
        }

        // Which version of Percolator are we using
        String version = analysis.getAnalysisProgramVersion();
        boolean hasPEP = true;
        try {
            float vf = Float.parseFloat(version.trim());
            if(vf < 1.06)   hasPEP = false;
        }
        catch(NumberFormatException e){
            log.error("Cannot detrmine if this version of Percolator prints PEP. Version: "+version);
        }

        // Will we display the PEP or the Discriminant Score filters in the form
        myForm.setUsePEP(hasPEP);
        
        
        // Set up for tabular display
        TabularPercolatorResults tabResults = new TabularPercolatorResults(results, hasPEP, hasBullsEyeArea);
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
