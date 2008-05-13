/*
 * ViewMicroscopySRUploadImage.java
 * Created on Jun 6, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 6, 2006
 */

public class ViewMicroscopySRUploadImage extends Action {

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
		
		String idCode = request.getParameter("idCode");
		String id = request.getParameter("id");
		String scaleStr = request.getParameter("scale");
		
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

		
		List srImages = ffImage.getSrImages();
		
		// try to get the scale modifier if it was supplied.
		double scale = 1.0;
		if (scaleStr != null) {
			try {
				scale = Double.parseDouble( scaleStr );
			} catch (Exception e) { ; }
		}

		iter = srImages.iterator();
		
		UploadedSelectedRegion usr = null;
		while (iter.hasNext()) {
			UploadedSelectedRegion tsr = (UploadedSelectedRegion)iter.next();
			if (tsr.getId().equals( id )) {
				usr = tsr;
				break;
			}
		}
		
		if (usr == null)
			return mapping.findForward( "Failure" );

		
		BufferedImage cImage = usr.getImage();
		if (scale != 1.0) {

			try {
				AffineTransform af = AffineTransform.getScaleInstance(scale, scale );
	
	
				Map hints = new HashMap();
				hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
				RenderingHints rh = new RenderingHints(hints);
	
				AffineTransformOp transform = new AffineTransformOp(af,rh);
	
				BufferedImage destImg =
				transform.createCompatibleDestImage(cImage, cImage.getColorModel());
				transform.filter(cImage, destImg);
				
				if (destImg != null)
					request.setAttribute( "image", destImg );
				else
					request.setAttribute( "image", cImage );
			} catch (Exception e) {
				request.setAttribute( "image", cImage );
			}

			
			
		} else {
			request.setAttribute( "image", cImage );
		}

		cImage = null;
		
		return mapping.findForward( "Success" );	
	}
}
