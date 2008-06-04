/*
 * ViewTestImageAction.java
 * Created on Sep 14, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.microscopy.Experiment;
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

public class ViewSelectedRegionThumbnail  extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		int MAX_HEIGHT = 50;
		
		String strid = request.getParameter("id");
		int id = Integer.parseInt(strid);
		
		SelectedRegionImage sr = SelectedRegionImageFactory.getInstance().getSelectedRegionImage(id);

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
			Experiment experiment = sr.getFullFieldImage().getExperiment();
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
		
		
		
		
		
		
		BufferedImage bi = ImageDataRetriever.getInstance().getImage(sr);	    
	    
		float yScale = (float)MAX_HEIGHT / (float)bi.getHeight();
		float xScale = yScale;
		
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(bi); // The source image
		pb.add(xScale);         // The xScale
		pb.add(yScale);         // The yScale
		pb.add(0.0F);           // The x translation
		pb.add(0.0F);           // The y translation
		pb.add(new InterpolationBilinear()); // The interpolation 

		bi = JAI.create("scale", pb, null).getAsBufferedImage();
	    request.setAttribute("image", bi);
		
		return mapping.findForward("Success");
	}
}