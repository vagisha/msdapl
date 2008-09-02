/*
 * UploadedSelectedRegion.java
 * Created on Jun 5, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.awt.image.BufferedImage;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 5, 2006
 */

public class UploadedSelectedRegion {

	
	
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the image.
	 */
	public BufferedImage getImage() {
		return image;
	}
	/**
	 * @param image The image to set.
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	private BufferedImage image;
	private String id;
	
}
