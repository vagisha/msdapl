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
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchResultProteinDAOImpl extends BaseSqlMapDAO implements MsSearchResultProteinDAO {


    public MsSearchResultProteinDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchResultProtein> loadResultProteins(int resultId) {
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
    public void save(MsSearchResultProteinIn resultProtein, String sequenceDbName, int resultId) {
        int proteinId = NrSeqLookupUtil.getProteinId(sequenceDbName, resultProtein.getAccession());
        if (proteinId == 0)
            throw new NrSeqLookupException(sequenceDbName, resultProtein.getAccession());
        save("MsResultProtein.insert", new SearchResultProteinBean(resultId, proteinId));
    }
    
    public void save(MsSearchResultProteinIn resultProtein, int sequenceDbId, int resultId) {
        int proteinId = NrSeqLookupUtil.getProteinId(sequenceDbId, resultProtein.getAccession());
        if (proteinId == 0)
            throw new NrSeqLookupException(sequenceDbId, resultProtein.getAccession());
        save("MsResultProtein.insert", new SearchResultProteinBean(resultId, proteinId));
    }
    
    @Override
    public void saveAll(List<MsSearchResultProtein> proteinMatchList) {
        if (proteinMatchList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsSearchResultProtein match: proteinMatchList) {
            values.append(",(");
            values.append(match.getResultId() == 0 ? "NULL" : match.getResultId());
            values.append(",");
            values.append(match.getProteinId() == 0 ? "NULL" : match.getProteinId());
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
}


