/**
 * ProteinProphetPeptide.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinprophet;

/**
 * 
 */
public class ProteinProphetPeptide {

    private String sequence;
    private int charge;
    private boolean isUniqueToProtein;
    private double numSiblingPeptides;
    private boolean isContributingEvidence;
    
    
    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    public boolean isUniqueToProtein() {
        return isUniqueToProtein;
    }
    public void setUniqueToProtein(boolean isUniqueToProtein) {
        this.isUniqueToProtein = isUniqueToProtein;
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
    public int getCharge() {
        return charge;
    }
    public void setCharge(int charge) {
        this.charge = charge;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(sequence);
        buf.append("\t");
        buf.append("chg: "+charge);
        buf.append("\t");
        buf.append("isUniq: "+isUniqueToProtein);
        buf.append("\t");
        buf.append("isContrib: "+isContributingEvidence);
        return buf.toString();
    }
}
