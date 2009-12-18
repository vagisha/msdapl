/* ViewProgressReportAction.java
 * Created on May 21, 2004
 */
package org.yeastrc.www.progress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.progress.ProgressReportGenerator;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 21, 2004
 *
 */
public class ViewProgressReportAction extends Action {

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
		if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("adminHome");
		}

		ProgressReportGenerator prg = new ProgressReportGenerator();
		prg.runReport();
		
		request.setAttribute("XML", prg.getXML());
		response.setContentType("text/xml");
		
		
		return mapping.findForward("Success");
	}

}
