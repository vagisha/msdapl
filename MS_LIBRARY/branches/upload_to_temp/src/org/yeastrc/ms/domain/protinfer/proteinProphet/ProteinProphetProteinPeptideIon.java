/**
 * ProteinProphetProteinPeptide.java
 * @author Vagisha Sharma
 * Jul 17, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

/**
 * 
 */
public class ProteinProphetProteinPeptideIon extends GenericProteinferIon<ProteinferSpectrumMatch> {

    private int pinferProteinId;
   
    private double initialProbability;
    private double nspAdjProbability;
    private double weight;
    private double numSiblingPeptides;
    private boolean isContributingEvidence;
    private List<ProteinferSpectrumMatch> psmList;
    
    private List<Modification> modifications;
    private String unmodifiedSequence;
    
    

    public ProteinProphetProteinPeptideIon() {
        psmList = new ArrayList<ProteinferSpectrumMatch>();
        modifications = new ArrayList<Modification>();
    }
    
    public double getInitialProbability() {
        return initialProbability;
    }
    public void setInitialProbability(double initialProbability) {
        this.initialProbability = initialProbability;
    }
    public double getNspAdjProbability() {
        return nspAdjProbability;
    }
    public void setNspAdjProbability(double nspAdjProbability) {
        this.nspAdjProbability = nspAdjProbability;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public boolean isContributingEvidence() {
        return isContributingEvidence;
    }
    public void setContributingEvidence(boolean isContributingEvidence) {
        this.isContributingEvidence = isContributingEvidence;
    }
    public double getNumSiblingPeptides() {
        return numSiblingPeptides;
    }
    public void setNumSiblingPeptides(double numSiblingPeptides) {
        this.numSiblingPeptides = numSiblingPeptides;
    }
    
    public List<ProteinferSpectrumMatch> getPsmList() {
        return psmList;
    }
    
    public void addPsm(ProteinferSpectrumMatch psm) {
        this.psmList.add(psm);
    }
    
    public List<Modification> getModifications() {
        return modifications;
    }
    
    public void addModification(Modification mod) {
        this.modifications.add(mod);
    }
    
    public int getPinferProteinId() {
        return pinferProteinId;
    }

    public void setPinferProteinId(int pinferProteinId) {
        this.pinferProteinId = pinferProteinId;
    }
    
    public String getUnmodifiedSequence() {
        return unmodifiedSequence;
    }

    public void setUnmodifiedSequence(String unmodifiedSequence) {
        this.unmodifiedSequence = unmodifiedSequence;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append("\t");
        buf.append("initProb: "+initialProbability);
        buf.append("\t");
        buf.append("nspAdjProb: "+nspAdjProbability);
        buf.append("\t");
        buf.append("wt: "+weight);
        buf.append("\t");
        buf.append("numsiblingPept: "+numSiblingPeptides);
        buf.append("\t");
        buf.append("contrib_evidence: "+this.isContributingEvidence);
        return buf.toString();
    }
    
    public static class Modification {
        private int position;
        private BigDecimal mass;
        
        public Modification(int pos, BigDecimal mass) {
            this.position = pos;
            this.mass = mass;
        }
        public int getPosition() {
            return position;
        }
        public void setPosition(int position) {
            this.position = position;
        }
        public BigDecimal getMass() {
            return mass;
        }
        public void setMass(BigDecimal mass) {
            this.mass = mass;
        }
    }
}
