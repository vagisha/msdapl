/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;

/**
 * 
 */
public interface MsRunSearchDb extends MsRunSearchBase {

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

    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabaseDb> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<MsSearchModificationDb> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsSearchModificationDb> getDynamicModifications();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzymeDb> getEnzymeList();
}
