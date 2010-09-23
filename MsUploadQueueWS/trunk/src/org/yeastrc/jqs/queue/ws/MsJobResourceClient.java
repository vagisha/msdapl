/**
 * MsJobResourceClient.java
 * @author Vagisha Sharma
 * Sep 9, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
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
	
	public void getJob(int jobId) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		try {
			ClientResponse response = webRes.path(String.valueOf(jobId)).accept("application/xml").get(ClientResponse.class);
			Status status = response.getClientResponseStatus();
			if(status == Status.OK) {
				MsJob job = response.getEntity(MsJob.class);
				System.out.println(job);
			}
			else {
				System.out.println(response.getEntity(String.class));
			}
		}
		catch(UniformInterfaceException e) {
			ClientResponse resp = webRes.head();
			System.err.println(resp.getClientResponseStatus().getStatusCode());
		}
	}
	
	public void addJob(MsJob job) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		Response response = webRes.path("add").type("application/xml").accept("application/xml").post(Response.class, job);
		
	}
	
	public static void main(String[] args) {
		
		MsJobResourceClient client = MsJobResourceClient.getInstance();
		
		MsJob job = new MsJob();
		job.setSubmitterLoginName("vsharma");
		job.setProjectId(123);
		job.setDataDirectory("/test/dir");
		job.setPipeline("TPP");
		job.setDate(new Date());
		
		client.addJob(job);
		
		int jobId = 31;
		client.getJob(jobId);
		
	}

}
