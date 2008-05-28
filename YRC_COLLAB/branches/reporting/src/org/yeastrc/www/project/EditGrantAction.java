package org.yeastrc.www.project;

import java.sql.SQLException;
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
import org.yeastrc.grant.FundingSourceType;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantRecord;
import org.yeastrc.grant.FundingSourceType.SourceName;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class EditGrantAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws SQLException, InvalidIDException {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		// get the funding source types
		List<FundingSourceType> sources = FundingSourceType.getFundingSources();
		request.getSession().setAttribute("sourceTypes", sources);
		
		// get the federal funding agency names
		List<SourceName> federalSources = FundingSourceType.FEDERAL.getAcceptedSourceNames();
		request.getSession().setAttribute("federalSources", federalSources);
		
		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		List <Researcher> researchers = Projects.getAllResearchers(); // this is already sorted by last name!
		request.getSession().setAttribute("researchers", researchers);
		
		// user wants to edit an existing grant
		if (request.getParameter("grantID") != null) {
			return editGrantForm(mapping, request);
		}
		// user wants to create a new grant
		else {
			return newGrantForm(mapping, request, user);
		}
	}

	private ActionForward newGrantForm(ActionMapping mapping,
			HttpServletRequest request, User user) {
		// if a PI ID was sent with the request, use this in the grant form
		int PI = 0;
		try {
			PI = Integer.parseInt(request.getParameter("PI"));
		}
		catch (NumberFormatException e) {}
		
		// if no PI ID was found in the request, use the ID of the user
		if (PI == 0) {
			PI = user.getResearcher().getID();
		}
		
		// Create a new form and set the selected PI
		EditGrantForm grantForm = new EditGrantForm();
		grantForm.setPI(PI);
		request.setAttribute("editGrantForm", grantForm);
		
		request.setAttribute("newGrant", "true");
		
		return mapping.findForward("Success");
	}

	private ActionForward editGrantForm(ActionMapping mapping,
			HttpServletRequest request) throws SQLException, InvalidIDException {
		
		int grantID = 0;
		try {
			grantID = Integer.parseInt(request.getParameter("grantID"));
		}
		catch (NumberFormatException e) {}
		
		if (grantID == 0) {
			ActionErrors errors = new ActionErrors();
			errors.add("grant", new ActionMessage("error.grant.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// get the grant for the given ID;
		Grant grant = GrantRecord.load(grantID);
		if (grant == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("grant", new ActionMessage("error.grant.notfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Create a new form and set the values from the grant
		EditGrantForm grantForm = new EditGrantForm();
		grantForm.setGrantID(grant.getID());
		grantForm.setGrantTitle(grant.getTitle());
		grantForm.setPI(grant.getPIID());
		grantForm.setFundingType(grant.getFundingSource().getTypeName());
		if (grant.getFundingSource().isFederal()) {
			grantForm.setFedFundingAgencyName(grant.getFundingSource().getName());
		}
		else {
			grantForm.setFundingAgencyName(grant.getFundingSource().getDisplayName());
		}
		grantForm.setGrantNumber(grant.getGrantNumber());
		grantForm.setGrantAmount(grant.getGrantAmount());
		
		request.setAttribute("editGrantForm", grantForm);
		
		return mapping.findForward("Success");
	}
}
