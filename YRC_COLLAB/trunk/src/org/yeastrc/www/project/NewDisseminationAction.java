/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

/**
 * Controller class for creating a new Dissemination.
 */
public class NewDisseminationAction extends Action {

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
		EditDisseminationForm newForm = new EditDisseminationForm();
		request.setAttribute("editDisseminationForm", newForm);
		
		// Set the default PI to this user.
		newForm.setPI(researcher.getID());
		newForm.setEmail(researcher.getEmail());
		newForm.setName(researcher.getFirstName() + " " + researcher.getLastName());

		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection researchers = Projects.getAllResearchers();
		session.setAttribute("researchers", researchers);

		// Go!
		return mapping.findForward("Success");

	}
	
}