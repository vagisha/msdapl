/*
 * UploadMicroscopyAction.java
 * Created on May 26, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.FullFieldImage;
import org.yeastrc.microscopy.R3DImageExtractor;
import org.yeastrc.microscopy.R3DLogFileParser;
import org.yeastrc.nr_seq.NRDatabaseUtils;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NR_NCBIProteinSearcher;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 26, 2006
 */

public class UploadMicroscopyAction extends Action {

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

		// Restrict access to administrators
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), Projects.MICROSCOPY) &&
		  !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("adminHome");
		}
		
		// Attempt to look up the proteins here, if we can't find them all, kick to error
		// Get the protein ID for the tagged protein entered
		String orf1 = ((UploadMicroscopyForm)form).getOrf1();
		String orf2 = ((UploadMicroscopyForm)form).getOrf2();
		String orf3 = ((UploadMicroscopyForm)form).getOrf3();

		NRProtein bait1 = null;
		NRProtein bait2 = null;
		NRProtein bait3 = null;
		
		try {
			
			// find the protein for orf1
			if (orf1 != null && orf1.startsWith("gi|"))
				bait1 = NR_NCBIProteinSearcher.getInstance().getProteinFromNCBI(orf1).getProtein();
			else
				bait1 = NRDatabaseUtils.getInstance().findProteinByName(orf1, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
		
			// find the protein for orf2
			if (orf2 != null && !orf2.equals("")) {
				if (orf2.startsWith("gi|"))
					bait2 = NR_NCBIProteinSearcher.getInstance().getProteinFromNCBI(orf2).getProtein();
				else
					bait2 = NRDatabaseUtils.getInstance().findProteinByName(orf2, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );				
			}
			
			// find the protein for orf3
			if (orf3 != null && !orf3.equals("")) {
				if (orf3.startsWith("gi|"))
					bait3 = NR_NCBIProteinSearcher.getInstance().getProteinFromNCBI(orf2).getProtein();
				else
					bait3 = NRDatabaseUtils.getInstance().findProteinByName(orf2, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );				
			}			
			
		} catch (Exception e) { ; }
		
		
		// we were unable to find their intended protein, toss back an error
		if (bait1 == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.upload.microscopy.invalidproteinname", orf1) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		if (orf2 != null && !orf2.equals( "" ) && bait2 == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.upload.microscopy.invalidproteinname", orf2) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		if (orf3 != null && !orf3.equals( "" ) && bait3 == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.upload.microscopy.invalidproteinname", orf3) );
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		
		// Set up the Experiment object and populate it based on form input
		Experiment experiment = Experiment.getInstance();
		experiment.setProjectID( ((UploadMicroscopyForm)form).getProjectID() );
		experiment.setExperimentDate( ((UploadMicroscopyForm)form).getExperimentDate() );
		experiment.setCellStatus( ((UploadMicroscopyForm)form).getCellStatus() );
		experiment.setCellTreatment( ((UploadMicroscopyForm)form).getCellTreatment() );
		experiment.setCellGrowthMedium( ((UploadMicroscopyForm)form).getCellGrowthMedium() );
		experiment.setCellGrowthTemperature( ((UploadMicroscopyForm)form).getCellGrowthTemp() );
		experiment.setOpticsDichroicMirror( ((UploadMicroscopyForm)form).getOpticsDichroicMirror() );
		experiment.setComments( ((UploadMicroscopyForm)form).getComments() );

		
		experiment.setBait1( bait1 );
		experiment.setTag1( ((UploadMicroscopyForm)form).getTag1() );
		
		if (bait2 != null) {
			experiment.setBait2( bait2 );
			experiment.setTag2( ((UploadMicroscopyForm)form).getTag2() );
		}
		if (bait3 != null) {
			experiment.setBait3( bait3 );
			experiment.setTag3( ((UploadMicroscopyForm)form).getTag3() );
		}
		
		
		// Save this experimental information to the session, won't save to
		// database until after final step
		request.getSession().setAttribute( "experiment", experiment );
		
		// Extract image meta information from R3D log file
		// and populate apporpriate FullFieldImage objects
		
		FormFile r3dLogFile = null;
		r3dLogFile = ((UploadMicroscopyForm)form).getR3dLog();
		
		
		try {
			R3DLogFileParser parser = R3DLogFileParser.getInstance();
			InputStream is = r3dLogFile.getInputStream();
			
			// Save the FullFieldImages to the session
			List ffImages = parser.parseR3DLogFile( is, experiment);
			List uploadedImages = new ArrayList( ffImages.size() );
			Iterator ffIter = ffImages.iterator();
			while (ffIter.hasNext()) {
				UploadedFullFieldImage uffi = UploadedFullFieldImage.getInstance( (FullFieldImage)ffIter.next() );
				uploadedImages.add( uffi );
			}
			request.getSession().setAttribute( "ffImages", uploadedImages );
			ffImages = null;
			
			is.close();
			
			// Save the Log file text to the experiment
			experiment.setLogText( new String( r3dLogFile.getFileData() ) );
			//request.getSession().setAttribute( "r3dLog", new String( r3dLogFile.getFileData() ) );
			
		} finally {
			
			// Make sure we try to remove this file off the disk, no matter what
			try {
				r3dLogFile.destroy();
			} catch (Exception e) { ; }
			
		}
		r3dLogFile = null;
		
		
		// Extract the R3D image data from the R3D file, then save the image data as TIFFs to the session
		// get the array of Images:
		FormFile r3dDataFile = null;
		r3dDataFile = ((UploadMicroscopyForm)form).getR3dFile();
		try {
			byte[] r3dBytes = r3dDataFile.getFileData();
			InputStream is = new ByteArrayInputStream(r3dBytes);
			R3DImageExtractor ext = new R3DImageExtractor();
			
			// save the image data to the session (it is a List of TIFF RenderedImages)
			request.getSession().setAttribute( "ffImageData", ext.getImages( is ) );			

			// Add the MERGED image to ffImages if it exists
			if ( ((Object[])(request.getSession().getAttribute( "ffImageData" ))).length == ((java.util.List)(request.getSession().getAttribute( "ffImages" ))).size() + 1) {
				// we have more image data than full field images parsed from the r3d log file,
				// assume the final image is a merged image and create the FullFieldImage object and add it to ffImages
				FullFieldImage timage = FullFieldImage.getInstance();
				timage.setEMFilter( "merged" );
				UploadedFullFieldImage uimage = UploadedFullFieldImage.getInstance( timage );
				
				// TODO: add logic here for producing accurate comments for this image
				//timage.setComments( "Merged image of the YFP (red channel), CFP (green channel), DIC (blue channel) filters." );
				((java.util.List)(request.getSession().getAttribute( "ffImages" ))).add( uimage );
				timage = null;
				uimage = null;
			}
			
			
			
			// save the r3d data to the session
			request.getSession().setAttribute( "r3dData", r3dBytes);
			
			is.close();
		} finally {
			
			// Make sure we try to remove this file off the disk, no matter what
			try {
				r3dDataFile.destroy();
			} catch (Exception e) { ; }			
		}
		r3dDataFile = null;
		
		// set the experiment in the full field images
		Iterator iter = ((java.util.List)(request.getSession().getAttribute( "ffImages" ))).iterator();
		while (iter.hasNext()) {
			UploadedFullFieldImage ffi = (UploadedFullFieldImage)(iter.next());

			ffi.getFfImage().setExperiment( experiment );
			
			//ffi.getEmissionProtein();
			//ffi.getEmissionTag();
		}
		
		return mapping.findForward("Success");
	}
}
