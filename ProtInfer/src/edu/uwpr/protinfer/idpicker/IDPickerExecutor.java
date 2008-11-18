/**
 * IDPickerExecutor.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.infer.GraphBuilder;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinInferrerMaximal;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;
import edu.uwpr.protinfer.infer.graph.SetCoverFinder;


/**
 * 
 */
public class IDPickerExecutor {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    public  List<SequestHit> filterSearchHits(List<SequestHit> searchHits, IDPickerParams params) 
    throws FdrCalculatorException, FilterException {

        FdrCalculatorIdPicker<SequestHit> calculator = new FdrCalculatorIdPicker<SequestHit>();
        calculator.separateChargeStates(true);
        calculator.setDecoyRatio(params.getDecoyRatio());

        // Calculate FDR from deltaCN scores first.
        calculator.calculateFdr(searchHits, new Comparator<SequestHit>() {
            public int compare(SequestHit o1, SequestHit o2) {
                return o1.getDeltaCn().compareTo(o2.getDeltaCn());
            }});

        // Filter based on the given FDR cutoff
        FdrFilterCriteria filterCriteria = new FdrFilterCriteria(params.getMaxRelativeFdr());

        List<SequestHit> filteredHits = Filter.filter(searchHits, filterCriteria);

        // Clear the fdr scores and calculate FDR from xCorr scores
        for (SequestHit hit: filteredHits)
            hit.setFdr(1.0);

        calculator.calculateFdr(searchHits, new Comparator<SequestHit>() {
            public int compare(SequestHit o1, SequestHit o2) {
                return o1.getXcorr().compareTo(o2.getXcorr());
            }});

        filterCriteria = new FdrFilterCriteria(params.getMaxRelativeFdr());

        return Filter.filter(searchHits, filterCriteria);
    }


    private <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> 
        List<InferredProtein<S>> inferAllProteins(List<T> searchHits, IDPickerParams params) {
        
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
        
        return allProteins;
    }
    
    
    public <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> 
        List<InferredProtein<S>> inferProteins(List<T> searchHits, SearchSummary summary, IDPickerParams params) {

        
        if (!params.getDoParsimonyAnalysis()) {
            return inferAllProteins(searchHits, params);
        }
        else {
            
            
            ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
            List<InferredProtein<S>> allProteins = inferrer.inferProteins(searchHits);
            
            // the # of proteins after fdr filtering and before min.peptide filtering
            summary.setFilteredProteinsFdr(allProteins.size());
            
            // filter all proteins with less than the required number of peptides
            int minPeptCnt = params.getMinDistinctPeptides();
            if(params.getMinDistinctPeptides() > 1) {
                Iterator<InferredProtein<S>> iterator = allProteins.iterator();
                while(iterator.hasNext()) {
                    InferredProtein<S> prot = iterator.next();
                    if(prot.getPeptideEvidenceCount() < minPeptCnt)
                        iterator.remove();
                }
            }
            
            // the # of proteins before parsimony analysis and after min.peptide filtering
            summary.setFilteredProteinsMinPeptCount(allProteins.size());
            
            // build a graph
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ProteinVertex, PeptideVertex> graph = graphBuilder.buildGraph(allProteins);
            if(graph.getLeftVertices().size() != allProteins.size()) {
                System.out.println("all proteins: "+allProteins.size()+" vertices: "+graph.getLeftVertices().size());
                System.exit(1);
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
            
            for(InferredProtein<S> prot: allProteins) {
                if(prot.getProteinGroupId() == 0) {
                    System.out.println("groupID not found for protein: "+prot.getAccession());
                    System.exit(1);
                }
                
                for(PeptideEvidence<S> peptide: prot.getPeptides()) {
                    if(peptide.getPeptide().getPeptideGroupId() == 0) {
                        System.out.println("groupID not found for peptide: "+peptide.getPeptideSeq());
                        System.exit(1);
                    }
                }
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
            
            // # of proteins after parsimony analysis
            summary.setFilteredProteinsParsimony(inferredProteins.size());
            
            System.out.println("All: "+allProteins.size()+" Parsimonious: "+inferredProteins.size());
            return allProteins;
        }
    }
}
