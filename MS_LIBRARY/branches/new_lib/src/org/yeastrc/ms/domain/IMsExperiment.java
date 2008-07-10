package org.yeastrc.ms.domain;

import java.sql.Date;

public interface IMsExperiment {

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