/*
 * SaveMicroscopyCommentsAction.java
 * Created on Jul 13, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.internal.microscopy.ExperimentSaver;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.ExperimentFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 13, 2006
 */

public class SaveMicroscopyCommentsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// The screen we're viewing
		int experimentID = ((MicroscopyCommentsForm)form).getId();
		String comments = ((MicroscopyCommentsForm)form).getComments();
		
		
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		

		Project project = null;
		Experiment experiment = null;
		try {

			// Load our experiment
			experiment = ExperimentFactory.getInstance().getExperiment(experimentID);
		

			project = ProjectFactory.getProject(experiment.getProjectID());
			if (!project.checkAccess(user.getResearcher()))
				throw new Exception( "No access." );

			experiment.setComments( comments );
			ExperimentSaver.getInstance().save( experiment );
			
		} catch (Exception e) { ; }
	
		
		// Kick it to the view page
		ActionForward success = mapping.findForward( "Success" ) ;
		success = new ActionForward( success.getPath() + "?id=" + experiment.getId(), success.getRedirect() ) ;
		return success ;
		
	}

	
}
