package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.infer.GraphBuilder;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinInferrer;
import edu.uwpr.protinfer.infer.ProteinInferrerMaximal;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;
import edu.uwpr.protinfer.infer.graph.SetCoverFinder;
import edu.uwpr.protinfer.util.TimeUtils;

public class ProteinInferrerIdPicker implements ProteinInferrer {

    private static final Logger log = Logger.getLogger(ProteinInferrerIdPicker.class);
    
    private boolean doParsimonyAnalysis = true;

    public ProteinInferrerIdPicker(boolean doParosimonyAnalysis) {
        this.doParsimonyAnalysis = doParosimonyAnalysis;
    }
    
    @Override
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> List<InferredProtein<S>> inferProteins(List<T> psms) {

        if (!doParsimonyAnalysis) {
            return inferAllProteins(psms);
        }
        else {

            long s = System.currentTimeMillis();

            ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
            List<InferredProtein<S>> allProteins = inferrer.inferProteins(psms);

//            Set<Integer> protIds = new HashSet<Integer>();
//            for(InferredProtein<S> prot: allProteins) {
//                if(protIds.contains(prot.getProteinId()))
//                    System.out.println("Duplicate found!");
//                else
//                    protIds.add(prot.getProteinId());
//            }
            // build a graph
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ProteinVertex, PeptideVertex> graph = graphBuilder.buildGraph(allProteins);
            if(graph.getLeftVertices().size() != allProteins.size()) {
                log.error("Numbers don't match! All proteins: "+allProteins.size()+"; Vertices: "+graph.getLeftVertices().size()+"\nCannot continue...");
                return null;
            }
            System.out.println("# peptides in graph: "+graph.getRightVertices().size());


            // collapse vertices
            GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
            try {
                collapser.collapseGraph(graph);
            }
            catch (InvalidVertexException e) {
                log.error("Error building graph: "+e.getMessage());
                return null;
            }
            // set the protein and peptide group ids.
            int groupId = 1;
            for(ProteinVertex vertex: graph.getLeftVertices()) {
                for(Protein prot: vertex.getProteins()) {
                    prot.setProteinGroupId(groupId);
                }
                groupId++;
            }
            groupId = 1;
            for(PeptideVertex vertex: graph.getRightVertices()) {
                for(Peptide pept: vertex.getPeptides()) {
                    pept.setPeptideGroupId(groupId);
                }
                groupId++;
            }

            // find protein clusters
            ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
            connCompFinder.findAllConnectedComponents(graph);


            // do parsimony analysis
            SetCoverFinder<ProteinVertex, PeptideVertex> setCoverFinder = new SetCoverFinder<ProteinVertex, PeptideVertex>();
            List<ProteinVertex> cover = setCoverFinder.getGreedySetCover(graph);
            for (ProteinVertex vertex: cover) 
                vertex.setAccepted(true);

            List<InferredProtein<S>> inferredProteins = new ArrayList<InferredProtein<S>>(allProteins.size());
            for (InferredProtein<S> prot: allProteins) {
                if (prot.getIsAccepted()) {
                    inferredProteins.add(prot);
                }
            }

            long e = System.currentTimeMillis();
            log.info("Inferred proteins in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds \nAll: "+
                    allProteins.size()+" Parsimonious: "+inferredProteins.size());

            return allProteins;
        }
    }

    private <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
    List<InferredProtein<S>> inferAllProteins(List<T> searchHits) {

        long s = System.currentTimeMillis();

        ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
        List<InferredProtein<S>> allProteins = inferrer.inferProteins(searchHits);

        // build a graph
        GraphBuilder graphBuilder = new GraphBuilder();
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = graphBuilder.buildGraph(allProteins);

        // collapse vertices
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {
            collapser.collapseGraph(graph);
        }
        catch (InvalidVertexException e) {
            log.error("Error building graph: "+e.getMessage());
            return null;
        }
        // set the protein and peptide group ids.
        int groupId = 1;
        for(ProteinVertex vertex: graph.getLeftVertices()) {
            for(Protein prot: vertex.getProteins()) {
                prot.setProteinGroupId(groupId);
            }
            groupId++;
        }
        groupId = 1;
        for(PeptideVertex vertex: graph.getRightVertices()) {
            for(Peptide pept: vertex.getPeptides()) {
                pept.setPeptideGroupId(groupId);
            }
            groupId++;
        }

        // find protein clusters
        ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
        connCompFinder.findAllConnectedComponents(graph);

        long e = System.currentTimeMillis();
        log.info("Inferred proteins in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return allProteins;
    }
}
