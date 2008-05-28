/* AXISIForm.java
 * Created on Apr 29, 2004
 */
package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class AXISForm extends ActionForm {

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

	private String[] AXIS;
	

	/**
	 * @return
	 */
	public String[] getAXIS() {
		return AXIS;
	}

	/**
	 * @param strings
	 */
	public void setAXIS(String[] strings) {
		AXIS = strings;
	}

}