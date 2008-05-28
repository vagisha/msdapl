/* NewProjectUtils.java
 * Created on Jun 22, 2004
 */
package org.yeastrc.www.project;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.struts.util.MessageResources;
import org.yeastrc.project.Project;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 22, 2004
 *
 */
public class NewProjectUtils {

	/**
	 * Sends an email confirmation of the new project request to the Researcher who
	 * made the request.  If an exception is encountered in the process, the system does
	 * nothing.
	 * @param r The researcher who made the request
	 * @param p The project they've created
	 */
	public static void sendEmailConfirmation(Researcher r, Project p, MessageResources mr) {
		
		try {
			// set the SMTP host property value
			Properties properties = System.getProperties();
			properties.put("mail.smtp.host", "localhost");
			
			// create a JavaMail session
			javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
			
			// create a new MIME message
			MimeMessage message = new MimeMessage(mSession);
			
			// set the from address
			Address fromAddress = new InternetAddress("do_not_reply@yeastrc.org");
			message.setFrom(fromAddress);
			
			// set the to address
			Address[] toAddress = InternetAddress.parse(r.getEmail());
			message.setRecipients(Message.RecipientType.TO, toAddress);

			// set the subject
			message.setSubject("YRC - New " + p.getLongType() + " confirmation.");
			
			// set the message body
			String text = r.getFirstName() + " " + r.getLastName() + ",\n\n";
			text += "Your " + p.getLongType() + " request has been successfully submitted to the Yeast Resource Center.\n\n";
			text += "If you do not hear from us within the next fourteen days, please follow up with us at the following email addresses:\n\n";
			
			String[] groups = p.getGroupsArray();
			for (int i = 0; i < groups.length; i++) {
				if (p.getShortType().equals(Projects.DISSEMINATION)) {
						text += groups[i] + " Group (" + mr.getMessage("email.groups.plasmids." + groups[i]) + ")\n";
				} else {
						text += groups[i] + " Group (" + mr.getMessage("email.groups." + groups[i]) + ")\n";
				}
			}			

			if (p.getShortType().equals(Projects.DISSEMINATION)) {
				text += "\nIn all publications that include data, strains  or plasmids from the Yeast\n";
				text += "Resource Center, please acknowledge grant P41 RR11823 from the National Center\n";
				text += "for Research Resources at the National Institutes of Health to T. N. Davis.\n";
			}
			
			text += "\nThank you,\nThe Yeast Resource Center\n";

			message.setText(text);

			// send the message
			Transport.send(message);

		} catch (Exception e) { ; }
	}
}