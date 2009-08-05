/**
 * 
 */
package org.yeastrc.www.jobqueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.jobqueue.JobResetter;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * @author Mike
 *
 */
public class ResetJobAction extends Action {

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
			
			try {
				MSJob job = MSJobFactory.getInstance().getJob( Integer.parseInt( request.getParameter( "id" ) ) );
			
				Project project = job.getProject();
	            if(!project.checkAccess(user.getResearcher())) {
	                 ActionErrors errors = new ActionErrors();
	                 errors.add("username", new ActionMessage("error.general.errorMessage", 
	                         "You may reset upload jobs only for projects to which you are affiliated"));
	                 saveErrors( request, errors );
	                 return mapping.findForward( "Failure" );
	            }
	            
				if (job == null)
					return mapping.findForward( "Failure" );

				JobResetter.getInstance().resetJob( job );
				request.setAttribute( "job", job );
				
			} catch (Exception e) {
				return mapping.findForward( "Failure" );
			}
			
			
			return mapping.findForward( "Success" );
		}
	}