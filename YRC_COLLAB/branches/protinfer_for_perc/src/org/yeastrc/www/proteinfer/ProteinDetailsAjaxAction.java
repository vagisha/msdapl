package org.yeastrc.www.proteinfer;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideIonWSpectra;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

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

        System.out.println("Got request for protien inference protein ID: "+pinferProtId+" of protein inference run: "+pinferId);


        
        // get the protein 
        WIdPickerProtein iProt = IdPickerResultsLoader.getIdPickerProtein(pinferId, pinferProtId);
        request.setAttribute("protein", iProt);
        
        // get other proteins in this group
        List<WIdPickerProtein> groupProteins = IdPickerResultsLoader.getGroupProteins(pinferId, iProt.getProtein().getGroupId());
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
        
        // We will return all the filtered search hits for each peptide ion
        // First we need to find out the search program used (Sequest, ProLuCID etc. ) so 
        // that we can query the appropriate tables. 
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferRun run = factory.getProteinferRunDao().getProteinferRun(pinferId);
        int runSearchId = run.getInputSummaryList().get(0).getRunSearchId();
        DAOFactory msDaoFactory = DAOFactory.instance();
        SearchProgram searchProgram = msDaoFactory.getMsRunSearchDAO().loadSearchProgramForRunSearch(runSearchId);
        
        
        if(searchProgram == SearchProgram.SEQUEST || searchProgram == SearchProgram.EE_NORM_SEQUEST) {
            // load sequest results
            request.setAttribute("searchProgram", "sequest");
            List<WIdPickerPeptideIonWSpectra<SequestSearchResult>> psmList = 
                    IdPickerResultsLoader.getPeptideIonsWithSequestResults(iProt.getProtein().getId());
            request.setAttribute("ionList", psmList);
        }
        
        else if (searchProgram == SearchProgram.PROLUCID) {
            // load ProLuCID results
            request.setAttribute("searchProgram", "prolucid");
            List<WIdPickerPeptideIonWSpectra<ProlucidSearchResult>> psmList = 
                    IdPickerResultsLoader.getPeptideIonsWithProlucidResults(iProt.getProtein().getId());
            request.setAttribute("ionList", psmList);
        }
        
        
        request.setAttribute("pinferProtId", pinferProtId);
        request.setAttribute("pinferId", pinferId);
        
        return mapping.findForward("Success");
    }
}
