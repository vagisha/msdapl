/**
 * DoProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Projects;
import org.yeastrc.www.proteinfer.ProgramParameters.Param;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.ProgramParam;
import edu.uwpr.protinfer.ProgramParam.ParamMaker;

/**
 * 
 */
public class DoProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DoProteinInferenceAction.class);
    
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
        
        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        ProteinInferInputSummary inputSummary = prinferForm.getInputSummary();
        ProgramParameters params = prinferForm.getProgramParams();
        
        // If "remove ambiguous spectrum" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean found = false;
        ProgramParam progParam = ParamMaker.makeRemoveAmbigSpectraParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(progParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                found = true;
                break;
            }
        }
        if(!found) {
            Param myParam = new Param(progParam);
            myParam.setValue("false");
            params.addParam(myParam);
        }
        
        if(prinferForm.isIndividualRuns()) {
            ProteinferJobSaver.instance().saveMultiJobToDatabase(user.getID(), inputSummary, params, 
                    prinferForm.getInputType(), prinferForm.getComments());
        }
        else {
            ProteinferJobSaver.instance().saveJobToDatabase(user.getID(), inputSummary, params, 
                prinferForm.getInputType(), prinferForm.getComments());
        }
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+prinferForm.getProjectId(), success.getRedirect() ) ;
        return success;

    }
    
}
