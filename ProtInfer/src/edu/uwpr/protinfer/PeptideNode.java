/**
 * PeptideNode.java
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

public final class PeptideNode extends Node {
    
    private PeptideHit[] hits;
    
    public PeptideNode(PeptideHit... hits) throws InvalidNodeException {
        super("");
        if (hits.length == 0)
            throw new InvalidNodeException("Cannot create a PeptideNode with 0 PeptideHits");
        StringBuilder buf = new StringBuilder();
        for (PeptideHit hit: hits) 
            buf.append("_"+hit.getLabel());
        buf.deleteCharAt(0);
        setLabel(buf.toString());
        this.hits = hits;
    }
    
    public PeptideHit[] getPeptideHits() {
        return hits;
    }

    @Override
    public String getLongLabel() {
        return getLabel();
    }
}

final class PeptideNodeCombiner implements NodeCombiner<PeptideNode> {

    @Override
    public PeptideNode combineNodes(PeptideNode... nodes) throws InvalidNodeException {
        List<PeptideHit> hits = new ArrayList<PeptideHit>(nodes.length);
        for (PeptideNode node: nodes)
            hits.addAll(Arrays.asList(node.getPeptideHits()));
        PeptideHit[] hitArray = new PeptideHit[hits.size()];
        return new PeptideNode(hits.toArray(hitArray));
    }
}