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

    private MsScanDAO<MsScan, MsScanDb> msScanDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsRunDAOImpl(SqlMapClient sqlMap, MsEnzymeDAO enzymeDao , 
            MsScanDAO<MsScan, MsScanDb> msScanDAO) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
        this.msScanDao = msScanDAO;
    }

    public int saveRun(MsRun run, int msExperimentId) {
        
        MsRunSqlMapParam runDb = new MsRunSqlMapParam(msExperimentId, run);
        // save the run
        int runId = saveAndReturnId("MsRun.insert", runDb);
        
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
    
    @Override
    public int loadExperimentIdForRun(int runId) {
        Integer id = (Integer)queryForObject("MsRun.selectExperimentIdForRun", runId);
        if (id != null)
            return id;
        return 0;
    }
    
    public List<MsRunDb> loadExperimentRuns(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
    
    public List<Integer> loadRunIdsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunIdsForExperiment", msExperimentId);
    }
    
    public List<Integer> runIdsFor(String fileName, String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunIdsForFileNameAndSha1Sum", map);
    }
    
    
    public void delete(int runId) {
        
        // delete enzyme information first
        enzymeDao.deleteEnzymesForRun(runId);
        
        // delete scans
        msScanDao.deleteScansForRun(runId);
        
        // delete the run
        delete("MsRun.delete", runId);
    }
   
    
    /**
     * This will delete all the runs associated with the given experimentId, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs, as well as the scans
     * 
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public List<Integer> deleteRunsForExperiment(int msExperimentId) {
        List<Integer> runIds = loadRunIdsForExperiment(msExperimentId);
        
        if (runIds.size() > 0) {
            // delete enzyme associations
            enzymeDao.deleteEnzymesForRuns(runIds);
        }
        
        for (Integer runId: runIds) {
            // delete scans for this run
            msScanDao.deleteScansForRun(runId);
        }
        
        // finally, delete the runs
        delete("MsRun.deleteByExperimentId", msExperimentId);
        return runIds;
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
    //---------------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------------
    /**
     * Convenience class for encapsulating a MsRun and associated experiment id
     */
    public static class MsRunSqlMapParam implements MsRun {

        private int experimentId;
        private MsRun run;
        
        public MsRunSqlMapParam(int experimentId, MsRun run) {
            this.experimentId = experimentId;
            this.run = run;
        }
        
        /**
         * @return the experimentId
         */
        public int getExperimentId() {
            return experimentId;
        }

        public List<MsEnzyme> getEnzymeList() {
            return run.getEnzymeList();
        }

        public String getAcquisitionMethod() {
            return run.getAcquisitionMethod();
        }

        public String getComment() {
            return run.getComment();
        }

        public String getConversionSW() {
            return run.getConversionSW();
        }

        public String getConversionSWOptions() {
            return run.getConversionSWOptions();
        }

        public String getConversionSWVersion() {
            return run.getConversionSWVersion();
        }

        public String getCreationDate() {
            return run.getCreationDate();
        }

        public String getDataType() {
            return run.getDataType();
        }

        public String getFileName() {
            return run.getFileName();
        }

        public String getInstrumentModel() {
            return run.getInstrumentModel();
        }

        public String getInstrumentSN() {
            return run.getInstrumentSN();
        }

        public String getInstrumentVendor() {
            return run.getInstrumentVendor();
        }

        public RunFileFormat getRunFileFormat() {
            return run.getRunFileFormat();
        }

        public String getSha1Sum() {
            return run.getSha1Sum();
        }
    }
    //---------------------------------------------------------------------------------------
}
