/**
 * ProteinInferInputGetter.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

/**
 * 
 */
public class ProteinInferInputGetter {

    private static final ProteinInferInputGetter instance = new ProteinInferInputGetter();

    private ProteinInferInputGetter() {}

    public static ProteinInferInputGetter instance() {
        return instance;
    }

    public ProteinInferInputSummary getInputSearchSummary(int searchId) {
        DAOFactory daoFactory = DAOFactory.instance();

        ProteinInferInputSummary searchSummary = new ProteinInferInputSummary();
        searchSummary.setSearchId(searchId);

        // get the name of the search program
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        searchSummary.setSearchProgram(search.getSearchProgram().displayName());
        searchSummary.setSearchProgramVersion(search.getSearchProgramVersion());

        // get the name(s) of the search databases.
        StringBuilder databases = new StringBuilder();
        for(MsSearchDatabase db: search.getSearchDatabases()) {
            databases.append(", ");
            databases.append(db.getDatabaseFileName());
        }
        if(databases.length() > 0)  databases.deleteCharAt(0);
        searchSummary.setSearchDatabase(databases.toString());



        MsRunSearchDAO runSearchDao = daoFactory.getMsRunSearchDAO();
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);

        for (int id: runSearchIds) {
            String filename = runSearchDao.loadFilenameForRunSearch(id);
            ProteinInferIputFile rs = new ProteinInferIputFile(id, filename);
            rs.setIsSelected(true);
            searchSummary.addInputFile(rs);
        }
        Collections.sort(searchSummary.getInputFiles(), new Comparator<ProteinInferIputFile>(){
            public int compare(ProteinInferIputFile o1, ProteinInferIputFile o2) {
                return o1.getRunName().compareToIgnoreCase(o2.getRunName());
            }});
        return searchSummary;
    }

    public ProteinInferInputSummary getInputAnalysisSummary(int analysisId) {
        DAOFactory daoFactory = DAOFactory.instance();

        ProteinInferInputSummary inputSummary = new ProteinInferInputSummary();
        inputSummary.setSearchAnalysisId(analysisId);
        
        // get the name of the analysis program
        MsSearchAnalysisDAO analysisDao = daoFactory.getMsSearchAnalysisDAO();
        MsSearchAnalysis analysis = analysisDao.load(analysisId);
        inputSummary.setAnalysisProgram(analysis.getAnalysisProgram().displayName());
        inputSummary.setAnalysisProgramVersion(analysis.getAnalysisProgramVersion());
        
        int searchId = analysis.getSearchId();
        inputSummary.setSearchId(searchId);
        
        // get the name of the search program
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        inputSummary.setSearchProgram(search.getSearchProgram().displayName());
        inputSummary.setSearchProgramVersion(search.getSearchProgramVersion());

        // get the name(s) of the search databases.
        StringBuilder databases = new StringBuilder();
        for(MsSearchDatabase db: search.getSearchDatabases()) {
            databases.append(", ");
            databases.append(db.getDatabaseFileName());
        }
        if(databases.length() > 0)  databases.deleteCharAt(0);
        inputSummary.setSearchDatabase(databases.toString());



        MsRunSearchAnalysisDAO rsAnalysisDao = daoFactory.getMsRunSearchAnalysisDAO();

        List<Integer> rsAnalysisIds = rsAnalysisDao.getRunSearchAnalysisIdsForAnalysis(analysisId);

        for (int id: rsAnalysisIds) {
            String filename = rsAnalysisDao.loadFilenameForRunSearchAnalysis(id);
            ProteinInferIputFile rs = new ProteinInferIputFile(id, filename);
            rs.setIsSelected(true);
            inputSummary.addInputFile(rs);
        }
        Collections.sort(inputSummary.getInputFiles(), new Comparator<ProteinInferIputFile>(){
            public int compare(ProteinInferIputFile o1, ProteinInferIputFile o2) {
                return o1.getRunName().compareToIgnoreCase(o2.getRunName());
            }});
        return inputSummary;
    }
}
