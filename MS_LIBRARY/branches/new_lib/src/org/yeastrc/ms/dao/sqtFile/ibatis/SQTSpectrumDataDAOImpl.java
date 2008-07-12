/**
 * SQTSpectrumDataDAO.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;
import org.yeastrc.ms.domain.sqtFile.impl.SQTSearchScanDbImpl;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSpectrumDataDAOImpl extends BaseSqlMapDAO implements SQTSearchScanDAO {

    public SQTSpectrumDataDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchScanDbImpl load(int searchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("searchId", searchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return (SQTSearchScanDbImpl) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(SQTSearchScan scanData, int searchId, int scanId) {
        SQTSearchScanSqlMapParam scanDataDb = new SQTSearchScanSqlMapParam(searchId, scanId, scanData);
        save("SqtSpectrum.insert", scanDataDb);
    }
    
    public void deleteForSearch(int searchId) {
        delete("SqtSpectrum.deleteForSearch", searchId);
    }

}
