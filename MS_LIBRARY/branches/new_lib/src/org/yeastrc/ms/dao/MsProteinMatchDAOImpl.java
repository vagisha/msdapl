/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsProteinMatch;

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
    public void save(MsProteinMatch proteinMatch) {
        save("MsResultProtein.insert", proteinMatch);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsProteinMatchDAO#delete(int)
     */
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
}
