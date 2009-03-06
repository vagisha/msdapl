package org.yeastrc.www.proteinfer.alignment;

import java.util.ArrayList;
import java.util.List;

public class AlignedProtein {

    private int pinferProteinId;
    private int nrseqId;
    private String accession;
    private String description;
    private List<AlignedPosition> gappedSequence;
    private StringBuilder gapless;
    private List<Integer> gaplessToGapped;

    
    public AlignedProtein() {
        gappedSequence = new ArrayList<AlignedPosition>();
        gaplessToGapped = new ArrayList<Integer>();
    }
    
    public void addToSequence(char seqChar, boolean mismatched, boolean covered) {
        AlignedPosition pos = new AlignedPosition(seqChar, mismatched, covered);
        gappedSequence.add(pos);
        if(!pos.isGap()) {
            gapless.append(gapless);
            gaplessToGapped.add(gappedSequence.size() - 1);
        }
    }
    
    public void markCovered(String subseq) {
        String seq = gapless.toString();
        int s = 0;
        int idx = 0;
        while((idx = seq.indexOf(subseq, s)) != -1) {
            
            for (int j = idx; j < idx+subseq.length(); j++) {
                gappedSequence.get(gaplessToGapped.get(j)).setCovered(true);
            }
            
            s = idx + 1;
            if(s >= seq.length())   break;
        }
    }
    
    public int getLength() {
        return gappedSequence.size();
    }

    public int getPinferProteinId() {
        return pinferProteinId;
    }

    public int getNrseqId() {
        return nrseqId;
    }

    public void setNrseqId(int nrseqId) {
        this.nrseqId = nrseqId;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AlignedPosition> getSequence() {
        return gappedSequence;
    }


    public static final class AlignedPosition {
        private char character;
        private boolean mismatched;
        private boolean covered;
        
        public AlignedPosition(char seqChar, boolean mismatched, boolean covered) {
            this.character = seqChar;
            this.mismatched = mismatched;
            this.covered = covered;
        }

        public char getCharacter() {
            return character;
        }

        public boolean isMismatched() {
            return mismatched;
        }
        
        public void setMismatches(boolean mismatched) {
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


