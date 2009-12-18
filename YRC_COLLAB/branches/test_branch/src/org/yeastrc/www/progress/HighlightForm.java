/* HighlightForm.java
 * Created on Jun 7, 2004
 */
package org.yeastrc.www.progress;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class HighlightForm extends ActionForm {

	public HighlightForm() {
		this.id = 0;
		this.projectID = 0;
		this.body = null;
		this.title = null;
		this.year = (new Date()).getYear() + 1900;
	}

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		

		if (this.getTitle() == null || this.getTitle().length() < 1) {
			errors.add("highlight", new ActionMessage("error.highlight.title"));
		}

		if (this.getBody() == null || this.getBody().length() < 1) {
			errors.add("highlight", new ActionMessage("error.highlight.body"));
		}

		return errors;
	}

	private int id;
	private String title;
	private int projectID;
	private String body;
	private int year;

	/**
	 * @return
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 */
	public void setBody(String string) {
		body = string;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * @param i
	 */
	public void setProjectID(int i) {
		projectID = i;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * @return
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param i
	 */
	public void setYear(int i) {
		year = i;
	}

}