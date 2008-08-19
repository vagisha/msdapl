/**
 * MsResultDynamicResidueModDb.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

/**
 * 
 */
public interface MsResultDynamicResidueModDb extends MsResultDynamicResidueMod {

    /**
     * @return database id of the modification which appears in the peptide sequence of the result
     */
    public abstract int getModification();
    
    /**
     * @return database id of the result which has this modification
     */
    public abstract int getResultId();
}
