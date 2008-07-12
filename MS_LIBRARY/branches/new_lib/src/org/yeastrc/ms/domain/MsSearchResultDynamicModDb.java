/**
 * MsSearchResultDynamicModDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public interface MsSearchResultDynamicModDb extends MsSearchModification {

    /**
     * @return database id of the result which has this modification
     */
    public abstract int getResultId();
    
    /**
     * @return database id of the modification which appears in the peptide sequence of the result
     */
    public abstract int getModificationId();
    
    /**
     * @return index (0 based) in the protein sequence at which this modification is present.
     */
    public abstract int getModifiedPosition();
    
}
