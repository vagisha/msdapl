package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;

public class ProteinGroupFinder {

    public <T extends SpectrumMatch> void groupProteins(List<InferredProtein<T>> inferredProteins) {
        
        
    }
    
    public static void main(String[] args) {
        
        ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
        List<PeptideSpectrumMatch<SMatch>> psmList = new ArrayList<PeptideSpectrumMatch<SMatch>>();
        List<InferredProtein<SMatch>> protList = inferrer.inferProteins(psmList);
        ProteinGroupFinder finder = new ProteinGroupFinder();
        finder.groupProteins(protList);
    }
    
    private static final class SMatch implements SpectrumMatch {

        @Override
        public int getCharge() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String getPeptideSequence() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getScanNumber() {
            // TODO Auto-generated method stub
            return 0;
        }
        
    }
}
