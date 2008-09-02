/**
 * MsSearchDatabaseDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public interface MsSearchDatabaseDb extends MsSearchDatabase {

    /**
     * @return database id of the search database
     */
    public abstract int getId();
    
    /**
     * The name of the fasta file
     * @return
     */
    public abstract String getDatabaseFileName();
}
