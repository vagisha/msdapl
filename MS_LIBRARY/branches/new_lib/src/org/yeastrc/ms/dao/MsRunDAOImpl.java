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
    
    public List<Integer> loadRunIdsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunIdsForExperiment", msExperimentId);
    }
    
    public List<MsRun> loadRunsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }

    public List<MsRun> loadRunsForFileNameAndSha1Sum(String fileName,
            String sha1Sum) {
        MsRun run = new MsRun();
        run.setFileName(fileName);
        run.setSha1Sum(sha1Sum);
        return queryForList("MsRun.selectRunsForFileNameAndSha1Sum", run);
    }
}
