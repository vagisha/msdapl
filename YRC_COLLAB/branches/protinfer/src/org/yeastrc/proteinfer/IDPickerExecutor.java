/**
 * IDPickerExecutor.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.proteinfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.idpicker.FdrCalculatorIdPicker;
import edu.uwpr.protinfer.infer.GraphBuilder;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
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


    public <T extends PeptideSpectrumMatch<S>, S extends SpectrumMatch> 
        List<InferredProtein<S>> inferProteins(List<T> searchHits, IDPickerParams params) {

        ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
        List<InferredProtein<S>> allProteins = inferrer.inferProteins(searchHits);
        
        if (!params.getDoParsimonyAnalysis()) {
            return allProteins;
        }
        else {
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
            
            // find protein groups
            ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
            connCompFinder.findAllConnectedComponents(graph);
            
            
            // do parsimony analysis
            SetCoverFinder<ProteinVertex, PeptideVertex> setCoverFinder = new SetCoverFinder<ProteinVertex, PeptideVertex>();
            List<ProteinVertex> cover = setCoverFinder.getGreedySetCover(graph);
            for (ProteinVertex vertex: cover) 
                vertex.setAccepted(true);
            
            List<InferredProtein<S>> inferredProteins = new ArrayList<InferredProtein<S>>(allProteins.size());
            for (InferredProtein<S> prot: allProteins) {
                if (prot.getProtein().isAccepted()) {
                    inferredProteins.add(prot);
                }
            }
            return inferredProteins;
        }
    }
}
