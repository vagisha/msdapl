/*
 * NewSeminarsAction.java
 * Created on Jul 18, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.project;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Controller class for creating a new Training.
 */
public class NewSeminarsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// The Researcher
		Researcher researcher = user.getResearcher();

		
		// Create our ActionForm
		EditTrainingForm newForm = new EditTrainingForm();
		request.setAttribute("editTrainingForm", newForm);
		
		// Set the default PI to this user.
		newForm.setPI(researcher.getID());

		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection researchers = Projects.getAllResearchers();
		session.setAttribute("researchers", researchers);

		// Go!
		return mapping.findForward("Success");

	}
	
}