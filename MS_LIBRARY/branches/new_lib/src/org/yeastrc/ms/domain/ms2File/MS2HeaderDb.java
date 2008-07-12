/**
 * MS2Header.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

/**
 * 
 */
public interface MS2HeaderDb extends MS2Field {

    /**
     * @return database id of the run this belongs to.
     */
    public abstract int getRunId();
    
    /**
     * @return database id of the header.
     */
    public int getId();
    
}
