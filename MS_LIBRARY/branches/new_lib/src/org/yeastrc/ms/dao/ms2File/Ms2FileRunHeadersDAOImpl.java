/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.MS2FileHeader;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class Ms2FileRunHeadersDAOImpl extends BaseSqlMapDAO implements MS2FileRunHeadersDAO {

    public Ms2FileRunHeadersDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public boolean save(MS2FileHeader headers) {
        return save("Ms2FileRunHeaders.insert", headers);
    }
    
    public List<MS2FileHeader> loadHeadersForRun(int runId) {
        return queryForList("Ms2FileRunHeaders.selectHeadersForRun", runId);
    }
}
