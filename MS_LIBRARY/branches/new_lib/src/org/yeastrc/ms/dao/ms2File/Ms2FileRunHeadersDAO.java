/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.sql.SQLException;

import org.yeastrc.ms.dao.BaseDAO;
import org.yeastrc.ms.ms2File.db.Ms2FileHeaders;

/**
 * 
 */
public class Ms2FileRunHeadersDAO extends BaseDAO {

    public void insert(Ms2FileHeaders headers) {
        try {
            sqlMap.insert("Ms2FileRunHeaders.insert", headers);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
