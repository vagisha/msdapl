/*
 * TestR3DImageExtract.java
 * Created on Apr 25, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.microscopy.R3DImageExtractor;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Apr 25, 2006
 */

public class TestR3DImageExtract extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		String r3d = request.getParameter("r3d");
		
		String filename = "C:\\Documents and Settings\\Administrator\\Desktop\\Bir1Ndc10\\" + r3d;
		File file = new File( filename );
		String imageToView = (String)request.getParameter("num");
		
		InputStream is = new FileInputStream( file );
		R3DImageExtractor ext = new R3DImageExtractor();
		RenderedImage[] images = ext.getImages( is );
		is.close();
		
		// test writing these out as TIFFs
		String directory = "C:\\";
		for (int i = 0; i < images.length; i++) {
			
			ImageIO.write( images[i], "tiff", new File( directory + "\\test" + i + ".tiff") );
			
		}
		
		
		request.setAttribute("image", images[Integer.parseInt(imageToView)]);
		
		return mapping.findForward("Success");
	}
}
