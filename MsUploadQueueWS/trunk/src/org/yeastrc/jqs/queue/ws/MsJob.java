package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "msJob")
@XmlAccessorType(XmlAccessType.FIELD)

public class MsJob {
	
	private Integer id;
	
	@XmlElement
	/**
	 * The submitter's login name in MSDaPl
	 */
	private String submitterName;
	
	@XmlElement(required = true)
	/**
	 * ID of the project this upload job should be assigned
	 */
	private Integer projectId;
	
	@XmlElement(required = true)
	/**
	 * Location of the data to be uploaded
	 */
	private String dataDirectory;
	
	@XmlElement(required = true)
	/**
	 * Either "TPP" or "MACCOSS"
	 */
	private String pipeline;
	
	@XmlElement(required = true)
	private Date date;
	
	
	/**
	 * NCBI taxonomy ID
	 */
	@XmlElement
	private Integer targetSpecies;
	
	/**
	 * Name of the instrument. Should match one of the instruments available in MSDaPl
	 */
	@XmlElement
	private String instrument;
	
	@XmlElement
	private String comments;
	
	@XmlElement
	private String status;
	
	@XmlElement
	private String remoteServer;
	
	
	public MsJob() {}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSubmitterName() {
		return submitterName;
	}
	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}
	
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	
	public String getDataDirectory() {
		return dataDirectory;
	}
	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
	
	public Integer getTargetSpecies() {
		return targetSpecies;
	}
	public void setTargetSpecies(Integer targetSpecies) {
		this.targetSpecies = targetSpecies;
	}

	public String getPipeline() {
		return pipeline;
	}
	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getInstrument() {
		return instrument;
	}
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemoteServer() {
		return remoteServer;
	}

	public void setRemoteServer(String remoteServer) {
		this.remoteServer = remoteServer;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("ID: "+this.getId()+"\n");
		buf.append("Submitter: "+this.getSubmitterName()+"\n");
		buf.append("ProjectID: "+this.getProjectId()+"\n");
		buf.append("Directory: "+this.getDataDirectory()+"\n");
		if(this.getRemoteServer() != null)
			buf.append("Remote Server: "+this.getRemoteServer()+"\n");
		buf.append("Pipeline: "+this.getPipeline()+"\n");
		buf.append("Date: "+this.getDate()+"\n");
		buf.append("Status: "+this.getStatus()+"\n");
		buf.append("Instrument: "+this.getInstrument()+"\n");
		buf.append("Species: "+this.getTargetSpecies()+"\n");
		buf.append("Comments: "+this.getComments()+"\n");
		return buf.toString();
	}
	
}