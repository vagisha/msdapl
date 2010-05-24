/**
 * PhiliusResultsAction.java
 * @author Vagisha Sharma
 * Feb 2, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.proteinfer.ProteinSequenceHtmlBuilder;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PhiliusResultsAjaxAction extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
        	response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // get the protein inference protein ID
        int nrseqProteinId = 0;
        try {nrseqProteinId = Integer.parseInt((String)request.getParameter("nrseqProteinId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid nrseq protein ID
        // return an error.
        if(nrseqProteinId <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid protein ID: "+nrseqProteinId);
            return null;
        }
        
        // the token for the Philius job
        int philiusToken = 0;
        try {philiusToken = Integer.parseInt(request.getParameter("philiusToken"));}
        catch(NumberFormatException e){};
        if(philiusToken <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid Philius job token in request: "+philiusToken);
            return null;
        }
        
        // check if the job is done; otherwise tell the requester to wait
        if(!jobDone(philiusToken)) {
        	response.setContentType("text/html");
            response.getWriter().write("WAIT");
            return null;
        }
        else {
        	
        	// Get the covered peptide sequences for this protein, if available
            Set<String>peptideSequences = (Set<String>) request.getAttribute("peptides");
            if(peptideSequences == null) {
            	peptideSequences = new HashSet<String>(0);
            }
            
            
            PhiliusSequenceAnnotationWS result = getResult(philiusToken);

            request.setAttribute("philiusAnnotation", result);
            String html = "";
            if(!(result.getSegments() == null || result.getSegments().size() == 0)) {
            	html = PhiliusSequenceHtmlFormatter.getInstance().format(result, peptideSequences);
            	request.setAttribute("sequenceHtml", html);
            }
            else {
            	html = ProteinSequenceHtmlBuilder.getInstance().build(result.getSequence(), peptideSequences);
            }
            request.setAttribute("sequenceHtml", html);
        	
        	request.setAttribute( "philiusmap", PhiliusImageMapMaker.getInstance().getImageMap(result));
        	
    		// set the result in the session for future use.  Will be needed for building the 
    		// Philius graphic
        	PhiliusResult pres = new PhiliusResult();
            pres.setAnnotation(result);
            pres.setCoveredSequences(new ArrayList<String>(peptideSequences));
    		request.getSession().setAttribute( "philiusResult", pres);
    		
    		// hack to prevent caching of philius image
    		request.setAttribute("philiusToken", philiusToken);
            return mapping.findForward("Success");
        }
        
        
    }
    
    private boolean jobDone(int philiusToken) throws PhiliusWSException_Exception {
    	PhiliusPredictorService service = new PhiliusPredictorService();
    	PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
    	return port.isJobDone(philiusToken);
    }
    
    private PhiliusSequenceAnnotationWS getResult(int philiusToken) throws PhiliusWSException_Exception {
        
        PhiliusSequenceAnnotationWS psa = null;
        PhiliusPredictorService service = new PhiliusPredictorService();
        PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
        psa = port.getResults(philiusToken);

        return psa;
    }
}
