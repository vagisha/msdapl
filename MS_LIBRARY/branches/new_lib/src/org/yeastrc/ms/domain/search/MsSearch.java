/**
 * MsSearch.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public interface MsSearch extends MsSearchBase {

    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabase> getSearchDatabases();
    
    /**
     * @return the staticModifications
     */
    public abstract List<MsSearchModification> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsSearchModification> getDynamicModifications();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzyme> getEnzymeList();
   
}

interface MsSearchBase {
    
    /**
     * @return the serverAddress
     */
    public abstract String getServerAddress();

    /**
     * @return the serverDirectory
     */
    public abstract String getServerDirectory();

    /**
     * @return the searchDate
     */
    public abstract Date getSearchDate();
    
    /**
     * @return the analysisProgramName
     */
    public abstract String getAnalysisProgramName();

    /**
     * @return the analysisProgramVersion
     */
    public abstract String getAnalysisProgramVersion();

}
