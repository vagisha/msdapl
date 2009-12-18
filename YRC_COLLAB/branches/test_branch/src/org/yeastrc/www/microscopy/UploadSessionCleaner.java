/*
 * UploadSessionCleaner.java
 * Created on Jun 21, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import javax.servlet.http.HttpSession;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class UploadSessionCleaner {

	// private constructor
	private UploadSessionCleaner() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static UploadSessionCleaner getInstance() {
		return new UploadSessionCleaner();
	}
	
	public void cleanUploadSession( HttpSession session ) {
		if (session == null) return;
		
		
		
	}
	
}
