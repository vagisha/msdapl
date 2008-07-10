package org.yeastrc.ms.dao.ms2File;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.ms2File.db.MS2FileChargeDependentAnalysis;


public class MS2FileChargeDependentAnalysisDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperartionsOnMS2FileChargeDependentAnalysis() {
        
        // nothing in the database; should return empty result
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(1).size());
        
        // save something
        MS2FileChargeDependentAnalysis da11 = makeDAnalysis(1, "name_11", "value_11");
        MS2FileChargeDependentAnalysis da12 = makeDAnalysis(1, "name_12", "value_12");
        MS2FileChargeDependentAnalysis da21 = makeDAnalysis(2, "name_21", "value_21");
        MS2FileChargeDependentAnalysis da31 = makeDAnalysis(3, "name_31", "value_31");
        
        dAnalDao.save(da11);
        dAnalDao.save(da12);
        dAnalDao.save(da21);
        dAnalDao.save(da31);
        
        // check saved entries
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(1).size());
        List<MS2FileChargeDependentAnalysis> daList = dAnalDao.loadAnalysisForScanCharge(1);
        // sort so that we get the entries in the order we inserted them
        Collections.sort(daList, new Comparator<MS2FileChargeDependentAnalysis>() {
            public int compare(MS2FileChargeDependentAnalysis o1,
                    MS2FileChargeDependentAnalysis o2) {
                return new Integer(o1.getId()).compareTo(o2.getId());
            }});
        compare(da11, daList.get(0));
        compare(da12, daList.get(1));
        
        assertEquals(1, dAnalDao.loadAnalysisForScanCharge(2).size());
        compare(da21, dAnalDao.loadAnalysisForScanCharge(2).get(0));
        
        assertEquals(1, dAnalDao.loadAnalysisForScanCharge(3).size());
        compare(da31, dAnalDao.loadAnalysisForScanCharge(3).get(0));
        
        // delete
        dAnalDao.deleteByScanChargeId(1);
        dAnalDao.deleteByScanChargeId(2);
        dAnalDao.deleteByScanChargeId(3);
        
        // really deleted everything?
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(1).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(2).size());
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(3).size());
        
    }
}
