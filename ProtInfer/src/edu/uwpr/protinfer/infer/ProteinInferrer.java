package edu.uwpr.protinfer.infer;

import java.util.List;


public interface ProteinInferrer {

    public <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> List<InferredProtein<S>> inferProteins(List<T> psmList);
}
