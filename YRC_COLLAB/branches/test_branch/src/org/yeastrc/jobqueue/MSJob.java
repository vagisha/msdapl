/**
 * 
 */
package org.yeastrc.jobqueue;

import java.util.Date;
import org.yeastrc.project.*;
/**
 * @author Mike
 *
 */
public class MSJob extends Job {

	public MSJob() {
		super();
	}
	
	private int projectID;
	private String serverDirectory;
	private Date runDate;
	private int baitProtein;
	private String baitProteinDescription;
	private int targetSpecies;
	private String comments;
	private int runID;
	private int group;
	private Project project;
	
	/**
	 * Get the Project object for which this MS run aws submitted
	 * @return the project, null if it can't be obtained for any reason
	 */
	public Project getProject() {
		if (this.project == null) {
			
			try {
				this.project = ProjectFactory.getProject( this.projectID );
			} catch (Exception e) { ; }
		}
		
		return this.project;
	}
	
	
	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	/**
	 * @return the baitProtein
	 */
	public int getBaitProtein() {
		return baitProtein;
	}
	/**
	 * @param baitProtein the baitProtein to set
	 */
	public void setBaitProtein(int baitProtein) {
		this.baitProtein = baitProtein;
	}
	/**
	 * @return the baitProteinDescription
	 */
	public String getBaitProteinDescription() {
		return baitProteinDescription;
	}
	/**
	 * @param baitProteinDescription the baitProteinDescription to set
	 */
	public void setBaitProteinDescription(String baitProteinDescription) {
		this.baitProteinDescription = baitProteinDescription;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
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
	 * @return the runDate
	 */
	public Date getRunDate() {
		return runDate;
	}
	/**
	 * @param runDate the runDate to set
	 */
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	/**
	 * @return the runID
	 */
	public int getRunID() {
		return runID;
	}
	/**
	 * @param runID the runID to set
	 */
	public void setRunID(int runID) {
		this.runID = runID;
	}
	/**
	 * @return the serverDirectory
	 */
	public String getServerDirectory() {
		return serverDirectory;
	}
	/**
	 * @param serverDirectory the serverDirectory to set
	 */
	public void setServerDirectory(String serverDirectory) {
		this.serverDirectory = serverDirectory;
	}
	/**
	 * @return the targetSpecies
	 */
	public int getTargetSpecies() {
		return targetSpecies;
	}
	/**
	 * @param targetSpecies the targetSpecies to set
	 */
	public void setTargetSpecies(int targetSpecies) {
		this.targetSpecies = targetSpecies;
	}

}
