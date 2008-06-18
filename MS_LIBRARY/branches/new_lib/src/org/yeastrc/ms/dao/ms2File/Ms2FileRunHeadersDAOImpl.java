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
public class Ms2FileRunHeadersDAOImpl extends BaseSqlMapDAO implements MS2FileRunHeadersDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.MS2FileRunHeadersDAO#save(org.yeastrc.ms.ms2File.Ms2FileHeaders)
     */
    public void save(Ms2FileHeaders headers) {
        insert("Ms2FileRunHeaders.insert", headers);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.MS2FileRunHeadersDAO#loadHeadersForRun(int)
     */
    public Ms2FileHeaders loadHeadersForRun(int runId) {
        return (Ms2FileHeaders) queryForObject("Ms2FileRunHeaders.selectHeadersForRun", runId);
    }
    
}
