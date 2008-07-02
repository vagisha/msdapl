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
public class MS2FileHeaderDAOImpl extends BaseSqlMapDAO implements MS2FileHeaderDAO {

    public MS2FileHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2FileHeader header) {
        save("Ms2Header.insert", header);
    }
    
    public List<MS2FileHeader> loadHeadersForRun(int runId) {
        return queryForList("Ms2Header.selectHeadersForRun", runId);
    }
}
