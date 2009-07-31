/**
 * ProteinProphetProtein.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

/**
 * 
 */
public class ProteinProphetProtein extends GenericProteinferProtein<ProteinProphetProteinPeptide> {

    private String proteinName;
    private double probability;
    private int totalSpectrumCount;
    private double pctSpectrumCount;
    private double confidence;
    private boolean isSubsumed = false;
    private String subsumingProteinEntry;
    
    private int indistinguishableGroupId;
    private int proteinProphetGroupId;
    
    private List<String> indistinguishableProteins;
    private List<ProteinProphetProteinPeptide> peptides;
    
    
    public ProteinProphetProtein() {
        this(10);
    }
    
    public ProteinProphetProtein(int numIndistinguishableProteins) {
        this.indistinguishableProteins = new ArrayList<String>(numIndistinguishableProteins);
        peptides = new ArrayList<ProteinProphetProteinPeptide>();
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

    public ProteinProphetProtein getIndistinguishableProtein(String name) {
        if(!indistinguishableProteins.contains(name))
            return null;
        ProteinProphetProtein protein = new ProteinProphetProtein();
        protein.setProteinName(name);
        protein.setConfidence(this.confidence);
        protein.setCoverage(this.getCoverage());
        protein.setIndistinguishableGroupId(this.indistinguishableGroupId);
        protein.setPctSpectrumCount(this.pctSpectrumCount);
        protein.setPeptideDefinition(this.getPeptideDefinition());
        protein.setPeptides(this.getPeptides());
        protein.setProbability(this.probability);
        protein.setProteinferId(this.getProteinferId());
        protein.setProteinProphetGroupId(this.getProteinProphetGroupId());
        protein.setSubsumed(this.isSubsumed);
        protein.setSubsumingProteinEntry(this.subsumingProteinEntry);
        protein.setTotalSpectrumCount(this.totalSpectrumCount);
        protein.setUserAnnotation(this.getUserAnnotation());
        protein.setUserValidation(this.getUserValidation());
        return protein;
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

    public List<ProteinProphetProteinPeptide> getPeptides() {
        return peptides;
    }
    
    public void addPeptide(ProteinProphetProteinPeptide peptide) {
        peptides.add(peptide);
    }
    
    public int getIndistinguishableGroupId() {
        return indistinguishableGroupId;
    }

    public void setIndistinguishableGroupId(int indistinguishableGroupId) {
        this.indistinguishableGroupId = indistinguishableGroupId;
    }

    public int getProteinProphetGroupId() {
        return proteinProphetGroupId;
    }

    public void setProteinProphetGroupId(int proteinProphetGroupId) {
        this.proteinProphetGroupId = proteinProphetGroupId;
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
        
        for(ProteinProphetProteinPeptide peptide: this.peptides) {
            buf.append("\n");
            buf.append(peptide.toString());
        }
        return buf.toString();
    }
}
