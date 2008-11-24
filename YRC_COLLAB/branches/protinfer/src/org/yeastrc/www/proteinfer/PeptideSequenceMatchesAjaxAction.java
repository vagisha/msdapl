package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.SequestSpectrumMatch;

public class PeptideSequenceMatchesAjaxAction extends Action {

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
//            ActionErrors errors = new ActionErrors();
//            errors.add("username", new ActionMessage("error.login.notloggedin"));
//            saveErrors( request, errors );
//            return mapping.findForward("authenticate");
        }

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }

        int runSearchId = 0;
        try {runSearchId = Integer.parseInt(request.getParameter("runSearchId"));}
        catch(NumberFormatException e) {}

        if(runSearchId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid run search ID: "+runSearchId+"</b>");
            return null;
        }

        System.out.println("Got request for run search ID: "+runSearchId+" of protein inference run: "+pinferId);

        long s = System.currentTimeMillis();
        List<SequestSpectrumMatch> psmList = ProteinferLoader.getSpectrummatchesForRunSearch(pinferId, runSearchId);
        long e = System.currentTimeMillis();
        System.out.println("Time: "+((e-s)/1000.0)+" seconds");
        request.setAttribute("psmList", psmList);
        request.setAttribute("runSearchId", runSearchId);
        request.setAttribute("filename", psmList.get(0).getSearchSource().getFileName());
        
        // Go!
        return mapping.findForward("Success");
    }
    
   
    
}
