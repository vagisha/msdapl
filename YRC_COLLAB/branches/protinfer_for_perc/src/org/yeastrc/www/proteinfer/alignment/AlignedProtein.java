package org.yeastrc.www.proteinfer.alignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlignedProtein extends Protein {

    private String alignedSequence;
    private Set<Integer> mismatchedIndexes;
    private List<AlignedPosition> alignedPositions;

    
    public AlignedProtein(int pinferProteinId, int pinferProteinGroupId, String sequence) {
        super(pinferProteinId, pinferProteinGroupId, sequence);
        mismatchedIndexes = new HashSet<Integer>();
    }
    
    public AlignedProtein(Protein protein) {
        super(protein.getPinferProteinId(), protein.getPinferProteinGroupId(), protein.getSequence());
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
        if(alignedPositions != null)
            return alignedPositions;
        List<AlignedPosition> list = new ArrayList<AlignedPosition>(this.getAlignedLength());
        for(int i = 0; i < alignedSequence.length(); i++) {
            AlignedPosition pos = new AlignedPosition(alignedSequence.charAt(i));
            if(mismatchedIndexes.contains(i))
                pos.setMismatched(true);
            
            list.add(pos);
        }
        markCovered(list);
        alignedPositions = list;
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
    
    public List<Block> getUngappedBlocks() {
        List<AlignedPosition> posList = getAlignedPositions();
        List<Block> blks = new ArrayList<Block>();
        int lastIdx = -1;
        Block currBlk = null;
        for(int i = 0; i < posList.size(); i++) {
            AlignedPosition pos = posList.get(i);
            if(!pos.isGap()) {
                if(currBlk == null) {
                    currBlk = new Block();
                    currBlk.start = i;
                    currBlk.end = i;
                    blks.add(currBlk);
                    lastIdx = i;
                }
                else {
                    if(i == lastIdx + 1) {
                        currBlk.end = i;
                        lastIdx = i;
                    }
                    else {
                        currBlk = new Block();
                        currBlk.start = i;
                        currBlk.end = i;
                        blks.add(currBlk);
                        lastIdx = i;
                    }
                }
            }
        }
        return blks;
    }
    
    public List<Block> getCoveredBlocks() {
        List<AlignedPosition> posList = getAlignedPositions();
        List<Block> blks = new ArrayList<Block>();
        int lastIdx = -1;
        Block currBlk = null;
        for(int i = 0; i < posList.size(); i++) {
            AlignedPosition pos = posList.get(i);
            if(pos.isCovered()) {
                if(currBlk == null) {
                    currBlk = new Block();
                    currBlk.start = i;
                    currBlk.end = i;
                    blks.add(currBlk);
                    lastIdx = i;
                }
                else {
                    if(i == lastIdx + 1) {
                        currBlk.end = i;
                        lastIdx = i;
                    }
                    else {
                        currBlk = new Block();
                        currBlk.start = i;
                        currBlk.end = i;
                        blks.add(currBlk);
                        lastIdx = i;
                    }
                }
            }
        }
        return blks;
    }
    
    public List<Block> getMismatchedBlocks() {
        List<AlignedPosition> posList = getAlignedPositions();
        List<Block> blks = new ArrayList<Block>();
        int lastIdx = -1;
        Block currBlk = null;
        for(int i = 0; i < posList.size(); i++) {
            AlignedPosition pos = posList.get(i);
            if(!pos.isGap() && pos.isMismatched()) {
                if(currBlk == null) {
                    currBlk = new Block();
                    currBlk.start = i;
                    currBlk.end = i;
                    blks.add(currBlk);
                    lastIdx = i;
                }
                else {
                    if(i == lastIdx + 1) {
                        currBlk.end = i;
                        lastIdx = i;
                    }
                    else {
                        currBlk = new Block();
                        currBlk.start = i;
                        currBlk.end = i;
                        blks.add(currBlk);
                        lastIdx = i;
                    }
                }
            }
        }
        return blks;
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
        public String toString() {
            StringBuilder buf = new StringBuilder();
//            buf.append(character);
            if(mismatched)buf.append("-");
            else buf.append(" ");
            return buf.toString();
        }
    }
    
    public static final class Block {
        private int start;
        private int end;
        public int getStart() {
            return start;
        }
        public int getEnd() {
            return end;
        }
        
    }
}


