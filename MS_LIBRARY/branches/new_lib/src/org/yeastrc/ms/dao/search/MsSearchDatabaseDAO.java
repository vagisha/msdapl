package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;

public interface MsSearchDatabaseDAO {

    public abstract List<MsSearchDatabaseDb> loadSearchDatabases(int searchId);

    public abstract void deleteSearchDatabases(int searchId);

    /**
     * Links the search (represented by the searchId) with the given sequence database. 
     * If the given sequence database does not already exist in the 
     * msSequenceDatabaseDetails table, it is saved first.
     * @param database
     * @param searchId
     * @return id of the sequence database that was linked to the searchId
     */
    public abstract int saveSearchDatabase(MsSearchDatabase database, int searchId);

}