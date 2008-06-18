/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import org.yeastrc.ms.MsRun;

/**
 * 
 */
public class MsRunDAOImpl extends BaseSqlMapDAO {

    
    public int save(MsRun run) {
        return (Integer)insert("MsRun.insert", run);
    }
    
}
