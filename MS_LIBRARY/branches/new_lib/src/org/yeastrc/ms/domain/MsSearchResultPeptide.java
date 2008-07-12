/**
 * IMsSearchResultPeptide.java
 * @author Vagisha Sharma
 * Jul 9, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

/**
 * 
 */
public interface MsSearchResultPeptide {

    public abstract String getPeptideSequence();
    
    public abstract char getPreResidue();
    
    public abstract char getPostResidue();
    
    public abstract int getSequenceLength();
    
    public abstract List<? extends MsSearchResultModification> getDynamicModifications();
}
