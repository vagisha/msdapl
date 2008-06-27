/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsRun;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO {

    public MsRunDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MsRun run) {
        return saveAndReturnId("MsRun.insert", run);
    }
    
    public MsRun load(int runId) {
        return (MsRun) queryForObject("MsRun.select", runId);
    }
    
    public List<MsRun> loadRunsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
}
