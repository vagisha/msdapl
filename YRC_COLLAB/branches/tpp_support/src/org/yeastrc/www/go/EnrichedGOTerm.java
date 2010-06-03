/**
 * EnrichedGONode.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.yeastrc.bio.go.GONode;

/**
 * 
 */
public class EnrichedGOTerm implements Comparable<EnrichedGOTerm> {

    private final GONode goNode;
    private double pValue = 1.0;
    private Set<Integer> nrseqProteinIds;
    private final int totalProteins; // number of proteins in the "universe" annotated with this term
    
    public EnrichedGOTerm(GONode goNode, int totalProteins) {
        this.goNode = goNode;
        this.totalProteins = totalProteins;
        nrseqProteinIds = new HashSet<Integer>();
    }

    public double getPValue() {
        return pValue;
    }

    public String getPvalueString() {      
        DecimalFormat df = new DecimalFormat("0.####E0");
        return df.format(this.getPValue());       
    }
    
    public void setPValue(double value) {
        pValue = value;
    }

    public GONode getGoNode() {
        return goNode;
    }

    public void addProtein(int nrseqProteinId) {
        nrseqProteinIds.add(nrseqProteinId);
    }
    
    public void addProteins(Collection<Integer> nrseqProteinIds) {
        this.nrseqProteinIds.addAll(nrseqProteinIds);
    }
    
    public Set<Integer> getProteins() {
        return nrseqProteinIds;
    }
    
    public int getNumAnnotatedProteins() {
        return nrseqProteinIds.size();
    }
    
    public int getTotalAnnotatedProteins() {
        return totalProteins;
    }
    
    @Override
    public int compareTo(EnrichedGOTerm o) {
        try {
            if (this.getPValue() < o.getPValue() ) return -1;
            if (this.getPValue() > o.getPValue() ) return 1;
        } catch (Exception e) { ; }
        
        return 0;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ID: "+goNode.getId());
        buf.append("\t");
        buf.append("pval: "+getPValue());
        buf.append("\t");
        buf.append(goNode.getAccession());
        buf.append("\t");
        buf.append(goNode.getName());
        buf.append("\t");
        buf.append("Num proteins: "+this.getNumAnnotatedProteins());
        buf.append("\t");
        buf.append("Num total proteins: "+this.getTotalAnnotatedProteins());
        return buf.toString();
    }
}
