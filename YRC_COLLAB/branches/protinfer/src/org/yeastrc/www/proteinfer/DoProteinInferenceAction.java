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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

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
        
        
        if (request.getSession().getAttribute("inferredProteins") != null) {
            return mapping.findForward("Success");
        }
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        MsSearchSummary searchSummary = prinferForm.getSearchSummary();
        ProgramParameters params = prinferForm.getProgramParams();
        
        // TODO validate the parameters (should be done in form?)
        ProteinferJobSaver.instance().saveJobToDatabase(user.getID(), searchSummary, params);
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+prinferForm.getProjectId(), success.getRedirect() ) ;
        return success;

    }
    
    private static float getTime(long start, long end) {
        long time = end - start;
        float seconds = (float)time / (1000.0f);
        return seconds;
    }
}
