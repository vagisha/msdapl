/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsEnzymeDAO;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.RunFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<MsRun, MsRunDb> {

    private MsExperimentDAO expDao;
    private MsScanDAO<MsScan, MsScanDb> msScanDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsRunDAOImpl(SqlMapClient sqlMap, MsEnzymeDAO enzymeDao , 
            MsScanDAO<MsScan, MsScanDb> msScanDAO, MsExperimentDAO expDao) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
        this.msScanDao = msScanDAO;
        this.expDao = expDao;
    }

    public int saveRun(MsRun run, int experimentId) {
        
        int runId = saveAndReturnId("MsRun.insert", run);
        expDao.saveRunExperiment(experimentId, runId);
        
        // save the enzyme information
        List<MsEnzyme> enzymes = run.getEnzymeList();
        for (MsEnzyme enzyme: enzymes) 
            // use the enzyme name attribute only to look for a matching enzyme.
            enzymeDao.saveEnzymeforRun(enzyme, runId, Arrays.asList(new EnzymeProperties[] {EnzymeProperties.NAME}));
        
        return runId;
    }

    public MsRunDb loadRun(int runId) {
        return (MsRunDb) queryForObject("MsRun.select", runId);
    }
    
    public List<MsRunDb> loadExperimentRuns(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
    
    @Override
    public int loadRunIdForExperimentAndFileName(int experimentId, String fileName) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("experimentId", experimentId);
        map.put("fileName", fileName);
        Integer runId = (Integer)queryForObject("MsRun.selectRunIdForExperimentAndFileName", map);
        if (runId != null)
            return runId;
        return 0;
    }
    
    public List<Integer> runIdsFor(String fileName, String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunIdsForFileNameAndSha1Sum", map);
    }
    
    
    /**
     * Delete only the top level run; everything else is deleted via SQL triggers.
     */
    public void delete(int runId) {
        // delete the run
        delete("MsRun.delete", runId);
    }
   
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        MsRunDb run = loadRun(runId);
        
        if (run == null) {
            throw new Exception("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between RunFileFormat and JDBC's VARCHAR types. 
     */
    public static class RunFileFormatTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String format = getter.getString();
            if (getter.wasNull())
                return RunFileFormat.UNKNOWN;
            return RunFileFormat.instance(format);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(((RunFileFormat)parameter).name());
        }

        public Object valueOf(String s) {
            return RunFileFormat.instance(s);
        }
    }
}
