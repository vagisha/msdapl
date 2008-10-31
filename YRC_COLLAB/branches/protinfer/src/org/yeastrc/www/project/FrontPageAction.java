/* FrontPageAction.java
 * Created on Jun 23, 2004
 */
package org.yeastrc.www.project;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.y2h.Y2HScreenSearcher;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * Controller class for viewing all highlights.
 */
public class FrontPageAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// Get all NEW projects for this YRC member
		Collection c = user.getNewProjects();
		request.setAttribute("newProjects", c);

		// Get all projects for this YRC user
		c = user.getProjects();
		request.setAttribute("userProjects", c);

		Groups groupMan = Groups.getInstance();
		if (groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			Y2HScreenSearcher yss = new Y2HScreenSearcher();
			yss.setMostRecent(true);
			yss.setNumResults(10);
			request.setAttribute("y2hdata", yss.search());
			yss = null;
			
			YatesRunSearcher yrs = new YatesRunSearcher();
			yrs.setMostRecent(true);
			yrs.setNumResults(10);
			request.setAttribute("yatesdata", yrs.search());
			yrs = null;
		}
		
		
		// Go!
		return mapping.findForward("Success");
	}	
}
