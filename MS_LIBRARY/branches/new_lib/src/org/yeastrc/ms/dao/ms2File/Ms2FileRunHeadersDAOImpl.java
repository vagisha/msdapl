/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.Ms2FileHeaders;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class Ms2FileRunHeadersDAOImpl extends BaseSqlMapDAO implements MS2FileRunHeadersDAO {

    public Ms2FileRunHeadersDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public boolean save(Ms2FileHeaders headers) {
        return save("Ms2FileRunHeaders.insert", headers);
    }
    
    public Ms2FileHeaders loadHeadersForRun(int runId) {
        return (Ms2FileHeaders) queryForObject("Ms2FileRunHeaders.selectHeadersForRun", runId);
    }
    
}
