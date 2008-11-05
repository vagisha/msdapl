package edu.uwpr.protinfer.infer;

import java.util.List;

import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;

public class GraphBuilder {

    public <T extends SpectrumMatch> BipartiteGraph<ProteinVertex, PeptideVertex> 
        buildGraph(List<InferredProtein<T>> inferredProteins) {
        
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = new BipartiteGraph<ProteinVertex, PeptideVertex>();
        for (InferredProtein<T> protein: inferredProteins) {
            
            ProteinVertex protVertex = new ProteinVertex(protein.getProtein());
            
            for(PeptideEvidence<T> peptide: protein.getPeptides()) {
                PeptideVertex peptVertex = new PeptideVertex(peptide.getPeptide());
                
                try {
                    graph.addEdge(protVertex, peptVertex);
                }
                catch (InvalidVertexException e) {
                    e.printStackTrace();
                }
            }
        }
        return graph;
    }
}
