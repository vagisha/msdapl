/**
 * Response.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */
@XmlRootElement(name="response")
public class Response {

	private String result;
	
	private List<String> errors;

	public Response() {
		errors = new ArrayList<String>();
	}
	
	@XmlElement(required=true)
	/**
	 * Either the ID of the submitted job if job was submitted successfully
	 * or FAIL
	 */
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@XmlElementWrapper(name="errors")
	@XmlElement(required=true, name="error")
	/**
	 * Contains the error messages, if any
	 */
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		if(errors == null)
			return;
		this.errors = errors;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Result: "+result+"\n");
		if(errors != null) {
			for(String err: errors)
				buf.append("ERROR: "+err+"\n");
		}
		return buf.toString();
	}
	
}
