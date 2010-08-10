/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;

import javax.mail.*;
import javax.mail.internet.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import org.yeastrc.data.*;

/**
 * Controller class for saving a project.
 */
public class SaveNewTrainingAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		String title = null;
		String[] groups = null;
		String comments = null;
		String description = null;
		int hours = 0;
		int days = 0;
		boolean sendEmail;
		boolean isSeminars;

		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// Load our project
		Training project = new Training();

		// Set this project in the request, as a bean to be displayed on the view
		//request.setAttribute("project", project);

		// We're saving!
		title = ((EditTrainingForm)(form)).getTitle();
		groups = ((EditTrainingForm)(form)).getGroups();
		comments = ((EditTrainingForm)(form)).getComments();
		description = ((EditTrainingForm)(form)).getDescription();
		hours = ((EditTrainingForm)(form)).getHours();
		days = ((EditTrainingForm)(form)).getDays();
		sendEmail = ((EditTrainingForm)form).getSendEmail();
		isSeminars = ((EditTrainingForm)form).isSeminars();
		
		// Set blank items to null
		if (title != null && title.equals("")) title = null;
		if (comments != null && comments.equals("")) comments = null;
		if (description != null && description.equals("")) description = null;

		if (title == null && isSeminars) {
			if (((EditTrainingForm)(form)).getPI() != null)
				title = "Seminars given by " + ((EditTrainingForm)(form)).getPI().getFirstName() + " " + ((EditTrainingForm)(form)).getPI().getLastName();
			else
				title = "Seminars given";
		}
		
		// Set up the groups
		project.clearGroups();
		
		if (groups != null) {
			if (groups.length > 0) {
				for (int i = 0; i < groups.length; i++) {
					try { project.setGroup(groups[i]); }
					catch (InvalidIDException iie) {
					
						// Somehow got an invalid group...
						ActionErrors errors = new ActionErrors();
						errors.add("project", new ActionMessage("error.project.invalidgroup"));
						saveErrors( request, errors );
						return mapping.findForward("Failure");					
					}
				}
			}
		}

		// Set all of the new values in the project
		project.setTitle(title);

		// set the researchers
		project.setResearchers( null );
		project.setResearchers( ((EditTrainingForm)(form)).getResearcherList() );
		project.setPI( ((EditTrainingForm)(form)).getPI());
		
		
		project.setComments(comments);
		project.setDescription(description);
		project.setHours(hours);
		project.setDays(days);


		if (sendEmail) {
			// Send email to the groups they're collaboration with
			try {
				// set the SMTP host property value
				Properties properties = System.getProperties();
				properties.put("mail.smtp.host", "localhost");
			
				// create a JavaMail session
				javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
			
				// create a new MIME message
				MimeMessage message = new MimeMessage(mSession);
			
				// set the from address
				Address fromAddress = new InternetAddress(((Researcher)(user.getResearcher())).getEmail());
				message.setFrom(fromAddress);
			
				// set the to address by assembling a comma delimited list of addresses associated with the groups selected
				String emailStr = "";
				MessageResources mr = getResources(request);
				for (int i = 0; i < groups.length; i++) {
					if (i > 0) { emailStr = emailStr + ","; }
					
					emailStr = emailStr + mr.getMessage("email.groups." + groups[i]);
				}
				
				Address[] toAddress = InternetAddress.parse(emailStr);
				message.setRecipients(Message.RecipientType.TO, toAddress);
			
				// set the subject
				message.setSubject("New Training Request");
			
				// set the message body
				String text = ((Researcher)(user.getResearcher())).getFirstName() + " ";
				text += ((Researcher)(user.getResearcher())).getLastName() + " ";
				text += "has requested training from your group.  Replying to this email should reply directly to the researcher.\n\n";
				text += "Details:\n\n";
				
				if (project.getPI() != null)
					text += "PI: " + project.getPI().getListing() + "\n\n";
	
				text += "Groups: " + project.getGroupsString() + "\n\n";
				text += "Title: " + project.getTitle() + "\n\n";
				text += "Description: " + project.getDescription() + "\n\n";
				text += "Comments: " + project.getComments() + "\n\n";
			
				message.setText(text);
			
				// send the message
				Transport.send(message);
			
			}
			catch (Exception e) { ; }
		}


		// Save the project
		project.save();

		// Send signup confirmation to researcher
		if (!isSeminars)
			NewProjectUtils.sendEmailConfirmation(user.getResearcher(), project, getResources(request));

		// Go!
		ActionForward success = mapping.findForward("Success") ;
		success = new ActionForward( success.getPath() + "?ID=" + project.getID(), success.getRedirect() ) ;
		return success ;

	}
	
}