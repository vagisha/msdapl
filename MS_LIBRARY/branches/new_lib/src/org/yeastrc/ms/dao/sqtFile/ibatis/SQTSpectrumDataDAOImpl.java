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
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAO;
import org.yeastrc.ms.domain.sqtFile.ISQTSearchScan;
import org.yeastrc.ms.domain.sqtFile.db.SQTSpectrumData;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSpectrumDataDAOImpl extends BaseSqlMapDAO implements SQTSpectrumDataDAO {

    public SQTSpectrumDataDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSpectrumData load(int searchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("searchId", searchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return (SQTSpectrumData) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(ISQTSearchScan scanData) {
        save("SqtSpectrum.insert", scanData);
    }
    
    public void deleteForSearch(int searchId) {
        delete("SqtSpectrum.deleteForSearch", searchId);
    }

}
