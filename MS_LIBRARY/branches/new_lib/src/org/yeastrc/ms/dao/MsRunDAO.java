/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.sql.SQLException;

import org.yeastrc.ms.MsRun;

/**
 * 
 */
public class MsRunDAO extends BaseSqlMapDAO {

    public int insertMsRun(MsRun run) {
        try {
            return (Integer)sqlMap.insert("MsRun.insert", run);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
