/**
 * ProteinNode.java
 * @author Vagisha Sharma
 * Oct 8, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uwpr.protinfer.graph.InvalidNodeException;
import edu.uwpr.protinfer.graph.Node;
import edu.uwpr.protinfer.graph.NodeCombiner;

public final class ProteinNode extends Node {
    private ProteinHit[] hits;
    
    public ProteinNode(ProteinHit... hits) throws InvalidNodeException {
        super("");
        if (hits.length == 0)
            throw new InvalidNodeException("Cannot create a ProteinNode with 0 ProteinHits");
        StringBuilder buf = new StringBuilder();
        for (ProteinHit hit: hits) 
            buf.append("_"+hit.getLabel());
        buf.deleteCharAt(0);
        setLabel(buf.toString());
        this.hits = hits;
    }
    
    public ProteinHit[] getProteinHits() {
        return hits;
    }

    @Override
    public String getLongLabel() {
        return getLabel();
    }
}

final class ProteinNodeCombiner implements NodeCombiner<ProteinNode> {

    @Override
    public ProteinNode combineNodes(ProteinNode... nodes) throws InvalidNodeException {
        List<ProteinHit> hits = new ArrayList<ProteinHit>(nodes.length);
        for (ProteinNode node: nodes)
            hits.addAll(Arrays.asList(node.getProteinHits()));
        ProteinHit[] hitArray = new ProteinHit[hits.size()];
        return new ProteinNode(hits.toArray(hitArray));
    }
}