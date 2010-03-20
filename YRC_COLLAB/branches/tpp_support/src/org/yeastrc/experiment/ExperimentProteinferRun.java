/**
 * ExperimentProteinferRun.java
 * @author Vagisha Sharma
 * May 27, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.www.proteinfer.job.ProteinferJob;

/**
 * 
 */
public class ExperimentProteinferRun {

    private final ProteinferJob job;
    private int uniqPeptideSequenceCount;
    private int numParsimoniousProteins;
    private int numParsimoniousProteinGroups;
    
    public ExperimentProteinferRun(ProteinferJob job) {
        this.job = job;
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

    public ProteinferJob getJob() {
        return job;
    }
}
