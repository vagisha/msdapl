/* ViewY2HScreenAction.java
 * Created on Jun 16, 2004
 */
package org.yeastrc.www.y2h;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.y2h.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 16, 2004
 *
 */
public class ViewY2HScreenAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// The screen we're viewing
		int screenID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		Y2HScreen ys = null;
		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("y2h", new ActionMessage("error.y2h.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			screenID = Integer.parseInt(strID);

			// Load our screen
			ys = new Y2HScreen();
			ys.load(screenID);
		

			Project project = ProjectFactory.getProject(ys.getProjectID());
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("y2h", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (InvalidIDException iie) {
			ActionErrors errors = new ActionErrors();
			errors.add("y2h", new ActionMessage("error.y2h.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("screen", ys);
		return mapping.findForward("Success");
	}

}
