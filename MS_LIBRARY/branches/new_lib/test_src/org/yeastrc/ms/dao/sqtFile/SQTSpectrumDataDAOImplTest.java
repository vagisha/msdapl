package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.domain.sqtFile.db.SQTSpectrumData;

public class SQTSpectrumDataDAOImplTest extends SQTBaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSQTSpectrumData() {
        
        assertNull(sqtSpectrumDao.load(1, 1, 1));
        
        SQTSpectrumData data = new SQTSpectrumData();
        data.setSearchId(0); // invalid search id
        data.setScanId(2);
        data.setCharge(3);
        data.setProcessTime(0);
        
        try {
            sqtSpectrumDao.save(data);
            fail("Save should have failed");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        data.setSearchId(1024);
        sqtSpectrumDao.save(data);
        
        assertNull(sqtSpectrumDao.load(1, 1, 1));
        
        SQTSpectrumData data_db = sqtSpectrumDao.load(1024, 2, 3);
        assertNotNull(data_db);
        
        assertEquals(data.getSearchId(), data_db.getSearchId());
        assertEquals(data.getScanId(), data_db.getScanId());
        assertEquals(data.getCharge(), data_db.getCharge());
        assertEquals(0, data_db.getProcessTime());
        assertEquals(null, data_db.getTotalIntensity());
        assertEquals(null, data_db.getLowestSp());
        
        sqtSpectrumDao.deleteForSearch(1024);
        
        data_db = sqtSpectrumDao.load(1024, 2, 3);
        assertNull(data_db);
        
        
    }
}
