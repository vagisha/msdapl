package edu.uwpr.protinfer.infer;

import java.util.List;


public interface ProteinInferrer {

    public <T extends SpectrumMatch> List<InferredProtein<T>> inferProteins(List<PeptideSpectrumMatch<T>> psmList);
}
