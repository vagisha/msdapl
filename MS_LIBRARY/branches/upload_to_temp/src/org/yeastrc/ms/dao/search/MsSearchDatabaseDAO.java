package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchDatabase;

public interface MsSearchDatabaseDAO {

    public abstract List<MsSearchDatabase> loadSearchDatabases(int searchId);

    public abstract void deleteSearchDatabases(int searchId);

    /**
     * Links the search (represented by the searchId) with the given sequence database. 
     * If the given sequence database does not already exist in the 
     * msSequenceDatabaseDetails table, it is saved first.
     * @param database
     * @param searchId
     * @return id (from msSequenceDatabaseDetail) of the database that was linked to the searchId
     */
    public abstract int saveSearchDatabase(MsSearchDatabase database, int searchId);

    /**
     * Returns the stored nrseq database ID for a fasta database with the given filepath.
     * @param serverPath
     * @return
     */
    public abstract int getSequenceDatabaseId(String serverPath);
    
}