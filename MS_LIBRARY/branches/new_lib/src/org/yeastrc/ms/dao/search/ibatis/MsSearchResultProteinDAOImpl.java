/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchResultProteinDAOImpl extends BaseSqlMapDAO implements MsSearchResultProteinDAO {


    public MsSearchResultProteinDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchResultProteinDb> loadResultProteins(int resultId) {
        return queryForList("MsResultProtein.selectResultProteins", resultId);
    }
    
    @Override
    public boolean resultProteinExists(int resultId, int proteinId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("resultId", resultId);
        map.put("proteinId", proteinId);
        
        Integer count = (Integer) queryForObject("MsResultProtein.selectResultProteinCount", map);
        if (count == null || count == 0)
            return false;
        return true;
    }
    
    /**
     * @throws NrSeqLookupException if no matching id was found for the protein.
     */
    public void save(MsSearchResultProtein resultProtein, String searchDbName, int resultId) {
        int proteinId = NrSeqLookupUtil.getProteinId(searchDbName, resultProtein.getAccession());
        if (proteinId == 0)
            throw new NrSeqLookupException(searchDbName, resultProtein.getAccession());
        save("MsResultProtein.insert", new MsResultProteinSqlMapParam(resultId, proteinId));
    }
    
    @Override
    public void saveAll(List<MsSearchResultProteinDb> proteinMatchList) {
        if (proteinMatchList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsSearchResultProteinDb match: proteinMatchList) {
            values.append(",(");
            values.append(match.getResultId());
            values.append(",");
            values.append(match.getProteinId());
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
   
    public static final class MsResultProteinSqlMapParam implements MsSearchResultProteinDb {
        private int resultId;
        private int proteinId;
        public MsResultProteinSqlMapParam(int resultId, int proteinId) {
            this.resultId = resultId;
            this.proteinId = proteinId;
        }
        public int getResultId() {
            return resultId;
        }
        public int getProteinId() {
            return proteinId;
        }
    }
}


