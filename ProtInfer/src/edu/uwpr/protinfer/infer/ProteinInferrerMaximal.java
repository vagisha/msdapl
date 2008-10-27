package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProteinInferrerMaximal implements ProteinInferrer {

    @Override
    public <T extends SpectrumMatch> List<InferredProtein<T>> inferProteins(List<PeptideSpectrumMatch<T>> psmList) {
        
        Map<String, InferredProtein<T>> proteinMap = new HashMap<String, InferredProtein<T>>();
        
        
        // for each peptide sequence match
        for (PeptideSpectrumMatch<T> psm: psmList) {
            
            // for each protein match to the peptide
            List<ProteinHit> protHitList = psm.getPeptideHit().getProteinList();
            for (ProteinHit protHit: protHitList) {
                
                InferredProtein<T> inferredProtein = proteinMap.get(protHit.getAccession());
                
                // if we have not seen this protein add a new InferredProtein to the proteinMat
                if (inferredProtein == null) {
                    inferredProtein = new InferredProtein<T>(protHit.getProtein());
                    PeptideEvidence<T> evidence = new PeptideEvidence<T>(psm.getPeptideHit().getPeptide());
                    evidence.addSpectrumMatch(psm.getSpectrumMatch());
                    proteinMap.put(protHit.getAccession(), inferredProtein);
                }
                // if we already saw this protein --
                // if we also saw this peptide before, add a SpectrumMatch to the PeptideEvidence
                // otherwise add a new PeptideEvidence to the InferredProtein
                else {
                    PeptideEvidence<T> evidence = inferredProtein.getPeptideEvidence(psm.getPeptideSequence());
                    if (evidence == null) {
                        evidence = new PeptideEvidence<T>(psm.getPeptideHit().getPeptide());
                        inferredProtein.addPeptideEvidence(evidence);
                    }
                    evidence.addSpectrumMatch(psm.getSpectrumMatch());
                }
            }
        }
        
        List<InferredProtein<T>> inferredProteins = new ArrayList<InferredProtein<T>>();
        inferredProteins.addAll(proteinMap.values());
        return inferredProteins;
    }
}
