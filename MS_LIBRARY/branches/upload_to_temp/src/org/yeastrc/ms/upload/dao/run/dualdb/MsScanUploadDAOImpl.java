/**
 * MsScanUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.dualdb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;
import org.yeastrc.ms.upload.dao.BaseJDBCUploadDAO;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.util.PeakStringBuilder;

/**
 * Deals with the tables: 
 * 1. msScan
 * 2. msScanData
 */
public class MsScanUploadDAOImpl extends BaseJDBCUploadDAO implements MsScanUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsScanUploadDAOImpl.class.getName());
    
    private final MsScanUploadDAO scanDao;
    private final MsScanUploadDAO mainScanDao;
    private boolean useTempTable;
    
    private PeakStorageType peakStorageType;
    
    /**
     * @param mainScanDao -- DAO for the MAIN database table
     * @param scanDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsScanUploadDAOImpl(MsScanUploadDAO mainScanDao, MsScanUploadDAO scanDao, 
            boolean useTempTable, PeakStorageType type) {
        this.mainScanDao = mainScanDao;
        if(scanDao == null)
            this.scanDao = mainScanDao;
        else
            this.scanDao = scanDao;
        this.useTempTable = useTempTable;
        this.peakStorageType = type;
    }

    public int save(MsScanIn scan, int runId, int precursorScanId) {
        
        // save an entry in the msScan table first
        int scanId = saveScan(scan, runId, precursorScanId);
        // now save the scan data (peaks)
        saveScanData(scan, scanId);
        
        return scanId;
    }

    private void saveScanData(MsScanIn scan, int scanId) {
        
        String sql = "INSERT INTO msScanData (scanID,type,data) VALUES (?,?,COMPRESS(?))";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.prepareStatement(sql);
            prepareScanDataInsertStatement(stmt, scan, scanId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        catch (IOException e) {
            log.error("Error convering peak data", e);
            throw new RuntimeException("Failed to save peak data", e);
        }
        finally {
            close(conn, stmt);
        }
    }
    
    private void prepareScanDataInsertStatement(PreparedStatement stmt, MsScanIn scan, int scanId) throws SQLException, IOException {
        stmt.setInt(1, scanId);
        if(peakStorageType == null)
            stmt.setString(2, null);
        else
            stmt.setString(2, peakStorageType.getCode());
        
        byte[] peakData = null;
        if(peakStorageType == PeakStorageType.DOUBLE_FLOAT)
            peakData = getPeakBinaryData(scan);
        else if(peakStorageType == PeakStorageType.STRING)
            peakData = getPeakDataString(scan);
        stmt.setBytes(3, peakData);
    }

    private byte[] getPeakDataString(MsScanIn scan) {
        List<String[]> peaksStr = scan.getPeaksString();
        PeakStringBuilder builder = new PeakStringBuilder();
        for(String[] peak: peaksStr) {
            builder.addPeak(peak[0], peak[1]);
        }
        return builder.getPeaksAsString().getBytes();
    }
    
    private byte[] getPeakBinaryData(MsScanIn scan) throws IOException {
        
        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;
        
        List<Peak> peaks = scan.getPeaks();
        try {
            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);
            for(Peak peak: peaks) {
                dos.writeDouble(peak.getMz());
                dos.writeFloat(peak.getIntensity());
            }
            dos.flush();
        }
        finally {
            if(dos != null) dos.close();
            if(bos != null) bos.close();
        }
        byte [] data = bos.toByteArray();
        return data;
    }
    
    private int saveScan(MsScanIn scan, int runId, int precursorScanId) {
        
        String sql = "INSERT INTO msScan (runID, startScanNumber, endScanNumber, ";
        sql +=       "level, preMZ, preScanID, prescanNumber, ";
        sql +=       "retentionTime, fragmentationType, isCentroid, peakCount) ";
        sql +=       "VALUES (";
        
        // runId
        if(runId == 0)  sql += "NULL, ";
        else            sql +=       runId+", ";
        
        // start scan number
        if(scan.getStartScanNum() == -1)    sql +=   "NULL, ";
        else                                sql +=    scan.getStartScanNum()+", ";
        
        // end scan number
        if(scan.getEndScanNum() == -1)      sql +=   "NULL, ";
        else                                sql +=   scan.getEndScanNum()+", ";
        
        // ms level
        if(scan.getMsLevel() == 0)          sql +=   "NULL, ";
        else                                sql +=   scan.getMsLevel()+", ";
        
        // precursor m/z
        if(scan.getPrecursorMz() == null)   sql +=   "NULL, ";
        else                                sql +=   scan.getPrecursorMz()+", ";
        
        // precursor scan ID
        if(precursorScanId == 0)            sql +=   "NULL, ";
        else                                sql +=   precursorScanId+", ";
        
        // precursor scan number
        if(scan.getPrecursorScanNum() == -1)    sql +=   "NULL, ";
        else                                    sql +=   scan.getPrecursorScanNum()+", ";
        
        // retention time
        if(scan.getRetentionTime() == null)     sql +=   "NULL, ";
        else                                    sql +=   scan.getRetentionTime()+", ";
        
        // fragmentation type
        if(scan.getFragmentationType() == null) sql +=   "NULL, ";
        else                                    sql +=   scan.getFragmentationType()+", ";
        
        // data conversion type (centroid or not)
        String dataConvType = getDataConversionTypeString(scan.getDataConversionType());
        if(dataConvType == null)            sql +=   "NULL, ";
        else                                sql +=   "\""+dataConvType+"\", ";
        
        // peak count
        if(scan.getPeakCount() == -1)       sql +=   "NULL, ";
        else                                sql +=   scan.getPeakCount()+"";
        
        sql +=       ")";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
            rs = stmt.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("Failed to get auto_increment key for sql: "+sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            close(conn, stmt, rs);
        }
    }
    
    private String getDataConversionTypeString(DataConversionType type) {
        if (DataConversionType.CENTROID == type)                 return "T";
        else if (DataConversionType.NON_CENTROID == type)        return "F";
        else                                                     return null;
    }

    public int save(MsScanIn scan, int runId) {
        return save(scan, runId, 0); // a value of 0 for precursorScanId should insert NULL in the database.
    }
    
    @Override
    public <T extends MsScanIn> List<Integer> save(List<T> scans, int runId) {
        return scanDao.save(scans, runId);
    }

    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        int scanId = scanDao.loadScanIdForScanNumRun(scanNum, runId);
        if(scanId == 0 && useTempTable) {
            scanId = mainScanDao.loadScanIdForScanNumRun(scanNum, runId);
        }
        return scanId;
    }
    
    public void delete(int scanId) {
        scanDao.delete(scanId);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required tables
            copier.copyToMainTableFromFile("msScan");
            copier.copyToMainTableFromFile("msScanData");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msScan", "id"))
            return false;
        if(!copier.checkColumnValues("msScanData", "scanID"))
            return false;
        return true;
    }
}
