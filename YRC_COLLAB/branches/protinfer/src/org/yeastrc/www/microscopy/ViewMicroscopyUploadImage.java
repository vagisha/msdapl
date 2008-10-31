/*
 * ViewTestImageAction.java
 * Created on Sep 14, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.RenderedImage;
import java.util.List;

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
 * @version Sep 14, 2005
 */

public class ViewMicroscopyUploadImage  extends Action {

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
		
		String idCode = request.getParameter("idCode");
		
		RenderedImage ri = null;

		    
		List ffImages = (List)request.getSession().getAttribute( "ffImages" );
		if (ffImages == null)
			return mapping.findForward( "Failure" );
		
		if ( request.getSession().getAttribute( "ffImageData" ) == null )
			throw new Exception(" Got null for image data... ");
		
		RenderedImage[] ffImageData = (RenderedImage[])request.getSession().getAttribute( "ffImageData" );

		for (int i = 0; i < ffImages.size(); i++) {
			if ( idCode.equals( ((UploadedFullFieldImage)ffImages.get( i )).getIdCode() ) ) {
				ri = ffImageData[ i ];
				break;
			}
		}		
		
	    request.setAttribute("image", ri);
	    
		return mapping.findForward("Success");
	}
}