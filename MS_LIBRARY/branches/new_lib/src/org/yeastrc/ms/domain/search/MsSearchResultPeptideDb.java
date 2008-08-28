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
public interface MsSearchResultPeptideDb extends MsSearchResultPeptideBase {

    /**
     * Returns a list of dynamic residue modifications, along with the index (0-based) at which 
     * they are present, in the peptide sequence for this result.
     */
    public abstract List<MsResultDynamicResidueModDb> getResultDynamicResidueModifications();
    
    /**
     * Returns a list of dynamic residue modifications.
     * @return
     */
    public abstract List<MsResultDynamicTerminalModDb> getResultDynamicTerminalModifications();
}
