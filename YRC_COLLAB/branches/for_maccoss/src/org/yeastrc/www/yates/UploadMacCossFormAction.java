/*
 * UploadMacCossFormAction.java
 * Created on May 18, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.ProjectLite;
import org.yeastrc.project.ProjectLiteDAO;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 18, 2005
 */

public class UploadMacCossFormAction extends Action {

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
		if (!groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS) &&
		  !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");
		}

		try {
			int projectID = Integer.parseInt( request.getParameter( "projectID" ) );
			UploadDataForm newForm = new UploadDataForm();
			newForm.setProjectID( projectID );
			request.setAttribute( "uploadDataForm", newForm );
		} catch (Exception e ) { ; }
		
		// get a list of projects on which this researcher is listed
		List<ProjectLite> projects = ProjectLiteDAO.instance().getResearcherWritableProjects(user.getResearcher().getID());
		request.setAttribute("projects", projects);
		
		// Kick it to the view page
		return mapping.findForward("Success");

	}

}