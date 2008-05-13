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
public class EditDisseminationForm extends EditProjectForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		
		if (this.getEmail() == null || this.getEmail().length() < 1) {
			errors.add("project", new ActionMessage("error.dissemination.noemail"));
		}
		
		if (this.getAddress() == null || this.getAddress().length() < 1) {
			errors.add("project", new ActionMessage("error.dissemination.noaddress"));
		}

		if (this.getName() == null || this.getName().length() < 1) {
			errors.add("project", new ActionMessage("error.dissemination.noname"));
		}
		
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
	
	/** Set the email addy */
	public void setEmail(String arg) { this.email = arg; }
	
	/** Set the phone number */
	public void setPhone(String arg) { this.phone = arg; }
	
	/** Set the description */
	public void setDescription(String arg) { this.description = arg; }
	
	/** Set the name */
	public void setName(String arg) { this.name = arg; }
	
	/** Set the address */
	public void setAddress(String arg) { this.address = arg; }
	
	/** Set the FEDEX # */
	public void setFEDEX(String arg) { this.FEDEX = arg; }
	
	/** Set whether or not this is for commercial purposes */
	public void setCommercial(boolean arg) { this.commercial = arg; }
	
	/** Set whether or not this has been shipped */
	public void setShipped(boolean arg) { this.shipped = arg; }


	/** Get the groups */
	public String[] getGroups() { return this.groups; }
	
	/** Get the email addy */
	public String getEmail() { return this.email; }
	
	/** Get the phone number */
	public String getPhone() { return this.phone; }
	
	/** Get the description */
	public String getDescription() { return this.description; }
	
	/** get the name */
	public String getName() { return this.name; }
	
	/** Get the address */
	public String getAddress() { return this.address; }
	
	/** Get the FEDEX # */
	public String getFEDEX() { return this.FEDEX; }
	
	/** Get whether or not this is for commercial purposes */
	public boolean getCommercial() { return this.commercial; }
	
	/** Get whether or not this plasmid has been shipped */
	public boolean getShipped() { return this.shipped; }



	/** The groups for this collaboration */
	private String[] groups = new String[0];
	
	/** The email addy */
	private String email = null;
	
	/** The phone number */
	private String phone = null;
	
	/** The description */
	private String description = null;
	
	/** The name */
	private String name = null;
	
	/** The address */
	private String address = null;
	
	/** The FEDEX # */
	private String FEDEX = null;
	
	/** For commercial purposes? */
	private boolean commercial = false;
	
	/** Whether or not this has been shipped. */
	private boolean shipped = false;

}