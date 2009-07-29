/**
 * ProteinProphetProtein.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinprophet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

/**
 * 
 */
public class ProteinProphetProtein extends GenericProteinferProtein {

    private String proteinName;
    private double probability;
    private double coverage;
    private int totalSpectrumCount;
    private double pctSpectrumCount;
    private double confidence;
    private boolean isSubsumed = false;
    private String subsumingProteinEntry;
    
    private List<String> indistinguishableProteins;
    private List<ProteinProphetProteinPeptideIon> peptides;
    
    
    public ProteinProphetProtein() {
        this(10);
    }
    
    public ProteinProphetProtein(int numIndistinguishableProteins) {
        this.indistinguishableProteins = new ArrayList<String>(numIndistinguishableProteins);
        peptides = new ArrayList<ProteinProphetProteinPeptideIon>();
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
        this.indistinguishableProteins.add(proteinName);
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public int getTotalSpectrumCount() {
        return totalSpectrumCount;
    }

    public void setTotalSpectrumCount(int totalSpectrumCount) {
        this.totalSpectrumCount = totalSpectrumCount;
    }

    public double getPctSpectrumCount() {
        return pctSpectrumCount;
    }

    public void setPctSpectrumCount(double pctSpectrumCount) {
        this.pctSpectrumCount = pctSpectrumCount;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public List<String> getIndistinguishableProteins() {
        return indistinguishableProteins;
    }

    public void addIndistinguishableProteins(String protein) {
        if(indistinguishableProteins.contains(protein))
            return;
        this.indistinguishableProteins.add(protein);
    }
    
    public boolean isSubsumed() {
        return isSubsumed;
    }

    public void setSubsumed(boolean isSubsumed) {
        this.isSubsumed = isSubsumed;
    }

    public String getSubsumingProteinEntry() {
        return subsumingProteinEntry;
    }
    
    public Set<String> getSusumingProteins() {
        if(subsumingProteinEntry == null || subsumingProteinEntry.length() == 0)
            return new HashSet<String>(0);
        String[] tokens = subsumingProteinEntry.split("\\s+");
        Set<String> set = new HashSet<String>(tokens.length*2);
        for(String tok: tokens)
            set.add(tok);
        return set;
    }

    public void setSubsumingProteinEntry(String subsumingProteinEntry) {
        if(subsumingProteinEntry == null)
            return;
        this.subsumingProteinEntry = subsumingProteinEntry.trim();
        this.isSubsumed = !(subsumingProteinEntry == null || subsumingProteinEntry.trim().length() == 0);
    }

    public List<ProteinProphetProteinPeptideIon> getPeptides() {
        return peptides;
    }
    
    public void addPeptide(ProteinProphetProteinPeptideIon peptide) {
        peptides.add(peptide);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(proteinName);
        buf.append("\t");
        buf.append("prob: "+probability);
        buf.append("\t");
        buf.append("subsumed: "+isSubsumed);
        buf.append("\t");
        buf.append("numIndistinct: "+indistinguishableProteins.size());
        if(indistinguishableProteins.size() > 1) {
            buf.append("\t");
            buf.append("(");
            for(String protein: indistinguishableProteins) {
                buf.append(protein+",");
            }
            buf.deleteCharAt(buf.length() - 1); // remove last comma
            buf.append(")");
        }
        buf.append("\t");
        buf.append("totalSpecCnt: "+totalSpectrumCount);
        buf.append("\t");
        buf.append("pctSpecCnt: "+pctSpectrumCount);
        
        for(ProteinProphetProteinPeptideIon peptide: this.peptides) {
            buf.append("\n");
            buf.append(peptide.toString());
        }
        return buf.toString();
    }
}
