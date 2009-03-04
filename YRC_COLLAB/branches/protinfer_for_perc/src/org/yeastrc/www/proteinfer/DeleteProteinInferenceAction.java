/**
 * DeleteProteinInferenceAction.java
 * @author Vagisha Sharma
 * Mar 3, 2009
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
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;

/**
 * 
 */
public class DeleteProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DeleteProteinInferenceAction.class);
    
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
        
        // get the project ID so we can redirect back in case of failure
        int projectId = -1;
        if (request.getParameter("projectId") != null) {
            try {projectId = Integer.parseInt(request.getParameter("projectId"));}
            catch(NumberFormatException e) {projectId = -1;}
        }
        if(projectId == -1) {
            log.error("Invalid project id: "+projectId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.project.noprojectid"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        
        // get the protein inference id
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            ActionForward failure = mapping.findForward( "Failure" ) ;
            failure = new ActionForward( failure.getPath() + "?ID="+projectId, failure.getRedirect() ) ;
            return failure;
        }
        
        // first delete the job from the job queue database
        ProteinferJob job = ProteinferRunSearcher.getJobForPinferRunId(pinferId);
        
        if(job != null) {
            JobDeleter.getInstance().deleteJob(job);
        }
        else {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.proteinfer.deletejob", "Could not get job for protein inference ID: "+pinferId));
            saveErrors(request, errors);
            ActionForward failure = mapping.findForward( "Failure" ) ;
            failure = new ActionForward( failure.getPath() + "?ID="+projectId, failure.getRedirect() ) ;
            return failure;
        }
        
        // now delete the protein inference run from the mass spec database.
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        try {
            fact.getProteinferRunDao().delete(pinferId);
        }
        catch(Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.deletejob", "Error deleting protein inference ID: "+pinferId));
            saveErrors(request, errors);
            ActionForward failure = mapping.findForward( "Failure" ) ;
            failure = new ActionForward( failure.getPath() + "?ID="+projectId, failure.getRedirect() ) ;
            return failure;
        }
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+projectId, success.getRedirect() ) ;
        return success;
    }
}
