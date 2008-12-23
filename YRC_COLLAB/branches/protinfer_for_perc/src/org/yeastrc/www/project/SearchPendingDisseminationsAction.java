/* SearchPendingDisseminationsAction.java
 * Created on Jun 22, 2004
 */
package org.yeastrc.www.project;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import java.util.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 6, 2004
 *
 */
public class SearchPendingDisseminationsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		String searchString;
		String[] groups;
		String[] types;
		
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// The Researcher
		Researcher researcher = user.getResearcher();
		
		// Get our list of projects
		List projects = DisseminationUtils.getUnshippedProjects();

		// Filter out the shipped disseminations
		Iterator iter = projects.iterator();
		while (iter.hasNext()) {
			Dissemination p = (Dissemination)(iter.next());
			if (!p.checkAccess(researcher)) iter.remove();
		}
		
		// Set this list into the request
		request.setAttribute("unshipped", projects);

		// Go!
		return mapping.findForward("Success");
	}
}