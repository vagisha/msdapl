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
	
	@XmlElement(required = true)
	/**
	 * The login name in MSDaPl
	 */
	private String submitterLoginName;
	
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
	private Integer targetSpecies;
	/**
	 * Name of the instrument. Should match one of the instruments availalble in MSDaPl
	 */
	private String instrument;
	private String comments;
	
	
	public MsJob() {}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSubmitterLoginName() {
		return submitterLoginName;
	}
	public void setSubmitterLoginName(String submitterLoginName) {
		this.submitterLoginName = submitterLoginName;
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
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("ID: "+this.getId()+"\n");
		buf.append("Submitter: "+this.getSubmitterLoginName()+"\n");
		buf.append("ProjectID: "+this.getProjectId()+"\n");
		buf.append("Directory: "+this.getDataDirectory()+"\n");
		buf.append("Pipeline: "+this.getPipeline()+"\n");
		buf.append("Date: "+this.getDate()+"\n");
		buf.append("Instrument: "+this.getInstrument()+"\n");
		buf.append("Species: "+this.getTargetSpecies()+"\n");
		buf.append("Comments: "+this.getComments()+"\n");
		return buf.toString();
	}
	
}