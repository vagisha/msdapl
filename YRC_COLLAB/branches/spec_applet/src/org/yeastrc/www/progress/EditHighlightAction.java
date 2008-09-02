/* EditHighlightAction.java
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
 * Controller class for editing a highlight.
 */
public class EditHighlightAction extends Action {

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
		
		// Get the id we were passed and load the object
		int id = Integer.parseInt(request.getParameter("id"));
		Highlight highlight = new Highlight();
		highlight.load(id);
		
		// Create our ActionForm
		HighlightForm newForm = new HighlightForm();
		request.setAttribute("highlightForm", newForm);
		
		newForm.setTitle(highlight.getTitle());
		newForm.setBody(highlight.getBody());
		newForm.setProjectID(highlight.getProjectID());
		newForm.setId(highlight.getId());
		newForm.setYear(highlight.getYear());

		// Go!
		return mapping.findForward("Success");
	}	
}