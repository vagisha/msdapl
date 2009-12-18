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
public class SaveNewDisseminationAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		int pi = 0;
		int researcherB = 0;
		int researcherC = 0;
		int researcherD = 0;
		String[] groups = null;
		//String[] fundingTypes = null;
		//String[] federalFundingTypes = null;
		String description = null;
		String name = null;
		String phone = null;
		String email = null;
		String address = null;
		String FEDEX = null;
		String comments = null;
		boolean commercial = false;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		// Load our project
		Dissemination project = new Dissemination();
		

		// Set this project in the request, as a bean to be displayed on the view
		//request.setAttribute("project", project);

		// We're saving!
		pi = ((EditDisseminationForm)(form)).getPI();
		researcherB = ((EditDisseminationForm)(form)).getResearcherB();
		researcherC = ((EditDisseminationForm)(form)).getResearcherC();
		researcherD = ((EditDisseminationForm)(form)).getResearcherD();
		groups = ((EditDisseminationForm)(form)).getGroups();
		//fundingTypes = ((EditDisseminationForm)(form)).getFundingTypes();
		//federalFundingTypes = ((EditDisseminationForm)(form)).getFederalFundingTypes();

		description = ((EditDisseminationForm)(form)).getDescription();
		name = ((EditDisseminationForm)(form)).getName();
		phone = ((EditDisseminationForm)(form)).getPhone();
		email = ((EditDisseminationForm)(form)).getEmail();
		address = ((EditDisseminationForm)(form)).getAddress();
		FEDEX = ((EditDisseminationForm)(form)).getFEDEX();
		commercial = ((EditDisseminationForm)(form)).getCommercial();
		comments = ((EditDisseminationForm)(form)).getComments();



		// Set blank items to null
		if (description.equals("")) description = null;
		if (name.equals("")) name = null;
		if (phone.equals("")) phone = null;
		if (email.equals("")) email = null;
		if (address.equals("")) address = null;
		if (comments.equals("")) comments = null;
		if (FEDEX.equals("")) FEDEX = null;
		
		// Set up our researchers
		Researcher oPI = null;
		Researcher orB = null;
		Researcher orC = null;
		Researcher orD = null;		
		try {
			if (pi != 0) {
				oPI = new Researcher();
				oPI.load(pi);
			}			
			if (researcherB != 0) {
				orB = new Researcher();
				orB.load(researcherB);
			}
			
			if (researcherC != 0) {
				orC = new Researcher();
				orC.load(researcherC);
			}
			
			if (researcherD != 0) {
				orD = new Researcher();
				orD.load(researcherD);
			}
		} catch (InvalidIDException iie) {

			// Couldn't load the researcher.
			ActionErrors errors = new ActionErrors();
			errors.add("project", new ActionMessage("error.project.invalidresearcher"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		/*
		// Set up the funding types
		project.clearFundingTypes();
		
		if (fundingTypes != null) {
			if (fundingTypes.length > 0) {
				for (int i = 0; i < fundingTypes.length; i++) {
					project.setFundingType(fundingTypes[i]);
				}
			}
		}
		
		// Set up the federal funding types
		project.clearFederalFundingTypes();
		
		if (federalFundingTypes != null) {
			if (federalFundingTypes.length > 0) {
				for (int i = 0; i < federalFundingTypes.length; i++) {
					project.setFederalFundingType(federalFundingTypes[i]);
				}
			}
		}
		*/
		
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
		project.setPI(oPI);
		project.setResearcherB(orB);
		project.setResearcherC(orC);
		project.setResearcherD(orD);
		project.setDescription(description);
		project.setName(name);
		project.setPhone(phone);
		project.setEmail(email);
		project.setAddress(address);
		project.setFEDEX(FEDEX);
		project.setCommercial(commercial);
		project.setComments(comments);


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
				
				emailStr = emailStr + mr.getMessage("email.groups.plasmids." + groups[i]);
			}
			
			Address[] toAddress = InternetAddress.parse(emailStr);
			message.setRecipients(Message.RecipientType.TO, toAddress);
		
			// set the subject
			message.setSubject("New Plasmid Dissemination Request");
		
			// set the message body
			String text = ((Researcher)(user.getResearcher())).getFirstName() + " ";
			text += ((Researcher)(user.getResearcher())).getLastName() + " ";
			text += "has requested plasmids from your group.  Replying to this email should reply directly to the researcher.\n\n";
			text += "Details:\n\n";
			
			if (oPI != null)
				text += "PI: " + oPI.getListing() + "\n\n";

			text += "Groups: " + project.getGroupsString() + "\n\n";
			text += "Title: " + project.getTitle() + "\n\n";
			text += "Description: " + project.getDescription() + "\n\n";
			text += "Ship name: " + project.getName() + "\n\n";
			text += "Phone: " + project.getPhone() + "\n\n";
			text += "Email: " + project.getEmail() + "\n\n";
			text += "Address:\n" + project.getAddress() + "\n\n";
			text += "FEDEX: " + project.getFEDEX() + "\n\n";
			text += "Commercial use: " + project.getCommercial() + "\n\n";
			text += "Comments: " + project.getComments() + "\n\n";
		
			message.setText(text);
		
			// send the message
			Transport.send(message);
		
		}
		catch (Exception e) { ; }

		
		// Save the project
		project.save();

		// Send signup confirmation to researcher
		NewProjectUtils.sendEmailConfirmation(user.getResearcher(), project, getResources(request));

		// Go!
		ActionForward success = mapping.findForward("Success") ;
		success = new ActionForward( success.getPath() + "?ID=" + project.getID(), success.getRedirect() ) ;
		return success ;

	}
	
}