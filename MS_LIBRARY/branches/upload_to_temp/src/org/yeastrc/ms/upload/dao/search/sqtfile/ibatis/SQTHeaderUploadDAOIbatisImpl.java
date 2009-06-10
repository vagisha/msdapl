/**
 * SQTHeaderUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTHeaderUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTHeaderUploadDAOIbatisImpl extends BaseSqlMapDAO implements SQTHeaderUploadDAO {

    public SQTHeaderUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void saveSQTHeader(SQTHeaderItem header) {
        save("SqtHeader.insertHeader",header);
    }
}
