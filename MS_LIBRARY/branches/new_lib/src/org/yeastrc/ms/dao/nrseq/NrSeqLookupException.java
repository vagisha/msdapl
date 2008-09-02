/**
 * NrSeqLookupException.java
 * @author Vagisha Sharma
 * Aug 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.nrseq;

/**
 * 
 */
public class NrSeqLookupException extends RuntimeException {

    public NrSeqLookupException(String dbName, String accession) {
        super("No proteinID found for database: "+dbName+" and accession: "+accession);
    }
    
    public NrSeqLookupException(int dbId, String accession) {
        super("No proteinID found for databaseId: "+dbId+" and accession: "+accession);
    }
    
    public NrSeqLookupException(String dbName) {
        super("No database found with name: "+dbName);
    }
    
    public NrSeqLookupException(int databaseId, int proteinId) {
        super("No entry found with databaseId: "+databaseId+" and proteinId: "+proteinId);
    }
}
