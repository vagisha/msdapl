package edu.uwpr.protinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;

import edu.uwpr.protinfer.graph.BipartiteGraph;
import edu.uwpr.protinfer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.graph.GraphCollapser;
import edu.uwpr.protinfer.graph.GreedySetCover;
import edu.uwpr.protinfer.graph.InvalidNodeException;
import edu.uwpr.protinfer.graph.Node;

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
            graph.addEdge(new ProteinNode(new ProteinHit[]{prot}), peptide);
        }
    }
    
    public List<ProteinNode> getMinimalProteinList() throws InvalidNodeException {
        
        System.out.println("Beginning parsimony analysis");
        // collapse the graph
        System.out.println("Collapsing graph");
        GraphCollapser<ProteinNode, PeptideNode> collapser = new GraphCollapser<ProteinNode, PeptideNode>();
        collapser.collapseGraph(graph);
        
        // find the greedy set cover
        System.out.println("Computing set cover");
        GreedySetCover<ProteinNode, PeptideNode> coverFinder = new GreedySetCover<ProteinNode, PeptideNode>();
        List<ProteinNode> setCover = coverFinder.getGreedySetCover(graph);
        
        // find components in this graph and print them
    //        System.out.println("Getting connected components");
    //        ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
    //        connCompFinder.findAllConnectedComponents(graph);
    //        List<Node> allNodes = graph.getAllNodes();
    //        Collections.sort(allNodes, new Comparator<Node>() {
    //            @Override
    //            public int compare(Node o1, Node o2) {
    //                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
    //            }});
    //        int currComponent = -1;
    //        for (Node node: allNodes) {
    //            int idx = node.getComponentIndex();
    //            if (idx != currComponent) {
    //                currComponent = idx;
    //                System.out.println("COMPONENT: "+currComponent);
    //            }
    //            if (node instanceof ProteinNode) {
    //                System.out.print(node.getLabel()+" --> ");
    //                List<PeptideNode> adjNodes = graph.getAdjacentNodesL((ProteinNode) node);
    //                for (PeptideNode pn: adjNodes) {
    //                    System.out.print(pn.getLabel()+", ");
    //                }
    //                System.out.println();
    //            }
    //        }
        return setCover;
    }
    
    public static void main(String[] args) {
        
        ParsimonyAnalyzer pars = new ParsimonyAnalyzer();
        
        String file = "TEST_DATA/large/PARC_depleted_b1_02.sqt";
        SequestSQTFileReader reader = new SequestSQTFileReader();
        int peptideCount = 0;
        int proteinCount = 0;
        try {
            reader.open(file, false);
            reader.getSearchHeader();
            SequestSearchScan scan = null;
            while(reader.hasNextSearchScan()) {
                scan = reader.getNextSearchScan();
//                System.out.println("Scan: "+scan.getScanNumber());
                for (SequestSearchResultIn result: scan.getScanResults()) {
                    MsSearchResultPeptide peptide = result.getResultPeptide();
                    List<MsSearchResultProteinIn> proteins = result.getProteinMatchList();
                    PeptideHit peptideHit = new PeptideHit(peptide.getPeptideSequence(), ""+peptideCount);
                    peptideCount++;
                    for (MsSearchResultProteinIn prot: proteins) {
                        ProteinHit protHit = new ProteinHit(prot.getAccession(), ""+proteinCount);
                        proteinCount++;
                        peptideHit.addProteinHit(protHit);
                    }
                    pars.addPeptideHit(peptideHit);
                }
//                if (peptideCount >  100)
//                    break;
            }
            reader.close();
            System.out.println("Finished reading file: #peptide: "+peptideCount+"; #proteins: "+proteinCount);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
        catch (InvalidNodeException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
        
        try {
            List<ProteinNode> minimalProt = pars.getMinimalProteinList();
            System.out.println("#proteins (via parsimony): "+minimalProt.size());
        }
        catch (InvalidNodeException e) {
            e.printStackTrace();
        }
        
    }
}
