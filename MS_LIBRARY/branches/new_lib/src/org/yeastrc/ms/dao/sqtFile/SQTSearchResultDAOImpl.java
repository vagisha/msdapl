/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dao.MsPeptideSearchResultDAOImpl;
import org.yeastrc.ms.dto.sqtFile.SQTSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchResultDAOImpl extends MsPeptideSearchResultDAOImpl implements SQTSearchResultDAO {

    public SQTSearchResultDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchResult load(int resultId) {
        return (SQTSearchResult) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(SQTSearchResult sqtResult) {
        
        // first save the parent result
        int resultId = super.save(sqtResult);
        
        // now save the SQT specific information
        sqtResult.setResultId(resultId);
        save("SqtResult.insert", sqtResult);
        return resultId;
    }
    
    /**
     * Deletes the search result and any SQT specific information associated with the result
     * @param resultId
     */
    public void delete(int resultId) {
        
        // delete the parent entry in the msPeptideSearchResult table
        super.delete(resultId);
        
        delete("SqtResult.delete", resultId);
    }
}
