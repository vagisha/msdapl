/**
 * IMsSearchResultPeptide.java
 * @author Vagisha Sharma
 * Jul 9, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;


/**
 * 
 */
public interface MsSearchResultPeptide extends MsSearchResultPeptideBase {

    /**
     * Returns a list of dynamic modifications, along with the index (0-based) at which 
     * they are present, in the peptide sequence for this result.
     */
    public abstract List<MsResultDynamicResidueMod> getDynamicModifications();
    
    /**
     * Returns a list of terminal dynamic modifications.
     * @return
     */
    public abstract List<MsTerminalModification> getTerminalDynamicModifications();
}

interface MsSearchResultPeptideBase {
    
    public abstract String getPeptideSequence();
    
    public abstract char getPreResidue();
    
    public abstract char getPostResidue();
    
    public abstract int getSequenceLength();
    
}