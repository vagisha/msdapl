/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsProteinMatchDAO;
import org.yeastrc.ms.domain.IMsSearchResultProtein;
import org.yeastrc.ms.domain.db.MsProteinMatch;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsProteinMatchDAOImpl extends BaseSqlMapDAO implements MsProteinMatchDAO {


    public MsProteinMatchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#loadResultProteins(int)
     */
    public List<MsProteinMatch> loadResultProteins(int resultId) {
        return queryForList("MsResultProtein.selectResultProteins", resultId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#save(org.yeastrc.ms.dto.MsProteinMatch)
     */
    public void save(IMsSearchResultProtein proteinMatch, int resultId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("match", proteinMatch);
        map.put("resultId", resultId);
        save("MsResultProtein.insert", map);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#delete(int)
     */
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
 
    //-------------------------------------------------------------------------------------------------
    // Class used for iBatis parametermap
    //-------------------------------------------------------------------------------------------------
    public class MsProteinMatchDb {
        
        int resultId;
        IMsSearchResultProtein resultProtein;
        
        /**
         * @return the resultId
         */
        public int getResultId() {
            return resultId;
        }
        /**
         * @return the resultProtein
         */
        public IMsSearchResultProtein getResultProtein() {
            return resultProtein;
        }

    }
}


