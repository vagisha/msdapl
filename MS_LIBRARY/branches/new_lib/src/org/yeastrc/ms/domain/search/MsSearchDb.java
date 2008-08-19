/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeDb;

/**
 * 
 */
public interface MsSearchDb extends MsSearchBase {

    /**
     * @return the database id for this search
     */
    public abstract int getId();
    
    /**
     * @return the date this search was uploaded
     */
    public abstract Date getUploadDate();
    
    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabaseDb> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<MsResidueModificationDb> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsResidueModificationDb> getDynamicModifications();
    
    /**
     * @return the terminal static modifications
     */
    public abstract List<MsTerminalModificationDb> getTerminalStaticModifications();
    
    /**
     * @return the terminal dynamic modifications
     */
    public abstract List<MsTerminalModificationDb> getTerminalDynamicModifications();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzymeDb> getEnzymeList();
}
