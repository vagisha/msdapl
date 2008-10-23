/**
 * ProteinNode.java
 * @author Vagisha Sharma
 * Oct 8, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.assemble.idpicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uwpr.protinfer.Protein;
import edu.uwpr.protinfer.assemble.idpicker.graph.InvalidNodeException;
import edu.uwpr.protinfer.assemble.idpicker.graph.Node;
import edu.uwpr.protinfer.assemble.idpicker.graph.NodeCombiner;

public final class ProteinNode extends Node {
    private Protein[] hits;
    
    public ProteinNode(Protein... hits) throws InvalidNodeException {
        super("");
        if (hits.length == 0)
            throw new InvalidNodeException("Cannot create a ProteinNode with 0 ProteinHits");
        StringBuilder buf = new StringBuilder();
        for (Protein hit: hits) 
            buf.append("_"+hit.getAccession());
        buf.deleteCharAt(0);
        setLabel(buf.toString());
        this.hits = hits;
    }
    
    public Protein[] getProteinHits() {
        return hits;
    }

    @Override
    public String getLongLabel() {
        StringBuilder buf = new StringBuilder();
        for (Protein hit: hits) 
            buf.append("_"+hit.getAccession());
        buf.deleteCharAt(0);
        return buf.toString();
    }
}

final class ProteinNodeCombiner implements NodeCombiner<ProteinNode> {

    @Override
    public ProteinNode combineNodes(List<ProteinNode> nodes) throws InvalidNodeException {
        List<Protein> hits = new ArrayList<Protein>(nodes.size());
        for (ProteinNode node: nodes)
            hits.addAll(Arrays.asList(node.getProteinHits()));
        Protein[] hitArray = new Protein[hits.size()];
        return new ProteinNode(hits.toArray(hitArray));
    }
}