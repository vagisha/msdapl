package org.yeastrc.www.proteinfer;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;

public class FilterProteinferResultsAjaxAction extends Action {

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

        int peptideCnt = 0;
        try {peptideCnt = Integer.parseInt(request.getParameter("peptideCnt"));}
        catch(NumberFormatException e) {}

        if(peptideCnt == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid peptide count: "+peptideCnt+"</b>");
            return null;
        }
        
        int uniqPeptideCnt = -1;
        try {uniqPeptideCnt = Integer.parseInt(request.getParameter("uniqPeptideCnt"));}
        catch(NumberFormatException e) {}

        if(uniqPeptideCnt == -1) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid unique peptide count: "+uniqPeptideCnt+"</b>");
            return null;
        }
        
        int spectraCnt = 0;
        try {spectraCnt = Integer.parseInt(request.getParameter("spectraCnt"));}
        catch(NumberFormatException e) {}

        if(spectraCnt == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid spectra count: "+spectraCnt+"</b>");
            return null;
        }
        
        int coverage = -1;
        try {coverage = Integer.parseInt(request.getParameter("coverage"));}
        catch(NumberFormatException e) {}

        if(coverage == -1) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid value for coverage: "+coverage+"</b>");
            return null;
        }
        

        System.out.println("Got request to filter results for protein inference run: "+pinferId+
                            "\n\tPeptide Count: "+peptideCnt+
                            "\n\tUnique Peptide Count: "+uniqPeptideCnt+
                            "\n\tSpectra Count: "+spectraCnt+
                            "\n\tCoverage: "+coverage);

        List<IdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinferProteinGroups(pinferId);
        
        // filter
        Iterator<IdPickerProteinGroup> iter = proteinGroups.iterator();
        while(iter.hasNext()) {
            IdPickerProteinGroup grp = iter.next();
            if(grp.getMatchingPeptideCount() < peptideCnt) {
                iter.remove();
                continue;
            }
            else if (grp.getUniqMatchingPeptideCount() < uniqPeptideCnt) {
                iter.remove();
                continue;
            }
            else if(grp.getSpectrumCount() < spectraCnt) {
                iter.remove();
                continue;
            }
            
            Iterator<ProteinferProtein> protIter = grp.getProteins().iterator();
            while(protIter.hasNext()) {
                BaseProteinferProtein<T> prot = protIter.next();
                if(prot.getCoverage() < coverage) {
                    protIter.remove();
                }
            }
            if(grp.getProteinCount() == 0) {
                iter.remove();
            }
        }
        
        request.setAttribute("proteinGroups", proteinGroups);
        return mapping.findForward("Success");
    }
}
