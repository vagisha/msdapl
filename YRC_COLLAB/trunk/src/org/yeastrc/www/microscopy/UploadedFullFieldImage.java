/*
 * UploadedFullFieldImage.java
 * Created on Jun 23, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.microscopy.FullFieldImage;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 23, 2006
 */

public class UploadedFullFieldImage {
	
	// private constructor
	private UploadedFullFieldImage() { }
	
	private static int LOAD_COUNT = 0;

	/**
	 * Get an instance of this class
	 * @return
	 */
	public static UploadedFullFieldImage getInstance() {
		UploadedFullFieldImage uffi = new UploadedFullFieldImage();
		
		UploadedFullFieldImage.LOAD_COUNT += 1;
		uffi.setIdCode( "UFFI-" + uffi.hashCode() + "-" + LOAD_COUNT);
		return uffi;
	}

	/**
	 * Get an instance of this class w/ the fupplised FullFieldImage set
	 * @param ffi
	 * @return
	 */
	public static UploadedFullFieldImage getInstance( FullFieldImage ffi ) {
		UploadedFullFieldImage uffi = UploadedFullFieldImage.getInstance();
		uffi.setFfImage( ffi );
		return uffi;
	}
	
	/**
	 * Add an SR image to this FF image
	 * @param sr
	 */
	public void addSRImage(UploadedSelectedRegion sr) {
		if (this.srImages == null)
			this.srImages = new ArrayList();
		
		this.srImages.add( sr );
	}

	/**
	 * Add a GO node to this FF image
	 * @param node
	 */
	public void addGOTerm( GONode node ) {
		if (this.goTerms == null)
			this.goTerms = new ArrayList();
		
		this.goTerms.add( node );
	}
	
	/**
	 * @return Returns the ffImage.
	 */
	public FullFieldImage getFfImage() {
		return ffImage;
	}
	/**
	 * @param ffImage The ffImage to set.
	 */
	public void setFfImage(FullFieldImage ffImage) {
		this.ffImage = ffImage;
	}
	/**
	 * @return Returns the srImages.
	 */
	public List getSrImages() {
		return srImages;
	}
	/**
	 * @param srImages The srImages to set.
	 */
	public void setSrImages(List srImages) {
		this.srImages = srImages;
	}

	


	/**
	 * @return Returns the goTerms.
	 */
	public List getGoTerms() {
		return goTerms;
	}
	/**
	 * @param goTerms The goTerms to set.
	 */
	public void setGoTerms(List goTerms) {
		this.goTerms = goTerms;
	}
	/**
	 * @return Returns the idCode.
	 */
	public String getIdCode() {
		return idCode;
	}
	/**
	 * @param idCode The idCode to set.
	 */
	private void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	private FullFieldImage ffImage;
	private List srImages;
	private List goTerms;
	private String idCode;
	
}
