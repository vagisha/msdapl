package edu.uwpr.protinfer.idpicker;

import java.util.List;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.ProteinInferrer;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class ProteinInferrerIdPicker implements ProteinInferrer {

    @Override
    public <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> List<InferredProtein<S>> inferProteins(
            List<T> psmList) {
        // TODO Auto-generated method stub
        return null;
    }

    

}
