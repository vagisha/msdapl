package org.yeastrc.www.proteinfer;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIonForProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ProteinDetailsAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinDetailsAjaxAction.class);
    
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

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }

        int pinferProtId = 0;
        try {pinferProtId = Integer.parseInt(request.getParameter("pinferProtId"));}
        catch(NumberFormatException e) {}

        if(pinferProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein inference protein ID: "+pinferProtId+"</b>");
            return null;
        }

        // Get the peptide definition from the session, if present
        PeptideDefinition peptideDef = null;
        Integer pinferId_session = (Integer)request.getSession().getAttribute("pinferId");
        if(pinferId_session != null && pinferId_session == pinferId) {
            ProteinFilterCriteria filterCriteria = (ProteinFilterCriteria) request.getAttribute("pinferFilterCriteria");
            if(filterCriteria != null) {
                peptideDef = filterCriteria.getPeptideDefinition();
            }
        }
        if(peptideDef == null) peptideDef = new PeptideDefinition();
        
        System.out.println("Got request for protien inference protein ID: "+pinferProtId+" of protein inference run: "+pinferId);

        long s = System.currentTimeMillis();
        
        // get the protein 
        WIdPickerProtein iProt = IdPickerResultsLoader.getIdPickerProtein(pinferId, pinferProtId, peptideDef);
        request.setAttribute("protein", iProt);
        
        // get other proteins in this group
        List<WIdPickerProtein> groupProteins = IdPickerResultsLoader.getGroupProteins(pinferId, 
                                                    iProt.getProtein().getGroupId(), 
                                                    peptideDef);
        if(groupProteins.size() == 1)
            groupProteins.clear();
        else {
            Iterator<WIdPickerProtein> protIter = groupProteins.iterator();
            while(protIter.hasNext()) {
                WIdPickerProtein prot = protIter.next();
                if(prot.getProtein().getId() == iProt.getProtein().getId()) {
                    protIter.remove();
                    break;
                }
            }
        }
        request.setAttribute("groupProteins", groupProteins);
        
        // We will return the best filtered search hit for each peptide ion (along with terminal residues in the protein).
        List<WIdPickerIonForProtein> ionsWAllSpectra = IdPickerResultsLoader.getPeptideIonsForProtein(pinferId, pinferProtId);
        request.setAttribute("ionList", ionsWAllSpectra);
        
        
        request.setAttribute("pinferProtId", pinferProtId);
        request.setAttribute("pinferId", pinferId);
        
        
        IdPickerRun run = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        request.setAttribute("protInferProgram", run.getProgram().name());
        request.setAttribute("inputGenerator", run.getInputGenerator().name());
        
        long e = System.currentTimeMillis();
        log.info("Total time (ProteinDetailsAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        return mapping.findForward("Success");
    }
}
