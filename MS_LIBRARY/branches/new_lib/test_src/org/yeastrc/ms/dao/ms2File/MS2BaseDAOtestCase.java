package org.yeastrc.ms.dao.ms2File;

import java.math.BigDecimal;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;

public class MS2BaseDAOtestCase extends BaseDAOTestCase {

    protected MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
    protected MS2FileChargeDependentAnalysisDAO dAnalDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
    protected MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
    protected MS2FileScanDAO ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
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

    private MS2FileScanCharge makeMS2ScanCharge(Integer charge, String mass,
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

}
