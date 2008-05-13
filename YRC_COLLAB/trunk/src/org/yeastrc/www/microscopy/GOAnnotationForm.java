/*
 * GOAnnotationForm.java
 * Created on Jun 6, 2006
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
 * @version Jun 6, 2006
 */

public class GOAnnotationForm extends ActionForm {

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
	
	private String goAcc;
	private String idCode;
	
	
	
	/**
	 * @return Returns the wl.
	 */
	public String getIdCode() {
		return idCode;
	}
	/**
	 * @param wl The wl to set.
	 */
	public void setIdCode(String wl) {
		this.idCode = wl;
	}
	/**
	 * @return Returns the goAcc.
	 */
	public String getGoAcc() {
		return goAcc;
	}
	/**
	 * @param goAcc The goAcc to set.
	 */
	public void setGoAcc(String goAcc) {
		this.goAcc = goAcc;
	}
}
