/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.List;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import org.yeastrc.data.*;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.ProjectGrantRecord;

/**
 * Controller class for saving a project.
 */
public class SaveCollaborationAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		String title = null;
		String[] groups = null;
		String projectAbstract = null;
		String publicAbstract = null;
		String progress = null;
		//String keywords = null;
		String publications = null;
		String comments;
		float bta = (float)0.0;
		String axisI = null;
		String axisII = null;

		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Load our project
		Collaboration project;
		
		try {
			project = (Collaboration)(ProjectFactory.getProject(projectID));
			if (!project.checkAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		//request.setAttribute("project", project);

		// We're saving!
		title = ((EditCollaborationForm)(form)).getTitle();
		groups = ((EditCollaborationForm)(form)).getGroups();
		projectAbstract = ((EditCollaborationForm)(form)).getAbstract();
		publicAbstract = ((EditCollaborationForm)(form)).getPublicAbstract();
		//keywords = ((EditCollaborationForm)(form)).getKeywords();
		progress = ((EditCollaborationForm)(form)).getProgress();
		publications = ((EditCollaborationForm)(form)).getPublications();
		comments = ((EditCollaborationForm)(form)).getComments();
		bta = ((EditCollaborationForm)(form)).getBTA();
		axisI = ((EditCollaborationForm)(form)).getAxisI();
		axisII = ((EditCollaborationForm)(form)).getAxisII();
		
		// Set blank items to null
		if (title.equals("")) title = null;
		if (projectAbstract.equals("")) projectAbstract = null;
		//if (keywords.equals("")) keywords = null;
		if (progress.equals("")) progress = null;
		if (publications.equals("")) publications = null;
		if (comments.equals("")) comments = null;

		// Set up the groups
		project.clearGroups();
		
		if (groups != null) {
			if (groups.length > 0) {
				for (int i = 0; i < groups.length; i++) {
					try { project.setGroup(groups[i]); }
					catch (InvalidIDException iie) {
					
						// Somehow got an invalid group...
						ActionErrors errors = new ActionErrors();
						errors.add("project", new ActionMessage("error.project.invalidgroup"));
						saveErrors( request, errors );
						return mapping.findForward("Failure");					
					}
				}
			}
		}

		// Set all of the new values in the project
		project.setTitle(title);

		// set the researchers
		project.setResearchers( null );
		project.setResearchers( ((EditCollaborationForm)(form)).getResearcherList() );
		project.setPI( ((EditCollaborationForm)(form)).getPI());		

		
		project.setAbstract(projectAbstract);
		project.setPublicAbstract(publicAbstract);
		//project.setKeywords(keywords);
		project.setProgress(progress);
		project.setPublications(publications);
		project.setComments(comments);
		project.setBTA(bta);
		project.setAxisI(axisI);
		project.setAxisII(axisII);
		
		
		// Save the project
		project.save();

		// save the project grants
		List<Grant> grants = ((EditCollaborationForm)(form)).getGrantList();
		ProjectGrantRecord.getInstance().saveProjectGrants(project.getID(), grants);
		
		// remove the project, if it exists in the session
        request.getSession().removeAttribute("project");
        
		// Go!
		return mapping.findForward("viewProject");

	}
	
}