/*
 * SaveY2HCommentsAction.java
 * Created on Jul 13, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
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
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.y2h.Y2HScreen;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 13, 2006
 */

public class SaveY2HCommentsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		Y2HScreen ys = null;
		int screenID = ((Y2HCommentsForm)form).getScreenID();
		String comments = ((Y2HCommentsForm)form).getComments();
		
		try {

			// Load our screen
			ys = new Y2HScreen();
			ys.load(screenID);

			// save the screen to the request object
			request.setAttribute("screen", ys);
			
			Project project = ProjectFactory.getProject(ys.getProjectID());
			if (!project.checkAccess(user.getResearcher())) {
				throw new Exception("No access.");
			}

			if (comments == null || comments.equals("")) {
				
				// Kick it to the view page
				ActionForward success = mapping.findForward( "Success" ) ;
				success = new ActionForward( success.getPath() + "?ID=" + ys.getID(), success.getRedirect() ) ;
				return success ;
			}
			
			ys.setComments( comments );
			ys.save();
			
		
		} catch (Exception e) { ; }
		
		
		// Kick it to the view page
		ActionForward success = mapping.findForward( "Success" ) ;
		success = new ActionForward( success.getPath() + "?ID=" + ys.getID(), success.getRedirect() ) ;
		return success ;
	}
}
