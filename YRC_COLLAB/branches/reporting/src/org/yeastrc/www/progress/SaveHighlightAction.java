/* SaveHighlightAction.java
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
import org.yeastrc.progress.Highlight;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 7, 2004
 *
 */
public class SaveHighlightAction extends Action {

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
		
		// Get our already-validated form data
		HighlightForm hf = (HighlightForm)form;
		int id = hf.getId();
		int projectID = hf.getProjectID();
		int year = hf.getYear();
		String title = hf.getTitle();
		String body = hf.getBody();

		// Set up the highlight object appropriately (is it new or are we editing an existing one)
		Highlight highlight = new Highlight();
		if (id != 0) {
			highlight.load(id);
		}
		
		// Set the data in the object
		highlight.setTitle(title);
		highlight.setBody(body);
		highlight.setProjectID(projectID);
		highlight.setYear(year);
		
		// Save the object
		highlight.save();
		request.setAttribute("saved", "true");

		// Go!
		return mapping.findForward("Success");
	}	
}
