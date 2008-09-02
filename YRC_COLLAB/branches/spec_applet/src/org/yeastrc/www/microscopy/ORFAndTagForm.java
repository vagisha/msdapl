/*
 * ORFAndTagForm.java
 * Created on Jun 2, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 2, 2006
 */

public class ORFAndTagForm extends ActionForm {

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
	 * @return Returns the idCode.
	 */
	public String getIdCode() {
		return idCode;
	}
	/**
	 * @param idCode The idCode to set.
	 */
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	private String comments;
	private String idCode;
	
}
