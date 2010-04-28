package org.yeastrc.www.proteinfer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.GOSearcher;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIonForProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.proteinProphet.ProteinProphetResultsLoader;
import org.yeastrc.www.proteinfer.proteinProphet.WProteinProphetIon;
import org.yeastrc.www.proteinfer.proteinProphet.WProteinProphetProtein;
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
        
        log.info("Got request for protien inference protein ID: "+pinferProtId+" of protein inference run: "+pinferId);

        long s = System.currentTimeMillis();
        
        
        ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(pinferId);
        request.setAttribute("protInferProgram", run.getProgram().name());
        request.setAttribute("inputGenerator", run.getInputGenerator().name());
        request.setAttribute("isIdPicker", ProteinInferenceProgram.isIdPicker(run.getProgram()));
        
        request.setAttribute("pinferProtId", pinferProtId);
        request.setAttribute("pinferId", pinferId);
        
        Set<String> peptideSequences = null;
        int nrseqProteinId = 0;
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
            // get the protein 
            WIdPickerProtein iProt = IdPickerResultsLoader.getIdPickerProtein(pinferId, pinferProtId, peptideDef);
            request.setAttribute("protein", iProt);
            
            // Gene Ontology information
            Map goterms = GOSearcher.getGONodes(iProt.getProteinListing());
    		
    		if ( ((Collection)goterms.get("P")).size() > 0)
    			request.setAttribute("processes", goterms.get("P"));

    		if ( ((Collection)goterms.get("C")).size() > 0)
    			request.setAttribute("components", goterms.get("C"));
    		
    		if ( ((Collection)goterms.get("F")).size() > 0)
    			request.setAttribute("functions", goterms.get("F"));

    		
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
            
            // Get the unique peptide sequences for this protein (for building the protein sequence HTML)
            peptideSequences = new HashSet<String>(iProt.getProtein().getPeptideCount());
            for(IdPickerPeptideBase peptide: iProt.getProtein().getPeptides()) {
                peptideSequences.add(peptide.getSequence());
            }
            nrseqProteinId = iProt.getProtein().getNrseqProteinId();
            
        }
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
            
            // get the protein 
            WProteinProphetProtein pProt = ProteinProphetResultsLoader.getWProteinProphetProtein(pinferId, pinferProtId, peptideDef);
            request.setAttribute("protein", pProt);
            
            // Gene Ontology information
            Map goterms = GOSearcher.getGONodes(pProt.getProteinListing());
    		
    		if ( ((Collection)goterms.get("P")).size() > 0)
    			request.setAttribute("processes", goterms.get("P"));

    		if ( ((Collection)goterms.get("C")).size() > 0)
    			request.setAttribute("components", goterms.get("C"));
    		
    		if ( ((Collection)goterms.get("F")).size() > 0)
    			request.setAttribute("functions", goterms.get("F"));

    		
    		
            // get other proteins in this group
            List<WProteinProphetProtein> groupProteins = ProteinProphetResultsLoader.getGroupProteins(pinferId, 
                    pProt.getProtein().getGroupId(), 
                    peptideDef);
            if(groupProteins.size() == 1)
                groupProteins.clear();
            else {
                Iterator<WProteinProphetProtein> protIter = groupProteins.iterator();
                while(protIter.hasNext()) {
                    WProteinProphetProtein prot = protIter.next();
                    if(prot.getProtein().getId() == pProt.getProtein().getId()) {
                        protIter.remove();
                        break;
                    }
                }
            }
            request.setAttribute("groupProteins", groupProteins);
            
            // is this protein subsumed
            if(pProt.getProtein().getSubsumed()) {
            	List<WProteinProphetProtein> subsumingProteins = ProteinProphetResultsLoader.getSubsumingProteins(pProt.getProtein().getId(), pinferId);
            	request.setAttribute("subsumingProteins", subsumingProteins);
            }
            
            // Any proteins this protein is subsuming
            List<WProteinProphetProtein> subsumedProteins = ProteinProphetResultsLoader.getSubsumedProteins(pProt.getProtein().getId(), pinferId);
            if(subsumedProteins.size() > 0) {
            	request.setAttribute("subsumedProteins", subsumedProteins);
            }

            // We will return the best filtered search hit for each peptide ion (along with terminal residues in the protein).
            List<WProteinProphetIon> ionsWAllSpectra = ProteinProphetResultsLoader.getPeptideIonsForProtein(pinferId, pinferProtId);
            request.setAttribute("ionList", ionsWAllSpectra);
            
            // Get the unique peptide sequences for this protein (for building the protein sequence HTML)
            peptideSequences = new HashSet<String>(pProt.getProtein().getPeptideCount());
            for(ProteinProphetProteinPeptide peptide: pProt.getProtein().getPeptides()) {
                peptideSequences.add(peptide.getSequence());
            }
            nrseqProteinId = pProt.getProtein().getNrseqProteinId();
        }
        
        // Get the sequence for this protein
        String sequence = NrSeqLookupUtil.getProteinSequence(nrseqProteinId);
        String proteinSequenceHtml = ProteinSequenceHtmlBuilder.getInstance().build(sequence, peptideSequences);
        request.setAttribute("proteinSequenceHtml", proteinSequenceHtml);
        request.setAttribute("proteinSequence", sequence);
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (ProteinDetailsAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram()))
        	return mapping.findForward("SuccessIdPicker");
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET)
        	return mapping.findForward("SuccessProphet");
        else {
        	response.setContentType("text/html");
            response.getWriter().write("<b>Unrecognized Protein Inference program: "+run.getProgramString()+"</b>");
            return null;
        }
    }
}
