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
import java.util.HashSet;
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
        output.setNumInputProteins(input.getProteinIds().size());
        output.setNumInputSpeciesProteins(speciesProteins.size());
        if(input.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
        	output.setGoDomainName("Biological Process");
        else if(input.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
        	output.setGoDomainName("Cellular Component");
        else if(input.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
        	output.setGoDomainName("Molecular Function");
        
        getEnrichedTerms(output, speciesProteins, input.getGoAspect());
        return output;
    }
    
    private static void getEnrichedTerms(GOEnrichmentOutput output, List<Integer> proteinIds, int goAspect) throws Exception {
        
        int numSpeciesAnnotatedProteins = totalAnnotatedProteinCount(output.getSpeciesId(), goAspect);
        log.info("Total number of proteins annotated with "+goAspect+": "+numSpeciesAnnotatedProteins);
        
        output.setNumAllAnnotatedSpeciesProteins(numSpeciesAnnotatedProteins);
        
        double pValCutoff = output.getpValCutoff();
        int speciesId = output.getSpeciesId();
        
        // Get all the GO terms for our protein set
        Map<String, EnrichedGOTerm> goTerms = getAllGOTerms(speciesId, goAspect, proteinIds);
        
        List<EnrichedGOTerm> enrichedTerms = new ArrayList<EnrichedGOTerm>(goTerms.values());
        
        // get the number of proteins what had at least one GO annotation.
        Set<Integer> numInputAnnotatedSpeciesProteins = new HashSet<Integer>(proteinIds.size()*2);
        for(EnrichedGOTerm term: enrichedTerms) {
        	numInputAnnotatedSpeciesProteins.addAll(term.getProteins());
        }
        output.setNumInputAnnotatedSpeciesProteins(numInputAnnotatedSpeciesProteins.size());
        
        
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

        calculateEnrichment(enrichedTerms, numInputAnnotatedSpeciesProteins.size(), numSpeciesAnnotatedProteins);
        
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
        
        output.setEnrichedTerms(enrichedTerms);
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
