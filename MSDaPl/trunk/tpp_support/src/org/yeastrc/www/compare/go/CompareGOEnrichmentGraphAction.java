/**
 * CompareGOEnrichmentGraphAction.java
 * @author Vagisha Sharma
 * Jun 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.go;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.EnrichedGOTerm;
import org.yeastrc.www.go.EnrichedGOTermsGraphCreator;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class CompareGOEnrichmentGraphAction extends Action {

private static final Logger log = Logger.getLogger(CompareGOEnrichmentGraphAction.class.getName());
    
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
        
        long s = System.currentTimeMillis();
        
        List<EnrichedGOTerm> enrichedTerms = (List<EnrichedGOTerm>) request.getAttribute("enrichedTerms");
        if(enrichedTerms == null || enrichedTerms.size() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No enriched terms found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        EnrichedGOTermsGraphCreator graphCreator = new EnrichedGOTermsGraphCreator(enrichedTerms);
        BufferedImage bi = graphCreator.createGraph();
        
        graphCreator = null;
        System.gc();
        
        request.setAttribute("image", bi);
        
        long e = System.currentTimeMillis();
        log.info("CompareGOEnrichmentGraphAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return mapping.findForward("Success");
    }
}
