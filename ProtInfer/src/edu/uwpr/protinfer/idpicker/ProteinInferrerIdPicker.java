package edu.uwpr.protinfer.idpicker;

import java.util.List;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.ProteinInferrer;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class ProteinInferrerIdPicker implements ProteinInferrer {

    @Override
    public <T extends SpectrumMatch> List<InferredProtein<T>> inferProteins( List<PeptideSpectrumMatch<T>> psmList) {
        
        return null;
    }

}
