/**
 * MsJobDeleter.java
 * @author Vagisha Sharma
 * Sep 24, 2010
 */
package org.yeastrc.jqs.queue.ws;

import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class MsJobDeleter {

	private MsJobDeleter() {}
	
	private static MsJobDeleter instance = null;
	
	public static synchronized MsJobDeleter getInstance() {
		if(instance == null)
			instance = new MsJobDeleter();
		return instance;
	}
	
	public int delete(int jobId, User user, Messenger messenger) {
		
		Job msJob = null;
		try {
			msJob = MSJobFactory.getInstance().getJob(jobId);
			
		} catch (Exception e) {
			messenger.addError("Error getting job with ID: "+jobId+". The error message was: "+e.getMessage());
			return -1;
		}
		
		// Does the user have authority to delete this job? The user has to either be the job submitter
		// or an administrator
		Groups groups = Groups.getInstance();
		boolean access = false;
		if (groups.isMember(user.getResearcher().getID(), "administrators"))
			access = true;
		else if (user.getResearcher().getID() == msJob.getSubmitter()) 
			access = true;
		if(!access) {
			messenger.addError("User does not have authority to delete job with ID: "+jobId);
			return -2;
		}
		
		
		// Is the job in a delete-friendly state?
		if(msJob.getStatus() == JobUtils.STATUS_COMPLETE) {
			messenger.addError("Job with ID: "+jobId+" is complete. It could not be deleted.");
			return -2;
		}
		else if(msJob.getStatus() == JobUtils.STATUS_OUT_FOR_WORK) {
			messenger.addError("Job with ID: "+jobId+" is running. It could not be deleted.");
			return -2;
		}
		
		// delete the job
		try {
			if(JobDeleter.getInstance().deleteJob(msJob)) {
				return jobId;
			}
			else {
				messenger.addError("Job with ID: "+jobId+" is running. It could not be deleted.");
				return -3;
			}
		} catch (Exception e) {
			messenger.addError("Job with ID: "+jobId+" could not be deleted. The error message was: "+e.getMessage());
			return -3;
		}
	}
}
