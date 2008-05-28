package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantRecord;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class SaveGrantAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		EditGrantForm grantForm = (EditGrantForm) form;
		Grant grant = new Grant();
		grant.setID(grantForm.getGrantID());
		grant.setTitle(grantForm.getGrantTitle());
		Researcher PI = new Researcher();
		PI.load(grantForm.getPI());
		grant.setGrantPI(PI);
		grant.setFundingSource(GrantRecord.getFundingSource(grantForm.getFundingType(), grantForm.getFundingSourceName()));
		grant.setGrantNumber(grantForm.getGrantNumber());
		grant.setGrantAmount(grantForm.getGrantAmount());
		
		// save the grant in the database
		GrantRecord.save(grant);
		
		//if the grant id in the form is 0, it means this is a new grant.
		if (grantForm.getGrantID() <= 0) {
			String path = mapping.findForward("ListGrants").getPath()+"?PI="+PI.getID();
			return new ActionForward(path);
		}
		// the user is editing an existing grant.
		else {
			request.setAttribute("grant", grant);
			return mapping.findForward("SavedGrant");
		}
	}
}
