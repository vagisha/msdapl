package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Iterator;
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


    public <S extends SpectrumMatch> List<InferredProtein<S>> 
            inferProteins(List<InferredProtein<S>>  allProteins, IDPickerParams params) {
        
        long s = System.currentTimeMillis();
        
//      Set<Integer> protIds = new HashSet<Integer>();
//      for(InferredProtein<S> prot: allProteins) {
//      if(protIds.contains(prot.getProteinId()))
//      System.out.println("Duplicate found!");
//      else
//      protIds.add(prot.getProteinId());
//      }
        
        
        // build a graph
        GraphBuilder graphBuilder = new GraphBuilder();
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = graphBuilder.buildGraph(allProteins);
        if(graph.getLeftVertices().size() != allProteins.size()) {
            log.error("Numbers don't match! All proteins: "+allProteins.size()+"; Vertices: "+graph.getLeftVertices().size()+"\nCannot continue...");
            return null;
        }
        log.info("# proteins in graph: "+graph.getLeftVertices().size());
        log.info("# peptides in graph: "+graph.getRightVertices().size());
        

        // collapse vertices
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {
            collapser.collapseGraph(graph);
        }
        catch (InvalidVertexException e) {
            log.error("Error building graph: "+e.getMessage());
            return null;
        }
        
        // FILTER!!
        log.info("Number of proteins before filtering: "+allProteins.size());
        if(params != null) {
            if(params.getMinPeptides() > 1) {
                removeProteinsForMinPeptides(allProteins, graph, params.getMinPeptides());
                log.info("Number of proteins after filtering for "+params.getMinPeptides()+" minPeptides: "+allProteins.size());
            }
            removeProteinsForMinUniquePeptides(allProteins, graph, params.getMinUniquePeptides());
            log.info("Number of proteins after filtering for "+params.getMinUniquePeptides()+" minUniquePeptides: "+allProteins.size());
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

        int parsimCount = 0;
        for(InferredProtein<S> prot: allProteins) 
            if(prot.getProtein().isAccepted())  parsimCount++;
        
        long e = System.currentTimeMillis();
        log.info("Inferred proteins in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds \nAll: "+
                allProteins.size()+" Parsimonious Groups: "+cover.size()+"; Parsimonious Proteins: "+parsimCount);

        return allProteins;
    }


    private <S extends SpectrumMatch> void removeProteinsForMinPeptides(List<InferredProtein<S>> proteins, 
            BipartiteGraph<ProteinVertex, PeptideVertex> graph, int minPeptides) {
       
        // mark proteins that will not be accepted
        List<ProteinVertex> removed = new ArrayList<ProteinVertex>();
        for(ProteinVertex v: graph.getLeftVertices()) {
            if(graph.getAdjacentVerticesL(v).size() < minPeptides) {
                v.setAccepted(false);
                removed.add(v);
            }
            else
                v.setAccepted(true);
        }
        
        // remove the proteins
        Iterator<InferredProtein<S>> iter = proteins.iterator();
        while(iter.hasNext()) {
            InferredProtein<S> prot = iter.next();
            if(!prot.getProtein().isAccepted()) {
                iter.remove();
            }
        }
        
        // now remove vertices from the graph
        for(ProteinVertex v: removed) {
            if(!v.isAccepted())
                graph.removeLeftVertex(v);
        }
        
        // reset everything to false
        for(ProteinVertex v: graph.getLeftVertices()) {
            v.setAccepted(false);
        }
    }
    
    private <S extends SpectrumMatch> void removeProteinsForMinUniquePeptides(List<InferredProtein<S>> proteins, 
            BipartiteGraph<ProteinVertex, PeptideVertex> graph, int minUniqPeptides) {
       
        // mark proteins that will not be accepted
        List<ProteinVertex> removed = new ArrayList<ProteinVertex>();
        for(ProteinVertex v: graph.getLeftVertices()) {
            int uniqCount = 0;
            List<PeptideVertex> peptList = graph.getAdjacentVerticesL(v);
            for(PeptideVertex pept: peptList) {
                if(graph.getAdjacentVerticesR(pept).size() == 1) {
                    // mark all the peptides in this vertex as unique
                    for(Peptide p: pept.getPeptides())
                        p.markUnique(true);
                    uniqCount++;
                }
            }
            if(uniqCount < minUniqPeptides)
                v.setAccepted(false);
            else
                v.setAccepted(true);
        }
        
        if(minUniqPeptides > 0) {
            // remove the proteins
            Iterator<InferredProtein<S>> iter = proteins.iterator();
            while(iter.hasNext()) {
                InferredProtein<S> prot = iter.next();
                if(!prot.getProtein().isAccepted()) {
                    iter.remove();
                }
            }

            // now remove vertices from the graph
            for(ProteinVertex v: removed) {
                if(!v.isAccepted())
                    graph.removeLeftVertex(v);
            }
        }
        
        // reset everything to false
        for(ProteinVertex v: graph.getLeftVertices()) {
            v.setAccepted(false);
        }
    }


    @Override
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> List<InferredProtein<S>> inferProteins(List<T> psms) {


        ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
        List<InferredProtein<S>> allProteins = inferrer.inferProteins(psms);

        return inferProteins(allProteins, null);
    }

}
