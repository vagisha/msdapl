package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Peptide {

    private final String sequence;
    private String unmodifiedPeptide;
    private List<PeptideModification> modifications;
    
    public Peptide(String sequence) {
        this.sequence = sequence;
        modifications = new ArrayList<PeptideModification>();
    }
    
    public Peptide(String sequence, List<ProteinHit> proteins) {
        this(sequence);
    }
    
    /**
     * Returns the sequence of the peptide with modifications. E.g. PEP(80.0)TIDE
     * @return
     */
    public String getPeptideSeq() {
        
        if (unmodifiedPeptide != null)
            return unmodifiedPeptide;
        
        if (modifications.size() == 0) {
            unmodifiedPeptide = sequence;
        }
        else {
            int lastIdx = 0;
            StringBuilder seq = new StringBuilder();
            sortModifications();
            for (PeptideModification mod: modifications) {
                seq.append(sequence.subSequence(lastIdx, mod.getPosition()+1)); // get sequence up to an including the modified position.
                seq.append("("+mod.getMassShift()+")");
                lastIdx = mod.getPosition()+1;
            }
            if (lastIdx < sequence.length())
                seq.append(sequence.subSequence(lastIdx, sequence.length()));
            unmodifiedPeptide = seq.toString();
        }
        return unmodifiedPeptide;
    }
    
    /**
     * Returns the unmodified sequence of the peptide
     * @return
     */
    private void sortModifications() {
        Collections.sort(modifications, new Comparator<PeptideModification>(){
            public int compare(PeptideModification o1, PeptideModification o2) {
                return Integer.valueOf(o1.getPosition()).compareTo(Integer.valueOf(o2.getPosition()));
            }});
    }
    
    public String getUnmodifiedSequence() {
        return sequence;
    }
    
    public void addModification(PeptideModification modification) {
        this.modifications.add(modification);
    }
    
    public String toString() {
       return getPeptideSeq();
    }
}
