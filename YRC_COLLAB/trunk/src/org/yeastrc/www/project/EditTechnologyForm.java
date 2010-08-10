/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Project;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class EditTechnologyForm extends EditProjectForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		
		/*
		// we need atleast one grant
		if (validGrantCount() < 1) {
		    // there could be grant information for the project entered via the old interface.
		    // if so, we will not return an error message
		    Project project = (Project) request.getSession().getAttribute("project");
		    if (project == null ||
		        project.getFundingTypes() == null ||
		        project.getFundingTypes().length() < 1) {
		        errors.add("grants", new ActionMessage("error.grant.nogrants"));
		    }
		}
		*/
		
		if (this.getTitle() == null || this.getTitle().length() < 1) {
			errors.add("title", new ActionMessage("error.project.notitle"));
		}

		if (this.getAbstract() == null || this.getAbstract().length() < 1) {
			errors.add("project", new ActionMessage("error.project.noabstract"));
		}
		
		/*
		if (this.getKeywords() == null || this.getKeywords().length() < 1) {
			errors.add("project", new ActionMessage("error.project.nokeywords"));
		}
		*/
		
		String[] groups = this.getGroups();
		if (groups == null || groups.length < 1) {
			errors.add("groups", new ActionMessage("error.collaboration.nogroups"));
		}

		return errors;

	}


	/** Set the groups */
	public void setGroups(String[] groups) {
		if (groups != null) { this.groups = groups; }
	}
	
	/** Get the groups */
	public String[] getGroups() { return this.groups; }

	/** The groups for this collaboration */
	private String[] groups = new String[0];
	

}