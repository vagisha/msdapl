package edu.uwpr.protinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        
        // collapse the graph
        GraphCollapser<ProteinNode, PeptideNode> collapser = new GraphCollapser<ProteinNode, PeptideNode>();
        collapser.collapseGraph(graph);
        
        // find the greedy set cover
        GreedySetCover<ProteinNode, PeptideNode> coverFinder = new GreedySetCover<ProteinNode, PeptideNode>();
        List<ProteinNode> setCover = coverFinder.getGreedySetCover(graph);
        
        // find components in this graph and print them
        ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
        connCompFinder.findAllConnectedComponents(graph);
        List<Node> allNodes = graph.getAllNodes();
        Collections.sort(allNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
            }});
        int currComponent = -1;
        for (Node node: allNodes) {
            int idx = node.getComponentIndex();
            if (idx != currComponent) {
                currComponent = idx;
                System.out.println("COMPONENT: "+currComponent);
            }
            if (node instanceof ProteinNode) {
                System.out.print(node.getLabel()+" --> ");
                List<PeptideNode> adjNodes = graph.getAdjacentNodesL((ProteinNode) node);
                for (PeptideNode pn: adjNodes) {
                    System.out.print(pn.getLabel()+", ");
                }
                System.out.println();
            }
        }
        return setCover;
    }
}
