package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ModifiedPeptide {

    private final Peptide peptide;
    
    private String modifiedPeptide;
    private List<PeptideModification> modifications;
    
    public ModifiedPeptide(Peptide peptide) {
        this.peptide = peptide;
        modifications = new ArrayList<PeptideModification>();
    }
    
    public ModifiedPeptide(Peptide peptide, List<PeptideModification> modifications) {
        this(peptide);
        if (modifications != null) 
            this.modifications = modifications;
    }
    
    public Peptide getPeptide() {
        return peptide;
    }
    
    public void addModification(PeptideModification modification) {
        modifications.add(modification);
    }
    
    /**
     * Returns the sequence of the peptide with modifications. E.g. PEP(80.0)TIDE
     * @return
     */
    public String getPeptideSeq() {
        
        if (modifiedPeptide != null)
            return modifiedPeptide;
        
        if (modifications.size() == 0) {
            modifiedPeptide = peptide.getSequence();
        }
        else {
            int lastIdx = 0;
            StringBuilder seq = new StringBuilder();
            sortModifications();
            String sequence = peptide.getSequence();
            for (PeptideModification mod: modifications) {
                seq.append(sequence.subSequence(lastIdx, mod.getModifiedIndex()+1)); // get sequence up to an including the modified position.
                seq.append("("+mod.getMassShift()+")");
                lastIdx = mod.getModifiedIndex()+1;
            }
            if (lastIdx < sequence.length())
                seq.append(sequence.subSequence(lastIdx, sequence.length()));
            modifiedPeptide = seq.toString();
        }
        return modifiedPeptide;
    }
    
    /**
     * Returns the unmodified sequence of the peptide
     * @return
     */
    private void sortModifications() {
        Collections.sort(modifications, new Comparator<PeptideModification>(){
            public int compare(PeptideModification o1, PeptideModification o2) {
                return Integer.valueOf(o1.getModifiedIndex()).compareTo(Integer.valueOf(o2.getModifiedIndex()));
            }});
    }
    
    public String getUnmodifiedSequence() {
        return peptide.getSequence();
    }
    
    public String toString() {
        return getPeptideSeq();
    }
}
