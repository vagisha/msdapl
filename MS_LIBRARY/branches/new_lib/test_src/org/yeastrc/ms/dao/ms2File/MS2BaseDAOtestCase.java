package org.yeastrc.ms.dao.ms2File;

import java.math.BigDecimal;
import java.util.Random;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.ms2File.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.domain.ms2File.MS2FileChargeIndependentAnalysis;
import org.yeastrc.ms.domain.ms2File.MS2FileRun;
import org.yeastrc.ms.domain.ms2File.MS2FileScan;
import org.yeastrc.ms.domain.ms2File.MS2FileScanCharge;

public class MS2BaseDAOtestCase extends BaseDAOTestCase {

    protected MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
    protected MS2FileChargeDependentAnalysisDAO dAnalDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
    protected MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
    protected MsScanDAO<MS2FileScan> ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
    protected MS2FileHeaderDAO ms2HeaderDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
    protected MsRunDAO<MS2FileRun> ms2RunDao = DAOFactory.instance().getMS2FileRunDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //---------------------------------------------------------------------------------------
    // charge INdependent analysis
    //---------------------------------------------------------------------------------------
    protected MS2FileChargeIndependentAnalysis makeIAnalysis(int scanId, String name, String value) {
        MS2FileChargeIndependentAnalysis iAnalysis = makeIAnalysis(name, value);
        iAnalysis.setScanId(scanId);
        return iAnalysis;
    }

    protected MS2FileChargeIndependentAnalysis makeIAnalysis(String name, String value) {
        MS2FileChargeIndependentAnalysis iAnalysis = new MS2FileChargeIndependentAnalysis();
        iAnalysis.setName(name);
        iAnalysis.setValue(value);
        return iAnalysis;
    }

    protected void compare(MS2FileChargeIndependentAnalysis a1, MS2FileChargeIndependentAnalysis a2) {
        assertEquals(a1.getScanId(), a2.getScanId());
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }

    //---------------------------------------------------------------------------------------
    // charge dependent analysis
    //---------------------------------------------------------------------------------------
    protected MS2FileChargeDependentAnalysis makeDAnalysis(int scanChargeId, String name, String value) {
        MS2FileChargeDependentAnalysis dAnalysis = makeDAnalysis(name, value);
        dAnalysis.setScanChargeId(scanChargeId);
        return dAnalysis;
    }

    protected MS2FileChargeDependentAnalysis makeDAnalysis(String name, String value) {
        MS2FileChargeDependentAnalysis dAnalysis = new MS2FileChargeDependentAnalysis();
        dAnalysis.setName(name);
        dAnalysis.setValue(value);
        return dAnalysis;
    }

    protected void compare(MS2FileChargeDependentAnalysis a1, MS2FileChargeDependentAnalysis a2) {
        assertEquals(a1.getScanChargeId(), a2.getScanChargeId());
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan charge
    //---------------------------------------------------------------------------------------
    protected MS2FileScanCharge makeMS2ScanCharge(Integer scanId, Integer charge, String mass,
            boolean addChgDepAnalysis) {

        MS2FileScanCharge scanCharge = makeMS2ScanCharge(charge, mass, addChgDepAnalysis);
        scanCharge.setScanId(scanId);
        return scanCharge;
    }

    protected MS2FileScanCharge makeMS2ScanCharge(Integer charge, String mass,
            boolean addChgDepAnalysis) {
        MS2FileScanCharge scanCharge = new MS2FileScanCharge();

        scanCharge.setCharge(charge);
        if (mass != null)
            scanCharge.setMass(new BigDecimal(mass));

        if (addChgDepAnalysis) {
            MS2FileChargeDependentAnalysis da1 = makeDAnalysis("name_1", "value_1");
            scanCharge.addChargeDependentAnalysis(da1);
            MS2FileChargeDependentAnalysis da2 = makeDAnalysis("name_2", "value_2");
            scanCharge.addChargeDependentAnalysis(da2);
        }
        return scanCharge;
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan 
    //---------------------------------------------------------------------------------------
    protected MS2FileScan makeMS2FileScan(int runId, int startScanNum, boolean addScanCharges,
            boolean addChgIAnalysis) {
        MS2FileScan scan = new MS2FileScan(makeMsScan(runId, startScanNum));
        if (addScanCharges) {
            scan.addScanCharge(makeMS2ScanCharge(2, "100.0", true));
            scan.addScanCharge(makeMS2ScanCharge(3, "200.0", true));
        }
        if (addChgIAnalysis) {
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_1", "value_1"));
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_2", "value_2"));
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_3", "value_3"));
        }
        return scan;
    }
    
    protected void saveScansForRun(int runId, int scanCount) {
        Random random = new Random();
        for (int i = 0; i < scanCount; i++) {
            int scanNum = random.nextInt(100);
            MS2FileScan scan = makeMS2FileScan(runId, scanNum, true, true);
            ms2ScanDao.save(scan);
        }
    }

}
