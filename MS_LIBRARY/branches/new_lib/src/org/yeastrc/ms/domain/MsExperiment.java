/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.sql.Date;

public interface MsExperiment {

    /**
     * @return the serverAddress
     */
    public abstract String getServerAddress();

    /**
     * @return the serverDirectory
     */
    public abstract String getServerDirectory();

    /**
     * @return the date
     */
    public abstract Date getDate();

}