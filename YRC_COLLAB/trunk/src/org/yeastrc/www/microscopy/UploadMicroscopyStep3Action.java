/*
 * UploadMicroscopyStep3Action.java
 * Created on Jun 5, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.microscopy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 5, 2006
 */

public class UploadMicroscopyStep3Action extends Action {

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
		
		// make sure we have all of the necessary information up to this point
		// if not, kick them back to the ORFAndTag forms
		
		Experiment experiment = (Experiment)request.getSession().getAttribute( "experiment" );
		
		// make sure we have at least one bait/tag listed
		if (experiment.getBait1() == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.upload.microscopy.nobait1"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		if (experiment.getTag1() == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.upload.microscopy.notag1"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		/*
		// if we have more than 2 images, then we have a YFP and CFP channel.  Make sure there is a 2nd orf/tag listed
		if ( ((List)(request.getSession().getAttribute( "ffImages" ))).size() > 2 ) {

			if (experiment.getBait2() == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("upload", new ActionMessage("error.upload.microscopy.nobait2"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			if (experiment.getTag2() == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("upload", new ActionMessage("error.upload.microscopy.notag2"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			
		}
		*/

		
		// we have everything we need now, kick it to the next step
		return mapping.findForward( "Success" );
		
	}
}
