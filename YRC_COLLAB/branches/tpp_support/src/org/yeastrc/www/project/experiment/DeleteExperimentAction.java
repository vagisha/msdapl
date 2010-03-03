/**
 * DeleteExperimentAction.java
 * @author Vagisha Sharma
 * Dec 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.proteinfer.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DeleteExperimentAction extends Action {

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

        int experimentId = 0;
        
        // get the experiment ID
        String strId = request.getParameter("experimentId");
        try {
            if(strId != null)
                experimentId = Integer.parseInt(strId.trim());
        }
        catch (NumberFormatException nfe) {
            experimentId = 0;
        }
        
        if(experimentId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", 
                    "deleting experiment: "+strId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentId);
        
        if(projectId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                    "No project ID found for experiment ID; "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        // Make sure the user has access to delete this project (administrators or researchers associated
        // with this project)
        Project project = ProjectFactory.getProject(projectId);
        if (!project.checkAccess(user.getResearcher())) {
            
            // This user doesn't have write access to this project.
            ActionErrors errors = new ActionErrors();
            errors.add("project", new ActionMessage("error.project.noaccess"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Make sure this experimentID is not currently being uploaded
        MSJob job = null;
        try {
            job = MSJobFactory.getInstance().getJobForProjectExperiment(projectId, experimentId);
            int status = job.getStatus();
            if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK
               || status == JobUtils.STATUS_PENDING_UPLOAD) {
              
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "Experiment ID "+experimentId+" cannot be deleted. It is still being uploaded."));
                saveErrors( request, errors );
                return mapping.findForward("standardHome");
            }
            
        }
        catch(Exception e) {;} // because job with experimentID does not exist. Should not really happen.
        
        // Make sure there are no running protein inference jobs for this experiment
        List<ProteinferJob> piJobs = ProteinInferJobSearcher.instance().getProteinferJobsForMsExperiment(experimentId);
        for(ProteinferJob piJob: piJobs) {
            int status = piJob.getStatus();
            if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK
                    || status == JobUtils.STATUS_PENDING_UPLOAD) {
                   
                     ActionErrors errors = new ActionErrors();
                     errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                             "Experiment ID "+experimentId+" cannot be deleted. "+
                             "There are unfinished Protein Inference jobs."));
                     saveErrors( request, errors );
                     return mapping.findForward("standardHome");
                 }
        }
        
        // add to experiment deleter.
        // project and experiment will be unlinked first so that they don't show up on the project page.
        try {
            ExperimentDeleter.getInstance().addExperimentId(experimentId, projectId, true);
        }
        catch(SQLException e) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                    "Error deleting experiment ID "+experimentId+". "+e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
       
        ActionForward success = mapping.findForward("Success") ;
        success = new ActionForward( success.getPath() + "?experimentId="+experimentId+"&projectId="+projectId,
                success.getRedirect()) ;
        
        return success ;
    }
}
