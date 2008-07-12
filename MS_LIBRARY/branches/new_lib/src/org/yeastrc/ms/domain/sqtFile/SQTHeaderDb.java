/**
 * SQTHeaderDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.sqtFile;

/**
 * 
 */
public interface SQTHeaderDb extends SQTField {

    /**
     * @return database id of the search to which this header bolongs
     */
    public abstract int getSearchId();
    
    /**
     * @return database id of this header.
     */
    public abstract int getId();
}
