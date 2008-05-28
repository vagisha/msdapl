/*
 * UploadMicroscopyForm.java
 * Created on May 26, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

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
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 26, 2006
 */

public class UploadMicroscopyForm extends ActionForm {

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
		
		this.experimentDate = df.parse(this.month + "/" + this.day + "/" + this.year, pp);
		if (pp.getIndex() == 0) {
			errors.add("upload", new ActionMessage("error.upload.invaliddate"));
		}
		
		if (this.r3dFile == null || this.r3dFile.getFileSize() < 1) {
			errors.add("upload", new ActionMessage("error.upload.nodatafile"));
		}
		
		if (this.r3dLog == null || this.r3dLog.getFileSize() < 1) {
			errors.add("upload", new ActionMessage("error.upload.nologfile"));
		}
		
		// make sure the R3D log and data files are the same xperiment
		if (!r3dLog.getFileName().equals( r3dFile.getFileName() + ".log") ) {
			
			errors.add("upload", new ActionMessage("error.upload.microscopy.r3d"));
		}
		
		if (this.orf1 == null || this.orf1.equals( "" ))
			errors.add("upload", new ActionMessage("error.upload.noorf"));
		
		if (this.tag1.equals( "0" ))
			errors.add("upload", new ActionMessage("error.upload.notag1"));
		
		if (this.orf2 != null && !this.orf2.equals("") && this.tag2.equals( "0" ))
			errors.add("upload", new ActionMessage("error.upload.notag2"));
		
		if (this.orf3 != null && !this.orf3.equals("") && this.tag3.equals( "0" ))
			errors.add("upload", new ActionMessage("error.upload.notag3"));		
		
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
	 * @return Returns the day.
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day The day to set.
	 */
	public void setDay(String day) {
		this.day = day;
	}
	/**
	 * @return Returns the month.
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month The month to set.
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return Returns the projectID.
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the r3dFile.
	 */
	public FormFile getR3dFile() {
		return r3dFile;
	}
	/**
	 * @param file The r3dFile to set.
	 */
	public void setR3dFile(FormFile file) {
		r3dFile = file;
	}
	/**
	 * @return Returns the r3dLog.
	 */
	public FormFile getR3dLog() {
		return r3dLog;
	}
	/**
	 * @param log The r3dLog to set.
	 */
	public void setR3dLog(FormFile log) {
		r3dLog = log;
	}
	/**
	 * @return Returns the year.
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year The year to set.
	 */
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return Returns the cellGrowthMedium.
	 */
	public String getCellGrowthMedium() {
		return cellGrowthMedium;
	}
	/**
	 * @param cellGrowthMedium The cellGrowthMedium to set.
	 */
	public void setCellGrowthMedium(String cellGrowthMedium) {
		this.cellGrowthMedium = cellGrowthMedium;
	}
	/**
	 * @return Returns the cellGrowthTemp.
	 */
	public String getCellGrowthTemp() {
		return cellGrowthTemp;
	}
	/**
	 * @param cellGrowthTemp The cellGrowthTemp to set.
	 */
	public void setCellGrowthTemp(String cellGrowthTemp) {
		this.cellGrowthTemp = cellGrowthTemp;
	}
	/**
	 * @return Returns the cellStatus.
	 */
	public String getCellStatus() {
		return cellStatus;
	}
	/**
	 * @param cellStatus The cellStatus to set.
	 */
	public void setCellStatus(String cellStatus) {
		this.cellStatus = cellStatus;
	}
	/**
	 * @return Returns the cellTreatment.
	 */
	public String getCellTreatment() {
		return cellTreatment;
	}
	/**
	 * @param cellTreatment The cellTreatment to set.
	 */
	public void setCellTreatment(String cellTreatment) {
		this.cellTreatment = cellTreatment;
	}
	/**
	 * @return Returns the opticsDichroicMirror.
	 */
	public String getOpticsDichroicMirror() {
		return opticsDichroicMirror;
	}
	/**
	 * @param opticsDichroicMirror The opticsDichroicMirror to set.
	 */
	public void setOpticsDichroicMirror(String opticsDichroicMirror) {
		this.opticsDichroicMirror = opticsDichroicMirror;
	}
	
	
	/**
	 * @return Returns the experimentDate.
	 */
	public Date getExperimentDate() {
		return experimentDate;
	}
	/**
	 * @param experimentDate The experimentDate to set.
	 */
	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}
	/**
	 * @return Returns the orf1.
	 */
	public String getOrf1() {
		return orf1;
	}
	/**
	 * @param orf1 The orf1 to set.
	 */
	public void setOrf1(String orf1) {
		this.orf1 = orf1;
	}
	/**
	 * @return Returns the orf2.
	 */
	public String getOrf2() {
		return orf2;
	}
	/**
	 * @param orf2 The orf2 to set.
	 */
	public void setOrf2(String orf2) {
		this.orf2 = orf2;
	}
	/**
	 * @return Returns the orf3.
	 */
	public String getOrf3() {
		return orf3;
	}
	/**
	 * @param orf3 The orf3 to set.
	 */
	public void setOrf3(String orf3) {
		this.orf3 = orf3;
	}
	/**
	 * @return Returns the tag1.
	 */
	public String getTag1() {
		return tag1;
	}
	/**
	 * @param tag1 The tag1 to set.
	 */
	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}
	/**
	 * @return Returns the tag2.
	 */
	public String getTag2() {
		return tag2;
	}
	/**
	 * @param tag2 The tag2 to set.
	 */
	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}
	/**
	 * @return Returns the tag3.
	 */
	public String getTag3() {
		return tag3;
	}
	/**
	 * @param tag3 The tag3 to set.
	 */
	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}
	
	
	int projectID;
	private FormFile r3dFile;
	private FormFile r3dLog;
	private String month;
	private String day;
	private String year;
	private String comments;
	private String cellStatus;
	private String cellTreatment;
	private String cellGrowthMedium;
	private String cellGrowthTemp = "30";
	private String opticsDichroicMirror;
	private Date experimentDate;
	private String orf1;
	private String orf2;
	private String orf3;
	private String tag1;
	private String tag2;
	private String tag3;
}
