/**
 * SQTHeaderDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile;


/**
 * 
 */
public interface SQTHeaderDb extends SQTField {

    /**
     * @return database id of the search to which this header belongs
     */
    public abstract int getRunSearchId();
    
    /**
     * @return database id of this header.
     */
    public abstract int getId();
}
