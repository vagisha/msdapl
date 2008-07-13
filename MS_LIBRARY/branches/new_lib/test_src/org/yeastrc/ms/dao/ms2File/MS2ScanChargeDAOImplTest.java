package org.yeastrc.ms.dao.ms2File;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;
import org.yeastrc.ms.domain.ms2File.MS2ScanChargeDb;

public class MS2ScanChargeDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadAndSave() {
        
        // nothing in the database right now
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
        // put some data in (don't add any charge dependent data)
        MS2ScanCharge sc11 = makeMS2ScanCharge(1, "100.0", false); // charge = 1; mass = 100.0
        chargeDao.save(sc11, 1);
        
        // read it back
        List<MS2ScanChargeDb> sclist1 = chargeDao.loadScanChargesForScan(1);
        assertEquals(1, sclist1.size());
        
        // make sure NO charge dependent data was saved 
        assertEquals(0, sclist1.get(0).getChargeDependentAnalysisList().size());
        
        // put some data in (ADD charge dependent data)
        MS2ScanCharge sc12 = makeMS2ScanCharge(2, "200.0", true);
        chargeDao.save(sc12, 1);
        
        // read it back
        sclist1 = chargeDao.loadScanChargesForScan(1);
        assertEquals(2, sclist1.size());
        Collections.sort(sclist1, new Comparator<MS2ScanChargeDb>() {
            public int compare(MS2ScanChargeDb o1, MS2ScanChargeDb o2) {
                return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }});
        
        // make sure charge dependent data was saved (this will be for the second object in the list)
        assertEquals(2, sclist1.get(1).getChargeDependentAnalysisList().size());
        
        
        // compare values
        compare(sc11, sclist1.get(0));
        compare(sc12, sclist1.get(1));
        
        // delete everything
        chargeDao.deleteByScanId(1);
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
    }
    
    private void compare(MS2ScanCharge input, MS2ScanChargeDb output) {
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getMass().doubleValue(), output.getMass().doubleValue());
        assertEquals(input.getChargeDependentAnalysisList().size(), output.getChargeDependentAnalysisList().size());
    }

    public void testNullValues() {
        MS2ScanCharge sc = makeMS2ScanCharge(1, null, false);
        try {
            chargeDao.save(sc, 0);
            fail("Should not be able to save with null scan id");
        }
        catch(RuntimeException e){}
        
        sc = makeMS2ScanCharge(0, null, false);
        try {
            chargeDao.save(sc, 1);
            fail("Should not be able to save with null charge");
        }
        catch(RuntimeException e){}
    }
    
    public void testDelete() {
        MS2ScanCharge sc = makeMS2ScanCharge(2, "200.0", true);
        int scanChargeId = chargeDao.save(sc, 1);
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId).size());
        
        // delete 
        chargeDao.deleteByScanId(1);
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
        // make sure the charge dependent analysis was deleted
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId).size());
    }
    
    public void testDeleteCascade1() {
        MS2ScanCharge sc1 = makeMS2ScanCharge(1, "100.0", true);
        int scanChargeId_1 = chargeDao.save(sc1, 1);
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId_1).size());
        
        // delete
        chargeDao.deleteByScanIdCascade(1);
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
        // make sure everything was deleted
        assertEquals(0, chargeDao.loadScanChargesForScan(1).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId_1).size());
       
    }
    
    public void testDeleteCascade2() {
        
        // save scan charges for scanId_1
        int scanId_1 = 1;
        // one WITH charge dependent data
        MS2ScanCharge sc11 = makeMS2ScanCharge(1, "100.0", true);
        int scanChargeId_11 = chargeDao.save(sc11, scanId_1);
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId_11).size());
        // one WITHOUT charge dependent data
        MS2ScanCharge sc12 = makeMS2ScanCharge(1, "100.0", false);
        int scanChargeId_12 = chargeDao.save(sc12, scanId_1);
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId_12).size());
        
        // insert a scan charge for another scan id
        int scanId_2 = 2;
        MS2ScanCharge sc2 = makeMS2ScanCharge(2, "200.0", true);
        int scanChargeId_2 = chargeDao.save(sc2, scanId_2);
        assertEquals(1, chargeDao.loadScanChargesForScan(scanId_2).size());
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId_2).size());
        
        // delete
        chargeDao.deleteByScanIdCascade(scanId_1);
        
        // make sure everything for scanId_1 was deleted
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(scanId_1).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId_11).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId_12).size());
        
        // make sure nothing for scanId_2 was NOT deleted
        assertEquals(1, chargeDao.loadScanChargesForScan(scanId_2).size());
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId_2).size());
        
        // now delete for scanId_2 as well
        chargeDao.deleteByScanIdCascade(scanId_2);
        assertEquals(0, chargeDao.loadScanChargesForScan(scanId_2).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId_2).size());
        
    }
    
}
