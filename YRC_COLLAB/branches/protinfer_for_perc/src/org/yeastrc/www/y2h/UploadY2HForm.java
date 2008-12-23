/* UploadY2HForm.java
 * Created on May 13, 2004
 */
package org.yeastrc.www.y2h;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 13, 2004
 *
 */
public class UploadY2HForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();

		if (this.projectID == 0)
			errors.add("upload", new ActionMessage("error.upload.noproject"));

		
		DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
		ParsePosition pp = new ParsePosition(0);
		
		if (this.day == null || this.month == null || this.year == null) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		if (getDay().equals("0") || getMonth().equals("0") || getYear().equals("0")) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		this.screenDate = df.parse(this.month + "/" + this.day + "/" + this.year, pp);
		if (pp.getIndex() == 0) {
			errors.add("upload", new ActionMessage("error.upload.invaliddate"));
		}
		
		if (this.dataFile == null || this.dataFile.getFileSize() < 1) {
			errors.add("upload", new ActionMessage("error.upload.nodatafile"));
		}
		
		if (this.startResidue == 0 && this.endResidue != 0) {
			errors.add("upload", new ActionMessage("error.upload.y2h.nostart"));
		}
		
		if (this.startResidue != 0 && this.endResidue == 0) {
			errors.add("upload", new ActionMessage("error.upload.y2h.noend"));
		}
		
		return errors;
	}

	// Instance vars
	private int projectID;
	private String month;
	private String day;
	private String year;
	private String comments;
	private Date screenDate;
	private FormFile dataFile;
	private String mutations;
	private String vectorConfig;
	private int startResidue;
	private int endResidue;

	/**
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * @param i
	 */
	public void setProjectID(int i) {
		projectID = i;
	}

	/**
	 * @return
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @return
	 */
	public String getDay() {
		return day;
	}

	/**
	 * @return
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		comments = string;
	}

	/**
	 * @param string
	 */
	public void setDay(String string) {
		day = string;
	}

	/**
	 * @param string
	 */
	public void setMonth(String string) {
		month = string;
	}

	/**
	 * @param string
	 */
	public void setYear(String string) {
		year = string;
	}

	/**
	 * @return
	 */
	public Date getScreenDate() {
		return screenDate;
	}

	/**
	 * @param date
	 */
	public void setScreenDate(Date date) {
		screenDate = date;
	}

	/**
	 * @return
	 */
	public FormFile getDataFile() {
		return dataFile;
	}

	/**
	 * @param file
	 */
	public void setDataFile(FormFile file) {
		dataFile = file;
	}

	/**
	 * @return Returns the endResidue.
	 */
	public int getEndResidue() {
		return endResidue;
	}
	/**
	 * @param endResidue The endResidue to set.
	 */
	public void setEndResidue(int endResidue) {
		this.endResidue = endResidue;
	}
	/**
	 * @return Returns the mutations.
	 */
	public String getMutations() {
		return mutations;
	}
	/**
	 * @param mutations The mutations to set.
	 */
	public void setMutations(String mutations) {
		this.mutations = mutations;
	}
	/**
	 * @return Returns the startResidue.
	 */
	public int getStartResidue() {
		return startResidue;
	}
	/**
	 * @param startResidue The startResidue to set.
	 */
	public void setStartResidue(int startResidue) {
		this.startResidue = startResidue;
	}
	/**
	 * @return Returns the vectorConfig.
	 */
	public String getVectorConfig() {
		return vectorConfig;
	}
	/**
	 * @param vectorConfig The vectorConfig to set.
	 */
	public void setVectorConfig(String vectorConfig) {
		this.vectorConfig = vectorConfig;
	}
}
