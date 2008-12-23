/*
 * ORFAndTagAction.java
 * Created on Jun 2, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.util.List;

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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 2, 2006
 */

public class ORFAndTagAction extends Action {

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
		
		// make sure we have our session object
		if (request.getSession().getAttribute( "ffImages" ) == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.upload.microscopy.missingsession"));
			saveErrors( request, errors );
			
			return mapping.findForward("uploadMicroscopyAction");
		}
		
		ORFAndTagForm otForm = (ORFAndTagForm)form;
		
		String idCode = otForm.getIdCode();
		String comments = otForm.getComments();
		
		// This is the experiment we're adding data to
		Experiment experiment = (Experiment)request.getSession().getAttribute( "experiment" );
		
		// clear the form before we send it off
		request.setAttribute( "ORFAndTagForm", new ORFAndTagForm() );
		
		// Get the fullfield image object we're adding data to
		FullFieldImage ffImage = null;
		List ffImages = (List)request.getSession().getAttribute( "ffImages" );

		for ( int i = 0; i < ffImages.size(); i++ ) {
			
			if ( ((UploadedFullFieldImage)ffImages.get( i )).getIdCode().equals( idCode ) ) {
				ffImage = ((UploadedFullFieldImage)ffImages.get( i )).getFfImage();
			}
			
		}
		
		// save the comments
		if (ffImage != null && comments != null && !comments.equals( "" ))
			ffImage.setComments( comments );
		
		
		return mapping.findForward( "Success" );
	}


}
