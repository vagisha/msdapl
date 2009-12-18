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
import org.apache.struts.action.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class EditTrainingForm extends EditProjectForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		
		String[] groups = this.getGroups();
		if (groups == null || groups.length < 1) {
			errors.add("groups", new ActionMessage("error.collaboration.nogroups"));
		}

		String desc = this.getDescription();
		if (desc == null || desc.equals("")) {
			errors.add("training", new ActionMessage("error.training.nodescription"));
		}

		return errors;

	}


	/**
	 * @return Returns the isSeminars.
	 */
	public boolean isSeminars() {
		return isSeminars;
	}
	/**
	 * @param isSeminars The isSeminars to set.
	 */
	public void setSeminars(boolean isSeminars) {
		this.isSeminars = isSeminars;
	}
	
	/** Set the groups */
	public void setGroups(String[] groups) {
		if (groups != null) { this.groups = groups; }
	}

	/** Set the description */
	public void setDescription(String arg) { this.description = arg; }
	
	/** Set the hours */
	public void setHours(int arg) { this.hours = arg; }
	
	/** Set the days */
	public void setDays(int arg) { this.days = arg; }

	/** Set whether or not to send emails */
	public void setSendEmail(boolean arg) {
		this.sendEmail = arg;
	}
	
	/** Get the groups */
	public String[] getGroups() { return this.groups; }

	/** Get the description */
	public String getDescription() { return this.description; }
	
	/** Get the hours */
	public int getHours() { return this.hours; }
	
	/** Get the days */
	public int getDays() { return this.days; }

	/** Get whether or not to send email */
	public boolean getSendEmail() { return this.sendEmail; }

	/** The groups for this collaboration */
	private String[] groups = new String[0];
	
	/** The description */
	private String description = null;
	
	/** The hours */
	private int hours = 0;
	
	/** The days */
	private int days = 0;
	
	/** Whether or not to send email to YRC groups */
	private boolean sendEmail = true;
	
	/** Whether or not this is a seminars training project */
	private boolean isSeminars;

}