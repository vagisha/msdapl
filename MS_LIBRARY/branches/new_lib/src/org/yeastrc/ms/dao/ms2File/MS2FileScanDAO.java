/**
 * MS2FileScanDAO.java
 * @author Vagisha Sharma
 * Jun 30, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import org.yeastrc.ms.dto.ms2File.MS2FileScan;

/**
 * 
 */
public interface MS2FileScanDAO {

    public abstract int save(MS2FileScan scan);

    public abstract MS2FileScan load(int scanId);
    
}
