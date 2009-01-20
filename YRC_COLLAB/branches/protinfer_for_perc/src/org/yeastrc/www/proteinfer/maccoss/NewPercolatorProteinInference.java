package org.yeastrc.www.proteinfer.maccoss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.www.proteinfer.ProgramParameters;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.ProteinInferenceForm;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

import edu.uwpr.protinfer.ProteinInferenceProgram;

public class NewPercolatorProteinInference extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        int searchId = -1;
        if (request.getParameter("searchId") != null) {
            try {searchId = Integer.parseInt(request.getParameter("searchId"));}
            catch(NumberFormatException e) {searchId = -1;}
        }
        
        if (searchId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.searchId", searchId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int analysisId = -1;
        if (request.getParameter("analysisId") != null) {
            try {analysisId = Integer.parseInt(request.getParameter("analysisId"));}
            catch(NumberFormatException e) {analysisId = -1;}
        }
        
        if (analysisId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.analysisId", analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        // Create our ActionForm
        ProteinInferenceForm newForm = new ProteinInferenceForm();
        request.setAttribute("proteinInferenceForm", newForm);
        
        ProteinInferInputSummary inputSummary = getInputSummary(searchId, analysisId);
        newForm.setInputSummary(inputSummary);
        
        // we will be using IDPicker -- set the IDPicker parameters
        ProgramParameters params = new ProgramParameters(ProteinInferenceProgram.IDPICKER_PERC);
        newForm.setProgramParams(params);
        
        // Go!
        return mapping.findForward( "Success" ) ;

    }
    
    private ProteinInferInputSummary getInputSummary(int searchId, int analysisId) {
        DAOFactory daoFactory = DAOFactory.instance();
        
        ProteinInferInputSummary inputSummary = new ProteinInferInputSummary();
        inputSummary.setSearchId(searchId);
        inputSummary.setSearchAnalysisId(analysisId);
        
        // get the name of the search program
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        inputSummary.setSearchProgram(search.getSearchProgram().displayName());
        inputSummary.setSearchProgramVersion(search.getSearchProgramVersion());
        
        // get the name of the analysis program
        MsSearchAnalysisDAO analysisDao = daoFactory.getMsSearchAnalysisDAO();
        MsSearchAnalysis analysis = analysisDao.load(analysisId);
        inputSummary.setAnalysisProgram(analysis.getAnalysisProgram().displayName());
        inputSummary.setAnalysisProgramVersion(analysis.getAnalysisProgramVersion());
        
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
        return inputSummary;
    }
}
