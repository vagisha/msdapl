/**
 * SQTSpectrumDataDAO.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchScanDAOImpl extends BaseSqlMapDAO implements SQTSearchScanDAO {

    public SQTSearchScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchScanBean load(int runSearchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return (SQTSearchScanBean) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(SQTSearchScan scanData) {
        save("SqtSpectrum.insert", scanData);
    }
    
    public void deleteForRunSearch(int runSearchId) {
        delete("SqtSpectrum.deleteForRunSearch", runSearchId);
    }

    @Override
    public void delete(int runSearchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        delete("SqtSpectrum.delete", map);
    }
}
