/*
 * ViewTestImageAction.java
 * Created on Sep 14, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.RenderedImage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.FullFieldImage;
import org.yeastrc.microscopy.FullFieldImageFactory;
import org.yeastrc.microscopy.ImageDataRetriever;
import org.yeastrc.microscopy.SelectedRegionImage;
import org.yeastrc.microscopy.SelectedRegionImageFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Sep 14, 2005
 */

public class ViewMicroscopyImage  extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		String strid = request.getParameter("id");
		String type = request.getParameter("type");
		Experiment experiment = null;
		
		int id = Integer.parseInt(strid);
		RenderedImage ri = null;
		
		if (type.equals("FF")) {
			FullFieldImage ff = FullFieldImageFactory.getInstance().getFullFieldImage(id);
			ri = ImageDataRetriever.getInstance().getImage(ff);
			experiment = ff.getExperiment();
		} else if (type.equals("SR")) {
			SelectedRegionImage sr = SelectedRegionImageFactory.getInstance().getSelectedRegionImage(id);
			ri = ImageDataRetriever.getInstance().getImage(sr);
			experiment = sr.getFullFieldImage().getExperiment();
		}

		
		
		
		// ADD SECURITY CODE
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		try {
			Project project = ProjectFactory.getProject(experiment.getProjectID());

			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}		
		    
		
	    request.setAttribute("image", ri);
		
		return mapping.findForward("Success");
	}
}