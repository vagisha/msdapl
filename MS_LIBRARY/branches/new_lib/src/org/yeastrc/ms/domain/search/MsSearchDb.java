/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;

/**
 * 
 */
public interface MsSearchDb extends MsSearch {

    /**
     * @return the database id for this search
     */
    public abstract int getId();
    
    /**
     * @return the date this search was uploaded
     */
    public abstract Date getUploadDate();
}
