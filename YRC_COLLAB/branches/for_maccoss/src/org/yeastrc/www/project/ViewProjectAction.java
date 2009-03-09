/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.Collections;
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
import org.yeastrc.grant.GrantRecord;
import org.yeastrc.microscopy.ExperimentBaitComparator;
import org.yeastrc.microscopy.ExperimentSearcher;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Projects;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.ProteinferRunSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.y2h.Y2HScreenSearcher;
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
		
		// get the grants for this project and set them in the request
		List<Grant> grants = GrantRecord.getInstance().getGrantsForProject(project.getID());
		request.setAttribute("grants", grants);
		
		// Check for experiment data for this project
		if (project.getShortType().equals(Projects.COLLABORATION) || project.getShortType().equals(Projects.TECHNOLOGY)) {

			// Check for yeast two-hybrid data
			Y2HScreenSearcher yss = new Y2HScreenSearcher();
			yss.setProjectID(project.getID());
			request.setAttribute("y2hdata", yss.search());
			
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
			
			
			
			ExperimentSearcher es = ExperimentSearcher.getInstance();
			es.setProjectID(project.getID());
			List tmpList = es.search();
			Collections.sort(tmpList, new ExperimentBaitComparator());
			request.setAttribute("locdata", tmpList);
			
			Groups groupMan = Groups.getInstance();
			if ( project.getGroups().contains( Projects.YATES ) &&
					( groupMan.isMember( user.getResearcher().getID(), Projects.YATES ) || groupMan.isMember( user.getResearcher().getID(), "administrators" ) ) )
				request.setAttribute( "showYatesUpload", new Boolean( true ) );
			else
				request.setAttribute( "showYatesUpload", new Boolean( false ) );
			
			if ( project.getGroups().contains( Projects.MACCOSS ) && 
					( groupMan.isMember( user.getResearcher().getID(), Projects.MACCOSS ) || groupMan.isMember( user.getResearcher().getID(), "administrators" ) ) )
				request.setAttribute( "showMacCossUpload", new Boolean( true ) );
			else
				request.setAttribute( "showMacCossUpload", new Boolean( false ) );
			
			if ( project.getGroups().contains( Projects.MICROSCOPY ) && 
					( groupMan.isMember( user.getResearcher().getID(), Projects.MICROSCOPY ) || groupMan.isMember( user.getResearcher().getID(), "administrators" ) ) )
				request.setAttribute( "showMicroUpload", new Boolean( true ) );
			else
				request.setAttribute( "showMicroUpload", new Boolean( false ) );
			
		}
		

		// Forward them on to the happy success view page!
		String theForward = null;
		
		if (project.getShortType().equals(Projects.COLLABORATION))
			return mapping.findForward("Collaboration");

		if (project.getShortType().equals(Projects.DISSEMINATION))
			return mapping.findForward("Dissemination");

		if (project.getShortType().equals(Projects.TECHNOLOGY))
			return mapping.findForward("Technology");

		if (project.getShortType().equals(Projects.TRAINING))
			return mapping.findForward("Training");

		ActionErrors errors = new ActionErrors();
		errors.add("username", new ActionMessage("error.project.invalidtype"));
		saveErrors( request, errors );
		return mapping.findForward("Failure");

	}
	
}