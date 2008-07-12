package org.yeastrc.ms.dao.ms2File;

import java.math.BigDecimal;
import java.util.Random;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;
import org.yeastrc.ms.domain.ms2File.db.MS2ChargeDependentAnalysisDbImpl;
import org.yeastrc.ms.domain.ms2File.db.MS2ChargeIndependentAnalysisDbImpl;
import org.yeastrc.ms.domain.ms2File.db.MS2RunDbImpl;
import org.yeastrc.ms.domain.ms2File.db.MS2ScanDbImpl;
import org.yeastrc.ms.domain.ms2File.db.MS2ScanChargeDbImpl;

public class MS2BaseDAOtestCase extends BaseDAOTestCase {

    protected MS2ScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
    protected MS2ChargeDependentAnalysisDAO dAnalDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
    protected MS2ChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
    protected MsScanDAO<MS2ScanDbImpl> ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
    protected MS2HeaderDAO ms2HeaderDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
    protected MsRunDAO<MS2RunDbImpl> ms2RunDao = DAOFactory.instance().getMS2FileRunDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //---------------------------------------------------------------------------------------
    // charge INdependent analysis
    //---------------------------------------------------------------------------------------
    protected MS2ChargeIndependentAnalysisDbImpl makeIAnalysis(int scanId, String name, String value) {
        MS2ChargeIndependentAnalysisDbImpl iAnalysis = makeIAnalysis(name, value);
        iAnalysis.setScanId(scanId);
        return iAnalysis;
    }

    protected MS2ChargeIndependentAnalysisDbImpl makeIAnalysis(String name, String value) {
        MS2ChargeIndependentAnalysisDbImpl iAnalysis = new MS2ChargeIndependentAnalysisDbImpl();
        iAnalysis.setName(name);
        iAnalysis.setValue(value);
        return iAnalysis;
    }

    protected void compare(MS2ChargeIndependentAnalysisDbImpl a1, MS2ChargeIndependentAnalysisDbImpl a2) {
        assertEquals(a1.getScanId(), a2.getScanId());
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }

    //---------------------------------------------------------------------------------------
    // charge dependent analysis
    //---------------------------------------------------------------------------------------
    protected MS2ChargeDependentAnalysisDbImpl makeDAnalysis(int scanChargeId, String name, String value) {
        MS2ChargeDependentAnalysisDbImpl dAnalysis = makeDAnalysis(name, value);
        dAnalysis.setScanChargeId(scanChargeId);
        return dAnalysis;
    }

    protected MS2ChargeDependentAnalysisDbImpl makeDAnalysis(String name, String value) {
        MS2ChargeDependentAnalysisDbImpl dAnalysis = new MS2ChargeDependentAnalysisDbImpl();
        dAnalysis.setName(name);
        dAnalysis.setValue(value);
        return dAnalysis;
    }

    protected void compare(MS2ChargeDependentAnalysisDbImpl a1, MS2ChargeDependentAnalysisDbImpl a2) {
        assertEquals(a1.getScanChargeId(), a2.getScanChargeId());
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan charge
    //---------------------------------------------------------------------------------------
    protected MS2ScanCharge makeMS2ScanCharge(Integer scanId, Integer charge, String mass,
            boolean addChgDepAnalysis) {

        MS2ScanChargeDbImpl scanCharge = makeMS2ScanCharge(charge, mass, addChgDepAnalysis);
        scanCharge.setScanId(scanId);
        return scanCharge;
    }

    protected MS2ScanChargeDbImpl makeMS2ScanCharge(Integer charge, String mass,
            boolean addChgDepAnalysis) {
        MS2ScanChargeDbImpl scanCharge = new MS2ScanChargeDbImpl();

        scanCharge.setCharge(charge);
        if (mass != null)
            scanCharge.setMass(new BigDecimal(mass));

        if (addChgDepAnalysis) {
            MS2ChargeDependentAnalysisDbImpl da1 = makeDAnalysis("name_1", "value_1");
            scanCharge.addChargeDependentAnalysis(da1);
            MS2ChargeDependentAnalysisDbImpl da2 = makeDAnalysis("name_2", "value_2");
            scanCharge.addChargeDependentAnalysis(da2);
        }
        return scanCharge;
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan 
    //---------------------------------------------------------------------------------------
    protected MS2Scan makeMS2FileScan(int runId, int startScanNum, boolean addScanCharges,
            boolean addChgIAnalysis) {
        MS2ScanDbImpl scan = new MS2ScanDbImpl(makeMsScan(runId, startScanNum));
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
            MS2Scan scan = makeMS2FileScan(runId, scanNum, true, true);
            ms2ScanDao.save(scan);
        }
    }

}
