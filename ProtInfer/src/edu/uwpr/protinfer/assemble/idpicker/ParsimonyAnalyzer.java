package edu.uwpr.protinfer.assemble.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SequestSpectrumMatch.PsmComparator;
import edu.uwpr.protinfer.assemble.idpicker.algo.ConnectedComponentFinder;
import edu.uwpr.protinfer.assemble.idpicker.algo.GraphCollapser;
import edu.uwpr.protinfer.assemble.idpicker.algo.GreedySetCover;
import edu.uwpr.protinfer.assemble.idpicker.graph.BipartiteGraph;
import edu.uwpr.protinfer.assemble.idpicker.graph.InvalidNodeException;
import edu.uwpr.protinfer.assemble.idpicker.graph.Node;
import edu.uwpr.protinfer.filter.idpicker.FdrFilter;
import edu.uwpr.protinfer.idpicker.FdrCalculatorIdPicker;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;
import edu.uwpr.protinfer.pepxml.InteractPepXmlFileReader;
import edu.uwpr.protinfer.pepxml.ScanSearchResult;
import edu.uwpr.protinfer.pepxml.SequestSearchHit;

public class ParsimonyAnalyzer {

    private BipartiteGraph<ProteinNode, PeptideNode> graph;
    
    public ParsimonyAnalyzer() {
        ProteinNodeCombiner prCombiner = new ProteinNodeCombiner();
        PeptideNodeCombiner peptCombiner = new PeptideNodeCombiner();
        graph = new BipartiteGraph<ProteinNode, PeptideNode>(prCombiner, peptCombiner);
    }
    
    public void addPeptideHit(PeptideHit hit) throws InvalidNodeException {
        PeptideNode peptide = null;
        peptide = new PeptideNode( new PeptideHit[]{hit});
        
        List<ProteinHit> proteins = hit.getProteinList();
        for (ProteinHit prot: proteins) {
            graph.addEdge(new ProteinNode(new Protein[]{prot.getProtein()}), peptide);
        }
    }
    
    public List<ProteinNode> getMinimalProteinList() throws InvalidNodeException {
        
        System.out.println("Beginning parsimony analysis on graph with "+graph.getLeftNodes().size()+" left nodes and "+
                graph.getRightNodes().size()+" right nodes");
        // collapse the graph
        System.out.println("Collapsing graph");
        GraphCollapser<ProteinNode, PeptideNode> collapser = new GraphCollapser<ProteinNode, PeptideNode>();
        collapser.collapseGraph(graph);
        
        // find the greedy set cover    
        System.out.println("Computing set cover");
        GreedySetCover<ProteinNode, PeptideNode> coverFinder = new GreedySetCover<ProteinNode, PeptideNode>();
        List<ProteinNode> setCover = coverFinder.getGreedySetCover(graph);
        
        // find components in this graph and print them
        System.out.println("Getting connected components");
        ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
        int componentCount = connCompFinder.findAllConnectedComponents(graph);
        System.out.println("Found "+componentCount+" connected components");
        Collections.sort(setCover, new Comparator<Node>() {
            public int compare(Node o1, Node o2) {
                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
            }});
        
        int currComponent = -1;
        for (ProteinNode node: setCover) {
            int idx = node.getComponentIndex();
            if (idx != currComponent) {
              currComponent = idx;
              System.out.println("COMPONENT: "+currComponent);
            }
            System.out.print("\t"+node.getLongLabel()+"("+node.getLabel()+") --> ");
            Set<PeptideNode> adjNodes = graph.getAdjacentNodesL((ProteinNode) node);
            for (PeptideNode pn: adjNodes) {
                System.out.print(pn.getLongLabel('\n')+"\n\n");
            }
            System.out.println();
        }
        return setCover;
    }
    
    public static void main(String[] args) {
        
        String filePath = "TEST_DATA/for_vagisha/18mix/interact.pep.xml";
        
        InteractPepXmlFileReader reader = new InteractPepXmlFileReader();
        List<SequestHit> allHits = new ArrayList<SequestHit>();
        List<SequestSpectrumMatch> allAcceptedPsms = new ArrayList<SequestSpectrumMatch>();
        int sourceId = 0;
        
        try {
            reader.open(filePath);
            ScanSearchResult scan = null;
            while(reader.hasNextRunSummary()) {
                
                String sourceName = reader.getRunName();
                SearchSource source = new SearchSource(sourceName, sourceId++);
                
                while(reader.hasNextScanSearchResult()) {
                    scan = reader.getNextSearchScan();
                    SequestSearchHit seqHit = scan.getTopHit();
                    PeptideHit pept = seqHit.getPeptide();
                    for (ProteinHit prot: pept.getProteinList()) {
                        if (prot.getAccession().startsWith("rev_"))
                            prot.getProtein().setDecoy();
                    }
                    
                    SequestHit hit = new SequestHit(source, scan.getStartScan(), scan.getAssumedCharge(), 
                            seqHit.getXcorr().doubleValue(), 
                            pept);
                    allHits.add(hit);
                }
                
                FdrFilter fdrCalc = new FdrFilter();
                fdrCalc.setDecoyRatio(1.0);
                fdrCalc.separateChargeStates();
                fdrCalc.setThresholdFdr(0.25);
                fdrCalc.filterSearchHits(allHits, comparator);
                
            }
            reader.close();
            System.out.println("Finished reading file: # accepted hits: "+allAcceptedPsms.size()+
                    "; #peptide: "+reader.getPeptideHits().size()+
                    "; #proteins: "+reader.getProteinHits().size());
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
        
        ParsimonyAnalyzer pars = new ParsimonyAnalyzer();
        Map<String, Integer> peptides = new HashMap<String, Integer>();
        Map<String , Protein> proteins = new HashMap<String, Protein>();
        for (SequestSpectrumMatch psm: allAcceptedPsms) {
            SequestSearchHit topHit = psm.getScanSearchResult().getTopHit();
            PeptideHit peptide = topHit.getPeptide();
            peptides.put(peptide.getPeptideSeq(), 1);
            for (ProteinHit ph: peptide.getProteinList())
                proteins.put(ph.getAccession(), ph.getProtein());
            try {
                pars.addPeptideHit(psm.getScanSearchResult().getTopHit().getPeptide());
            }
            catch (InvalidNodeException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n\npeptides: "+peptides.size()+"; proteins: "+proteins.size()+"\n");
        
        try {
            List<ProteinNode> minimalProt = pars.getMinimalProteinList();
            System.out.println("\n#proteins (via parsimony): "+minimalProt.size());
        }
        catch (InvalidNodeException e) {
            e.printStackTrace();
        }
    }
}
