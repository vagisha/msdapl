package org.yeastrc.ms.dao.ms2File;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dto.ms2File.MS2FileChargeIndependentAnalysis;


public class MS2FileChargeIndependentAnalysisDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMS2FileChargeIndependentAnalysis() {
        
        // nothing in the database right now
        assertEquals(0, iAnalDao.loadAnalysisForScan(1).size());
        
     // save something
        MS2FileChargeIndependentAnalysis da11 = makeIAnalysis(1, "name_11", "value_11");
        MS2FileChargeIndependentAnalysis da12 = makeIAnalysis(1, "name_12", "value_12");
        MS2FileChargeIndependentAnalysis da21 = makeIAnalysis(2, "name_21", "value_21");
        MS2FileChargeIndependentAnalysis da31 = makeIAnalysis(3, "name_31", "value_31");
        
        iAnalDao.save(da11);
        iAnalDao.save(da12);
        iAnalDao.save(da21);
        iAnalDao.save(da31);
        
        // check saved entries
        assertEquals(2, iAnalDao.loadAnalysisForScan(1).size());
        List<MS2FileChargeIndependentAnalysis> daList = iAnalDao.loadAnalysisForScan(1);
        // sort so that we get the entries in the order we inserted them
        Collections.sort(daList, new Comparator<MS2FileChargeIndependentAnalysis>() {
            public int compare(MS2FileChargeIndependentAnalysis o1,
                    MS2FileChargeIndependentAnalysis o2) {
                return new Integer(o1.getId()).compareTo(o2.getId());
            }});
        compare(da11, daList.get(0));
        compare(da12, daList.get(1));
        
        assertEquals(1, iAnalDao.loadAnalysisForScan(2).size());
        compare(da21, iAnalDao.loadAnalysisForScan(2).get(0));
        
        assertEquals(1, iAnalDao.loadAnalysisForScan(3).size());
        compare(da31, iAnalDao.loadAnalysisForScan(3).get(0));
        
        // delete
        iAnalDao.deleteByScanId(1);
        iAnalDao.deleteByScanId(2);
        iAnalDao.deleteByScanId(3);
        
        // really deleted everything?
        assertEquals(0, iAnalDao.loadAnalysisForScan(1).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(2).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(3).size());
    }
    
}
