/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dto.sqtFile.SQTSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchResultDAOImpl extends BaseSqlMapDAO implements SQTSearchResultDAO {

    public SQTSearchResultDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchResult loadSQTResult(int resultId) {
        return (SQTSearchResult) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(SQTSearchResult sqtResult) {
        
        // first save the parent result
        MsPeptideSearchResultDAO resDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
        int resultId = resDao.save(sqtResult);
        
        // now save the SQT specific information
        sqtResult.setResultId(resultId);
        save("SqtResult.insert", sqtResult);
        return resultId;
    }
    
    public void deleteSQTResult(int resultId) {
        
        // delete the parent entry in the msPeptideSearchResult table
        MsPeptideSearchResultDAO resDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
        resDao.delete(resultId);
        
        delete("SqtResult.delete", resultId);
    }
}
