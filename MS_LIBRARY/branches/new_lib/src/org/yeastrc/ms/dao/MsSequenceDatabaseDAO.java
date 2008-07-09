package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsSequenceDatabase;
import org.yeastrc.ms.dto.MsSequenceDatabase;

public interface MsSequenceDatabaseDAO {

    public abstract List<MsSequenceDatabase> loadSearchDatabases(
            int searchId);

    public abstract void deleteSearchDatabases(int searchId);

    /**
     * Links the search with the sequence database in the msSearchDababase table. 
     * If the given sequence database does not already exist in the 
     * msSequenceDatabaseDetails table, it is saved first.
     * @param database
     * @param searchId
     * @return id of the sequence database that was linked to the searchId
     */
    public abstract int saveSearchDatabase(IMsSequenceDatabase database,
            int searchId);

}