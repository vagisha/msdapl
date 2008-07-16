/**
 * MS2RunDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.MsRunDb;

/**
 * 
 */
public interface MS2RunDb extends MsRunDb {

    /**
     * @return the list of headers for the MS2 run.
     */
    public abstract List<MS2HeaderDb> getHeaderList();
}
