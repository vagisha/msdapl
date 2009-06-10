/**
 * MS2ScanChargeUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.dualdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.upload.dao.BaseJDBCUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAO;

/**
 * Deals with tables:
 * 1. MS2FileScanCharge
 */
public class MS2ScanChargeUploadDAOImpl extends BaseJDBCUploadDAO implements MS2ScanChargeUploadDAO {

    private static final Logger log = Logger.getLogger(MS2ScanChargeUploadDAOImpl.class.getName());
    
    private final MS2ScanChargeUploadDAO mainScanChargeDao;
    private final MS2ScanChargeUploadDAO scanChargeDao;
    private final boolean useTempTable;
    
    /**
     * @param mainScanChargeDao -- DAO for the MAIN database table
     * @param scanChargeDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MS2ScanChargeUploadDAOImpl(MS2ScanChargeUploadDAO mainScanChargeDao, MS2ScanChargeUploadDAO scanChargeDao, 
            boolean useTempTable) {
        
        this.mainScanChargeDao = mainScanChargeDao;
        this.scanChargeDao = scanChargeDao;
        this.useTempTable = useTempTable;
    }

    public List<MS2ScanCharge> loadScanChargesForScan(int scanId) {
        List<MS2ScanCharge> scancharges = scanChargeDao.loadScanChargesForScan(scanId);
        if(scancharges == null || scancharges.size() == 0) {
            if(useTempTable) {
                scancharges = mainScanChargeDao.loadScanChargesForScan(scanId);
            }
        }
        return scancharges;
    }
    

    @Override
    public int save(MS2ScanCharge scanCharge, int scanId) {
        return scanChargeDao.save(scanCharge, scanId);
    }
    
    public int saveScanChargeOnly(MS2ScanCharge scanCharge, int scanId) {
        
        String sql = "INSERT INTO MS2FileScanCharge (scanID, charge, mass) VALUES ( ";
        // scanID
        if(scanId == 0) sql +=   "NULL, ";
        else            sql +=       scanId+", ";
        
        // charge
        if(scanCharge.getCharge() == 0) sql +=   "NULL, ";
        else                            sql += scanCharge.getCharge()+", ";
        
        // mass
        if(scanCharge.getMass() == null)    sql +=   "NULL, ";
        else                                sql += scanCharge.getMass();
        
        sql +=       ")";     
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
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
}
