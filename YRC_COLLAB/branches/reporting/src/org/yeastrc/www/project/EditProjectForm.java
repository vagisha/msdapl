/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class EditProjectForm extends ActionForm {


	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.getPI() == 0) {
			errors.add("PI", new ActionMessage("error.project.nopi"));
		}

		return errors;
	}
	
	/** Set the title */
	public void setTitle(String arg) { this.title = arg; }

	/** Set the abstract */
	public void setAbstract(String arg) { this.projectAbstract = arg; }
	
	/** Set the public abstract */
	public void setPublicAbstract(String arg) { this.publicAbstract = arg; }

//	/** Set the funding types */
//	public void setFundingTypes(String[] arg) {
//		if (arg != null)
//			this.fundingTypes = arg;
//	}
//
//	/** Set the federal funding types */
//	public void setFederalFundingTypes(String[] arg) {
//		if (arg != null)
//			this.federalFundingTypes = arg;
//	}

	/** Set the progress */
	public void setProgress(String arg) { this.progress = arg; }

	/** Set the keywords */
	public void setKeywords(String arg) { this.keywords = arg; }

	/** Set the comments */
	public void setComments(String arg) { this.comments = arg; }

	/** Set the publications */
	public void setPublications(String arg) { this.publications = arg; }

	/** Set the BTA */
	public void setBTA(float arg) { this.bta = arg; }

	/** Set the AXIS I */
	public void setAxisI(String arg) { this.axisi = arg; }

	/** Set the AXIS II */
	public void setAxisII(String arg) { this.axisii = arg; }

	/** Set the PI ID */
	public void setPI(int arg) { this.pi = arg; }

	/** Set Researcher B ID */
	public void setResearcherB(int arg) { this.researcherB = arg; }

	/** Set Researcher C ID */
	public void setResearcherC(int arg) { this.researcherC = arg; }

	/** Set Researcher D ID */
	public void setResearcherD(int arg) { this.researcherD = arg; }



	/** Get the title */
	public String getTitle() { return this.title; }

	/** Get the abstract */
	public String getAbstract() { return this.projectAbstract; }
	
	/** Get the public abstract */
	public String getPublicAbstract() { return this.publicAbstract; }

//	/** Get the funding types */
//	public String[] getFundingTypes() { return this.fundingTypes; }
//
//	/** Get the federal funding types */
//	public String[] getFederalFundingTypes() { return this.federalFundingTypes; }

	/** Get the progress */
	public String getProgress() { return this.progress; }

	/** Get the keywords */
	public String getKeywords() { return this.keywords; }

	/** Get the comments */
	public String getComments() { return this.comments; }

	/** Get the publications */
	public String getPublications() { return this.publications; }

	/** Get the BTA */
	public float getBTA() { return this.bta; }

	/** Get the AXIS I */
	public String getAxisI() { return this.axisi; }

	/** Get the AXIS II */
	public String getAxisII() { return this.axisii; }

	/** Get the PI ID */
	public int getPI() { return this.pi; }
	
	/** Get the Researcher B ID */
	public int getResearcherB() { return this.researcherB; }

	/** Get the Researcher C ID */
	public int getResearcherC() { return this.researcherC; }

	/** Get the Researcher D ID */
	public int getResearcherD() { return this.researcherD; }

	

//	/**
//	 * @return Returns the foundationName.
//	 */
//	public String getFoundationName() {
//		return foundationName;
//	}
//	/**
//	 * @param foundationName The foundationName to set.
//	 */
//	public void setFoundationName(String foundationName) {
//		this.foundationName = foundationName;
//	}
//	/**
//	 * @return Returns the grantAmount.
//	 */
//	public String getGrantAmount() {
//		return grantAmount;
//	}
//	/**
//	 * @param grantAmount The grantAmount to set.
//	 */
//	public void setGrantAmount(String grantAmount) {
//		this.grantAmount = grantAmount;
//	}
//	/**
//	 * @return Returns the grantNumber.
//	 */
//	public String getGrantNumber() {
//		return grantNumber;
//	}
//	/**
//	 * @param grantNumber The grantNumber to set.
//	 */
//	public void setGrantNumber(String grantNumber) {
//		this.grantNumber = grantNumber;
//	}
	// The form variables we'll be tracking
	private String title = null;
	private String projectAbstract = null;
	private String publicAbstract = null;
//	private String[] fundingTypes = new String[0];
//	private String[] federalFundingTypes = new String[0];
	private String progress = null;
	private String keywords = null;
	private String comments = null;
	private String publications = null;
	private float bta = (float)0;
	private String axisi = null;
	private String axisii = null;
	
	private int pi = 0;
	private int researcherB = 0;
	private int researcherC = 0;
	private int researcherD = 0;

//	private String foundationName = null;
//	private String grantNumber = null;
//	private String grantAmount = null;
	
	protected List<Integer> grants = new ArrayList<Integer>();

	public void setGrant(int index, Integer grantID) {
		while(index >= grants.size())
			grants.add(null);
		grants.set(index, grantID);
	}
	
	public Integer getGrant(int index) {
		while(index >= grants.size())
			grants.add(null);
		return grants.get(index);
	}
	
	public List <Integer> getGrants() {
		List<Integer> validGrants = new ArrayList<Integer>();
		for (Integer grantID: grants)
			if (grantID != null && grantID > 0)
				validGrants.add(grantID);
		return validGrants;
	}
	
	public void setGrants(List <Integer> grants) {
		this.grants = grants;
	}
	
	public int validGrantCount() {
		int i = 0;
		for (Integer grantID: grants) {
			if (grantID != null && grantID > 0)	i++;
		}
		return i;
	}
}