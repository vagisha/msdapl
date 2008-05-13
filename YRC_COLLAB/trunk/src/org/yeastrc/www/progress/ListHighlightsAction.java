/* ListHighlightsAction.java
 * Created on Jun 7, 2004
 */
package org.yeastrc.www.progress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.progress.*;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import java.util.*;

/**
 * Controller class for viewing all highlights.
 */
public class ListHighlightsAction extends Action {

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

		// Get and save all highlights to the request
		Collection c = HighlightUtils.getAllHighlights();
		request.setAttribute("highlights", c);

		// Go!
		return mapping.findForward("Success");
	}	
}