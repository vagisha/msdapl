/*
 * SaveMicroscopyExperimentAction.java
 * Created on Jul 10, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.RenderedImage;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.internal.microscopy.ExperimentDeleter;
import org.yeastrc.internal.microscopy.ExperimentSaver;
import org.yeastrc.internal.microscopy.FullFieldImageSaver;
import org.yeastrc.internal.microscopy.GOAnnotationSaver;
import org.yeastrc.internal.microscopy.ImageDataSaver;
import org.yeastrc.internal.microscopy.R3DDataSaver;
import org.yeastrc.internal.microscopy.SelectedRegionSaver;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.SelectedRegionImage;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 10, 2006
 */

public class SaveMicroscopyExperimentAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		// make sure we have our session objects
		if (request.getSession().getAttribute( "ffImages" ) == null ||
			request.getSession().getAttribute( "experiment" ) == null ||
			request.getSession().getAttribute( "ffImageData" ) == null ||
			request.getSession().getAttribute( "r3dData" ) == null) {

			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.upload.microscopy.missingsession"));
			saveErrors( request, errors );
			
			return mapping.findForward("uploadMicroscopyAction");
		}
		
		// get our session
		HttpSession session = request.getSession();
		
		// save the experiment to the database
		Experiment experiment = null;
		try {

			experiment = (Experiment)session.getAttribute( "experiment" );
			ExperimentSaver.getInstance().save( experiment );
			
		} catch (Exception e) {
			
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.save.microscopy.experiment", e.getMessage() ) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
			
		}
		
		// save the r3d data to the database
		try {
			R3DDataSaver.getInstance().save( experiment, (byte[])(session.getAttribute( "r3dData" )));
		} catch( Exception e) {
			
			// delete the experiment
			ExperimentDeleter.getInstance().delete( experiment );
			
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.save.microscopy.r3ddata", e.getMessage() ) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
			
		}
		
		
		// save the full field images and their data to the database
		List ffImages = (List)(session.getAttribute( "ffImages" ));
		RenderedImage[] imageData = (RenderedImage[])(session.getAttribute( "ffImageData" ));
		
		if (ffImages.size() != imageData.length ) {
			
			// delete the experiment
			ExperimentDeleter.getInstance().delete( experiment );
			
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.save.microscopy.sizemismatch") );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
			
		}
		
		try {
			for (int i = 0; i < ffImages.size(); i++) {
				UploadedFullFieldImage uff = (UploadedFullFieldImage)(ffImages.get( i ));

				// save the full field image object
				FullFieldImageSaver.getInstance().save( uff.getFfImage() );

				// save the image data
				ImageDataSaver.getInstance().save( uff.getFfImage(), imageData[i] );
				
				// save the selected regions for this full field image
				List selectedRegions = uff.getSrImages();
				if (selectedRegions != null) {
					Iterator srIter = selectedRegions.iterator();
					while (srIter.hasNext()) {
						UploadedSelectedRegion usr = (UploadedSelectedRegion)srIter.next();
						
						// Set up and save the SelectedRegion object
						SelectedRegionImage sr = SelectedRegionImage.getInstance();
						sr.setFullFieldImage( uff.getFfImage() );
						SelectedRegionSaver.getInstance().save( sr );
	
						// Save the image data
						ImageDataSaver.getInstance().save( sr, usr.getImage() );
	
					}
					srIter = null;
					selectedRegions = null;
				}
				
				// save the go terms for this full field image
				List goTerms = uff.getGoTerms();
				if (goTerms != null) {
					Iterator goIter = goTerms.iterator();
					while (goIter.hasNext()) {
						GONode node = (GONode)goIter.next();
						GOAnnotationSaver.getInstance().save( uff.getFfImage(), node );
					}
					goIter = null;
					goTerms = null;
				}
			
			}
		} catch (Exception e) {

			// delete the experiment
			ExperimentDeleter.getInstance().delete( experiment );
			
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.save.microscopy.images", e.getMessage() ) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
			
			
		}
		
		// looks like saving was a success
		
		// clear the variables in this user's session
		session.removeAttribute( "experiment" );
		session.removeAttribute( "ffImages" );
		session.removeAttribute( "ffImageData" );
		session.removeAttribute( "r3dData" );
		
		// clean up memory
		System.gc();
		
		// Kick it to the view page
		ActionForward success = mapping.findForward("Success") ;
		success = new ActionForward( success.getPath() + "?id=" + experiment.getId(), success.getRedirect() ) ;
		return success ;
	}
	
}
