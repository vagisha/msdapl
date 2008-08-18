/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public interface MsRunSearchDb extends MsRunSearch {

    /**
     * @return database id of the run on which this search was done.
     */
    public abstract int getRunId();

    /**
     * @return database id of the search group
     */
    public abstract int getSearchId();
    
    /**
     * @return database id of this search
     */
    public abstract int getId();
    
}
