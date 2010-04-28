/**
 * ProteinProphetGOEnrichmentAction.java
 * @author Vagisha Sharma
 * Dec 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOEnrichmentCalculator;
import org.yeastrc.www.go.GOEnrichmentInput;
import org.yeastrc.www.go.GOEnrichmentOutput;
import org.yeastrc.www.go.GOEnrichmentTabular;


/**
 * 
 */
public class ProteinProphetGOEnrichmentAction extends Action {

 private static final Logger log = Logger.getLogger(ProteinProphetGOEnrichmentAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm) form;
        // get the protein inference id
        int pinferId = filterForm.getPinferId();
        
        long s = System.currentTimeMillis();
        
        int speciesId = filterForm.getSpeciesId();
        GOEnrichmentOutput enrichment = doGoEnrichmentAnalysis(pinferId, speciesId, filterForm);
        // Biological Process
        if(filterForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS) {
            GOEnrichmentTabular bpTabular = new GOEnrichmentTabular();
            bpTabular.setEnrichedTerms(enrichment.getBiologicalProcessEnriched());
            bpTabular.setTitle("Biological Process");
            bpTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedBiologicalProcess());
            bpTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("bioProcessTerms", bpTabular);
        }
        
        // Cellular Component
        if(filterForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT) {
            GOEnrichmentTabular ccTabular = new GOEnrichmentTabular();
            ccTabular.setEnrichedTerms(enrichment.getCellularComponentEnriched());
            ccTabular.setTitle("Cellular Component");
            ccTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedCellularComponent());
            ccTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("cellComponentTerms", ccTabular);
        }
        
        // Molecular Function
        if(filterForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION) {
            GOEnrichmentTabular mfTabular = new GOEnrichmentTabular();
            mfTabular.setEnrichedTerms(enrichment.getMolecularFunctionEnriched());
            mfTabular.setTitle("Molecular Function");
            mfTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedMolecularFunction());
            mfTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("molFunctionTerms", mfTabular);
        }
        
        request.setAttribute("enrichment", enrichment);
        request.setAttribute("pinferId", pinferId);
        request.setAttribute("species", Species.getInstance(speciesId));
        request.setAttribute("proteinProphetFilterForm", filterForm);
        request.setAttribute("showGoForm", true);
        request.setAttribute("goView", true);
        
        
        long e = System.currentTimeMillis();
        log.info("ProteinInferGOEnrichmentAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return mapping.findForward("Success");
    }
    
    private GOEnrichmentOutput doGoEnrichmentAnalysis(int pinferId, int speciesId, ProteinProphetFilterForm filterForm) throws Exception {
        
        ProteinProphetRun idpRun = ProteinferDAOFactory.instance().getProteinProphetRunDao().loadProteinferRun(pinferId);
        
        // Get the filtering criteria
        ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverageDouble());
        filterCriteria.setMaxCoverage(filterForm.getMaxCoverageDouble());
        filterCriteria.setMinMolecularWt(filterForm.getMinMolecularWtDouble());
        filterCriteria.setMaxMolecularWt(filterForm.getMaxMolecularWtDouble());
        filterCriteria.setMinPi(filterForm.getMinPiDouble());
        filterCriteria.setMaxPi(filterForm.getMaxPiDouble());
        filterCriteria.setNumPeptides(filterForm.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(filterForm.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(filterForm.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(filterForm.getMaxSpectrumMatchesInteger());
        filterCriteria.setMinGroupProbability(filterForm.getMinGroupProbabilityDouble());
        filterCriteria.setMaxGroupProbability(filterForm.getMaxGroupProbabilityDouble());
        filterCriteria.setMinProteinProbability(filterForm.getMinProteinProbabilityDouble());
        filterCriteria.setMaxProteinProbability(filterForm.getMaxProteinProbabilityDouble());
        filterCriteria.setExcludeIndistinGroups(filterForm.isExcludeIndistinProteinGroups());
        filterCriteria.setGroupProteins(filterForm.isJoinProphetGroupProteins());
        if(filterForm.isExcludeSubsumed())
            filterCriteria.setParsimoniousOnly();
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        filterCriteria.setDescriptionNotLike(filterForm.getDescriptionNotLike());
        filterCriteria.setPeptide(filterForm.getPeptide());
        filterCriteria.setExactPeptideMatch(filterForm.getExactPeptideMatch());
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria);
        log.info(proteinIds.size()+" proteins for GO enrichment analysis");
        List<Integer> nrseqIds = new ArrayList<Integer>(proteinIds.size());
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        for(int proteinId: proteinIds) {
            ProteinferProtein protein = protDao.loadProtein(proteinId);
            nrseqIds.add(protein.getNrseqProteinId());
        }
        
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        input.setProteinIds(nrseqIds);
        if(filterForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
            input.setUseBiologicalProcess(true);
        if(filterForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
            input.setUseCellularComponent(true);
        if(filterForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
            input.setUseMolecularFunction(true);
        input.setPValCutoff(Double.parseDouble(filterForm.getGoEnrichmentPVal()));
        
        GOEnrichmentOutput enrichment = GOEnrichmentCalculator.calculate(input);
        return enrichment;
        
    }
}
