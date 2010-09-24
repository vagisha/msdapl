package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;

import com.sun.jersey.api.NotFoundException;


@Path("msjob")
@Produces("text/plain")
public class MsJobResource {

	//@Context
    //SecurityContext security;
	
	@GET
	@Path("{id}")
	public String getJobAsText(@PathParam("id") int jobId) {
	
		MsJob job = MsJobSearcher.getInstance().search(jobId);
		if(job != null)
			return job.toString();
		else
			throw new NotFoundException("Job with ID: "+jobId+" was not found in the database\n");
	}
	
	@GET
	@Path("{id}")
	@Produces({"application/xml", "application/json"})
	public MsJob getJobAsXmlOrJson(@PathParam("id") int jobId) {
	
		MsJob job = MsJobSearcher.getInstance().search(jobId);
		if(job != null)
			return job;
		else
			throw new NotFoundException("Job with ID: "+jobId+" was not found in the database\n");
	}
	
	@GET
	@Path("status/{id}")
	@Produces("text/plain")
	public String getStatus(@PathParam("id") int jobId) {
	
		MsJob job = MsJobSearcher.getInstance().search(jobId);
		if(job != null)
			return job.getStatus();
		else
			throw new NotFoundException("Job with ID: "+jobId+" was not found in the database\n");
	}
	
	
	@POST
	@Path("add")
	@Produces ({"text/plain"})
	@Consumes ({"application/xml", "application/json"})
	public String add(MsJob job) {
		return String.valueOf(submitJob(job));
	}

	@POST
	@Path("add")
	@Produces ({"text/plain"})
	public String add(@QueryParam("user") String user,
			@QueryParam("projectId") Integer projectId,
			@QueryParam("dir") String dataDirectory,
			@QueryParam("pipeline") String pipeline,
			@QueryParam("date") Date date,
			@QueryParam("instrument") String instrument,
			@QueryParam("taxId") Integer taxId,
			@QueryParam("comments") String comments,
			@QueryParam("group") String group
			) {
		
		MsJob job = new MsJob();
		job.setUserName(user);
		job.setProjectId(projectId);
		job.setDataDirectory(dataDirectory);
		job.setPipeline(pipeline);
		job.setDate(date);
		job.setInstrument(instrument);
		job.setTargetSpecies(taxId);
		job.setComments(comments);
		System.out.println(job);
		
		return String.valueOf(submitJob(job));
	}

	private int submitJob(MsJob job) {
		
		Messenger messenger = new Messenger();
		
		MsJobSumitter submitter = new MsJobSumitter();
		int jobId = submitter.submit(job, messenger);
		if(jobId == -1) { // data provided by the user was incorrect or incomplete
			String err = messenger.getMessagesString();
			// 400 error
			throw new BadRequestException(err);
		}
		else if(jobId == -2) { // there was an error saving the job to database
			String err = messenger.getMessagesString();
			// 500 error
			throw new ServerErrorException(err);
			//WebApplicationException ex = new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("hola").type("text/plain").build());
		}
		
		// all went well, return the database ID of the newly created job.
		return jobId;
	}
	
	

	@DELETE
	@Path("delete/{id}")
	@Produces("text/plain")
	public String delete(@PathParam("id") Integer jobId) {
		
		//String username = security.getUserPrincipal().getName();
		//System.out.println(username);
		
		MSJob msJob = null;
		try {
			msJob = MSJobFactory.getInstance().getJob(jobId);
			
		} catch (Exception e) {
			throw new NotFoundException("Error getting job with ID: "+jobId+". The error message was: "+e.getMessage());
		}
		
		if(msJob.getStatus() == JobUtils.STATUS_COMPLETE) {
			// 500 error
			String err = "Job with ID: "+jobId+" is complete. It could not be deleted.";
			throw new ServerErrorException(err);
		}
		else if(msJob.getStatus() == JobUtils.STATUS_OUT_FOR_WORK) {
			// 500 error
			String err = "Job with ID: "+jobId+" is running. It could not be deleted.";
			throw new ServerErrorException(err);
		}
		
		try {
			if(JobDeleter.getInstance().deleteJob(msJob)) {
				return "Job deleted. ID: "+jobId;
			}
			else {
				// 500 error
				String err = "Job with ID: "+jobId+" is running. It could not be deleted.";
				throw new ServerErrorException(err);
			}
		} catch (Exception e) {
			// 500 error
			String err = "Job with ID: "+jobId+" could not be deleted. The error message was: "+e.getMessage();
			throw new ServerErrorException(err);
		}
	}
	
}