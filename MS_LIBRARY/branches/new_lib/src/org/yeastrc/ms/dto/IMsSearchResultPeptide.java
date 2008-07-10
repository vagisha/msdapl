/**
 * IMsSearchResultPeptide.java
 * @author Vagisha Sharma
 * Jul 9, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

/**
 * 
 */
public interface IMsSearchResultPeptide {

    public abstract String getPeptideSequence();
    
    public abstract char getPreResidue();
    
    public abstract char getPostResidue();
    
    public abstract int getSequenceLength();
    
    public abstract IMsSearchModification getDynamicModificationAtIndex(int index);
}
