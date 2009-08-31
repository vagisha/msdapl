/*
 * UploadMacCossFormAction.java
 * Created on May 18, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.upload;

import java.util.Collections;
import java.util.Comparator;
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


public class UploadMSDataFormAction extends Action {

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

		// Restrict access to administrators and certain user groups
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS) &&
		  !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");
		}

		UploadMSDataForm newForm = new UploadMSDataForm();
		if(groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS)) {
		    newForm.setPipeline(Pipeline.MACOSS);
		}
		else {
		    newForm.setPipeline(Pipeline.TPP);
		}
		
		
		try {
			int projectID = Integer.parseInt( request.getParameter( "projectID" ) );
			newForm.setProjectID( projectID );
		} catch (Exception e ) { newForm.setProjectID(0); }
		
		request.setAttribute( "uploadMSDataForm", newForm );
		
		// get a list of projects on which this researcher is listed
		List<ProjectLite> projects = ProjectLiteDAO.instance().getResearcherWritableProjects(user.getResearcher().getID());
		Collections.sort(projects, new Comparator<ProjectLite>() {
            @Override
            public int compare(ProjectLite o1, ProjectLite o2) {
                return Integer.valueOf(o2.getId()).compareTo(o1.getId());
            }});
		
		request.getSession().setAttribute("researcherProjects", projects);
		
		// Kick it to the view page
		return mapping.findForward("Success");

	}
}