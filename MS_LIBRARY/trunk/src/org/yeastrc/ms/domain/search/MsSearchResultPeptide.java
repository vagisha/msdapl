/**
 * MsSearchResultPeptideDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;

import org.yeastrc.ms.service.ModifiedSequenceBuilderException;


/**
 * 
 */
public interface MsSearchResultPeptide {

    /**
     * Returns a list of dynamic residue modifications, along with the index (0-based) at which 
     * they are present, in the peptide sequence for this result.
     */
    public abstract List<MsResultResidueMod> getResultDynamicResidueModifications();
    
    public abstract void setDynamicResidueModifications(List<MsResultResidueMod> dynaMods);
    
    /**
     * Returns a list of dynamic terminal modifications.
     * @return
     */
    public abstract List<MsResultTerminalMod> getResultDynamicTerminalModifications();

    public abstract void setDynamicTerminalModifications(List<MsResultTerminalMod> termDynaMods);
    
    
    public abstract String getPeptideSequence();
    
    public abstract void setPeptideSequence(String sequence);
    
    
    /**
     * Returns the modified peptide sequence: e.g. PEP[80]TIDE
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getModifiedPeptide() throws ModifiedSequenceBuilderException;
    
    /**
     * Returns the modified peptide along with the pre and post residues
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getFullModifiedPeptide() throws ModifiedSequenceBuilderException;
    
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
    
    public abstract void setPreResidue(char preResidue);
    
    
    public abstract char getPostResidue();
    
    public abstract void setPostResidue(char postResidue);
    
    
    public abstract int getSequenceLength();
}
