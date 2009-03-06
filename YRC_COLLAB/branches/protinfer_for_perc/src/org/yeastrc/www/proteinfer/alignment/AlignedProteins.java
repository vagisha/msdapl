/**
 * AlignedProteins.java
 * @author Vagisha Sharma
 * Mar 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.alignment;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.proteinfer.alignment.AlignedProtein.AlignedPosition;

/**
 * 
 */
public class AlignedProteins {

    private int alignmentLength;
    private AlignedProtein anchorProtein;
    private List<AlignedProtein> alignedProteins;
    private List<AlignedPosition> alignmentTrack;
    
    public AlignedProteins() {
        alignedProteins = new ArrayList<AlignedProtein>();
    }
    
    public void setAnchorProtein(AlignedProtein protein) {
        this.anchorProtein = protein;
        this.alignmentLength = anchorProtein.getLength();
        
        alignmentTrack = new ArrayList<AlignedPosition>(alignmentLength);
        for(AlignedPosition pos: anchorProtein.getSequence()) {
            alignmentTrack.add(new AlignedPosition(' ', false, pos.isCovered()));
        }
    }
    
    public void addAlignedProtein(AlignedProtein protein) throws AlignmentException {
        if(protein.getLength() != alignmentLength) {
            throw new AlignmentException("Aligned protein "+protein.getAccession()+" not the same length as anchor protein");
        }
        
        List<AlignedPosition> protSeq = protein.getSequence();
        
        for(int i = 0;i < alignmentTrack.size(); i++) {
            AlignedPosition track_pos = alignmentTrack.get(i);
            AlignedPosition prot_pos = protSeq.get(i);
            
            if(track_pos.isGap() || prot_pos.isGap())
                continue;
            
            if(track_pos.getCharacter() != prot_pos.getCharacter()) {
                track_pos.setMismatches(true);
                prot_pos.setMismatches(true);
            }
            
            if(prot_pos.isCovered())
                track_pos.setCovered(true);
        }
        
        alignedProteins.add(protein);
    }

    public int getAlignmentLength() {
        return alignmentLength;
    }

    public AlignedProtein getAnchorProtein() {
        return anchorProtein;
    }

    public List<AlignedProtein> getAlignedProteins() {
        return alignedProteins;
    }

    public List<AlignedPosition> getAlignmentTrack() {
        return alignmentTrack;
    }
    
    
}
