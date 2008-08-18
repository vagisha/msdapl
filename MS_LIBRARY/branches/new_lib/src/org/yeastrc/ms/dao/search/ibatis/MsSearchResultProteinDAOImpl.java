/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
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

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#loadResultProteins(int)
     */
    public List<MsSearchResultProteinDb> loadResultProteins(int resultId) {
        return queryForList("MsResultProtein.selectResultProteins", resultId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#save(org.yeastrc.ms.dto.MsProteinMatch)
     */
    public void save(MsSearchResultProtein proteinMatch, int resultId) {
        save("MsResultProtein.insert", new MsResultProteinSqlMapParam(resultId, 
                                                    proteinMatch.getAccession(),
                                                    proteinMatch.getDescription()));
    }
    
    @Override
    public void saveAll(List<MsSearchResultProteinDb> proteinMatchList) {
        if (proteinMatchList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsSearchResultProteinDb match: proteinMatchList) {
            values.append("(");
            values.append(match.getResultId());
            values.append(",");
            if (match.getAccession() != null) {
                values.append("\"");
                values.append(match.getAccession());
                values.append("\"");
            }
            else
                values.append("NULL");
            
            values.append(",");
            
            if (match.getDescription() != null) {
                values.append("\"");
                values.append(match.getDescription());
                values.append("\"");
            }
            else {
                values.append("NULL");
            }
            values.append("),");
        }
        values.deleteCharAt(values.length() - 1);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#delete(int)
     */
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
   
    public static final class MsResultProteinSqlMapParam implements MsSearchResultProteinDb {
        private int resultId;
        private String accession;
        private String description;
        public MsResultProteinSqlMapParam(int resultId, String accession, String desription) {
            this.resultId = resultId;
            this.accession = accession;
            this.description = desription;
        }
        public int getResultId() {
            return resultId;
        }
        public String getAccession() {
            return accession;
        }
        public String getDescription() {
            return description;
        }
        @Override
        public int getId() {
            return 0;
        }
        
    }
}


