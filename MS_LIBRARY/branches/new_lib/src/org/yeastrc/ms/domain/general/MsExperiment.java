/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;

import java.sql.Date;

/**
 * 
 */
public interface MsExperiment {

    public abstract int getId();
    
    public abstract String getServerAddress();
    
    public abstract Date getUploadDate();
    
    public abstract Date getLastUpdateDate();
}
