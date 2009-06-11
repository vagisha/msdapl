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
import org.yeastrc.bio.go.GOSearcher;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.stats.StatUtils;

/**
 * 
 */
public class GOEnrichmentCalculator {

    private static final Logger log = Logger.getLogger(GOEnrichmentCalculator.class.getName());
    
    private GOEnrichmentCalculator() {}
    
    public static GOEnrichmentOutput calculate(GOEnrichmentInput input) throws Exception {
        
        List<NRProtein> nrProteins = new ArrayList<NRProtein>(input.getProteinIds().size());
        
        NRProteinFactory proteinFactory = NRProteinFactory.getInstance();
        
        int speciesId = 0;
        for(int nrseqProteinId: input.getProteinIds()) {
            NRProtein protein = (NRProtein) proteinFactory.getProtein(nrseqProteinId);
            
            // Make sure the speciesId is the same as the one we are interested in
            speciesId = protein.getSpecies().getId();
            if(speciesId != input.getSpeciesId())
                continue;
            
            nrProteins.add(protein);
        }
        
        log.info("Number of input proteins: "+input.getProteinIds().size()+"; Number of proteins with speciesID "+speciesId+": "+nrProteins.size());
        
        GOEnrichmentOutput output = new GOEnrichmentOutput(input);
        output.setNumSpeciesProteins(nrProteins.size());
        
        if(input.useBiologicalProcess()) {
            getEnrichedTerms(output, nrProteins, GOUtils.BIOLOGICAL_PROCESS);
        }
        if(input.useCellularComponent()) {
            getEnrichedTerms(output, nrProteins, GOUtils.CELLULAR_COMPONENT);
        }
        if(input.useMolecularFunction()) {
            getEnrichedTerms(output, nrProteins, GOUtils.MOLECULAR_FUNCTION);
        }
        return output;
    }
    
    public static List<EnrichedGOTerm> getEnrichedBiologicalProcessTerms(List<NRProtein> proteins, double pValCutoff, int speciesId) throws Exception {
        return getEnrichedTerms(proteins, pValCutoff, speciesId, GOUtils.BIOLOGICAL_PROCESS);
    }
    
    public static List<EnrichedGOTerm> getEnrichedCellularComponentTerms(List<NRProtein> proteins, double pValCutoff, int speciesId) throws Exception {
        return getEnrichedTerms(proteins, pValCutoff, speciesId, GOUtils.CELLULAR_COMPONENT);
    }
    
    public static List<EnrichedGOTerm> getEnrichedMolecularFunctionTerms(List<NRProtein> proteins, double pValCutoff, int speciesId) throws Exception {
        return getEnrichedTerms(proteins, pValCutoff, speciesId, GOUtils.MOLECULAR_FUNCTION);
    }
    
    
    private static void getEnrichedTerms(GOEnrichmentOutput output, List<NRProtein> proteins, int goAspect) throws Exception {
        
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
            output.setBiologicalProcessEnriched(getEnrichedTerms(proteins, pValCutoff, speciesId, goAspect));
        else if(goAspect == GOUtils.CELLULAR_COMPONENT)
            output.setCellularComponentEnriched(getEnrichedTerms(proteins, pValCutoff, speciesId, goAspect));
        else if (goAspect == GOUtils.MOLECULAR_FUNCTION)
            output.setMolecularFunctionEnriched(getEnrichedTerms(proteins, pValCutoff, speciesId, goAspect));
        
    }

    private static List<EnrichedGOTerm> getEnrichedTerms(List<NRProtein> proteins, double pValCutoff, int speciesId, int goAspect) throws Exception {
        
        int totalAnnotatedProteins = totalAnnotatedProteinCount(speciesId, goAspect);
        log.info("Total number of proteins annotated with "+goAspect+": "+totalAnnotatedProteins);
        
        return getEnrichedTerms(proteins, pValCutoff, speciesId, goAspect, totalAnnotatedProteins);
    }

    private static List<EnrichedGOTerm> getEnrichedTerms(List<NRProtein> proteins, double pValCutoff, int speciesId, int goAspect, int totalAnnotatedProteins) throws Exception {
        
        // Get all the GO terms for our protein set
        Map<String, EnrichedGOTerm> goTerms = getAllGOTerms(proteins,speciesId, goAspect);
        
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

        
        calculateEnrichment(enrichedTerms, proteins.size(), totalAnnotatedProteins);
        
        // returns a list of GO terms enriched above the given cutoff
        iter = enrichedTerms.iterator();
        while(iter.hasNext()) {
            
            EnrichedGOTerm term = iter.next();
            if(term.getPValue() > pValCutoff) {
                iter.remove();
                continue;
            }
            
            if(term.getGoNode().equals(aspectRoot)) {
                iter.remove();
                continue;
            }
            
            if(term.getGoNode().equals(rootNode)) {
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
            term.setPValue(StatUtils.PScore(term.numAnnotatedProteins(), term.totalAnnotatedProteins(), numProteinsInSet, totalAnnotatedProteins));
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

    private static Map<String, EnrichedGOTerm> getAllGOTerms( List<NRProtein> proteins, int speciesId, int goAspect)
            throws Exception {
        
        Map<String, EnrichedGOTerm> goTerms = new HashMap<String, EnrichedGOTerm>(); // unique GO terms
        GOSearcher searcher = GOSearcher.getInstance();
        
        for(NRProtein protein: proteins) {
            
            // Ignore this protein if it does not have the same speciesId as the one in the method arguments.
            if(protein.getSpecies().getId() != speciesId)
                continue;
            
            // Get a list of GO term annotations for this protein
            Set<GONode> nodes = null;
            try {
              nodes  = searcher.getGONodes(goAspect, protein);
            }
            catch (Exception e) {
                log.error("Could not get GO annotations for proteinID: "+protein.getId(), e);
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
                   term.addProtein(protein.getId());
                }
            }
        }
        
        
        // make a copy so that we do not get a concurrent modification exception
        Map<String, EnrichedGOTerm> copyOfGoTerms = new HashMap<String, EnrichedGOTerm>((int)(goTerms.size() * 1.5));
        copyOfGoTerms.putAll(goTerms);
        
        // add the parents of all the GO terms we found
        for(EnrichedGOTerm term: copyOfGoTerms.values()) {
            
            GONode node = term.getGoNode();
            
            Set<GONode> parentNodes;
            try {
                parentNodes = GOUtils.getAllParents(node);
            }
            catch (Exception e) {
                log.error("Could not get parent GO terms for: "+node.getAccession());
                continue;
            }
            
            for(GONode pNode: parentNodes) {
                EnrichedGOTerm pTerm = goTerms.get(pNode.getAccession());
                if(pTerm == null) {
                    pTerm = initEnrichedGOTerm(pNode, speciesId);
                    goTerms.put(pNode.getAccession(), pTerm);
                }
                pTerm.addProteins(term.getProteins());
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
