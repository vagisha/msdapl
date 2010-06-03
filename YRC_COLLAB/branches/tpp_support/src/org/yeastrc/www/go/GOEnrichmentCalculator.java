/**
 * GOEnrichmentCalculator.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrProtein;
import org.yeastrc.stats.StatUtils;

/**
 * 
 */
public class GOEnrichmentCalculator {

    private static final Logger log = Logger.getLogger(GOEnrichmentCalculator.class.getName());
    
    private GOEnrichmentCalculator() {}
    
    public static GOEnrichmentOutput calculate(GOEnrichmentInput input) throws Exception {
        
        List<Integer> speciesProteins = new ArrayList<Integer>(input.getProteinIds().size());
        
        int speciesId = 0;
        for(int nrseqProteinId: input.getProteinIds()) {
        	NrProtein protein = NrSeqLookupUtil.getNrProtein(nrseqProteinId);
        	
            // Make sure the speciesId is the same as the one we are interested in
            speciesId = protein.getSpeciesId();
            if(speciesId != input.getSpeciesId())
                continue;
            
            speciesProteins.add(nrseqProteinId);
        }
        
        log.info("Number of input proteins: "+input.getProteinIds().size()+"; Number of proteins with speciesID "+input.getSpeciesId()+": "+speciesProteins.size());
        
        GOEnrichmentOutput output = new GOEnrichmentOutput(input);
        output.setNumSpeciesProteins(speciesProteins.size());
        
        if(input.useBiologicalProcess()) {
            getEnrichedTerms(output, speciesProteins, GOUtils.BIOLOGICAL_PROCESS);
        }
        if(input.useCellularComponent()) {
            getEnrichedTerms(output, speciesProteins, GOUtils.CELLULAR_COMPONENT);
        }
        if(input.useMolecularFunction()) {
            getEnrichedTerms(output, speciesProteins, GOUtils.MOLECULAR_FUNCTION);
        }
        return output;
    }
    
    private static void getEnrichedTerms(GOEnrichmentOutput output, List<Integer> proteinIds, int goAspect) throws Exception {
        
        int totalAnnotatedProteins = totalAnnotatedProteinCount(output.getSpeciesId(), goAspect);
        log.info("Total number of proteins annotated with "+goAspect+": "+totalAnnotatedProteins);
        
        if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
            output.setTotalAnnotatedBiologicalProcess(totalAnnotatedProteins);
        else if(goAspect == GOUtils.CELLULAR_COMPONENT)
            output.setTotalAnnotatedCellularComponent(totalAnnotatedProteins);
        else if (goAspect == GOUtils.MOLECULAR_FUNCTION)
            output.setTotalAnnotatedMolecularFunction(totalAnnotatedProteins);
        
        
        double pValCutoff = output.getpValCutoff();
        int speciesId = output.getSpeciesId();
        
        if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
            output.setBiologicalProcessEnriched(getEnrichedTerms(proteinIds, pValCutoff, speciesId, goAspect, totalAnnotatedProteins));
        else if(goAspect == GOUtils.CELLULAR_COMPONENT)
            output.setCellularComponentEnriched(getEnrichedTerms(proteinIds, pValCutoff, speciesId, goAspect, totalAnnotatedProteins));
        else if (goAspect == GOUtils.MOLECULAR_FUNCTION)
            output.setMolecularFunctionEnriched(getEnrichedTerms(proteinIds, pValCutoff, speciesId, goAspect, totalAnnotatedProteins));
        
    }

    private static List<EnrichedGOTerm> getEnrichedTerms(List<Integer> proteinIds, double pValCutoff, int speciesId, int goAspect, int totalAnnotatedProteins) throws Exception {
        
        // Get all the GO terms for our protein set
        Map<String, EnrichedGOTerm> goTerms = getAllGOTerms(speciesId, goAspect, proteinIds);
        
        List<EnrichedGOTerm> enrichedTerms = new ArrayList<EnrichedGOTerm>(goTerms.values());
        Iterator<EnrichedGOTerm> iter = enrichedTerms.iterator();
        
        // get the root nodes
        GONode aspectRoot = GOUtils.getAspectRootNode(goAspect);
        GONode rootNode = GOUtils.getRootNode();
        
        // remove the root nodes
        while(iter.hasNext()) {
            
            EnrichedGOTerm term = iter.next();
            
            if(term.getGoNode().equals(aspectRoot))
                iter.remove();
            
            if(term.getGoNode().equals(rootNode))
                iter.remove();
        }

        
        calculateEnrichment(enrichedTerms, proteinIds.size(), totalAnnotatedProteins);
        
        // returns a list of GO terms enriched above the given cutoff
        iter = enrichedTerms.iterator();
        while(iter.hasNext()) {
            
            EnrichedGOTerm term = iter.next();
            if(term.getPValue() > pValCutoff) {
                iter.remove();
                continue;
            }
        }
        
        // sort by p-value
        Collections.sort(enrichedTerms);
        
        return enrichedTerms;
    }

    
    private static void calculateEnrichment(List<EnrichedGOTerm> enrichedTerms, int numProteinsInSet, int totalAnnotatedProteins) throws Exception {
        
        for(EnrichedGOTerm term: enrichedTerms) {
            term.setPValue(StatUtils.PScore(term.getNumAnnotatedProteins(), term.getTotalAnnotatedProteins(), numProteinsInSet, totalAnnotatedProteins));
        }
    }
    
    private static int totalAnnotatedProteinCount(int speciesId, int goAspect) throws Exception {
        if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
            return GOProteinCounter.getInstance().countAllBiologicalProcessProteins(speciesId);
        else if(goAspect == GOUtils.CELLULAR_COMPONENT)
            return GOProteinCounter.getInstance().countAllCellularComponentProteins(speciesId);
        else if (goAspect == GOUtils.MOLECULAR_FUNCTION)
            return GOProteinCounter.getInstance().countAllMolecularFunctionProteins(speciesId);
        else
            return 0;
    }

    
    private static Map<String, EnrichedGOTerm> getAllGOTerms(int speciesId, int goAspect, List<Integer> nrseqIds)
    throws Exception {

    	Map<String, EnrichedGOTerm> goTerms = new HashMap<String, EnrichedGOTerm>(); // unique GO terms

    	for(Integer nrseqId: nrseqIds) {

    		// Get a list of GO term annotations for this protein
    		Set<GONode> nodes = null;
    		try {
    			// setting the second argument (exact) to false should get us all terms for this protein
    			// This should include all ancestors of terms directly assigned to this protein.
    			nodes  = GoTermSearcher.getTermsForProtein(nrseqId, false, goAspect);
    		}
    		catch (Exception e) {
    			log.error("Could not get GO annotations for proteinID: "+nrseqId, e);
    		}

    		if(nodes != null) {
    			// Add the GO term to our map. 
    			// If the map already contains this term, add the proteinId to the term.
    			for(GONode node: nodes) {
    				EnrichedGOTerm term = goTerms.get(node.getAccession());
    				if(term == null) {
    					term = initEnrichedGOTerm(node, speciesId);
    					goTerms.put(node.getAccession(), term);
    				}
    				term.addProtein(nrseqId);
    			}
    		}
    	}
    	
    	return goTerms;
    }
    
    private static EnrichedGOTerm initEnrichedGOTerm(GONode node, int speciesId) throws Exception {
        int totalProteins = 0; // total proteins in the universe with this GO term
        totalProteins = GOProteinCounter.getInstance().countProteins(node, false, speciesId);
        return new EnrichedGOTerm(node, totalProteins);
    }
    
}
