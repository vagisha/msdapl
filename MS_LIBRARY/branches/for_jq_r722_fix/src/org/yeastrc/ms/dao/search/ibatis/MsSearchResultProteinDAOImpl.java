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
    
    public void save(MsSearchResultProteinIn resultProtein, int resultId) {
        save("MsResultProtein.insert", new SearchResultProteinBean(resultId, resultProtein.getAccession()));
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
            boolean hasAcc = match.getAccession() != null;
            if (hasAcc) values.append("\"");
            values.append(match.getAccession());
            if (hasAcc) values.append("\"");
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
}


