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
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
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

        // Restrict access to yrc members
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS) &&
          !groupMan.isMember( user.getResearcher().getID(), Projects.YATES) &&
          !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
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
        // make sure a search with the given Id exists
        MsSearch search = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId);
        if(search == null) {
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
        
        
        boolean useSearchInput = true;
        
        // Create an ActionForm -- this will be used if the user chooses to run
        // protein inference on output from a search program
        // This form should be created only if the seach program was sequest or prolucid
        ProteinInferenceForm formForSearch = createFormForSearchInput(search, projectId);
        if(formForSearch != null)
            request.setAttribute("proteinInferenceFormSearch", formForSearch);
        
        
        
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
            MsSearchAnalysis analysis = saDao.load(analysisId);
            // Create our ActionForm
            ProteinInferenceForm formForAnalysis = createFormForAnalysisInput(analysis, projectId);
            
            if(formForAnalysis != null)
                request.setAttribute("proteinInferenceFormAnalysis", formForAnalysis);
            
            if(formForSearch == null && formForAnalysis != null) 
                useSearchInput = false;
        }
        request.setAttribute("useSearchInput", useSearchInput);
        
        // Go!
        return mapping.findForward("Success");

    }

    private ProteinInferenceForm createFormForAnalysisInput(MsSearchAnalysis analysis, int projectId) {
        
        // This form should be created only if the analyis program is Percolator
        if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
            
            ProteinInferInputGetter inputGetter = ProteinInferInputGetter.instance();
            
            ProteinInferenceForm formForAnalysis = new ProteinInferenceForm();
            formForAnalysis.setInputType(InputType.ANALYSIS);
            formForAnalysis.setProjectId(projectId);
            ProteinInferInputSummary inputSummary = inputGetter.getInputAnalysisSummary(analysis);
            formForAnalysis.setInputSummary(inputSummary);
            // set the IDPicker parameters
            ProgramParameters params2 = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC);
            formForAnalysis.setProgramParams(params2);
            return formForAnalysis;
        }
        else
            return null;
    }

    private ProteinInferenceForm createFormForSearchInput(MsSearch search, int projectId) {
        
        // This form should be created ONLY if search program for this search was either 
        // Sequest or Percolator
        Program program = search.getSearchProgram();
        if(program == Program.SEQUEST // || program == Program.EE_NORM_SEQUEST 
                || program == Program.PROLUCID) {
            
            ProteinInferInputGetter inputGetter = ProteinInferInputGetter.instance();
            
            ProteinInferInputSummary searchSummary = inputGetter.getInputSearchSummary(search);
            ProteinInferenceForm formForSearch = new ProteinInferenceForm();
            formForSearch.setInputSummary(searchSummary);
            formForSearch.setInputType(InputType.SEARCH);
            formForSearch.setProjectId(projectId);
            // set the IDPicker parameters
            ProgramParameters params = null;
            if(program == Program.SEQUEST ) //|| program == Program.EE_NORM_SEQUEST)
                params = new ProgramParameters(ProteinInferenceProgram.PROTINFER_SEQ);
            else if(program == Program.PROLUCID)
                params = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PLCID);
            setProgramDetails(params, search);
            formForSearch.setProgramParams(params);
            return formForSearch;
        }
        else {
            return null;
        }
    }
    
    private void setProgramDetails(ProgramParameters params, MsSearch search) {
        Program program = search.getSearchProgram();
        
        if(program == Program.PROLUCID) {
            // TODO Tell the user the type of PrimaryScore reported by ProLuCID.
        }
    }
    
}
