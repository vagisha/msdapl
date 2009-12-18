/*
 * UploadRedirectAction.java
 * Created on Oct 14, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.misc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 14, 2004
 */

public class UploadRedirectAction extends Action {

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

		// Restrict access to administrators
		Groups groupMan = Groups.getInstance();
		
		if (groupMan.isMember(user.getResearcher().getID(), Projects.YATES))
			return mapping.findForward("Yates");

		if (groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS))
			return mapping.findForward("MacCoss");
		
		else if (groupMan.isMember(user.getResearcher().getID(), Projects.TWOHYBRID))
			return mapping.findForward("Y2H");

		else if (groupMan.isMember(user.getResearcher().getID(), Projects.MICROSCOPY))
			return mapping.findForward("Microscopy");
		
		else if (groupMan.isMember(user.getResearcher().getID(), "administrators"))
			return mapping.findForward("Yates");
		

		ActionErrors errors = new ActionErrors();
		errors.add("access", new ActionMessage("error.access.invalidgroup"));
		saveErrors( request, errors );
		
		return mapping.findForward("adminHome");
	}

}