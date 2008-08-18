package org.yeastrc.ms.dao.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;

public class SQTSearchScanDAOImplTest extends SQTBaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSQTSpectrumData() {
        
        assertNull(sqtSpectrumDao.load(1, 1, 1)); // searchId = 1; scanId = 1; charge = 1
        
        SQTSearchScan data = makeSearchScan(3, 0); // charge = 3; processtime = 0
        
        try {
            sqtSpectrumDao.save(data, 0, 1); // searchId = 0; scanId = 1;
            fail("Cannot save search scan with searchId of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        try {
            sqtSpectrumDao.save(data, 1, 0); // searchId = 1; scanId = 0;
            fail("Cannot save search scan with scanId of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        data = makeSearchScan(0,0); // charge = 0; processtime = 0
        try {
            sqtSpectrumDao.save(data, 1, 1); // searchId = 1; scanId = 1;
            fail("Cannot save search scan with charge of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        data = makeSearchScan(3,0);
        
        sqtSpectrumDao.save(data, 1024, 4201);
        
        SQTSearchScanDb data_db = sqtSpectrumDao.load(1024, 4201, 3);
        assertNotNull(data_db);
        
        assertEquals(1024, data_db.getSearchId());
        assertEquals(4201, data_db.getScanId());
        assertEquals(null, data_db.getLowestSp());
        checkSearchScan(data, data_db);
        
        sqtSpectrumDao.deleteForSearch(1024);
        data_db = sqtSpectrumDao.load(1024, 4201, 3);
        assertNull(data_db);
    }
    
    private void checkSearchScan(SQTSearchScan input, SQTSearchScanDb output) {
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getLowestSp(), output.getLowestSp());
        assertEquals(input.getProcessTime(), output.getProcessTime());
        assertEquals(input.getServerName(), output.getServerName());
        assertEquals(input.getTotalIntensity().doubleValue(), output.getTotalIntensity().doubleValue());
        assertEquals(input.getSequenceMatches(), output.getSequenceMatches());
        assertEquals(input.getObservedMass(), output.getObservedMass());
    }
    
    private SQTSearchScan makeSearchScan(final int charge, final int processTime) {
        SQTSearchScan scan = new SQTSearchScan() {

            public int getCharge() {
                return charge;
            }

            public BigDecimal getLowestSp() {
                return null;
            }

            public int getProcessTime() {
                return processTime;
            }

            public String getServerName() {
                return "pumice.gs.washington.edu";
            }

            public BigDecimal getTotalIntensity() {
                return new BigDecimal("12345.12345");
            }

            @Override
            public int getScanNumber() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public List<SequestRunSearchResult> getScanResults() {
                return new ArrayList<SequestRunSearchResult>(0);
            }

            @Override
            public int getSequenceMatches() {
                return 0;
            }

            @Override
            public BigDecimal getObservedMass() {
                return new BigDecimal("1124.08");
            }};
            return scan;
    }
}
