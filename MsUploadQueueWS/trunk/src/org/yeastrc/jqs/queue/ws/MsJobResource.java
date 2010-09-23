package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.NotFoundException;


@Path("msjob")
public class MsJobResource {

	
	
	@POST
	@Path("add")
	@Produces ({"application/xml", "application/json"})
	@Consumes ({"application/xml", "application/json"})
	public Response add(MsJob job) {
		
		return submitJob(job);
	}

	@POST
	@Path("add2")
	@Produces(MediaType.APPLICATION_XML)
	public Response add(@QueryParam("user") String user,
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
		job.setSubmitterLoginName(user);
		job.setProjectId(projectId);
		job.setDataDirectory(dataDirectory);
		job.setPipeline(pipeline);
		job.setDate(date);
		job.setInstrument(instrument);
		job.setTargetSpecies(taxId);
		job.setComments(comments);
		System.out.println(job);
		
		return submitJob(job);
	}

	private Response submitJob(MsJob job) {
		
		MsJobSumitter submitter = new MsJobSumitter();
		Messenger messenger = new Messenger();
		int jobId = submitter.submit(job, messenger);
		System.out.println(job);
		Response response = new Response();
		
		if(jobId == -1) {
			response.setResult("FAIL");
			response.setErrors(messenger.getMessages());
		}
		else {
			response.setResult(String.valueOf(jobId));
			response.setErrors(messenger.getMessages());
		}
		return response;
	}
	
	@GET
	@Path("{id}")
	@Produces({"application/xml", "application/json"})
	public MsJob getJob(@PathParam("id") int jobId) {
	
		MsJob job = new MsJobSearcher().search(jobId);
		if(job != null)
			return job;
		else
			throw new NotFoundException("Job with ID: "+jobId+" was not found in the database");
	}
	
	
}