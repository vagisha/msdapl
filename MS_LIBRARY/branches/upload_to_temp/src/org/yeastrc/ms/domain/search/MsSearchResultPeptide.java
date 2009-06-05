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
    
    /**
     * Returns the modified peptide sequence: e.g. PEP[80]TIDE
     * @return
     */
    public abstract String getModifiedPeptide();
    
    /**
     * Returns the modified peptide along with the pre and post residues
     * @return
     */
    public abstract String getFullModifiedPeptide();
    
    /**
     * Returns the modified peptide in a program specific format
     * @return
     */
    public abstract String getModifiedPeptidePS();
    
    /**
     * Returns the modified peptide along with the pre and post residues
     * @return
     */
    public abstract String getFullModifiedPeptidePS();
    
    public abstract char getPreResidue();
    
    public abstract char getPostResidue();
    
    public abstract int getSequenceLength();
}
