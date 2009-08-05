/**
 * ExperimentProteinInferRun.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;

/**
 * 
 */
public class ExperimentProteinProphetRun {

    private final ProteinProphetRun run;
    private int uniqPeptideSequenceCount;
    private int numParsimoniousProteins;
    private int numParsimoniousProteinGroups;
    
    public ExperimentProteinProphetRun(ProteinProphetRun run) {
        this.run = run;
    }
    
    public ProteinProphetRun getProteinProphetRun() {
        return run;
    }
    
    public int getUniqPeptideSequenceCount() {
        return uniqPeptideSequenceCount;
    }

    public void setUniqPeptideSequenceCount(int uniqPeptideSequenceCount) {
        this.uniqPeptideSequenceCount = uniqPeptideSequenceCount;
    }

    public int getNumParsimoniousProteins() {
        return numParsimoniousProteins;
    }

    public void setNumParsimoniousProteins(int numParsimoniousProteins) {
        this.numParsimoniousProteins = numParsimoniousProteins;
    }

    public int getNumParsimoniousProteinGroups() {
        return numParsimoniousProteinGroups;
    }

    public void setNumParsimoniousProteinGroups(int numParsimoniousProteinGroups) {
        this.numParsimoniousProteinGroups = numParsimoniousProteinGroups;
    }
    
}
