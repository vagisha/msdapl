package org.yeastrc.ms.dao.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ms2File.MS2RunDAOImplTest.MS2RunTest;
import org.yeastrc.ms.dao.ms2File.MS2ScanDAOImplTest.MS2ScanTest;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;

public class MS2BaseDAOtestCase extends BaseDAOTestCase {

    protected MS2ScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
    protected MS2ChargeDependentAnalysisDAO dAnalDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
    protected MS2ChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
    protected MsScanDAO<MS2Scan, MS2ScanDb> ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
    protected MS2HeaderDAO ms2HeaderDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
    protected MsRunDAO<MS2Run, MS2RunDb> ms2RunDao = DAOFactory.instance().getMS2FileRunDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //---------------------------------------------------------------------------------------
    // charge dependent and independent analysis
    //---------------------------------------------------------------------------------------
    protected MS2Field makeAnalysis(final String name, final String value) {
        MS2Field dAnalysis = new MS2Field() {
            public String getName() {
                return name;
            }
            public String getValue() {
                return value;
            }};
        return dAnalysis;
    }

    protected void compare(MS2Field a1, MS2ChargeDependentAnalysisDb a2) {
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }
    
    protected void compare(MS2Field a1, MS2ChargeIndependentAnalysisDb a2) {
        assertEquals(a1.getName(), a2.getName());
        assertEquals(a1.getValue(), a2.getValue());
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan charge
    //---------------------------------------------------------------------------------------
    protected MS2ScanCharge makeMS2ScanCharge(final Integer charge, final String mass,
            final boolean addChgDepAnalysis) {
        MS2ScanCharge scanCharge = new MS2ScanCharge() {
            public int getCharge() {
                return charge;
            }
            public List<? extends MS2Field> getChargeDependentAnalysisList() {
                if (addChgDepAnalysis) {
                    MS2Field da1 = makeAnalysis("name_1", "value_1");
                    MS2Field da2 = makeAnalysis("name_2", "value_2");
                    return Arrays.asList(new MS2Field[] {da1, da2});
                }
                else {
                    return new ArrayList<MS2Field>(0);
                }
            }
            public BigDecimal getMass() {
                if (mass == null)   return null;
                return new BigDecimal(mass);
            }};
        return scanCharge;
    }

    //---------------------------------------------------------------------------------------
    // MS2 scan 
    //---------------------------------------------------------------------------------------
    protected MS2Scan makeMS2Scan(int scanNum, int precursorScanNum, boolean addScanCharges,
            boolean addChgIAnalysis) {
        MS2ScanTest scan = new MS2ScanTest();
        scan.setStartScanNum(scanNum);
        scan.setEndScanNum(scanNum);
        scan.setFragmentationType("ETD");
        scan.setMsLevel(2);
        scan.setPrecursorMz(new BigDecimal("123.45"));
        scan.setPrecursorScanNum(precursorScanNum);
        scan.setRetentionTime(new BigDecimal("98.7"));
        
        if (addScanCharges) {
            scan.addScanCharge(makeMS2ScanCharge(2, "100.0", true));
            scan.addScanCharge(makeMS2ScanCharge(3, "200.0", true));
        }
        if (addChgIAnalysis) {
            scan.addChargeIndependentAnalysis(makeAnalysis("name_1", "value_1"));
            scan.addChargeIndependentAnalysis(makeAnalysis("name_2", "value_2"));
            scan.addChargeIndependentAnalysis(makeAnalysis("name_3", "value_3"));
        }
        return scan;
    }
    
    protected void saveScansForRun(int runId, int scanCount) {
        Random random = new Random();
        for (int i = 0; i < scanCount; i++) {
            int scanNum = random.nextInt(100);
            MS2Scan scan = makeMS2Scan(scanNum, 25, true, true);
            ms2ScanDao.save(scan, runId);
        }
    }

    //---------------------------------------------------------------------------------------
    // MS2 Run
    //---------------------------------------------------------------------------------------
    protected MS2RunTest makeMS2Run() {
        MS2RunTest run = new MS2RunTest();
        run.setRunFileFormat(RunFileFormat.MS2);
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.setConversionSW("ms2Convert");
        run.setConversionSWVersion("1.0");
        run.setConversionSWOptions("options string");
        run.setDataType("profile");
        run.setInstrumentModel("ETD");
        run.setInstrumentVendor("Thermo");
        run.setSha1Sum("sha1sum");
        return run;
    }
    
    protected void checkRun(MS2Run inputRun, MS2RunDb outputRun) {
        super.checkRun(inputRun, outputRun);
        assertEquals(inputRun.getHeaderList().size(), outputRun.getHeaderList().size());
    }
}
