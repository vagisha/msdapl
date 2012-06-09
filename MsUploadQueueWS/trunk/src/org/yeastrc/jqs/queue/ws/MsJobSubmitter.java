/**
 * MsJobSumitter.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.upload.MSUploadJobSaver;
import org.yeastrc.www.upload.Pipeline;
import org.yeastrc.www.upload.UserGroupIdGetter;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class MsJobSubmitter {

	private MsJobSubmitter() {}
	
	private static MsJobSubmitter instance = null;
	
	public static synchronized MsJobSubmitter getInstance() {
		if(instance == null)
			instance = new MsJobSubmitter();
		
		return instance;
	}
	
	public int submit(MsJob job, User submitter, Messenger messenger, boolean checkAccess) {
		
		if(job == null) {
			if(messenger != null) 
				messenger.addError("null job found.");
			return -1;
		}
		
		// check the required variables
		boolean pass = true;
		
		
		// Get the project ID
		if(job.getProjectId() == null) {
			messenger.addError("projectID cannot be null");
			pass = false;
		}
		else {
			// Make sure the project exists and the reseacher is a member or an administrator
			if(submitter != null) {
				try {
					Project project = ProjectFactory.getProject(job.getProjectId());
					
					if(checkAccess && !project.checkAccess(submitter.getResearcher())) {
						messenger.addError("Researcher ("+submitter.getUsername()+") does not have access to the project with ID: "+job.getProjectId());
						pass = false;
					}
					
				} catch (SQLException e1) {
					messenger.addError("There was an error during project lookup. The error message was: "+e1.getMessage());
					pass = false;
				} catch (InvalidIDException e1) {
					messenger.addError("No project found with ID: "+job.getProjectId());
					pass = false;
				}
			}
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
		// TODO -- hack.  Fix Pipeline
		if(job.getPipeline().equals("MACCOSS"))
			job.setPipeline(Pipeline.MACOSS.name());
		Pipeline pipeline = Pipeline.forName(job.getPipeline());
		if(pipeline == null) {
			messenger.addError("Not a valid pipeline: "+job.getPipeline());
			pass = false;
		}
		// TODO -- hack. Set it back
		if(job.getPipeline().equals(Pipeline.MACOSS.name()))
			job.setPipeline("MACCOSS");
		
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
		if(job.getRemoteServer() == null)
			jobSaver.setServerDirectory("local:"+job.getDataDirectory());
		else
			jobSaver.setServerDirectory(job.getRemoteServer()+":"+job.getDataDirectory());
		
		try {
			int jobGroupId = UserGroupIdGetter.getOneGroupId(submitter);
			jobSaver.setGroupID(jobGroupId);
		}
		catch(SQLException e) {
			// The groupID column is no longer being used. 
			// We will set it to 1
			// TODO: Get rid of the groupID column from the table
			jobSaver.setGroupID(1);
		}
		
		jobSaver.setPipeline(pipeline);
		jobSaver.setRunDate(job.getDate());
		
		Integer targetSpecies = job.getTargetSpecies();
		if(targetSpecies != null) {
			jobSaver.setTargetSpecies(job.getTargetSpecies());
		}
		
		if(job.getInstrument() != null) {
			InstrumentLookup lookup = InstrumentLookup.getInstance();
			int instrumentId;
			try {
				instrumentId = lookup.forName(job.getInstrument());
				if(instrumentId > 0) {
					jobSaver.setInstrumentId(instrumentId);
					job.setInstrument(lookup.nameForId(instrumentId));
				}
				else {
					job.setInstrument(null);
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
			int jobId = jobSaver.savetoDatabase();
			job.setId(jobId);
			return jobId;
		} catch (Exception e) {
			messenger.addError("There was error submitting the job.  The error message was: "+e.getMessage());
			return -2;
		}
	}
	
	
}
