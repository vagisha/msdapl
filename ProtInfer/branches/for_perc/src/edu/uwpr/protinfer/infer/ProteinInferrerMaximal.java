package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProteinInferrerMaximal implements ProteinInferrer {

    
    @Override
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
        List<InferredProtein<S>> inferProteins(List<T> psmList) {
        
        Map<Integer, InferredProtein<S>> proteinMap = new HashMap<Integer, InferredProtein<S>>();
        Map<Integer, PeptideEvidence<S>> peptideMap = new HashMap<Integer, PeptideEvidence<S>>();
        
        
        // for each peptide sequence match
        for (T psm: psmList) {
            
            // peptide for this peptide-spectrum-match
            Peptide psmPeptide = psm.getPeptideHit().getPeptide();
            
            // add this to the peptideMap if not already there
            PeptideEvidence<S> evidence = peptideMap.get(psmPeptide.getId());
            if(evidence == null) {
                evidence = new PeptideEvidence<S>(psmPeptide);
                evidence.setProteinMatchCount(psm.getPeptideHit().getMatchProteinCount());
                peptideMap.put(psmPeptide.getId(), evidence);
            }
            evidence.addSpectrumMatch(psm.getSpectrumMatch());
            
            // for each protein match to the peptide
            List<ProteinHit> protHitList = psm.getPeptideHit().getProteinList();
            for (ProteinHit protHit: protHitList) {
                
                InferredProtein<S> inferredProtein = proteinMap.get(protHit.getProtein().getId());
                
                // if we have not seen this protein add a new InferredProtein to the proteinMap
                if (inferredProtein == null) {
                    inferredProtein = new InferredProtein<S>(protHit.getProtein());
                    proteinMap.put(protHit.getProtein().getId(), inferredProtein);
                }
                
                // if this protein does not already have this peptide evidence add it
                if(inferredProtein.getPeptideEvidence(psmPeptide) == null) {
                    inferredProtein.addPeptideEvidence(evidence);
                }
            }
        }
        
        List<InferredProtein<S>> inferredProteins = new ArrayList<InferredProtein<S>>(proteinMap.size());
        inferredProteins.addAll(proteinMap.values());
        return inferredProteins;
    }
}
