package org.yeastrc.ms.service.sqtfile;

import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.service.UploadException;

public class ProlucidSQTDataUploadServiceTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUploadSqtFile() {
        String dir = "test_resources/ProlucidTest_dir/new_format/prim_binprob_sec_binprob";
        MsDataUploader uploader = new MsDataUploader();
        java.util.Date experimentDate = new java.util.Date();
        
        int searchId = 0;
        try {
            searchId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, experimentDate);
            assertNotSame(0, searchId);
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("Data is valid");
        }
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
        int runId1 = getRunId("1.ms2");
        int runId2 = getRunId("2.ms2");
        
        checkSearch(searchId, experimentDate);
    }

    private void checkSearch(int searchId, java.util.Date experimentDate) {
        
    }
    
    private int getRunId(String runFileName) {
        List<Integer> runIds = runDao.loadRunIdsForFileName(runFileName);
        assertEquals(1, runIds.size());
        int runId = runIds.get(0);
        assertNotSame(0, runId);
        return runId;
    }
}
