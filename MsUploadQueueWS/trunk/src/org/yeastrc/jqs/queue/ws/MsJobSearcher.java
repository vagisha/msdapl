/**
 * MsJobSearcher.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class MsJobSearcher {

	private MsJobSearcher() {}
	
	private static MsJobSearcher instance = null;
	
	public static synchronized MsJobSearcher getInstance() {
		if(instance == null)
			instance = new MsJobSearcher();
		
		return instance;
	}
	
	public MsJob search(int jobId) {
		
		if (jobId == 0)
			return null;
		
		try {
			Job job = MSJobFactory.getInstance().getJob(jobId);
			
			if(job instanceof MSJob) {
				MSJob msJob = (MSJob)job;
				
				MsJob myJob = new MsJob();
				myJob.setId(msJob.getId());
				myJob.setProjectId(msJob.getProjectID());
				myJob.setPipeline(msJob.getPipeline().name());
				String dataDir = msJob.getServerDirectory();
				int idx = dataDir.indexOf(":");
				String server = null;
				if(idx != -1)
					server = dataDir.substring(0, idx);
				else
					server = dataDir;
				String dir = dataDir.substring(idx+1, dataDir.length());
				if(!server.equals("local"))
					myJob.setRemoteServer(server);
				myJob.setDataDirectory(dir);
				
				User submitter = new User();
				submitter.load(msJob.getSubmitter());
				myJob.setSubmitterName(submitter.getUsername());
				
				myJob.setDate(msJob.getRunDate());
				myJob.setTargetSpecies(msJob.getTargetSpecies());
				myJob.setComments(msJob.getComments());
				String instrument = InstrumentLookup.getInstance().nameForId(msJob.getInstrumentId());
				myJob.setInstrument(instrument);
				
				myJob.setStatus(msJob.getStatusDescription());
				myJob.setLog(msJob.getLog());
				return myJob;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
