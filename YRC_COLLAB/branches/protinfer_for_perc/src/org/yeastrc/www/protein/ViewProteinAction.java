/*
 * ViewProteinAction.java
 * Created on Oct 4, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.protein;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.ExperimentBaitComparator;
import org.yeastrc.microscopy.ExperimentSearcher;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 4, 2004
 */

public class ViewProteinAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// The protein we're viewing
		int proteinID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		NRProtein protein = null;
		try {
			String strID = request.getParameter("id");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("protein", new ActionMessage("error.protein.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			proteinID = Integer.parseInt(strID);

			// Load our protein
			NRProteinFactory nrpf = NRProteinFactory.getInstance();
			protein = (NRProtein)(nrpf.getProtein(proteinID));
		
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("protein", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		Map goterms = protein.getGOAll();
		
		if ( ((Collection)goterms.get("P")).size() > 0)
			request.setAttribute("processes", goterms.get("P"));

		if ( ((Collection)goterms.get("C")).size() > 0)
			request.setAttribute("components", goterms.get("C"));
		
		if ( ((Collection)goterms.get("F")).size() > 0)
			request.setAttribute("functions", goterms.get("F"));
		
		// clean up
		goterms = null;
		
		YatesRunSearcher yrs = new YatesRunSearcher();
		yrs.setProtein(protein);
		Collection runs = yrs.search();
		
		// Make sure only runs belonging to projects this user has access to are listed.
		if (runs != null && runs.size() > 0) {
			Iterator iter = runs.iterator();
			while (iter.hasNext()) {
				YatesRun yr = (YatesRun)(iter.next());
				if (!yr.getProject().checkReadAccess(user.getResearcher()))
					iter.remove();
			}
			
			request.setAttribute("yatesdata", runs);
		}
		
		ExperimentSearcher es = ExperimentSearcher.getInstance();
		es.setProteinID(protein.getId());
		List tmpList = es.search();
		Collections.sort(tmpList, new ExperimentBaitComparator());

		// Make sure only experiments belonging to projects to which this user has access are listed
		if (tmpList != null && tmpList.size() > 0) {
			Iterator iter = tmpList.iterator();
			while ( iter.hasNext() ) {
				Experiment exp = (Experiment)(iter.next());
				if (!ProjectFactory.getProject(exp.getProjectID()).checkReadAccess(user.getResearcher()))
					iter.remove();
			}
			request.setAttribute("locdata", tmpList);
		}
		
		
		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("protein", protein);
		return mapping.findForward("Success");
	}

}