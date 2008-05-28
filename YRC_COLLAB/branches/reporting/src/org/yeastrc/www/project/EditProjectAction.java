/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantRecord;
import org.yeastrc.project.Dissemination;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.project.Training;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Controller class for editing a project.
 */
public class EditProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// Get the projectID they're after
		int projectID;
		
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
		Project project;
		
		try {
			project = ProjectFactory.getProject(projectID);
			if (!project.checkAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} 
		catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("project", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}


		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("project", project);

		// get the grants for this project and set them in the request
		List<Grant> grants = GrantRecord.getGrantsForProject(project.getID());
		request.setAttribute("grants", grants);
		
		// Where do you want to go from here?
		String forwardStr = "";


		EditProjectForm newForm = null;


		// Forward them on to the happy success view page!		
		if (project.getShortType().equals(Projects.COLLABORATION)) {
			newForm = new EditCollaborationForm();			
			request.setAttribute("editCollaborationForm", newForm);
			forwardStr = "Collaboration";
			
			String[] groups = project.getGroupsArray();
			((EditCollaborationForm)(newForm)).setGroups(groups);

//			newForm.setFundingTypes(project.getFundingTypesArray());
//			newForm.setFederalFundingTypes(project.getFederalFundingTypesArray());
//			newForm.setGrantAmount( project.getGrantAmount() );
//			newForm.setGrantNumber( project.getGrantNumber() );
//			newForm.setFoundationName( project.getFoundationName() );
			
			
		}


		else if (project.getShortType().equals(Projects.DISSEMINATION)) {
			newForm = new EditDisseminationForm();			
			request.setAttribute("editDisseminationForm", newForm);
			forwardStr = "Dissemination";
			
			String[] groups = project.getGroupsArray();

			((EditDisseminationForm)(newForm)).setGroups(groups);
			((EditDisseminationForm)(newForm)).setDescription(((Dissemination)project).getDescription());
			((EditDisseminationForm)(newForm)).setName(((Dissemination)project).getName());
			((EditDisseminationForm)(newForm)).setPhone(((Dissemination)project).getPhone());
			((EditDisseminationForm)(newForm)).setEmail(((Dissemination)project).getEmail());
			((EditDisseminationForm)(newForm)).setAddress(((Dissemination)project).getAddress());
			((EditDisseminationForm)(newForm)).setFEDEX(((Dissemination)project).getFEDEX());
			((EditDisseminationForm)(newForm)).setCommercial(((Dissemination)project).getCommercial());
			((EditDisseminationForm)(newForm)).setShipped(((Dissemination)project).getShipped());
		}

		else if (project.getShortType().equals(Projects.TECHNOLOGY)) {
			newForm = new EditTechnologyForm();			
			request.setAttribute("editTechnologyForm", newForm);
			forwardStr = "Technology";
			
			String[] groups = project.getGroupsArray();
			((EditTechnologyForm)(newForm)).setGroups(groups);

//			newForm.setFundingTypes(project.getFundingTypesArray());
//			newForm.setFederalFundingTypes(project.getFederalFundingTypesArray());
//			newForm.setGrantAmount( project.getGrantAmount() );
//			newForm.setGrantNumber( project.getGrantNumber() );
//			newForm.setFoundationName( project.getFoundationName() );
			
		
		}
		
		else if (project.getShortType().equals(Projects.TRAINING)) {
			newForm = new EditTrainingForm();			
			request.setAttribute("editTrainingForm", newForm);
			forwardStr = "Training";
			
			String[] groups = project.getGroupsArray();

			((EditTrainingForm)(newForm)).setGroups(groups);

			((EditTrainingForm)(newForm)).setDescription(((Training)project).getDescription());
			((EditTrainingForm)(newForm)).setHours(((Training)project).getHours());
			((EditTrainingForm)(newForm)).setDays(((Training)project).getDays());
		}
		
		else {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidtype"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}


		// Set the parameters available to all project types
		newForm.setTitle(project.getTitle());
		newForm.setAbstract(project.getAbstract());
		newForm.setPublicAbstract(project.getPublicAbstract());
		newForm.setProgress(project.getProgress());
		//newForm.setKeywords(project.getKeywords());
		newForm.setComments(project.getComments());
		newForm.setPublications(project.getPublications());
		
		// Set the admin parameters
		newForm.setBTA(project.getBTA());
		//newForm.setAxisI(project.getAxisI());
		//newForm.setAxisII(project.getAxisII());

		// Set the Researchers
		Researcher res = project.getPI();
		if (res != null) newForm.setPI(res.getID());
		
		res = project.getResearcherB();
		if (res != null) newForm.setResearcherB(res.getID());

		res = project.getResearcherC();
		if (res != null) newForm.setResearcherC(res.getID());

		res = project.getResearcherD();
		if (res != null) newForm.setResearcherD(res.getID());


		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection researchers = Projects.getAllResearchers();
		request.setAttribute("researchers", researchers);


		// Go!
		return mapping.findForward(forwardStr);

	}
	
}