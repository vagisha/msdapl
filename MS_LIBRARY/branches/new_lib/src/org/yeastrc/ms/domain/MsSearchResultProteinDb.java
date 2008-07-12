/**
 * MsSearchResultProteinDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public interface MsSearchResultProteinDb extends MsSearchResultProtein {

    /**
     * @return database id of the search result this protein matches.
     */
    public abstract int getResultId();
    
    /**
     * @return database id of this protein match
     */
    public abstract int getId();
}
