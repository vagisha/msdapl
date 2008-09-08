/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.impl.MsRunLocationWrap;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO {

    private MsEnzymeDAO enzymeDao;
    
    public MsRunDAOImpl(SqlMapClient sqlMap, MsEnzymeDAO enzymeDao) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
    }

    public int saveRun(MsRunIn run, String serverAddress, String serverDirectory) {
        
        int runId = saveAndReturnId("MsRun.insert", run);
        
        try {
            // save location information for the original file
            saveRunLocation(serverAddress, serverDirectory, runId);
        
            // save the enzyme information
            List<MsEnzymeIn> enzymes = run.getEnzymeList();
            for (MsEnzymeIn enzyme: enzymes) 
                // use the enzyme name attribute only to look for a matching enzyme.
                enzymeDao.saveEnzymeforRun(enzyme, runId, Arrays.asList(new EnzymeProperties[] {EnzymeProperties.NAME}));
        }
        catch(RuntimeException e) {
            delete(runId); // this will delete anything that got saved with this runId;
            throw e;
        }
        return runId;
    }

    @Override
    public void saveRunLocation(final String serverAddress, final String serverDirectory,
            final int runId) {
        save("MsRunLocation.insert", new MsRunLocationWrap(serverAddress, serverDirectory, runId));
            
    }
    
    public MsRun loadRun(int runId) {
        return (MsRun) queryForObject("MsRun.select", runId);
    }
    
    public List<Integer> loadRunIdsForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunIdsForFileNameAndSha1Sum", map);
    }
    

    @Override
    public List<Integer> loadRunIdsForFileName(String fileName) {
        return queryForList("MsRun.selectRunIdsForFileName", fileName);
    }
    
    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("fileName", runFileName);
        map.put("searchId", searchId);
        Integer runId = (Integer)queryForObject("MsRun.selectRunIdForSearchAndFileName", map);
        if (runId == null)
            return 0;
        return runId;
    }
    
    @Override
    public List<MsRunLocation> loadLocationsForRun(int runId) {
        return queryForList("MsRunLocation.selectLocationsForRun", runId);
    }

    @Override
    public int loadMatchingRunLocations(final int runId,
            final String serverAddress, final String serverDirectory) {
        MsRunLocationWrap loc = new MsRunLocationWrap(serverAddress, serverDirectory, runId);
        Integer count = (Integer) queryForObject("MsRunLocation.selectMatchingLocations", loc);
        if (count == null)
            return 0;
        return count;
    }

    @Override
    public List<MsRun> loadRuns(List<Integer> runIdList) {
        if (runIdList.size() == 0)
            return new ArrayList<MsRun>(0);
        StringBuilder buf = new StringBuilder();
        for (Integer i: runIdList) {
            buf.append(","+i);
        }
        buf.deleteCharAt(0);
        return queryForList("MsRun.selectRuns", buf.toString());
    }
    
    /**
     * Delete only the top level run; everything else is deleted via SQL triggers.
     */
    public void delete(int runId) {
        // delete the run
        delete("MsRun.delete", runId);
    }
   
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        MsRun run = loadRun(runId);
        
        if (run == null) {
            throw new Exception("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
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
