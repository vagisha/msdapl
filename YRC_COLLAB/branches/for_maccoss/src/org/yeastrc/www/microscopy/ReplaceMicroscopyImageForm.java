/*
 * ReplaceMicroscopyImageForm.java
 * Created on Jun 2, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 2, 2006
 */

public class ReplaceMicroscopyImageForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		request.setAttribute("idCode", this.getIdCode());
		
		// to a simple check to make sure the uploaded image is a tiff
		if (!tiffImage.getFileName().endsWith(".tif") &&
			!tiffImage.getFileName().endsWith(".tiff")) {
			errors.add("upload", new ActionMessage("error.upload.microscopy.notatiff"));
		}
		
		return errors;
	}
	
	private FormFile tiffImage;
	private String idCode;
	
	
	
	/**
	 * @return Returns the eMFilter.
	 */
	public String getIdCode() {
		return idCode;
	}
	/**
	 * @param filter The eMFilter to set.
	 */
	public void setIdCode(String filter) {
		idCode = filter;
	}
	/**
	 * @return Returns the tiffImage.
	 */
	public FormFile getTiffImage() {
		return tiffImage;
	}
	/**
	 * @param tiffImage The tiffImage to set.
	 */
	public void setTiffImage(FormFile tiffImage) {
		this.tiffImage = tiffImage;
	}
}
