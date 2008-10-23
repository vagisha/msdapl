package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.List;

public class PeptideEvidence {
    
    private Peptide peptide;
    private List<PeptideSequenceMatch> psmList;
    
    public PeptideEvidence(Peptide peptide) {
        this.peptide = peptide;
        psmList = new ArrayList<PeptideSequenceMatch>();
    }
    
    public PeptideEvidence(Peptide peptide, List<PeptideSequenceMatch> psmList) {
        this(peptide);
        if (psmList != null)
            this.psmList = psmList;
    }
    
    public void addPeptideSequenceMatch(PeptideSequenceMatch psm) {
        psmList.add(psm);
    }
    
    /**
     * Returns the sequence of the peptide without modifications;
     * @return
     */
    public String getPeptideSeq() {
        return peptide.getSequence();
    }
    
    /**
     * Returns the unmodified sequence of the peptide
     * @return
     */
    public String getUnmodifiedSequence() {
        return peptide.getUnmodifiedSequence();
    }
    
    public int getMatchSpectraCount() {
        return psmList.size();
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString()+"\n");
        for(PeptideSequenceMatch psm: psmList) {
            buf.append("\t"+psm.toString()+"\n");
        }
        return buf.toString();
    }
}
