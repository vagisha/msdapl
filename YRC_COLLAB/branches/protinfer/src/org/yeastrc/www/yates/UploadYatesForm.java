/*
 * UploadYatesForm.java
 * Created on Oct 12, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.taxonomy.TaxonomySearcher;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 12, 2004
 */

public class UploadYatesForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
		ParsePosition pp = new ParsePosition(0);
		
		if (this.projectID == 0)
			errors.add("upload", new ActionMessage("error.upload.noproject"));
		
		if (this.directoryName1 == null) {
			errors.add("upload", new ActionMessage("error.upload.nodirectoryname"));
		}
		
		/*
		if (this.targetSpecies1 == 0) {
			errors.add("upload", new ActionMessage("error.upload.notargetspecies"));
		}
		*/
		
		if (this.day1 == null || this.month1 == null || this.year1 == null) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		if (getDay1().equals("0") || getMonth1().equals("0") || getYear1().equals("0")) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		this.runDate1 = df.parse(this.month1 + "/" + this.day1 + "/" + this.year1, pp);
		if (pp.getIndex() == 0) {
			errors.add("upload", new ActionMessage("error.upload.invaliddate"));
		}
		
		int speciesID = this.getTargetSpecies1();
		TaxonomySearcher ts = TaxonomySearcher.getInstance();
		try {
			if (ts.getName(speciesID) == null)
				errors.add("upload", new ActionMessage("error.upload.invalidspecies"));
		} catch (Exception e) {
			errors.add("upload", new ActionMessage("error.upload.invalidspecies"));
		}
		
		
		if (this.directoryName2 != null && !this.directoryName2.equals( "" )) {
			/*
			if (this.targetSpecies2 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 2"));
			}
			*/
			
			if (this.day2 == null || this.month2 == null || this.year2 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 2"));
			}
			
			if (getDay2().equals("0") || getMonth2().equals("0") || getYear2().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 2"));
			}
			
			pp = new ParsePosition(0);
			this.runDate2 = df.parse(this.month2 + "/" + this.day2 + "/" + this.year2, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 2"));
			}
			
			speciesID = this.getTargetSpecies2();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 2"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 2"));
			}
		}
		if (this.directoryName3 != null && !this.directoryName3.equals( "" )) {	
			/*
			if (this.targetSpecies3 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 3"));
			}
			*/
			
			if (this.day3 == null || this.month3 == null || this.year3 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 3"));
			}
			
			if (getDay3().equals("0") || getMonth3().equals("0") || getYear3().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 3"));
			}
			
			pp = new ParsePosition(0);
			this.runDate3 = df.parse(this.month3 + "/" + this.day3 + "/" + this.year3, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 3"));
			}
			
			speciesID = this.getTargetSpecies3();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 3"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 3"));
			}
		}		
		if (this.directoryName4 != null && !this.directoryName4.equals( "" )) {
			/*
			if (this.targetSpecies4 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 4"));
			}
			*/
			
			if (this.day4 == null || this.month4 == null || this.year4 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 4"));
			}
			
			if (getDay4().equals("0") || getMonth4().equals("0") || getYear4().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 4"));
			}
			
			pp = new ParsePosition(0);
			this.runDate4 = df.parse(this.month4 + "/" + this.day4 + "/" + this.year4, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 4"));
			}
			
			speciesID = this.getTargetSpecies4();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 4"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 4"));
			}
		}
		if (this.directoryName5 != null && !this.directoryName5.equals( "" )) {
			/*
			if (this.targetSpecies5 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 5"));
			}
			*/
			
			if (this.day5 == null || this.month5 == null || this.year5 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 5"));
			}
			
			if (getDay5().equals("0") || getMonth5().equals("0") || getYear5().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 5"));
			}
			
			pp = new ParsePosition(0);
			this.runDate5 = df.parse(this.month5 + "/" + this.day5 + "/" + this.year5, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 5"));
			}
			
			speciesID = this.getTargetSpecies5();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 5"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 5"));
			}
		}
		if (this.directoryName6 != null && !this.directoryName6.equals( "" )) {
			/*
			if (this.targetSpecies6 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 6"));
			}
			*/
			
			if (this.day6 == null || this.month6 == null || this.year6 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 6"));
			}
			
			if (getDay6().equals("0") || getMonth6().equals("0") || getYear6().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 6"));
			}
			
			pp = new ParsePosition(0);
			this.runDate6 = df.parse(this.month6 + "/" + this.day6 + "/" + this.year6, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 6"));
			}
			
			speciesID = this.getTargetSpecies6();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 6"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 6"));
			}
		}
		if (this.directoryName7 != null && !this.directoryName7.equals( "" )) {	
			/*
			if (this.targetSpecies7 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 7"));
			}
			*/
			
			if (this.day7 == null || this.month7 == null || this.year7 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 7"));
			}
			
			if (getDay7().equals("0") || getMonth7().equals("0") || getYear7().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 7"));
			}
			
			pp = new ParsePosition(0);
			this.runDate7 = df.parse(this.month7 + "/" + this.day7 + "/" + this.year7, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 7"));
			}
			
			speciesID = this.getTargetSpecies7();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 7"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 7"));
			}
		}
		if (this.directoryName8 != null && !this.directoryName8.equals( "" )) {
			/*
			if (this.targetSpecies8 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 8"));
			}
			*/
			
			if (this.day8 == null || this.month8 == null || this.year8 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 8"));
			}
			
			if (getDay8().equals("0") || getMonth8().equals("0") || getYear8().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 8"));
			}
			
			pp = new ParsePosition(0);
			this.runDate8 = df.parse(this.month8 + "/" + this.day8 + "/" + this.year8, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 8"));
			}
			
			speciesID = this.getTargetSpecies8();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 8"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 8"));
			}
		}
		if (this.directoryName9 != null && !this.directoryName9.equals( "" )) {
			/*
			if (this.targetSpecies9 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 9"));
			}
			*/
			
			if (this.day9 == null || this.month9 == null || this.year9 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 9"));
			}
			
			if (getDay9().equals("0") || getMonth9().equals("0") || getYear9().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 9"));
			}
			
			pp = new ParsePosition(0);
			this.runDate9 = df.parse(this.month9 + "/" + this.day9 + "/" + this.year9, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 9"));
			}
			
			speciesID = this.getTargetSpecies9();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 9"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 9"));
			}
		}
		if (this.directoryName10 != null && !this.directoryName10.equals( "" )) {
			/*
			if (this.targetSpecies10 == 0) {
				errors.add("upload", new ActionMessage("error.upload.notargetspecies", "Form 10"));
			}
			*/
			
			if (this.day10 == null || this.month10 == null || this.year10 == null) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 10"));
			}
			
			if (getDay10().equals("0") || getMonth10().equals("0") || getYear10().equals("0")) {
				errors.add("upload", new ActionMessage("error.upload.noexperimentdate", "Form 10"));
			}
			
			pp = new ParsePosition(0);
			this.runDate10 = df.parse(this.month10 + "/" + this.day10 + "/" + this.year10, pp);
			if (pp.getIndex() == 0) {
				errors.add("upload", new ActionMessage("error.upload.invaliddate", "Form 10"));
			}
			
			speciesID = this.getTargetSpecies10();
			try {
				if (ts.getName(speciesID) == null)
					errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 10"));
			} catch (Exception e) {
				errors.add("upload", new ActionMessage("error.upload.invalidspecies", "Form 10"));
			}
		}
		
		
		return errors;
	}
	
	private Date runDate1;
	private Date runDate2;
	private Date runDate3;
	private Date runDate4;
	private Date runDate5;
	private Date runDate6;
	private Date runDate7;
	private Date runDate8;
	private Date runDate9;
	private Date runDate10;
	
	private String month1;
	private String month2;
	private String month3;
	private String month4;
	private String month5;
	private String month6;
	private String month7;
	private String month8;
	private String month9;
	private String month10;
	
	private String day1;
	private String day2;
	private String day3;
	private String day4;
	private String day5;
	private String day6;
	private String day7;
	private String day8;
	private String day9;
	private String day10;
	
	private String year1;
	private String year2;
	private String year3;
	private String year4;
	private String year5;
	private String year6;
	private String year7;
	private String year8;
	private String year9;
	private String year10;
	
	private String directoryName1;
	private String directoryName2;
	private String directoryName3;
	private String directoryName4;
	private String directoryName5;
	private String directoryName6;
	private String directoryName7;
	private String directoryName8;
	private String directoryName9;
	private String directoryName10;
	
	private String baitDesc1;
	private String baitDesc2;
	private String baitDesc3;
	private String baitDesc4;
	private String baitDesc5;
	private String baitDesc6;
	private String baitDesc7;
	private String baitDesc8;
	private String baitDesc9;
	private String baitDesc10;

	private int baitSpecies1;
	private int baitSpecies2;
	private int baitSpecies3;
	private int baitSpecies4;
	private int baitSpecies5;
	private int baitSpecies6;
	private int baitSpecies7;
	private int baitSpecies8;
	private int baitSpecies9;
	private int baitSpecies10;
		
	private int targetSpecies1;
	private int targetSpecies2;
	private int targetSpecies3;
	private int targetSpecies4;
	private int targetSpecies5;
	private int targetSpecies6;
	private int targetSpecies7;
	private int targetSpecies8;
	private int targetSpecies9;
	private int targetSpecies10;	

	private int otherSpecies1;
	private int otherSpecies2;
	private int otherSpecies3;
	private int otherSpecies4;
	private int otherSpecies5;
	private int otherSpecies6;
	private int otherSpecies7;
	private int otherSpecies8;
	private int otherSpecies9;
	private int otherSpecies10;
	
	private String comments1;
	private String comments2;
	private String comments3;
	private String comments4;
	private String comments5;
	private String comments6;
	private String comments7;
	private String comments8;
	private String comments9;
	private String comments10;
	
	private int projectID;
	private String group;
	/**
	 * @return the baitDesc1
	 */
	public String getBaitDesc1() {
		return baitDesc1;
	}
	/**
	 * @param baitDesc1 the baitDesc1 to set
	 */
	public void setBaitDesc1(String baitDesc1) {
		this.baitDesc1 = baitDesc1;
	}
	/**
	 * @return the baitDesc10
	 */
	public String getBaitDesc10() {
		return baitDesc10;
	}
	/**
	 * @param baitDesc10 the baitDesc10 to set
	 */
	public void setBaitDesc10(String baitDesc10) {
		this.baitDesc10 = baitDesc10;
	}
	/**
	 * @return the baitDesc2
	 */
	public String getBaitDesc2() {
		return baitDesc2;
	}
	/**
	 * @param baitDesc2 the baitDesc2 to set
	 */
	public void setBaitDesc2(String baitDesc2) {
		this.baitDesc2 = baitDesc2;
	}
	/**
	 * @return the baitDesc3
	 */
	public String getBaitDesc3() {
		return baitDesc3;
	}
	/**
	 * @param baitDesc3 the baitDesc3 to set
	 */
	public void setBaitDesc3(String baitDesc3) {
		this.baitDesc3 = baitDesc3;
	}
	/**
	 * @return the baitDesc4
	 */
	public String getBaitDesc4() {
		return baitDesc4;
	}
	/**
	 * @param baitDesc4 the baitDesc4 to set
	 */
	public void setBaitDesc4(String baitDesc4) {
		this.baitDesc4 = baitDesc4;
	}
	/**
	 * @return the baitDesc5
	 */
	public String getBaitDesc5() {
		return baitDesc5;
	}
	/**
	 * @param baitDesc5 the baitDesc5 to set
	 */
	public void setBaitDesc5(String baitDesc5) {
		this.baitDesc5 = baitDesc5;
	}
	/**
	 * @return the baitDesc6
	 */
	public String getBaitDesc6() {
		return baitDesc6;
	}
	/**
	 * @param baitDesc6 the baitDesc6 to set
	 */
	public void setBaitDesc6(String baitDesc6) {
		this.baitDesc6 = baitDesc6;
	}
	/**
	 * @return the baitDesc7
	 */
	public String getBaitDesc7() {
		return baitDesc7;
	}
	/**
	 * @param baitDesc7 the baitDesc7 to set
	 */
	public void setBaitDesc7(String baitDesc7) {
		this.baitDesc7 = baitDesc7;
	}
	/**
	 * @return the baitDesc8
	 */
	public String getBaitDesc8() {
		return baitDesc8;
	}
	/**
	 * @param baitDesc8 the baitDesc8 to set
	 */
	public void setBaitDesc8(String baitDesc8) {
		this.baitDesc8 = baitDesc8;
	}
	/**
	 * @return the baitDesc9
	 */
	public String getBaitDesc9() {
		return baitDesc9;
	}
	/**
	 * @param baitDesc9 the baitDesc9 to set
	 */
	public void setBaitDesc9(String baitDesc9) {
		this.baitDesc9 = baitDesc9;
	}
	/**
	 * @return the baitSpecies1
	 */
	public int getBaitSpecies1() {
		return baitSpecies1;
	}
	/**
	 * @param baitSpecies1 the baitSpecies1 to set
	 */
	public void setBaitSpecies1(int baitSpecies1) {
		this.baitSpecies1 = baitSpecies1;
	}
	/**
	 * @return the baitSpecies10
	 */
	public int getBaitSpecies10() {
		return baitSpecies10;
	}
	/**
	 * @param baitSpecies10 the baitSpecies10 to set
	 */
	public void setBaitSpecies10(int baitSpecies10) {
		this.baitSpecies10 = baitSpecies10;
	}
	/**
	 * @return the baitSpecies2
	 */
	public int getBaitSpecies2() {
		return baitSpecies2;
	}
	/**
	 * @param baitSpecies2 the baitSpecies2 to set
	 */
	public void setBaitSpecies2(int baitSpecies2) {
		this.baitSpecies2 = baitSpecies2;
	}
	/**
	 * @return the baitSpecies3
	 */
	public int getBaitSpecies3() {
		return baitSpecies3;
	}
	/**
	 * @param baitSpecies3 the baitSpecies3 to set
	 */
	public void setBaitSpecies3(int baitSpecies3) {
		this.baitSpecies3 = baitSpecies3;
	}
	/**
	 * @return the baitSpecies4
	 */
	public int getBaitSpecies4() {
		return baitSpecies4;
	}
	/**
	 * @param baitSpecies4 the baitSpecies4 to set
	 */
	public void setBaitSpecies4(int baitSpecies4) {
		this.baitSpecies4 = baitSpecies4;
	}
	/**
	 * @return the baitSpecies5
	 */
	public int getBaitSpecies5() {
		return baitSpecies5;
	}
	/**
	 * @param baitSpecies5 the baitSpecies5 to set
	 */
	public void setBaitSpecies5(int baitSpecies5) {
		this.baitSpecies5 = baitSpecies5;
	}
	/**
	 * @return the baitSpecies6
	 */
	public int getBaitSpecies6() {
		return baitSpecies6;
	}
	/**
	 * @param baitSpecies6 the baitSpecies6 to set
	 */
	public void setBaitSpecies6(int baitSpecies6) {
		this.baitSpecies6 = baitSpecies6;
	}
	/**
	 * @return the baitSpecies7
	 */
	public int getBaitSpecies7() {
		return baitSpecies7;
	}
	/**
	 * @param baitSpecies7 the baitSpecies7 to set
	 */
	public void setBaitSpecies7(int baitSpecies7) {
		this.baitSpecies7 = baitSpecies7;
	}
	/**
	 * @return the baitSpecies8
	 */
	public int getBaitSpecies8() {
		return baitSpecies8;
	}
	/**
	 * @param baitSpecies8 the baitSpecies8 to set
	 */
	public void setBaitSpecies8(int baitSpecies8) {
		this.baitSpecies8 = baitSpecies8;
	}
	/**
	 * @return the baitSpecies9
	 */
	public int getBaitSpecies9() {
		return baitSpecies9;
	}
	/**
	 * @param baitSpecies9 the baitSpecies9 to set
	 */
	public void setBaitSpecies9(int baitSpecies9) {
		this.baitSpecies9 = baitSpecies9;
	}
	/**
	 * @return the comments1
	 */
	public String getComments1() {
		return comments1;
	}
	/**
	 * @param comments1 the comments1 to set
	 */
	public void setComments1(String comments1) {
		this.comments1 = comments1;
	}
	/**
	 * @return the comments10
	 */
	public String getComments10() {
		return comments10;
	}
	/**
	 * @param comments10 the comments10 to set
	 */
	public void setComments10(String comments10) {
		this.comments10 = comments10;
	}
	/**
	 * @return the comments2
	 */
	public String getComments2() {
		return comments2;
	}
	/**
	 * @param comments2 the comments2 to set
	 */
	public void setComments2(String comments2) {
		this.comments2 = comments2;
	}
	/**
	 * @return the comments3
	 */
	public String getComments3() {
		return comments3;
	}
	/**
	 * @param comments3 the comments3 to set
	 */
	public void setComments3(String comments3) {
		this.comments3 = comments3;
	}
	/**
	 * @return the comments4
	 */
	public String getComments4() {
		return comments4;
	}
	/**
	 * @param comments4 the comments4 to set
	 */
	public void setComments4(String comments4) {
		this.comments4 = comments4;
	}
	/**
	 * @return the comments5
	 */
	public String getComments5() {
		return comments5;
	}
	/**
	 * @param comments5 the comments5 to set
	 */
	public void setComments5(String comments5) {
		this.comments5 = comments5;
	}
	/**
	 * @return the comments6
	 */
	public String getComments6() {
		return comments6;
	}
	/**
	 * @param comments6 the comments6 to set
	 */
	public void setComments6(String comments6) {
		this.comments6 = comments6;
	}
	/**
	 * @return the comments7
	 */
	public String getComments7() {
		return comments7;
	}
	/**
	 * @param comments7 the comments7 to set
	 */
	public void setComments7(String comments7) {
		this.comments7 = comments7;
	}
	/**
	 * @return the comments8
	 */
	public String getComments8() {
		return comments8;
	}
	/**
	 * @param comments8 the comments8 to set
	 */
	public void setComments8(String comments8) {
		this.comments8 = comments8;
	}
	/**
	 * @return the comments9
	 */
	public String getComments9() {
		return comments9;
	}
	/**
	 * @param comments9 the comments9 to set
	 */
	public void setComments9(String comments9) {
		this.comments9 = comments9;
	}
	/**
	 * @return the day1
	 */
	public String getDay1() {
		return day1;
	}
	/**
	 * @param day1 the day1 to set
	 */
	public void setDay1(String day1) {
		this.day1 = day1;
	}
	/**
	 * @return the day10
	 */
	public String getDay10() {
		return day10;
	}
	/**
	 * @param day10 the day10 to set
	 */
	public void setDay10(String day10) {
		this.day10 = day10;
	}
	/**
	 * @return the day2
	 */
	public String getDay2() {
		return day2;
	}
	/**
	 * @param day2 the day2 to set
	 */
	public void setDay2(String day2) {
		this.day2 = day2;
	}
	/**
	 * @return the day3
	 */
	public String getDay3() {
		return day3;
	}
	/**
	 * @param day3 the day3 to set
	 */
	public void setDay3(String day3) {
		this.day3 = day3;
	}
	/**
	 * @return the day4
	 */
	public String getDay4() {
		return day4;
	}
	/**
	 * @param day4 the day4 to set
	 */
	public void setDay4(String day4) {
		this.day4 = day4;
	}
	/**
	 * @return the day5
	 */
	public String getDay5() {
		return day5;
	}
	/**
	 * @param day5 the day5 to set
	 */
	public void setDay5(String day5) {
		this.day5 = day5;
	}
	/**
	 * @return the day6
	 */
	public String getDay6() {
		return day6;
	}
	/**
	 * @param day6 the day6 to set
	 */
	public void setDay6(String day6) {
		this.day6 = day6;
	}
	/**
	 * @return the day7
	 */
	public String getDay7() {
		return day7;
	}
	/**
	 * @param day7 the day7 to set
	 */
	public void setDay7(String day7) {
		this.day7 = day7;
	}
	/**
	 * @return the day8
	 */
	public String getDay8() {
		return day8;
	}
	/**
	 * @param day8 the day8 to set
	 */
	public void setDay8(String day8) {
		this.day8 = day8;
	}
	/**
	 * @return the day9
	 */
	public String getDay9() {
		return day9;
	}
	/**
	 * @param day9 the day9 to set
	 */
	public void setDay9(String day9) {
		this.day9 = day9;
	}
	/**
	 * @return the directoryName1
	 */
	public String getDirectoryName1() {
		return directoryName1;
	}
	/**
	 * @param directoryName1 the directoryName1 to set
	 */
	public void setDirectoryName1(String directoryName1) {
		this.directoryName1 = directoryName1;
	}
	/**
	 * @return the directoryName10
	 */
	public String getDirectoryName10() {
		return directoryName10;
	}
	/**
	 * @param directoryName10 the directoryName10 to set
	 */
	public void setDirectoryName10(String directoryName10) {
		this.directoryName10 = directoryName10;
	}
	/**
	 * @return the directoryName2
	 */
	public String getDirectoryName2() {
		return directoryName2;
	}
	/**
	 * @param directoryName2 the directoryName2 to set
	 */
	public void setDirectoryName2(String directoryName2) {
		this.directoryName2 = directoryName2;
	}
	/**
	 * @return the directoryName3
	 */
	public String getDirectoryName3() {
		return directoryName3;
	}
	/**
	 * @param directoryName3 the directoryName3 to set
	 */
	public void setDirectoryName3(String directoryName3) {
		this.directoryName3 = directoryName3;
	}
	/**
	 * @return the directoryName4
	 */
	public String getDirectoryName4() {
		return directoryName4;
	}
	/**
	 * @param directoryName4 the directoryName4 to set
	 */
	public void setDirectoryName4(String directoryName4) {
		this.directoryName4 = directoryName4;
	}
	/**
	 * @return the directoryName5
	 */
	public String getDirectoryName5() {
		return directoryName5;
	}
	/**
	 * @param directoryName5 the directoryName5 to set
	 */
	public void setDirectoryName5(String directoryName5) {
		this.directoryName5 = directoryName5;
	}
	/**
	 * @return the directoryName6
	 */
	public String getDirectoryName6() {
		return directoryName6;
	}
	/**
	 * @param directoryName6 the directoryName6 to set
	 */
	public void setDirectoryName6(String directoryName6) {
		this.directoryName6 = directoryName6;
	}
	/**
	 * @return the directoryName7
	 */
	public String getDirectoryName7() {
		return directoryName7;
	}
	/**
	 * @param directoryName7 the directoryName7 to set
	 */
	public void setDirectoryName7(String directoryName7) {
		this.directoryName7 = directoryName7;
	}
	/**
	 * @return the directoryName8
	 */
	public String getDirectoryName8() {
		return directoryName8;
	}
	/**
	 * @param directoryName8 the directoryName8 to set
	 */
	public void setDirectoryName8(String directoryName8) {
		this.directoryName8 = directoryName8;
	}
	/**
	 * @return the directoryName9
	 */
	public String getDirectoryName9() {
		return directoryName9;
	}
	/**
	 * @param directoryName9 the directoryName9 to set
	 */
	public void setDirectoryName9(String directoryName9) {
		this.directoryName9 = directoryName9;
	}
	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * @return the month1
	 */
	public String getMonth1() {
		return month1;
	}
	/**
	 * @param month1 the month1 to set
	 */
	public void setMonth1(String month1) {
		this.month1 = month1;
	}
	/**
	 * @return the month10
	 */
	public String getMonth10() {
		return month10;
	}
	/**
	 * @param month10 the month10 to set
	 */
	public void setMonth10(String month10) {
		this.month10 = month10;
	}
	/**
	 * @return the month2
	 */
	public String getMonth2() {
		return month2;
	}
	/**
	 * @param month2 the month2 to set
	 */
	public void setMonth2(String month2) {
		this.month2 = month2;
	}
	/**
	 * @return the month3
	 */
	public String getMonth3() {
		return month3;
	}
	/**
	 * @param month3 the month3 to set
	 */
	public void setMonth3(String month3) {
		this.month3 = month3;
	}
	/**
	 * @return the month4
	 */
	public String getMonth4() {
		return month4;
	}
	/**
	 * @param month4 the month4 to set
	 */
	public void setMonth4(String month4) {
		this.month4 = month4;
	}
	/**
	 * @return the month5
	 */
	public String getMonth5() {
		return month5;
	}
	/**
	 * @param month5 the month5 to set
	 */
	public void setMonth5(String month5) {
		this.month5 = month5;
	}
	/**
	 * @return the month6
	 */
	public String getMonth6() {
		return month6;
	}
	/**
	 * @param month6 the month6 to set
	 */
	public void setMonth6(String month6) {
		this.month6 = month6;
	}
	/**
	 * @return the month7
	 */
	public String getMonth7() {
		return month7;
	}
	/**
	 * @param month7 the month7 to set
	 */
	public void setMonth7(String month7) {
		this.month7 = month7;
	}
	/**
	 * @return the month8
	 */
	public String getMonth8() {
		return month8;
	}
	/**
	 * @param month8 the month8 to set
	 */
	public void setMonth8(String month8) {
		this.month8 = month8;
	}
	/**
	 * @return the month9
	 */
	public String getMonth9() {
		return month9;
	}
	/**
	 * @param month9 the month9 to set
	 */
	public void setMonth9(String month9) {
		this.month9 = month9;
	}
	/**
	 * @return the otherSpecies1
	 */
	public int getOtherSpecies1() {
		return otherSpecies1;
	}
	/**
	 * @param otherSpecies1 the otherSpecies1 to set
	 */
	public void setOtherSpecies1(int otherSpecies1) {
		this.otherSpecies1 = otherSpecies1;
	}
	/**
	 * @return the otherSpecies10
	 */
	public int getOtherSpecies10() {
		return otherSpecies10;
	}
	/**
	 * @param otherSpecies10 the otherSpecies10 to set
	 */
	public void setOtherSpecies10(int otherSpecies10) {
		this.otherSpecies10 = otherSpecies10;
	}
	/**
	 * @return the otherSpecies2
	 */
	public int getOtherSpecies2() {
		return otherSpecies2;
	}
	/**
	 * @param otherSpecies2 the otherSpecies2 to set
	 */
	public void setOtherSpecies2(int otherSpecies2) {
		this.otherSpecies2 = otherSpecies2;
	}
	/**
	 * @return the otherSpecies3
	 */
	public int getOtherSpecies3() {
		return otherSpecies3;
	}
	/**
	 * @param otherSpecies3 the otherSpecies3 to set
	 */
	public void setOtherSpecies3(int otherSpecies3) {
		this.otherSpecies3 = otherSpecies3;
	}
	/**
	 * @return the otherSpecies4
	 */
	public int getOtherSpecies4() {
		return otherSpecies4;
	}
	/**
	 * @param otherSpecies4 the otherSpecies4 to set
	 */
	public void setOtherSpecies4(int otherSpecies4) {
		this.otherSpecies4 = otherSpecies4;
	}
	/**
	 * @return the otherSpecies5
	 */
	public int getOtherSpecies5() {
		return otherSpecies5;
	}
	/**
	 * @param otherSpecies5 the otherSpecies5 to set
	 */
	public void setOtherSpecies5(int otherSpecies5) {
		this.otherSpecies5 = otherSpecies5;
	}
	/**
	 * @return the otherSpecies6
	 */
	public int getOtherSpecies6() {
		return otherSpecies6;
	}
	/**
	 * @param otherSpecies6 the otherSpecies6 to set
	 */
	public void setOtherSpecies6(int otherSpecies6) {
		this.otherSpecies6 = otherSpecies6;
	}
	/**
	 * @return the otherSpecies7
	 */
	public int getOtherSpecies7() {
		return otherSpecies7;
	}
	/**
	 * @param otherSpecies7 the otherSpecies7 to set
	 */
	public void setOtherSpecies7(int otherSpecies7) {
		this.otherSpecies7 = otherSpecies7;
	}
	/**
	 * @return the otherSpecies8
	 */
	public int getOtherSpecies8() {
		return otherSpecies8;
	}
	/**
	 * @param otherSpecies8 the otherSpecies8 to set
	 */
	public void setOtherSpecies8(int otherSpecies8) {
		this.otherSpecies8 = otherSpecies8;
	}
	/**
	 * @return the otherSpecies9
	 */
	public int getOtherSpecies9() {
		return otherSpecies9;
	}
	/**
	 * @param otherSpecies9 the otherSpecies9 to set
	 */
	public void setOtherSpecies9(int otherSpecies9) {
		this.otherSpecies9 = otherSpecies9;
	}
	/**
	 * @return the projectID
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID the projectID to set
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return the runDate1
	 */
	public Date getRunDate1() {
		return runDate1;
	}
	/**
	 * @param runDate1 the runDate1 to set
	 */
	public void setRunDate1(Date runDate1) {
		this.runDate1 = runDate1;
	}
	/**
	 * @return the runDate10
	 */
	public Date getRunDate10() {
		return runDate10;
	}
	/**
	 * @param runDate10 the runDate10 to set
	 */
	public void setRunDate10(Date runDate10) {
		this.runDate10 = runDate10;
	}
	/**
	 * @return the runDate2
	 */
	public Date getRunDate2() {
		return runDate2;
	}
	/**
	 * @param runDate2 the runDate2 to set
	 */
	public void setRunDate2(Date runDate2) {
		this.runDate2 = runDate2;
	}
	/**
	 * @return the runDate3
	 */
	public Date getRunDate3() {
		return runDate3;
	}
	/**
	 * @param runDate3 the runDate3 to set
	 */
	public void setRunDate3(Date runDate3) {
		this.runDate3 = runDate3;
	}
	/**
	 * @return the runDate4
	 */
	public Date getRunDate4() {
		return runDate4;
	}
	/**
	 * @param runDate4 the runDate4 to set
	 */
	public void setRunDate4(Date runDate4) {
		this.runDate4 = runDate4;
	}
	/**
	 * @return the runDate5
	 */
	public Date getRunDate5() {
		return runDate5;
	}
	/**
	 * @param runDate5 the runDate5 to set
	 */
	public void setRunDate5(Date runDate5) {
		this.runDate5 = runDate5;
	}
	/**
	 * @return the runDate6
	 */
	public Date getRunDate6() {
		return runDate6;
	}
	/**
	 * @param runDate6 the runDate6 to set
	 */
	public void setRunDate6(Date runDate6) {
		this.runDate6 = runDate6;
	}
	/**
	 * @return the runDate7
	 */
	public Date getRunDate7() {
		return runDate7;
	}
	/**
	 * @param runDate7 the runDate7 to set
	 */
	public void setRunDate7(Date runDate7) {
		this.runDate7 = runDate7;
	}
	/**
	 * @return the runDate8
	 */
	public Date getRunDate8() {
		return runDate8;
	}
	/**
	 * @param runDate8 the runDate8 to set
	 */
	public void setRunDate8(Date runDate8) {
		this.runDate8 = runDate8;
	}
	/**
	 * @return the runDate9
	 */
	public Date getRunDate9() {
		return runDate9;
	}
	/**
	 * @param runDate9 the runDate9 to set
	 */
	public void setRunDate9(Date runDate9) {
		this.runDate9 = runDate9;
	}
	/**
	 * @return the targetSpecies1
	 */
	public int getTargetSpecies1() {
		return targetSpecies1;
	}
	/**
	 * @param targetSpecies1 the targetSpecies1 to set
	 */
	public void setTargetSpecies1(int targetSpecies1) {
		this.targetSpecies1 = targetSpecies1;
	}
	/**
	 * @return the targetSpecies10
	 */
	public int getTargetSpecies10() {
		return targetSpecies10;
	}
	/**
	 * @param targetSpecies10 the targetSpecies10 to set
	 */
	public void setTargetSpecies10(int targetSpecies10) {
		this.targetSpecies10 = targetSpecies10;
	}
	/**
	 * @return the targetSpecies2
	 */
	public int getTargetSpecies2() {
		return targetSpecies2;
	}
	/**
	 * @param targetSpecies2 the targetSpecies2 to set
	 */
	public void setTargetSpecies2(int targetSpecies2) {
		this.targetSpecies2 = targetSpecies2;
	}
	/**
	 * @return the targetSpecies3
	 */
	public int getTargetSpecies3() {
		return targetSpecies3;
	}
	/**
	 * @param targetSpecies3 the targetSpecies3 to set
	 */
	public void setTargetSpecies3(int targetSpecies3) {
		this.targetSpecies3 = targetSpecies3;
	}
	/**
	 * @return the targetSpecies4
	 */
	public int getTargetSpecies4() {
		return targetSpecies4;
	}
	/**
	 * @param targetSpecies4 the targetSpecies4 to set
	 */
	public void setTargetSpecies4(int targetSpecies4) {
		this.targetSpecies4 = targetSpecies4;
	}
	/**
	 * @return the targetSpecies5
	 */
	public int getTargetSpecies5() {
		return targetSpecies5;
	}
	/**
	 * @param targetSpecies5 the targetSpecies5 to set
	 */
	public void setTargetSpecies5(int targetSpecies5) {
		this.targetSpecies5 = targetSpecies5;
	}
	/**
	 * @return the targetSpecies6
	 */
	public int getTargetSpecies6() {
		return targetSpecies6;
	}
	/**
	 * @param targetSpecies6 the targetSpecies6 to set
	 */
	public void setTargetSpecies6(int targetSpecies6) {
		this.targetSpecies6 = targetSpecies6;
	}
	/**
	 * @return the targetSpecies7
	 */
	public int getTargetSpecies7() {
		return targetSpecies7;
	}
	/**
	 * @param targetSpecies7 the targetSpecies7 to set
	 */
	public void setTargetSpecies7(int targetSpecies7) {
		this.targetSpecies7 = targetSpecies7;
	}
	/**
	 * @return the targetSpecies8
	 */
	public int getTargetSpecies8() {
		return targetSpecies8;
	}
	/**
	 * @param targetSpecies8 the targetSpecies8 to set
	 */
	public void setTargetSpecies8(int targetSpecies8) {
		this.targetSpecies8 = targetSpecies8;
	}
	/**
	 * @return the targetSpecies9
	 */
	public int getTargetSpecies9() {
		return targetSpecies9;
	}
	/**
	 * @param targetSpecies9 the targetSpecies9 to set
	 */
	public void setTargetSpecies9(int targetSpecies9) {
		this.targetSpecies9 = targetSpecies9;
	}
	/**
	 * @return the year1
	 */
	public String getYear1() {
		return year1;
	}
	/**
	 * @param year1 the year1 to set
	 */
	public void setYear1(String year1) {
		this.year1 = year1;
	}
	/**
	 * @return the year10
	 */
	public String getYear10() {
		return year10;
	}
	/**
	 * @param year10 the year10 to set
	 */
	public void setYear10(String year10) {
		this.year10 = year10;
	}
	/**
	 * @return the year2
	 */
	public String getYear2() {
		return year2;
	}
	/**
	 * @param year2 the year2 to set
	 */
	public void setYear2(String year2) {
		this.year2 = year2;
	}
	/**
	 * @return the year3
	 */
	public String getYear3() {
		return year3;
	}
	/**
	 * @param year3 the year3 to set
	 */
	public void setYear3(String year3) {
		this.year3 = year3;
	}
	/**
	 * @return the year4
	 */
	public String getYear4() {
		return year4;
	}
	/**
	 * @param year4 the year4 to set
	 */
	public void setYear4(String year4) {
		this.year4 = year4;
	}
	/**
	 * @return the year5
	 */
	public String getYear5() {
		return year5;
	}
	/**
	 * @param year5 the year5 to set
	 */
	public void setYear5(String year5) {
		this.year5 = year5;
	}
	/**
	 * @return the year6
	 */
	public String getYear6() {
		return year6;
	}
	/**
	 * @param year6 the year6 to set
	 */
	public void setYear6(String year6) {
		this.year6 = year6;
	}
	/**
	 * @return the year7
	 */
	public String getYear7() {
		return year7;
	}
	/**
	 * @param year7 the year7 to set
	 */
	public void setYear7(String year7) {
		this.year7 = year7;
	}
	/**
	 * @return the year8
	 */
	public String getYear8() {
		return year8;
	}
	/**
	 * @param year8 the year8 to set
	 */
	public void setYear8(String year8) {
		this.year8 = year8;
	}
	/**
	 * @return the year9
	 */
	public String getYear9() {
		return year9;
	}
	/**
	 * @param year9 the year9 to set
	 */
	public void setYear9(String year9) {
		this.year9 = year9;
	}


}