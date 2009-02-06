/**
 * NewProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

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
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.proteinfer.ProgramParameters.Param;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dto.ProteinferInput.InputType;

/**
 * 
 */
public class NewProteinInferenceAction extends Action {

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
        
        // We need the projectID so we can redirect back to the project page after
        // the protein inference job has been submitted.
        int projectId = -1;
        if (request.getParameter("projectId") != null) {
            try {projectId = Integer.parseInt(request.getParameter("projectId"));}
            catch(NumberFormatException e) {projectId = -1;}
        }
        
        if (projectId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.projectId", projectId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        request.setAttribute("projectId", projectId);
        
        ProteinInferInputGetter inputGetter = ProteinInferInputGetter.instance();
        
        // Create an ActionForm -- this will be used if the user chooses to run
        // protein inference on output from a search program
        ProteinInferenceForm newForm1 = new ProteinInferenceForm();
        request.setAttribute("proteinInferenceFormSearch", newForm1);
        newForm1.setInputType(InputType.SEARCH);
        newForm1.setProjectId(projectId);
        ProteinInferInputSummary searchSummary = inputGetter.getInputSearchSummary(searchId);
        newForm1.setInputSummary(searchSummary);
        // set the IDPicker parameters
        ProgramParameters params1 = new ProgramParameters(ProteinInferenceProgram.IDPICKER);
        setProgramDetails(params1, searchSummary.getSearchProgram());
        newForm1.setProgramParams(params1);
        
        
        // check if there is a post-search analysis
        MsSearchAnalysisDAO saDao = DAOFactory.instance().getMsSearchAnalysisDAO();
        List<Integer> saIds = saDao.getAnalysisIdsForSearch(searchId);
        
        
        if(saIds.size() > 0) {
            // Create another ActionForm -- this will be used if the user chooses to run
            // protein inference on output from a analysis program
            System.out.println("Found analysis for searchId: "+searchId);
            // TODO We are assuming for now that there is only ONE analysis done on a search
            // This may not be true later.
            int analysisId = saIds.get(0);
            // Create our ActionForm
            ProteinInferenceForm newForm2 = new ProteinInferenceForm();
            request.setAttribute("proteinInferenceFormAnalysis", newForm2);
            newForm2.setInputType(InputType.ANALYSIS);
            newForm2.setProjectId(projectId);
            ProteinInferInputSummary inputSummary = inputGetter.getInputAnalysisSummary(analysisId);
            newForm2.setInputSummary(inputSummary);
            
            // set the IDPicker parameters
            ProgramParameters params2 = new ProgramParameters(ProteinInferenceProgram.IDPICKER_PERC);
            newForm2.setProgramParams(params2);
        }
        

        // Go!
        return mapping.findForward("Success");

    }
    
    private void setProgramDetails(ProgramParameters params, String searchProgram) {
        if(searchProgram.equals(Program.SEQUEST.displayName()) || 
           searchProgram.equals(Program.EE_NORM_SEQUEST.displayName())) {
            for(Param p: params.getParamList()) {
                if(p.getName().equalsIgnoreCase("maxAbsFDR")) {
                    p.setNotes("For Score: XCorr");
                }
                else if (p.getName().equalsIgnoreCase("maxRelFDR")) {
                    p.setNotes("For Score: DeltaCN");
                }
                else if(p.getName().equalsIgnoreCase("decoyRatio")) {
                    p.setNotes("Decoy Ratio will not be used if using R/F for FDR calculation");
                }
            }
        }
        else if(searchProgram.equals(Program.PROLUCID.displayName())) {
            for(Param p: params.getParamList()) {
                if(p.getName().equalsIgnoreCase("maxAbsFDR")) {
                    p.setNotes("For Score: Primary Score"); // TODO what was the primary score used 
                    // for this Prolucid search?
                }
                else if (p.getName().equalsIgnoreCase("maxRelFDR")) {
                    p.setNotes("For Score: DeltaCN");
                }
                else if(p.getName().equalsIgnoreCase("decoyRatio")) {
                    p.setNotes("Decoy Ratio will not be used if using R/F for FDR calculation");
                }
            }
        }
    }

    
}
