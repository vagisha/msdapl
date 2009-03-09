/* UploadY2HAction.java
 * Created on May 14, 2004
 */
package org.yeastrc.www.y2h;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.yeastrc.data.InvalidFileFormatException;
import org.yeastrc.orf.InvalidORFException;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.y2h.Y2HResultsFile;
import org.yeastrc.y2h.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 14, 2004
 *
 */
public class UploadY2HAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		FormFile dataFile = null;
		Date screenDate = null;
		String comments = null;
		int projectID = 0;
		String mutations = null;
		int startResidue = 0;
		int endResidue = 0;
		String vectorConfig = null;
		

		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// Restrict access to administrators
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), Projects.TWOHYBRID) &&
		  !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("adminHome");
		}

		dataFile = ((UploadY2HForm)(form)).getDataFile();
		screenDate = ((UploadY2HForm)(form)).getScreenDate();
		comments = ((UploadY2HForm)(form)).getComments();
		projectID = ((UploadY2HForm)(form)).getProjectID();
		mutations = ((UploadY2HForm)(form)).getMutations();
		vectorConfig = ((UploadY2HForm)(form)).getVectorConfig();
		startResidue = ((UploadY2HForm)(form)).getStartResidue();
		endResidue = ((UploadY2HForm)(form)).getEndResidue();
		
		String data = new String(dataFile.getFileData());
		dataFile.destroy();
		
		Y2HResultsFile yrf = new Y2HResultsFile();
		
		try {
			yrf.setText(data);
			yrf.parseText();
			
			Y2HBait bait = yrf.getBait();
			
			// Set fragment data in bait
			if (startResidue != 0 && endResidue != 0) {
				bait.setFullLength(false);
				bait.setStartResidue(startResidue);
				bait.setEndResidue(endResidue);
			} else {
				bait.setFullLength(true);
			}
			
			// Parse the mutation list and add mutations to the bait
			if (mutations != null) {
				String[] muts = mutations.split(" ");
				for (int i = 0; i < muts.length; i++) {
					int mlen = muts[i].length();
					if (mlen < 3) continue;

					String orig = muts[i].substring(0, 1);
					String mut = muts[i].substring(mlen - 1, mlen);
					int position = Integer.parseInt(muts[i].substring(1, mlen - 1));
					
					//System.out.println("Adding mutation: " + orig + position + mut);
					bait.addMutation(position, orig, mut);
				}
			}
			
			// save the screen
			Y2HScreen ys = new Y2HScreen();
			ys.setBait(bait);
			ys.setComments(comments);
			ys.setProjectID(projectID);
			ys.setScreenDate(screenDate);
			ys.setUploadDate(new Date());
			ys.save();
			
			// save the screen results
			yrf.setScreenID(ys.getID());
			yrf.saveResults();
			
			// save the bait.
			//Y2HBaitUtils.getInstance().saveBait(bait);
			
			
			
		} catch (InvalidFileFormatException iffe) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.upload.invalidfileformat", iffe.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (InvalidORFException ioe) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.upload.invalidorf", ioe.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");			
		}

		
		// Kick it to the view page
		request.setAttribute("saved", "true");
		return mapping.findForward("Success");

	}

}
