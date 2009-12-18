/*
 * UploadMicroscopyGOAnnotation.java
 * Created on Jun 6, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 6, 2006
 */

public class UploadMicroscopyGOAnnotation extends Action {

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
		
		String idCode = ((GOAnnotationForm)form).getIdCode();
		String goAcc = ((GOAnnotationForm)form).getGoAcc();

		request.setAttribute( "GOAnnotationForm", new GOAnnotationForm() );
		
		// find the full field image we're saving this sr too
		Iterator iter = ((java.util.List)(request.getSession().getAttribute( "ffImages" ))).iterator();
		UploadedFullFieldImage ffImage = null;
		while (iter.hasNext()) {
			UploadedFullFieldImage ffi = (UploadedFullFieldImage)iter.next();
			if (idCode.equals( (ffi.getIdCode() ) )) {
				ffImage = ffi;
				break;
			}
		}

		// couldn't find the ff image, just kick it back to the web page
		if (ffImage == null)
			return mapping.findForward( "Success" );
		
		// Get the GONode corresponding to the GO term entered
		GONode node = null;
		try {
			node = GOCache.getInstance().getGONode( goAcc );
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("errors.upload.microscopoy.goacc_exception", goAcc, e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		if (node == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("errors.upload.microscopoy.goacc", goAcc));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		ffImage.addGOTerm( node );
		node = null;
		
		return mapping.findForward("Success");
	}
}
