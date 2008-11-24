package org.yeastrc.www.proteinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.infer.InferredProtein;

public class ProteinDetailsAjaxAction extends Action {

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

        int nrseqProtId = 0;
        try {nrseqProtId = Integer.parseInt(request.getParameter("nrseqProtId"));}
        catch(NumberFormatException e) {}

        if(nrseqProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Cluster ID: "+nrseqProtId+"</b>");
            return null;
        }

        System.out.println("Got request for nrseq protein ID: "+nrseqProtId+" of protein inference run: "+pinferId);

        InferredProtein<SequestSpectrumMatch> iProt = ProteinferLoader.getInferredProtein(pinferId, nrseqProtId);
        request.setAttribute("inferredProtein", iProt);
        List<ProteinferProtein> groupProteins = ProteinferLoader.getGroupProteins(pinferId, iProt.getProteinGroupId());
        if(groupProteins.size() == 1)
            groupProteins.clear();
        else {
            Iterator<ProteinferProtein> protIter = groupProteins.iterator();
            while(protIter.hasNext()) {
                ProteinferProtein prot = protIter.next();
                if(prot.getNrseqProteinId() == iProt.getProteinId()) {
                    protIter.remove();
                    break;
                }
            }
        }
        request.setAttribute("groupProteins", groupProteins);
        request.setAttribute("nrseqProtId", nrseqProtId);
        request.setAttribute("pinferId", pinferId);
        
        return mapping.findForward("Success");
    }
}
