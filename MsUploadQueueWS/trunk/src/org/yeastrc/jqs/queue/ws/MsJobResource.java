package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.yeastrc.project.Project;
import org.yeastrc.www.user.NoSuchUserException;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import com.sun.jersey.api.NotFoundException;


@Path("msjob")
@Produces("text/plain")
public class MsJobResource {

	@Context
    SecurityContext security;
	
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
	@Path("project/{id}")
	@Produces({"application/xml", "application/json"})
	public MsProject getProject(@PathParam("id") int projectId) {
		
		Project project = ProjectSearcher.getInstance().search(projectId);
		if(project != null)
			return MsProject.create(project);
		else
			throw new NotFoundException("Project not found. ID: "+projectId+"\n");
	}
	
	@GET
	@Path("checkAccess")
	public String checkProjectAccess(@QueryParam("projectId") int projectId, @QueryParam("userEmail") String userEmail) {
		
		Project project = ProjectSearcher.getInstance().search(projectId);
		if(project == null)
			return "Project not found. ID: "+projectId+"\n";
		
		User user = getUserWithEmail(userEmail);
		if(user == null)
			return "User not found. Email: "+userEmail+"\n";
		
		boolean access = project.checkAccess(user.getResearcher());
		if(access)
			return "Access Allowed";
		else
			return "Access Denied";
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
		return "Queued job. ID: "+String.valueOf(submitJob(job)+"\n");
	}

	@POST
	@Path("hermie/add")
	@Produces ({"text/plain"})
	@Consumes ({"application/json"})
	/*
	 * This method is only to be used by the hermie pipeline.
	 * cURL example:
	 * curl -u hermie:<hermie_password> -X POST  -H 'Content-Type: application/json' -d '{"projectId":"24", "dataDirectory":"/test/data", "targetSpecies":"6239", "instrument":"LTQ", "comments":"upload test", "submitterName":"vsharma"}' http://localhost:8080/msdapl_queue/services/msjob/hermie/add
	 */
	public String addHermieJob(MsJob job) {
		job.setPipeline("MACCOSS");
		job.setDate(new Date());
		
		// If a submitterName is not part of the request, the submitter will be set to "hermie". 
		// We will not do project access check in this case.
		if(job.getSubmitterName() == null)
			return "Queued job. ID: "+String.valueOf(submitJob(job, false, false)+"\n");
		else
			return "Queued job. ID: "+String.valueOf(submitJob(job, true, true)+"\n");
	}
	
	@POST
	@Path("add")
	@Produces ({"text/plain"})
	public String add(
			@QueryParam("projectId") Integer projectId,
			@QueryParam("dataDirectory") String dataDirectory,
			@QueryParam("pipeline") String pipeline,
			@QueryParam("date") Date date,
			@QueryParam("instrument") String instrument,
			@QueryParam("targetSpecies") Integer taxId,
			@QueryParam("comments") String comments,
			@QueryParam("remoteServer") String remoteServer
			) {
		
		MsJob job = new MsJob();
		job.setProjectId(projectId);
		job.setDataDirectory(dataDirectory);
		job.setRemoteServer(remoteServer);
		job.setPipeline(pipeline);
		job.setDate(date);
		job.setInstrument(instrument);
		job.setTargetSpecies(taxId);
		job.setComments(comments);
		System.out.println(job);
		
		return "Queued job. ID: "+String.valueOf(submitJob(job)+"\n");
	}

	private int submitJob(MsJob job) {
		return submitJob(job, true, false);
	}
	
	private int submitJob(MsJob job, boolean checkAccess, boolean sendEmailOnSuccess) {
		
		String username = job.getSubmitterName();
		if(username == null)
			username = security.getUserPrincipal().getName();
		
		User user = getUser(username);
		
		Messenger messenger = new Messenger();
		
		MsJobSubmitter submitter = MsJobSubmitter.getInstance();
		int jobId = submitter.submit(job, user, messenger, checkAccess);
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
		// But, first, send an email
		if(sendEmailOnSuccess) {
			Emailer emailer = Emailer.getInstance();
			emailer.emailJobQueued(job, user);
		}
		return jobId;
	}
	
	

	@DELETE
	@Path("delete/{id}")
	@Produces("text/plain")
	public String delete(@PathParam("id") Integer jobId) {
		
		String username = security.getUserPrincipal().getName();
		User user = getUser(username);
		
		Messenger messenger = new Messenger();
		int status = MsJobDeleter.getInstance().delete(jobId, user, messenger);
		
		if(status == jobId)
			return "Job deleted. ID: "+jobId+"\n";
		
		else {
			if(status == -1) // job not found
				throw new NotFoundException(messenger.getMessagesString());

			else if(status == -2) // job could not be deleted (either the user does not have authority or job is not in a deletion-friendly state
				throw new BadRequestException(messenger.getMessagesString());

			else if(status == -3) // error deleting the job
				throw new ServerErrorException(messenger.getMessagesString());
			
			else 
				throw new ServerErrorException(messenger.getMessagesString());
		}
	}
	
	private User getUser(String username) {
		try {
			return  UserUtils.getUser(username);
		} catch (NoSuchUserException e) {
			throw new BadRequestException("No user with username: "+username);
		} catch (SQLException e) {
			throw new ServerErrorException("There was an error during user lookup. The error message was: "+e.getMessage());
		}
	}
	
	private User getUserWithEmail(String email) {
		try {
			return  UserUtils.getUserWithEmail(email);
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}