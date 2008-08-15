/**
 * MsSearch.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;

/**
 * 
 */
public interface MsSearch {

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
