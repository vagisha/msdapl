/*
 * ReplaceMicroscopyImage.java
 * Created on Jun 2, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

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
 * @version Jun 2, 2006
 */

public class ReplaceMicroscopyImageAction extends Action {

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
		
		
		String idCode = ((ReplaceMicroscopyImageForm)form).getIdCode();
		FormFile tiffImage = ((ReplaceMicroscopyImageForm)form).getTiffImage();
		
		request.setAttribute("idCode", idCode);
		
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
		
		// make sure the uploaded image is 512x512
		int width = image.getWidth();
		int height = image.getHeight();
		
		// verify that it's the correct size
		if (width != 512 || height != 512) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.upload.microscopy.ffsize"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// save the BufferedImage to the full field images in this session
		RenderedImage[] ffImageData = (RenderedImage[])request.getSession().getAttribute( "ffImageData" );
		List ffImages = (List)request.getSession().getAttribute( "ffImages" );

		for ( int i = 0; i < ffImages.size(); i++ ) {
			if (idCode.equals( ((UploadedFullFieldImage)ffImages.get( i )).getIdCode() ) ) {
				ffImageData[ i ] = image;
				break;
			}
		}
		
		ffImageData = null;
		ffImages = null;
		
		
		return mapping.findForward( "Success" );
	}
}
