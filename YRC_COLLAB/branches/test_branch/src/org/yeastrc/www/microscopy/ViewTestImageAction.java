/*
 * ViewTestImageAction.java
 * Created on Sep 14, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

//import java.awt.image.RenderedImage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
//import org.yeastrc.microscopy.ImageDataRetriever;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Sep 14, 2005
 */

public class ViewTestImageAction  extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		

		//RenderedImage ri = ImageDataRetriever.getInstance().getTestImage(Integer.parseInt(request.getParameter("id")));	    
	    
	    //request.setAttribute("image", ri);
		
		return mapping.findForward("Success");
	}
}