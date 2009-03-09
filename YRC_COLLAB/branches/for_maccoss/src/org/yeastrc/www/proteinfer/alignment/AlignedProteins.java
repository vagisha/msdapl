/**
 * AlignedProteins.java
 * @author Vagisha Sharma
 * Mar 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.alignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class AlignedProteins {

    private AlignedProtein anchorProtein;
    private List<AlignedProtein> alignedProteins;
    
    public AlignedProteins() {
        alignedProteins = new ArrayList<AlignedProtein>();
    }
    
    public void setAnchorProtein(AlignedProtein protein) {
        this.anchorProtein = protein;
    }
    
    public void addAlignedPair(AlignedPair aPair) throws AlignmentException {
        
        AlignedProtein pairAProt = aPair.getProtein1(); // this should be the anchor protein in the aligned pair
        int i = 0, j = 0;
        for(; i < anchorProtein.getAlignedLength() && j < pairAProt.getAlignedLength(); i++, j++) {
            
            char c_i = anchorProtein.getCharAt(i);
            char c_j = pairAProt.getCharAt(j);
            if(c_i != c_j) {
                if(c_i == '-') {
                    aPair.insertGap(j);
                }
                else if(c_j == '-') {
                    this.insertGap(i);
                }
                else {
                    throw new AlignmentException("Inconsistent anchor sequences:\n"+
                            anchorProtein.getAlignedSequence()+"\n"+
                            pairAProt.getAlignedSequence());
                }
            }
        }
        for(; i < anchorProtein.getAlignedLength(); i++) {
            aPair.insertGap(aPair.getAlignedLength());
            j++;
        }
        for(; j < aPair.getAlignedLength(); j++) {
            anchorProtein.insertGap(anchorProtein.getAlignedLength());
            i++;
        }
        this.alignedProteins.add(aPair.getProtein2());
    }
    
    public void insertGap(int index) throws AlignmentException {
        anchorProtein.insertGap(index);
        for(AlignedProtein prot: alignedProteins)
            prot.insertGap(index);
    }
    
    public void updateMismatches() {
        
        Set<Integer> mismatchedIndexes = new HashSet<Integer>();
        for(int i = 0; i < anchorProtein.getAlignedLength(); i++) {
            
            char c = anchorProtein.getCharAt(i);
            if(c == '-')
                continue; // TODO this is not right
            for(AlignedProtein prot: alignedProteins) {
                char c_a = prot.getCharAt(i);
                if(c_a != '-' && c != c_a) {
                    mismatchedIndexes.add(i);
                    break;
                }
            }
        }
        System.out.println("NUMBER OF MIMATCHES: "+mismatchedIndexes.size());
        anchorProtein.setMismatchedIndexes(mismatchedIndexes);
        for(AlignedProtein prot: alignedProteins)
            prot.setMismatchedIndexes(mismatchedIndexes);
    }
    
    public int getAlignmentLength() {
        return anchorProtein.getAlignedLength();
    }

    public AlignedProtein getAnchorProtein() {
        return anchorProtein;
    }

    public List<AlignedProtein> getAlignedProteins() {
        return alignedProteins;
    }
    
    public String printAlignment() {
        StringBuilder buf = new StringBuilder();
        buf.append(anchorProtein.getAlignedSequence());
        buf.append("\n");
        for(AlignedProtein prot: alignedProteins) {
            buf.append(prot.getAlignedSequence());
            buf.append("\n");
        }
        return buf.toString();
    }
}
