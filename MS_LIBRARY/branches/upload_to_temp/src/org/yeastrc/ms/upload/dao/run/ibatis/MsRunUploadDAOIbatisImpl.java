/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.impl.MsRunLocationWrap;
import org.yeastrc.ms.upload.dao.general.MsEnzymeUploadDAO;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsRunUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsRunUploadDAO {

    private MsEnzymeUploadDAO enzymeDao;
    
    public MsRunUploadDAOIbatisImpl(SqlMapClient sqlMap, MsEnzymeUploadDAO enzymeDao) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
    }

    @Override
    public int saveRun(MsRunIn run, String serverDirectory) {
        
        int runId = saveAndReturnId("MsRun.insert", run);
        
        try {
            // save location information for the original file
            saveRunLocation(serverDirectory, runId);
        
            // save the enzyme information
            List<MsEnzymeIn> enzymes = run.getEnzymeList();
            for (MsEnzymeIn enzyme: enzymes) 
                // use all enzyme attributes to look for a matching enzyme.
                enzymeDao.saveEnzymeforRun(enzyme, runId);
        }
        catch(RuntimeException e) {
            delete(runId); // this will delete anything that got saved with this runId;
            throw e;
        }
        return runId;
    }

    @Override
    public void saveRunLocation(final String serverDirectory,
            final int runId) {
        save("MsRunLocation.insert", new MsRunLocationWrap(serverDirectory, runId));
            
    }
    
    @Override
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        Integer id = (Integer)queryForObject("MsRun.selectRunIdsForFileNameAndSha1Sum", map);
        if(id != null)
            return id;
        return 0;
    }
    
    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("fileName", runFileName);
        map.put("searchId", searchId);
        Integer runId = (Integer)queryForObject("MsRun.selectRunIdForSearchAndFileName", map);
        if (runId == null)
            return 0;
        return runId;
    }
    
    @Override
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("fileName", runFileName);
        map.put("experimentId", experimentId);
        Integer runId = (Integer)queryForObject("MsRun.selectRunIdForExperimentAndFileName", map);
        if (runId == null)
            return 0;
        return runId;
    }
    
    
    @Override
    public int loadMatchingRunLocations(final int runId, final String serverDirectory) {
        MsRunLocationWrap loc = new MsRunLocationWrap(serverDirectory, runId);
        Integer count = (Integer) queryForObject("MsRunLocation.selectMatchingLocations", loc);
        if (count == null)
            return 0;
        return count;
    }
    
    /**
     * Delete only the top level run; everything else is deleted via SQL triggers.
     */
    public void delete(int runId) {
        // delete the run
        delete("MsRun.delete", runId);
    }
   
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between RunFileFormat and JDBC's VARCHAR types. 
     */
    public static final class RunFileFormatTypeHandler implements TypeHandlerCallback {

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
