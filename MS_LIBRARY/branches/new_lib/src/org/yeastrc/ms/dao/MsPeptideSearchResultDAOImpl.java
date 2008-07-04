package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsPeptideSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsPeptideSearchResultDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchResultDAO {

    public MsPeptideSearchResultDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#load(int)
     */
    public MsPeptideSearchResult load(int id) {
        return (MsPeptideSearchResult) queryForObject("MsSearchResult.select", id);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#save(org.yeastrc.ms.dto.MsPeptideSearchResult)
     */
    public int save(MsPeptideSearchResult searchResult) {
        return saveAndReturnId("MsSearchResult.insert", searchResult);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#delete(int)
     */
    public void delete(int id) {
        delete("MsSearchResult.delete", id);
    }
}
