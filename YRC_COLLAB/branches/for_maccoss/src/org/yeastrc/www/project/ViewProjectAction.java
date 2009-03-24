/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.HashMap;
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
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.ProteinferRunSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunMsSearchLinker;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * Implements the logic to register a user
 */
public class ViewProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// Get the projectID they're after
		int projectID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Load our project
		Project project;
		
		try {
			project = ProjectFactory.getProject(projectID);
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("project", project);
		
		// Check for experiment data for this project

		// Check for yates MS data
		YatesRunSearcher yrs = new YatesRunSearcher();
		yrs.setProjectID(project.getID());
		yrs.setMostRecent( true );

		List<YatesRun> yatesRuns = yrs.search();
		request.setAttribute("yatesdata", yatesRuns);

		// Associate yatesRunIds with msData searchIds where possible AND
		// Associate yatesRunIds with already saved Protein Inference Runs
		Map<Integer, Integer> yatesRunToMsSearchMap = new HashMap<Integer, Integer>(yatesRuns.size());
		Map<Integer, List<ProteinferJob>> yatesRunToProteinferRunMap = new HashMap<Integer, List<ProteinferJob>>();
		for(YatesRun run: yatesRuns) {
		    int runId = run.getId();
		    int searchId = YatesRunMsSearchLinker.linkYatesRunToMsSearch(runId);

		    yatesRunToMsSearchMap.put(runId, searchId);
		    if(searchId > 0) {
		        List<ProteinferJob> proteinferRunIds = ProteinferRunSearcher.getProteinferJobsForMsSearch(searchId);
		        yatesRunToProteinferRunMap.put(searchId, proteinferRunIds);
		    }
		}
		request.setAttribute("yatesRunToMsSearchMap", yatesRunToMsSearchMap);
		request.setAttribute("yatesRunToProteinferRunMap", yatesRunToProteinferRunMap);
		request.setAttribute("projectId", projectID);



		Groups groupMan = Groups.getInstance();
		boolean showUpload = false;
		if(groupMan.isMember( user.getResearcher().getID(), "administrators" ) ) {
		    showUpload = true;
		}
		else {
		    int userId = user.getResearcher().getID();
		    for(Researcher researcher: project.getResearchers()) {
		        
		        if(researcher.getID() == userId) {
		            showUpload = true; 
		            break;
		        }
		    }
		}

		if(showUpload)
		    request.setAttribute( "showMacCossUpload", true );


		// Forward them on to the happy success view page!
		return mapping.findForward("Success");


	}
	
}