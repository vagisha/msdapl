/**
 * MsSearchResultPeptideDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;


/**
 * 
 */
public interface MsSearchResultPeptide {

    /**
     * Returns a list of dynamic residue modifications, along with the index (0-based) at which 
     * they are present, in the peptide sequence for this result.
     */
    public abstract List<MsResultResidueMod> getResultDynamicResidueModifications();
    
    /**
     * Returns a list of dynamic terminal modifications.
     * @return
     */
    public abstract List<MsResultTerminalMod> getResultDynamicTerminalModifications();

    
    public abstract String getPeptideSequence();
    
    public abstract String getModifiedPeptideSequence();
    
    public abstract char getPreResidue();
    
    public abstract char getPostResidue();
    
    public abstract int getSequenceLength();
}
