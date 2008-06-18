/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.ms2File.Ms2FileHeaders;

/**
 * 
 */
public class Ms2FileRunHeadersDAO extends BaseSqlMapDAO {

    public void save(Ms2FileHeaders headers) {
        insert("Ms2FileRunHeaders.insert", headers);
    }
}
