/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import org.yeastrc.data.*;

/**
 * Controller class for saving a project.
 */
public class SaveDisseminationAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		String[] groups = null;
		String description = null;
		String name = null;
		String phone = null;
		String email = null;
		String address = null;
		String FEDEX = null;
		String comments = null;
		boolean commercial = false;
		boolean shipped = false;
		
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
		Dissemination project;
		
		try {
			project = (Dissemination)(ProjectFactory.getProject(projectID));
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
		groups = ((EditDisseminationForm)(form)).getGroups();
		description = ((EditDisseminationForm)(form)).getDescription();
		name = ((EditDisseminationForm)(form)).getName();
		phone = ((EditDisseminationForm)(form)).getPhone();
		email = ((EditDisseminationForm)(form)).getEmail();
		address = ((EditDisseminationForm)(form)).getAddress();
		FEDEX = ((EditDisseminationForm)(form)).getFEDEX();
		commercial = ((EditDisseminationForm)(form)).getCommercial();
		shipped = ((EditDisseminationForm)(form)).getShipped();
		comments = ((EditDisseminationForm)(form)).getComments();



		// Set blank items to null
		if (description.equals("")) description = null;
		if (name.equals("")) name = null;
		if (phone.equals("")) phone = null;
		if (email.equals("")) email = null;
		if (address.equals("")) address = null;
		if (FEDEX.equals("")) FEDEX = null;
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

		// set the researchers
		project.setResearchers( null );
		project.setResearchers( ((EditDisseminationForm)(form)).getResearcherList() );
		project.setPI( ((EditDisseminationForm)(form)).getPI());

		project.setDescription(description);
		project.setName(name);
		project.setPhone(phone);
		project.setEmail(email);
		project.setAddress(address);
		project.setFEDEX(FEDEX);
		project.setCommercial(commercial);
		project.setShipped(shipped);
		project.setComments(comments);

		
		// Save the project
		project.save();

		// Go!
		return mapping.findForward("viewProject");

	}
	
}