/**
 * GoEnrichmentAction.java
 * @author Vagisha Sharma
 * Jun 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
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
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOEnrichmentCalculator;
import org.yeastrc.www.go.GOEnrichmentInput;
import org.yeastrc.www.go.GOEnrichmentOutput;

/**
 * 
 */
public class CompareGOEnrichmentAction extends Action {

    private static final Logger log = Logger.getLogger(CompareGOEnrichmentAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        long s = System.currentTimeMillis();
        
        ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
        if(comparison == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        request.setAttribute("comparison", comparison);
        request.setAttribute("goEnrichmentView", true);
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison form not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
       
        GOEnrichmentOutput enrichment = doGoEnrichmentAnalysis(comparison, myForm);
        // Biological Process
        if(myForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getBiologicalProcessEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular bpTabular = new GOEnrichmentTabular();
            bpTabular.setEnrichedTerms(enrichment.getBiologicalProcessEnriched());
            bpTabular.setTitle("Biological Process");
            bpTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedBiologicalProcess());
            bpTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("bioProcessTerms", bpTabular);
        }
        
        // Cellular Component
        if(myForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getCellularComponentEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular ccTabular = new GOEnrichmentTabular();
            ccTabular.setEnrichedTerms(enrichment.getCellularComponentEnriched());
            ccTabular.setTitle("Cellular Component");
            ccTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedCellularComponent());
            ccTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("cellComponentTerms", ccTabular);
        }
        
        // Molecular Function
        if(myForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getMolecularFunctionEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular mfTabular = new GOEnrichmentTabular();
            mfTabular.setEnrichedTerms(enrichment.getMolecularFunctionEnriched());
            mfTabular.setTitle("Molecular Function");
            mfTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedMolecularFunction());
            mfTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("molFunctionTerms", mfTabular);
        }
        
        
        request.setAttribute("enrichment", enrichment);
        request.setAttribute("species", Species.getInstance(myForm.getSpeciesId()));
        
        
        long e = System.currentTimeMillis();
        log.info("CompareGOEnrichmentAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return mapping.findForward("Success");
    }
    
    private GOEnrichmentOutput doGoEnrichmentAnalysis(ProteinComparisonDataset comparison, ProteinSetComparisonForm myForm) throws Exception {
        
        int speciesId = myForm.getSpeciesId();
        
        List<Integer> nrseqIds = new ArrayList<Integer>(comparison.getProteins().size());
        log.info(nrseqIds.size()+" proteins for GO enrichment analysis");
        for(ComparisonProtein protein: comparison.getProteins()) {
            nrseqIds.add(protein.getNrseqId());
        }
        
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        if(myForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
            input.setUseBiologicalProcess(true);
        if(myForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
            input.setUseCellularComponent(true);
        if(myForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
            input.setUseMolecularFunction(true);
        
        input.setPValCutoff(Double.parseDouble(myForm.getGoEnrichmentPVal()));
        input.setProteinIds(nrseqIds);
        
        GOEnrichmentOutput enrichment = GOEnrichmentCalculator.calculate(input);
        return enrichment;
    }
}
