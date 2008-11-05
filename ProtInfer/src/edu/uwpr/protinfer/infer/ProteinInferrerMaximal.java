package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProteinInferrerMaximal implements ProteinInferrer {

    @Override
    public <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> 
        List<InferredProtein<S>> inferProteins(List<T> psmList) {
        
        Map<String, InferredProtein<S>> proteinMap = new HashMap<String, InferredProtein<S>>();
        
        
        // for each peptide sequence match
        for (T psm: psmList) {
            
            // for each protein match to the peptide
            List<ProteinHit> protHitList = psm.getPeptideHit().getProteinList();
            for (ProteinHit protHit: protHitList) {
                
                InferredProtein<S> inferredProtein = proteinMap.get(protHit.getAccession());
                
                // if we have not seen this protein add a new InferredProtein to the proteinMat
                if (inferredProtein == null) {
                    inferredProtein = new InferredProtein<S>(protHit.getProtein());
                    PeptideEvidence<S> evidence = new PeptideEvidence<S>(psm.getPeptideHit().getPeptide());
                    evidence.addSpectrumMatch(psm.getSpectrumMatch());
                    proteinMap.put(protHit.getAccession(), inferredProtein);
                }
                // if we already saw this protein --
                // if we also saw this peptide before, add a SpectrumMatch to the PeptideEvidence
                // otherwise add a new PeptideEvidence to the InferredProtein
                else {
                    PeptideEvidence<S> evidence = inferredProtein.getPeptideEvidence(psm.getPeptideSequence());
                    if (evidence == null) {
                        evidence = new PeptideEvidence<S>(psm.getPeptideHit().getPeptide());
                        inferredProtein.addPeptideEvidence(evidence);
                    }
                    evidence.addSpectrumMatch(psm.getSpectrumMatch());
                }
            }
        }
        
        List<InferredProtein<S>> inferredProteins = new ArrayList<InferredProtein<S>>();
        inferredProteins.addAll(proteinMap.values());
        return inferredProteins;
    }
}
