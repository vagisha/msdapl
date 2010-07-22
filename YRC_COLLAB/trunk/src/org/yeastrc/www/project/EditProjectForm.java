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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import org.yeastrc.grant.Grant;
import org.yeastrc.project.Researcher;

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

	/** Get the title */
	public String getTitle() { return this.title; }

	/** Get the abstract */
	public String getAbstract() { return this.projectAbstract; }
	
	/** Get the public abstract */
	public String getPublicAbstract() { return this.publicAbstract; }

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


	// The form variables we'll be tracking
	private String title = null;
	private String projectAbstract = null;
	private String publicAbstract = null;
	private String progress = null;
	private String keywords = null;
	private String comments = null;
	private String publications = null;
	private float bta = (float)0;
	private String axisi = null;
	private String axisii = null;
    private List<Researcher> researchers = new ArrayList<Researcher>();

	
	private int pi = 0;

	private List<Grant> grants = new ArrayList<Grant>();
	
	private int ID = 0;
	
	private Date submitDate = null;
	
	public Grant getGrant(int index) {
		while(index >= grants.size())
			grants.add(new Grant());
		return grants.get(index);
	}
	
	public List <Grant> getGrantList() {
		List<Grant> validGrants = new ArrayList<Grant>();
		for (Grant grant: grants)
			if (grant != null && grant.getID() > 0)
				validGrants.add(grant);
		return validGrants;
	}
	
	public void setGrantList(List <Grant> grants) {
		this.grants = grants;
	}
	
	public int validGrantCount() {
		int i = 0;
		for (Grant grant: grants) {
			if (grant != null && grant.getID() > 0)	i++;
		}
		return i;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public Date getSubmitDate() {
		return submitDate;
	}
	
	public void setSubmitDate(Date date) {
		this.submitDate = date;
	}
	
	//----------------------------------------------------------------
    // Researchers
    //----------------------------------------------------------------
	public Researcher getResearcher(int index) {
	    //System.out.println("Getting researcher id at index: "+index);
	    while(index >= researchers.size())
	        researchers.add(new Researcher());
	    return researchers.get(index);
	}
	
	public List<Researcher> getResearcherList() {
	    //System.out.println("Getting researcher list");
	    List<Researcher> rList = new ArrayList<Researcher>();
	    for(Researcher r: researchers) {
	        if(r != null && r.getID() > 0)
	            rList.add(r);
	    }
	    return rList;
	}
	
	public void setResearcherList(List<Researcher> researchers) {
	    //System.out.println("Setting researcher");
	    this.researchers = researchers;
	}
	
	private int validResearcherCount() {
        int i = 0;
        for (Researcher researcher: researchers) {
            if (researcher != null && researcher.getID() > 0) i++;
        }
        return i;
    }
}