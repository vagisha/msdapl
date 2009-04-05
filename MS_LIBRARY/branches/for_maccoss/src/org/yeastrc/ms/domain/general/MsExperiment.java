/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 
 */
public interface MsExperiment {

    public abstract int getId();
    
    public abstract String getServerAddress();
    
    public abstract String getServerDirectory();
    
    public abstract Date getUploadDate();
    
    public abstract Timestamp getLastUpdateDate();
    
    public abstract String getComments();
}
