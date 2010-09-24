/**
 * MsJobResourceClient.java
 * @author Vagisha Sharma
 * Sep 9, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;



/**
 * 
 */
public class MsJobResourceClient {

	private static MsJobResourceClient instance = new MsJobResourceClient();
	
	private MsJobResourceClient() {}
	
	private String BASE_URI = "http://localhost:8080/msdapl_queue/services/msjob";
	
	public static MsJobResourceClient getInstance() {
		return instance;
	}
	
	/*
	 * Equivalent curl commands -- 
	 * TEXT OUTPUT: curl -i -X GET -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 * XML OUTPUT : curl -i -X GET -H "Accept:application/xml" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 * JSON OUTPUT: curl -i -X GET -H "Accept:text/json" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 */
	public void getJob(Integer jobId) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path(String.valueOf(jobId)).accept("application/json").get(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			MsJob job = response.getEntity(MsJob.class);
			System.out.println(job);
		}
		else {
			System.err.println(response.getEntity(String.class));
		}
		
	}
	
	/*
	 * Equivalent curl command -- 
	 * curl -i -X GET -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/status/<jobId>
	 */
	public void getJobStatus(Integer jobId) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("status/"+String.valueOf(jobId)).accept("text/plain").get(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobStatus = response.getEntity(String.class);
			System.out.println(jobStatus);
		}
		else {
			System.err.println(response.getEntity(String.class));
		}
	}
	
	/*
	 * Equivalent curl commands -- 
	 * JSON INPUT: curl -i -X POST -H "Accept:text/plain" -H 'Content-Type: application/json' -d '{"userName":"vsharma", "projectId":"24", "dataDirectory":"/test/data", "pipeline":"MACOSS", "date":"2010-03-29T00:00:00-07:00"}' "http://localhost:8080/msdapl_queue/services/msjob/add"
	 */
	public int addJob(MsJob job) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("add").type("application/xml").accept("text/plain").post(ClientResponse.class, job);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobId = response.getEntity(String.class);
			System.out.println(jobId);
			return Integer.parseInt(jobId);
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
			return 0;
		}
		
	}
	
	/*
	 * Equivalent curl commands -- 
	 * curl -i -X POST -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/add?user=vsharma&projectId=24&dir=/data/test&pipeline=MACOSS&date=09/24/10&instrument=LTQ&Id=9606&comments=some%20comments"
	 */
	public int addJobQueryParam(MsJob job) {

		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("add").
		queryParam("user", job.getUserName()).
		queryParam("projectId", String.valueOf(job.getProjectId())).
		queryParam("dir", job.getDataDirectory()).
		queryParam("pipeline", job.getPipeline()).
		queryParam("date", "09/23/2010").
		type("application/xml").accept("text/plain").post(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobId = response.getEntity(String.class);
			System.out.println(jobId);
			return Integer.parseInt(jobId);
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
			return 0;
		}

	}
	
	/*
	 * Equivalent curl command -- 
	 * curl -i -X DELETE "http://localhost:8080/msdapl_queue/services/msjob/delete/<jobId>"
	 */
	public void delete(int jobId) {

		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("delete").delete(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String resp = response.getEntity(String.class);
			System.out.println(resp);
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
		}

	}
	
	public static void main(String[] args) {
		
		MsJobResourceClient client = MsJobResourceClient.getInstance();
		
		MsJob job = new MsJob();
		job.setUserName("vsharma");
		job.setProjectId(24);
		job.setDataDirectory("/test/dir");
		job.setPipeline("TPP");
		job.setDate(new Date());
		client.addJob(job);
		
//		int jobId = 31;
//		client.getJob(jobId);
//		
//		client.getJobStatus(jobId);
	}

}
