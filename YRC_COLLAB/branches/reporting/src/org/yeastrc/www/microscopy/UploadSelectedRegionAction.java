/*
 * UploadSelectedRegionAction.java
 * Created on Jun 5, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 5, 2006
 */

public class UploadSelectedRegionAction extends Action {

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
		
		String idCode = ((SelectedRegionForm)form).getIdCode();
		FormFile tiffImage = ((SelectedRegionForm)form).getSrImage();
		
		if (tiffImage == null || tiffImage.getFileSize() < 1)
			return mapping.findForward("Success");
		
		// find the full field image we're saving this sr too
		Iterator iter = ((java.util.List)(request.getSession().getAttribute( "ffImages" ))).iterator();
		UploadedFullFieldImage theImage = null;
		while (iter.hasNext()) {
			UploadedFullFieldImage ffi = (UploadedFullFieldImage)iter.next();
			if (idCode.equals( (ffi.getIdCode() ) )) {
				theImage = ffi;
				break;
			}
		}

		// couldn't find the ff image, just kick it back to the web page
		if (theImage == null)
			return mapping.findForward( "Success" );


		// Create a BufferedImage out of the uploaded tiff data
		// get the tiff image data
		byte[] tiffData = tiffImage.getFileData();
		
		// erase the file
		tiffImage.destroy();
		tiffImage = null;
		
		// create a BufferedImage from the tiff image bytes
		InputStream is = new ByteArrayInputStream( tiffData );
		BufferedImage image = null;
		
		try {
			image = ImageIO.read( is );
			is.close();
			is = null;
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.upload.microscopy.tifferror", e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Add this selected region to the list
		UploadedSelectedRegion usr = new UploadedSelectedRegion();
		usr.setImage( image );
		usr.setId( theImage.getIdCode() + "_" + usr.hashCode() + "-" + (new Date()).getTime());
		theImage.addSRImage( usr );

		// kick it
		return mapping.findForward( "Success" );	
	}
}
