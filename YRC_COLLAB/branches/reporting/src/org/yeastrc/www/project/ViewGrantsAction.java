package org.yeastrc.www.project;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantRecord;
import org.yeastrc.grant.GrantUtils;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ViewGrantsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws InvalidIDException, SQLException {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		int PI = 0;
		try {
			PI = Integer.parseInt(request.getParameter("PI"));
		}
		catch (NumberFormatException e) {
			PI = 0;
		}
		
		// put the PI in the request for future use
		request.setAttribute("PI", PI);
		
		// get all the grants associated with this user and given PI
		List<Grant> grants = GrantRecord.getGrantForUserAndPI(user, PI);
		if (request.getParameter("sortby") != null) {
			sortGrantsBy(grants, request.getParameter("sortby"));
		}
		request.setAttribute("grants", grants);
		
		return mapping.findForward("Success");
		
	}
	
	private void sortGrantsBy(List<Grant> grants, String sortBy) {
		if (sortBy.equalsIgnoreCase("title")) 
			Collections.sort(grants, new GrantUtils.GrantTitleComparator());
			
		else if (sortBy.equalsIgnoreCase("pi"))
			Collections.sort(grants, new GrantUtils.GrantPIComparator());
		
		else if (sortBy.equalsIgnoreCase("sourceType"))
			Collections.sort(grants, new GrantUtils.GrantSourceTypeComparator());
		
		else if (sortBy.equalsIgnoreCase("sourceName"))
			Collections.sort(grants, new GrantUtils.GrantSourceNameComparator());
		
		else if (sortBy.equalsIgnoreCase("grantNum"))
			Collections.sort(grants, new GrantUtils.GrantNumberComparator());
		
		else if (sortBy.equalsIgnoreCase("grantAmount"))
			Collections.sort(grants, new GrantUtils.GrantAmountComparator());
		
	}
}
