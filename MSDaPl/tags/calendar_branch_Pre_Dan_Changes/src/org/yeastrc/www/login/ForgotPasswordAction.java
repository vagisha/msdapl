/*
 * ForgotPasswordAction.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.login;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.yeastrc.www.user.*;
import org.yeastrc.project.Researcher;


/**
 * Implements the logic to register a user
 */
public class ForgotPasswordAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// Get their session first.  Disallow them from logging in if they already are
		HttpSession session = request.getSession();
		session.removeAttribute("user");

		// These items should have already been validated in the ActionForm
		String username = ((ForgotPasswordForm)form).getUsername();
		String email = ((ForgotPasswordForm)form).getEmail();


		// Find the user in the database.
		User user = null;
		if (username != null && !username.equals("")) {
			try {
				user = UserUtils.getUser(username);
			} catch (NoSuchUserException nsue) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.forgotpassword.invaliduser"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} else {
			// Get the researcherID corresponding to this email address
			int researcherID = UserUtils.emailExists(email);

			if (researcherID == -1) {
				// Email address doesn't exist
				ActionErrors errors = new ActionErrors();
				errors.add("email", new ActionMessage("error.forgotpassword.invalidemail"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			} else {
				// Email address does exist
				if (UserUtils.userExists(researcherID)) {

					// This user has been found!  Create a user and load it
					user = new User();
					user.load(researcherID);

				} else {
					// The researcher exists, but not the user.  Create a new user, associate it w/ this researcher
					Researcher researcher = new Researcher();
					researcher.load(researcherID);
					
					user = new User();
					user.setUsername(researcher.getEmail());
					user.setResearcher(researcher);
				}
			}
		}
		
		// We should now have a valid User object
		
		// Generate a new password
		String password = UserUtils.generatePassword();
		
		// Set the password in the user and save it.
		user.setPassword(password);
		user.save();
		
		// Generate and send the email to the user.
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
			Address[] toAddress = InternetAddress.parse(user.getResearcher().getEmail());
			message.setRecipients(Message.RecipientType.TO, toAddress);

           // set the subject
           message.setSubject("YRC Registration Info");

           // set the message body
			String text = "Here is your login information for http://www.yeastrc.org/ :\n\n";
			text += "Username: " + user.getUsername() + "\n";
			text += "Password: " + password + "\n\n";
			text += "Thank you,\nThe Yeast Resource Center\n";
			
           message.setText(text);

           // send the message
           Transport.send(message);

       }
		catch (AddressException e) {
			// Invalid email address format
			ActionErrors errors = new ActionErrors();
			errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		catch (SendFailedException e) {
			// Invalid email address format
			ActionErrors errors = new ActionErrors();
			errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		catch (MessagingException e) {
			// Invalid email address format
			ActionErrors errors = new ActionErrors();
			errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}


		// Forward them on to the happy success page!
		return mapping.findForward("Success");
	}
	
}