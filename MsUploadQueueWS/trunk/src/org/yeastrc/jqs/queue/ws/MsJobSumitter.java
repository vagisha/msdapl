/**
 * MsJobSumitter.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.yeastrc.project.ProjectLiteDAO;
import org.yeastrc.www.upload.JobGroupIdGetter;
import org.yeastrc.www.upload.MSUploadJobSaver;
import org.yeastrc.www.upload.Pipeline;
import org.yeastrc.www.user.NoSuchUserException;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class MsJobSumitter {

	public int submit(MsJob job, Messenger messenger) {
		
		if(job == null) {
			if(messenger != null) 
				messenger.addError("null job found.");
			return -1;
		}
		
		User submitter = null;
		
		// check the required variables
		boolean pass = true;
		
		
		// get the user's login name in MSDaPl
		if(job.getSubmitterLoginName() == null) {
			if(messenger != null) {
				messenger.addError("submitter's login name cannot be null");
				pass = false;
			}
		}
		// find a user with this login name
		try {
			submitter = UserUtils.getUser(job.getSubmitterLoginName());
		} catch (NoSuchUserException e) {
			messenger.addError("No user found with login name: "+job.getSubmitterLoginName());
			pass = false;
		} catch (SQLException e) {
			messenger.addError("There was an error during user lookup. The error message was: "+e.getMessage());
			pass = false;
		}
		
		
		// Get the project ID
		if(job.getProjectId() == null) {
			messenger.addError("projectID cannot be null");
			pass = false;
		}
		// Make sure the project exists and the reseacher is a member
		ProjectLiteDAO projDao = ProjectLiteDAO.instance();
		try {
			if(!projDao.isResearcherProject(job.getProjectId(), submitter.getResearcher().getID())) {
				messenger.addError("Either no project with ID: "+job.getProjectId()+
						" exists in the database OR the researcher ("+job.getSubmitterLoginName()+") does not have access to the proejct.");
				pass = false;
			}
		} catch (SQLException e) {
			messenger.addError("There was an error during project lookup. The error message was: "+e.getMessage());
			pass = false;
		}
		
		
		// Get the location of the data to be uploaded.
		if(job.getDataDirectory() == null) {
			messenger.addError("data directory cannot be null");
			pass = false;
		}
		
		// Get the name of the pipeline that generated the data
		if(job.getPipeline() == null) {
			messenger.addError("pipeline cannot be null");
			pass = false;
		}
		// Has to be a valid pipeline
		Pipeline pipeline = Pipeline.forName(job.getPipeline());
		if(pipeline == null) {
			messenger.addError("Not a valid pipeline: "+job.getPipeline());
			pass = false;
		}
		
		
		// Date the data was generated
		if(job.getDate() == null) {
			messenger.addError("date cannot be null");
			pass = false;
		}
		
		
		if(!pass)
			return -1;
		
		
		// Actually submit the job to the upload queue
		// convert to an object type understood by MSUploadJobSaver
		MSUploadJobSaver jobSaver = new MSUploadJobSaver();
		jobSaver.setSubmitter(submitter.getResearcher().getID());
		jobSaver.setProjectID(job.getProjectId());
		jobSaver.setServerDirectory(job.getDataDirectory());
		int jobGroupId = JobGroupIdGetter.get(submitter);
		jobSaver.setGroupID(jobGroupId);
		jobSaver.setPipeline(pipeline);
		jobSaver.setRunDate(job.getDate());
		
		if(job.getTargetSpecies() > 0) {
			jobSaver.setTargetSpecies(job.getTargetSpecies());
		}
		
		if(job.getInstrument() != null) {
			InstrumentLookup lookup = InstrumentLookup.getInstance();
			int instrumentId;
			try {
				instrumentId = lookup.forName(job.getInstrument());
				if(instrumentId > 0)
					jobSaver.setInstrumentId(instrumentId);
				else {
					messenger.addWarning("Instrument: "+job.getInstrument()+" was not found in the database");
				}
			} catch (SQLException e) {
				messenger.addWarning("There was an error in instrument lookup. The error message was: "+e.getMessage());
			}
		}
		
		if(job.getComments() != null) {
			jobSaver.setComments(job.getComments());
		}
		
		try {
			jobSaver.savetoDatabase();
			return 1;
		} catch (Exception e) {
			messenger.addError("There was error submitting the job.  The error message was: "+e.getMessage());
			return -1;
		}
	}
	
	
}
