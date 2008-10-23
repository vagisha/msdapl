/**
 * PeptideNode.java
 * @author Vagisha Sharma
 * Oct 8, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.assemble.idpicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uwpr.protinfer.PeptideHit;
import edu.uwpr.protinfer.assemble.idpicker.graph.InvalidNodeException;
import edu.uwpr.protinfer.assemble.idpicker.graph.Node;
import edu.uwpr.protinfer.assemble.idpicker.graph.NodeCombiner;

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
        StringBuilder buf = new StringBuilder();
        for (PeptideHit hit: hits) 
            buf.append("_"+hit.getPeptideSeq());
        buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public String getLongLabel(char separator) {
        StringBuilder buf = new StringBuilder();
        for (PeptideHit hit: hits) 
            buf.append(separator+hit.getPeptideSeq());
        buf.deleteCharAt(0);
        return buf.toString();
    }
}

final class PeptideNodeCombiner implements NodeCombiner<PeptideNode> {

    @Override
    public PeptideNode combineNodes(List<PeptideNode>nodes) throws InvalidNodeException {
        List<PeptideHit> hits = new ArrayList<PeptideHit>(nodes.size());
        for (PeptideNode node: nodes)
            hits.addAll(Arrays.asList(node.getPeptideHits()));
        PeptideHit[] hitArray = new PeptideHit[hits.size()];
        return new PeptideNode(hits.toArray(hitArray));
    }
}