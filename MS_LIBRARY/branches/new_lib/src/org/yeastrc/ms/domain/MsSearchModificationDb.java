/**
 * MsSearchModificationDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public interface MsSearchModificationDb extends MsSearchModification {

    /**
     * @return database id of the search this modification belongs to.
     */
    public abstract int getSearchId();
    
    /**
     * @return database id of the modification
     */
    public abstract int getId();
}
