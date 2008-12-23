/*
 * UploadYatesDataAction.java
 * Created on Oct 12, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.nr_seq.NRDatabaseUtils;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import java.lang.reflect.Method;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 12, 2004
 */

public class UploadYatesAction extends Action {

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
		
		String group = ((UploadYatesForm)form).getGroup();
		int projectID = ((UploadYatesForm)form).getProjectID();
		
		// Restrict access to administrators
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), group) &&
		  !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("adminHome");
		}
		
		Date runDate = null;
		String baitDesc = null;
		int targetSpecies = 0;
		String comments = null;
		String directoryName = null;
		
		//try to save up to 10 jobs from the form into the database
		for (int i = 1; i <= 10; i++) {

			// dynamically call these methods, so I don't ahve to test for 10 different states
			Method runDateM = UploadYatesForm.class.getMethod( "getRunDate" + i, new Class[] { } );
			Method baitDescM = UploadYatesForm.class.getMethod( "getBaitDesc" + i, new Class[] { } );
			Method speciesM = UploadYatesForm.class.getMethod( "getTargetSpecies" + i, new Class[] { } );
			Method commentsM = UploadYatesForm.class.getMethod( "getComments" + i, new Class[] { } );
			Method directoryM = UploadYatesForm.class.getMethod( "getDirectoryName" + i, new Class[] { } );
			
			runDate = (Date)runDateM.invoke( (UploadYatesForm)form, new Object[] { } );
			baitDesc = (String)baitDescM.invoke( (UploadYatesForm)form, new Object[] { } );
			targetSpecies = (Integer)speciesM.invoke( (UploadYatesForm)form, new Object[] { } );
			comments = (String)commentsM.invoke( (UploadYatesForm)form, new Object[] { } );
			directoryName = (String)directoryM.invoke( (UploadYatesForm)form, new Object[] { } );

			if (directoryName == null || directoryName.equals( "" ) )
				continue;
			
			MSUploadJobSaver jobSaver = new MSUploadJobSaver();
			
			jobSaver.setProjectID( projectID );
			jobSaver.setRunDate( runDate );
			jobSaver.setBaitDescription( baitDesc );
			jobSaver.setTargetSpecies( targetSpecies );
			jobSaver.setComments( comments );
			jobSaver.setServerDirectory( directoryName );
			
			if (group.equals( Projects.YATES ))
				jobSaver.setGroupID( 0 );
			else
				jobSaver.setGroupID( 1 );
			
			
			jobSaver.setSubmitter( user.getID() );
	
			
			// Attempt to find the exact protein they're talking about for the bait
			Species tSpecies = new Species();
			tSpecies.setId(targetSpecies);
			try {
				NRProtein baitProtein = NRDatabaseUtils.getInstance().findProteinByName(baitDesc, tSpecies);
				if (baitProtein != null)
					jobSaver.setBaitProtein( baitProtein.getId() );
			} catch (Exception e) { ; }
			
	
			
			try {
	
				// Save data to the queue database
				jobSaver.savetoDatabase();
				
			} catch (Exception e) {
				ActionErrors errors = new ActionErrors();
				errors.add("upload", new ActionMessage("error.upload.saveerror", e.getMessage()));
				saveErrors (request, errors );
				return mapping.findForward("Failure");
			}
			
			

		}//end for loop
		
		request.setAttribute( "queued", new Boolean( true ) );
		
		// Kick it to the view page
		return mapping.findForward( "Success" );

	}

}