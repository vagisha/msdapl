/**
 * ProteinProphetProteinPeptide.java
 * @author Vagisha Sharma
 * Jul 17, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinprophet;

/**
 * 
 */
public class ProteinProphetProteinPeptide {

    private ProteinProphetPeptide peptide;
    private double initialProbability;
    private double nspAdjProbability;
    private double weight;
    private int numEnzymaticTermini;
    private int numInstances;
    
    public ProteinProphetProteinPeptide(ProteinProphetPeptide peptide) {
        this.peptide = peptide;
    }
    
    public int getNumInstances() {
        return numInstances;
    }
    public void setNumInstances(int numInstances) {
        this.numInstances = numInstances;
    }
    public ProteinProphetPeptide getPeptide() {
        return peptide;
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
    public int getNumEnzymaticTermini() {
        return numEnzymaticTermini;
    }
    public void setNumEnzymaticTermini(int numEnzymaticTermini) {
        this.numEnzymaticTermini = numEnzymaticTermini;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString());
        buf.append("\t");
        buf.append("initProb: "+initialProbability);
        buf.append("\t");
        buf.append("nspAdjProb: "+nspAdjProbability);
        buf.append("\t");
        buf.append("wt: "+weight);
        buf.append("\t");
        buf.append("ntt: "+numEnzymaticTermini);
        buf.append("\t");
        buf.append("numInst: "+numInstances);
        return buf.toString();
    }
}
