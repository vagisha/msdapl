package org.yeastrc.www.proteinfer.alignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlignedProtein extends Protein {

    private String alignedSequence;
    Set<Integer> mismatchedIndexes;

    
    public AlignedProtein(int pinferProteinId, String sequence) {
        super(pinferProteinId, sequence);
        mismatchedIndexes = new HashSet<Integer>();
    }
    
    public AlignedProtein(Protein protein) {
        super(protein.getPinferProteinId(), protein.getSequence());
        this.setAccession(protein.getAccession());
        this.setDescription(protein.getDescription());
        this.setNrseqId(protein.getNrseqId());
        this.setCoveredFragments(protein.getCoveredFragments());
    }
    
    public void setAlignedSequence(String sequence) {
        this.alignedSequence = sequence;
    }
    
    public int getAlignedLength() {
        return alignedSequence.length();
    }
    
    public String getAlignedSequence() {
        return alignedSequence;
    }
    
    public void insertGap(int index) throws AlignmentException {
        // should be able to insert gap at the end of the sequence
        if(index < 0 || index > alignedSequence.length())
            throw new AlignmentException("Invalid index for gap: "+index);
        alignedSequence = alignedSequence.substring(0, index)+"-"+alignedSequence.substring(index);
    }
    
    public void setMismatchedIndexes(Set<Integer> mismatchedIndexes) {
        if(mismatchedIndexes != null)
            this.mismatchedIndexes = mismatchedIndexes;
    }
    
    public List<AlignedPosition> getAlignedPositions() {
        
        List<AlignedPosition> list = new ArrayList<AlignedPosition>(this.getAlignedLength());
        for(int i = 0; i < alignedSequence.length(); i++) {
            AlignedPosition pos = new AlignedPosition(alignedSequence.charAt(i));
            if(mismatchedIndexes.contains(i))
                pos.setMismatched(true);
            
            list.add(pos);
        }
        markCovered(list);
        return list;
    }
    
    public char getCharAt(int index) {
        return alignedSequence.charAt(index);
    }
    
    private void markCovered(List<AlignedPosition> posList) {
        
        String origSeq = this.getSequence();
        List<Integer> gaplessToGapped = getGaplessToGappedIndexes();
        for(String fragment: this.getCoveredFragments()) {
            int s = 0;
            int idx = 0;
            while((idx = origSeq.indexOf(fragment, s)) != -1) {

                for (int j = idx; j < idx+fragment.length(); j++) {
                    posList.get(gaplessToGapped.get(j)).setCovered(true);
                }
                s = idx + 1;
                if(s >= origSeq.length())   break;
            }
        }
    }
    
    private List<Integer> getGaplessToGappedIndexes() {
        String origSeq = this.getSequence();
        List<Integer> gaplessToGapped = new ArrayList<Integer>(origSeq.length());
        for(int i = 0, j = 0; i < alignedSequence.length(); i++) {
            if(alignedSequence.charAt(i) == '-')
                continue;
            gaplessToGapped.add(j, i);
            j++;
        }
        return gaplessToGapped;
    }

    public static final class AlignedPosition {
        private char character;
        private boolean mismatched;
        private boolean covered;
        
        public AlignedPosition(char seqChar) {
            this.character = seqChar;
        }

        public char getCharacter() {
            return character;
        }

        public boolean isMismatched() {
            return mismatched;
        }
        
        public void setMismatched(boolean mismatched) {
            this.mismatched = mismatched;
        }

        public boolean isCovered() {
            return covered;
        }
        
        public void setCovered(boolean covered) {
            this.covered = covered;
        }
        
        public boolean isGap() {
            return character == '-';
        }
    }
}


