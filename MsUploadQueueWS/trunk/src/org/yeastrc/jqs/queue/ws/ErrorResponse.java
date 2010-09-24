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
@XmlRootElement(name="error_response")
public class ErrorResponse {

	private int statusCode;
	
	private List<String> messages;

	public ErrorResponse() {
		messages = new ArrayList<String>();
	}
	
	@XmlElement(required=true)
	/**
	 * Either the ID of the submitted job if job was submitted successfully
	 * or FAIL
	 */
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElementWrapper(name="messages")
	@XmlElement(required=true, name="message")
	/**
	 * Contains the error messages, if any
	 */
	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		if(messages == null)
			return;
		this.messages = messages;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Status Code: "+statusCode+"\n");
		if(messages != null) {
			for(String err: messages)
				buf.append(err+"\n");
		}
		return buf.toString();
	}
	
}
