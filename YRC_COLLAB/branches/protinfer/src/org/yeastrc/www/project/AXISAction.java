/* AXISAction.java
 * Created on Apr 29, 2004
 */
package org.yeastrc.www.project;

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

/**
 * 
 */
public class AXISAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;
		String type;
		Project project;
				
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// Load our project
		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("standardHome");
			}

			projectID = Integer.parseInt(strID);
			project = ProjectFactory.getProject(projectID);

		} catch (InvalidIDException iie) {
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");	

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");
		}

		type = request.getParameter("type");
		String fStr;
		String[] axisCodes = new String[0];
		if (type.equals("I")) {
			if (project.getAxisI() != null)
				axisCodes = project.getAxisI().split(" ");
		} else if (type.equals("II")) {
			if (project.getAxisII() != null)
				axisCodes = project.getAxisII().split(" ");
		}

		//AXISForm aForm = new AXISForm();
		((AXISForm)form).setAXIS(axisCodes);
		//request.setAttribute("axisForm", aForm);

		return mapping.findForward(type);
	}
}