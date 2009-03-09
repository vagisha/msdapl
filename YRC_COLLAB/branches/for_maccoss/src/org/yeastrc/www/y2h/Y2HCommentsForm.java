/*
 * Y2HCommentsForm.java
 * Created on Jul 13, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.y2h;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 13, 2006
 */

public class Y2HCommentsForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();

		
		return errors;
	}


	
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the screenID.
	 */
	public int getScreenID() {
		return screenID;
	}
	/**
	 * @param screenID The screenID to set.
	 */
	public void setScreenID(int screenID) {
		this.screenID = screenID;
	}
	
	
	private String comments;
	private int screenID;


}
