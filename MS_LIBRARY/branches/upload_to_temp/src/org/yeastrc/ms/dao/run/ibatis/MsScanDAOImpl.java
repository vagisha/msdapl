/**
 * MsScanDAOImpl.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ibatis;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;
import org.yeastrc.ms.upload.dao.run.ibatis.MsScanUploadDAOIbatisImpl.MsScanDataSqlMapParam;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO {

    private PeakStorageType peakStorageType;
    
    public MsScanDAOImpl(SqlMapClient sqlMap, PeakStorageType peakStorageType) {
        super(sqlMap);
        this.peakStorageType = peakStorageType;
    }

    public int save(MsScanIn scan, int runId, int precursorScanId) {
        MsScanSqlMapParam scanDb = new MsScanSqlMapParam(runId, precursorScanId, scan);
        int scanId = saveAndReturnId("MsScan.insert", scanDb);
        
        // save the peak data
        MsScanDataSqlMapParam param;
        String statementName = "insertPeakData";
        try {
            param = new MsScanDataSqlMapParam(scanId, scan, peakStorageType);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to execute statement: "+statementName, e);
        }
        
        save("MsScan."+statementName, param);
        return scanId;
    }

    public int save(MsScanIn scan, int runId) {
        return save(scan, runId, 0); // a value of 0 for precursorScanId should insert NULL in the database.
    }
    
    public MsScan load(int scanId) {
        return (MsScan) queryForObject("MsScan.select", scanId);
    }

    @Override
    public MsScan loadScanLite(int scanId) {
        return (MsScan) queryForObject("MsScan.selectScanNoData", scanId);
    }
    
    @Override
    public int loadScanNumber(int scanId) {
        Integer scanNum = (Integer)queryForObject("MsScan.selectScanNumber", scanId);
        if(scanNum != null)
            return scanNum;
        else
            return 0;
    }
    
    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }

    @Override
    public int numScans(int runId) {
        return (Integer)queryForObject("MsScan.selectScanCountForRun", runId);
    }
    
    
    @Override
    public int numScans(int runId, int level) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("runId", runId);
        map.put("level", level);
        return (Integer)queryForObject("MsScan.selectScanCountForRunLevel", map);
    }
    
    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanNum", scanNum);
        map.put("runId", runId);
        Integer id = (Integer)queryForObject("MsScan.selectScanIdForScanNumRun", map);
        if (id != null) return id;
        return 0;
    }
    
    public void delete(int scanId) {
        delete("MsScan.delete", scanId);
        delete("MsScan.deletePeakData", scanId);
    }

    /**
     * Convenience class for encapsulating a MsScan along with the associated runId 
     * and precursorScanId (if any)
     */
    public static class MsScanSqlMapParam implements MsScan {

        private int runId;
        private int precursorScanId;
        private MsScanIn scan;

        public MsScanSqlMapParam(int runId, int precursorScanId, MsScanIn scan) {
            this.runId = runId;
            this.precursorScanId = precursorScanId;
            this.scan = scan;
        }

        public int getRunId() {
            return runId;
        }
        
        public int getPrecursorScanId() {
            return precursorScanId;
        }

        public int getEndScanNum() {
            return scan.getEndScanNum();
        }

        public String getFragmentationType() {
            return scan.getFragmentationType();
        }

        public int getMsLevel() {
            return scan.getMsLevel();
        }

        public BigDecimal getPrecursorMz() {
            return scan.getPrecursorMz();
        }

        public int getPrecursorScanNum() {
            return scan.getPrecursorScanNum();
        }

        public BigDecimal getRetentionTime() {
            return scan.getRetentionTime();
        }

        public int getStartScanNum() {
            return scan.getStartScanNum();
        }

        public List<String[]> getPeaksString() {
            throw new UnsupportedOperationException();
        }
        
        public List<Peak> getPeaks() {
            throw new UnsupportedOperationException();
        }
        
        public PeakStorageType getPeakStorageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPeakCount() {
            return scan.getPeakCount();
        }

        @Override
        public DataConversionType getDataConversionType() {
            return scan.getDataConversionType();
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by MsScanSqlMapParam");
        }
        
    }
    

    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between 'F', 'T' from database to DataConversionType
     */
    public static class DataConversionTypeHandler implements TypeHandlerCallback {

        private static final String TRUE = "T";
        private static final String FALSE = "F";
        
        public Object getResult(ResultGetter getter) throws SQLException {
            return trueFalseToDataConversionType(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            String type = dataConversionTypeToTrueFalse((DataConversionType)parameter);
            if (type == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(type);
        }

        public Object valueOf(String s) {
            return trueFalseToDataConversionType(s);
        }
        
        private String dataConversionTypeToTrueFalse(DataConversionType type) {
            if (DataConversionType.CENTROID == type)                 return TRUE;
            else if (DataConversionType.NON_CENTROID == type)        return FALSE;
            else                                                     return null;
        }
        
        private DataConversionType trueFalseToDataConversionType(String val) {
            if (val == null)    
                return DataConversionType.UNKNOWN;
            if (TRUE.equalsIgnoreCase(val))
                return DataConversionType.CENTROID;
            else if (FALSE.equalsIgnoreCase(val))
                return DataConversionType.NON_CENTROID;
            else
                throw new IllegalArgumentException("Cannot convert "+val+" to DataConversionType");
        }
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between PeakStorageType to CHAR stored in the database
     */
    public static class PeakStorageTypeHandler implements TypeHandlerCallback {

        
        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToPeakStorageType(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null || !(parameter instanceof PeakStorageType))
                setter.setNull(java.sql.Types.CHAR);
            else 
                setter.setString(((PeakStorageType)parameter).getCode());
        }

        public Object valueOf(String s) {
            return stringToPeakStorageType(s);
        }

        private PeakStorageType stringToPeakStorageType(String val) {
            PeakStorageType type = PeakStorageType.instance(val);
            if(type == null)
                throw new IllegalArgumentException("Cannot convert "+val+" to PeakStorageType");
            return type;
        }
    }
}
