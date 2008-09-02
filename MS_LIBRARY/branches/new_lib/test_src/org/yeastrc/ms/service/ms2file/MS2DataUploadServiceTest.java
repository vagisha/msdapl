package org.yeastrc.ms.service.ms2file;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsRunLocationDb;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2HeaderDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanDb;
import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.util.Sha1SumCalculator;

public class MS2DataUploadServiceTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUploadRuns1() {
        String dir = "test_resources/validSequestData_dir";
        MsDataUploader uploader = new MsDataUploader();
        try {
            uploader.uploadExperimentToDb("remote.server", "remote/directory", dir, new Date());
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("directory has valid data");
        }
        
        List<Integer> runIds = runDao.loadRunIdsForFileName("1.ms2");
        assertEquals(1, runIds.size());
        int runId1 = runIds.get(0);
        assertNotSame(0, runId1);

        runIds = runDao.loadRunIdsForFileName("2.ms2");
        assertEquals(1, runIds.size());
        int runId2 = runIds.get(0);
        assertNotSame(0, runId2);
        
        // make sure there is only one run location for runId1
        // check values from msRunLocation table
        List<MsRunLocationDb> locs = runDao.loadLocationsForRun(runId1);
        assertEquals(1, locs.size());
        assertEquals("remote.server", locs.get(0).getServerAddress());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId1, locs.get(0).getRunId());
        
        locs = runDao.loadLocationsForRun(runId2);
        assertEquals(1, locs.size());
        assertEquals("remote.server", locs.get(0).getServerAddress());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId2, locs.get(0).getRunId());
        
        // upload with different values for serverAddress and serverDirectory
        try {
            uploader.uploadExperimentToDb("remote.server.again", "remote/directory/2", dir, new Date());
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("directory has valid data");
        }
        
        // the runs will not be uploaded again but we should have two locations for the each run
        locs = runDao.loadLocationsForRun(runId1);
        Collections.sort(locs, new Comparator<MsRunLocationDb>(){
            public int compare(MsRunLocationDb o1, MsRunLocationDb o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(2, locs.size());
        assertEquals("remote.server", locs.get(0).getServerAddress());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId1, locs.get(0).getRunId());
        assertEquals("remote.server.again", locs.get(1).getServerAddress());
        assertEquals("remote/directory/2", locs.get(1).getServerDirectory());
        assertEquals(runId1, locs.get(1).getRunId());
        
        locs = runDao.loadLocationsForRun(runId2);
        Collections.sort(locs, new Comparator<MsRunLocationDb>(){
            public int compare(MsRunLocationDb o1, MsRunLocationDb o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(2, locs.size());
        assertEquals("remote.server", locs.get(0).getServerAddress());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId2, locs.get(0).getRunId());
        assertEquals("remote.server.again", locs.get(1).getServerAddress());
        assertEquals("remote/directory/2", locs.get(1).getServerDirectory());
        assertEquals(runId2, locs.get(1).getRunId());
        
        
        // upload again but this time use the same values for serverAddress and serverDirectory
        // as the first upload
        try {
            uploader.uploadExperimentToDb("remote.server", "remote/directory", dir, new Date());
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("directory has valid data");
        }
        // no additional entries should be created 
        assertEquals(1, runDao.loadRunIdsForFileName("1.ms2").size());
        assertEquals(1, runDao.loadRunIdsForFileName("2.ms2").size());
        locs = runDao.loadLocationsForRun(runId1);
        assertEquals(2, locs.size());
        locs = runDao.loadLocationsForRun(runId2);
        assertEquals(2, locs.size());
    }
    
    public void testUploadRuns2() {
        String dir = "test_resources/validSequestData_dir";
        MsDataUploader uploader = new MsDataUploader();
        java.util.Date experimentDate = new java.util.Date();
        
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, experimentDate);
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("Data is valid");
        }
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
        try {
            checkRun("1.ms2", Sha1SumCalculator.instance().sha1SumFor(new File(dir+File.separator+"1.ms2")));
            checkRun("2.ms2", Sha1SumCalculator.instance().sha1SumFor(new File(dir+File.separator+"2.ms2")));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail("There should be no exception in sha1sum calculation");
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("There should be no exception in sha1sum calculation");
        }
    }
    
    private int checkRun(String runFileName, String sha1sum) {
        List<Integer> runIds = runDao.loadRunIdsForFileName(runFileName);
        assertEquals(1, runIds.size());
        int runId = runIds.get(0);
        assertNotSame(0, runId);
        
        MsRunDAO<MS2Run, MS2RunDb> ms2runDao = DAOFactory.instance().getMS2FileRunDAO();
        MS2RunDb run = ms2runDao.loadRun(runId);
        assertNotNull(run);
        
        // check values from msRun table
        assertEquals(runFileName, run.getFileName());
        assertEquals(sha1sum, run.getSha1Sum());
        if (runFileName.equals("1.ms2")) {
            assertEquals("3/22/2005 9:46:00 AM", run.getCreationDate());
            assertEquals("MakeMS2", run.getConversionSW());
            assertEquals("1.0", run.getConversionSWVersion());
            assertEquals("MS2", run.getConversionSWOptions());
            assertNull(run.getInstrumentModel());
            assertNull(run.getInstrumentVendor());
            assertNull(run.getInstrumentSN());
            assertNull(run.getAcquisitionMethod());
            assertEquals(RunFileFormat.MS2, run.getRunFileFormat());
            assertEquals("MakeMS2 written by Michael J. MacCoss, 2004", run.getComment());
        }
        else { 
            assertEquals("12/20/2007 2:29:19 PM", run.getCreationDate());
            assertEquals("RAWXtract", run.getConversionSW());
            assertEquals("1.8", run.getConversionSWVersion());
            assertEquals("MS2", run.getConversionSWOptions());
            assertEquals("ITMS", run.getInstrumentModel());
            assertNull(run.getInstrumentVendor());
            assertNull(run.getInstrumentSN());
            assertEquals("Data-Dependent", run.getAcquisitionMethod());
            assertEquals(RunFileFormat.MS2, run.getRunFileFormat());
            assertEquals("RawXtract written by John Venable, 2003", run.getComment());
        }
        
        // check values from msRunEnzyme table
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId).size());
        
        // check values from msRunLocation table
        List<MsRunLocationDb> locs = runDao.loadLocationsForRun(runId);
        assertEquals(1, locs.size());
        assertEquals("remoteServer", locs.get(0).getServerAddress());
        assertEquals("remoteDirectory", locs.get(0).getServerDirectory());
        assertEquals(runId, locs.get(0).getRunId());
        // testting the other method: should really be in test class for runDao
        List<MsRunLocationDb> locs2 = runDao.loadMatchingRunLocations(runId, "remoteServer", "remoteDirectory");
        assertEquals(1, locs2.size());
        assertEquals("remoteServer", locs2.get(0).getServerAddress());
        assertEquals("remoteDirectory", locs2.get(0).getServerDirectory());
        assertEquals(runId, locs2.get(0).getRunId());
        
        // check values in MS2FileHeaders table
        MS2HeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        List<MS2HeaderDb> headers = headerDao.loadHeadersForRun(runId);
        Collections.sort(headers, new Comparator<MS2HeaderDb>() {

            public int compare(MS2HeaderDb o1, MS2HeaderDb o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        if (runFileName.equals("1.ms2")) {
            assertEquals(5, headers.size());
            int i = 0;
            assertEquals("CreationDate", headers.get(i).getName());
            assertEquals("3/22/2005 9:46:00 AM", headers.get(i++).getValue());
            assertEquals("Extractor", headers.get(i).getName());
            assertEquals("MakeMS2", headers.get(i++).getValue());
            assertEquals("ExtractorVersion", headers.get(i).getName());
            assertEquals("1.0", headers.get(i++).getValue());
            assertEquals("Comments", headers.get(i).getName());
            assertEquals("MakeMS2 written by Michael J. MacCoss, 2004", headers.get(i++).getValue());
            assertEquals("ExtractorOptions", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
        }
        else {
            assertEquals(13, headers.size());
            int i = 0;
            assertEquals("FilteringProgram", headers.get(i).getName());
            assertEquals("Parc", headers.get(i++).getValue());
            assertEquals("CreationDate", headers.get(i).getName());
            assertEquals("12/20/2007 2:29:19 PM", headers.get(i++).getValue());
            assertEquals("Extractor", headers.get(i).getName());
            assertEquals("RAWXtract", headers.get(i++).getValue());
            assertEquals("ExtractorVersion", headers.get(i).getName());
            assertEquals("1.8", headers.get(i++).getValue());
            assertEquals("Comments", headers.get(i).getName());
            assertEquals("RawXtract written by John Venable, 2003", headers.get(i++).getValue());
            assertEquals("ExtractorOptions", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
            assertEquals("AcquisitionMethod", headers.get(i).getName());
            assertEquals("Data-Dependent", headers.get(i++).getValue());
            assertEquals("InstrumentType", headers.get(i).getName());
            assertEquals("ITMS", headers.get(i++).getValue());
            assertEquals("ScanType", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
            assertEquals("DataType", headers.get(i).getName());
            assertEquals("Centroid", headers.get(i++).getValue());
            assertEquals("IsolationWindow", headers.get(i).getName());
            assertNull(headers.get(i++).getValue());
            assertEquals("FirstScan", headers.get(i).getName());
            assertEquals("1", headers.get(i++).getValue());            
            assertEquals("LastScan", headers.get(i).getName());
            assertEquals("17903", headers.get(i++).getValue());
        }
        
        // check values in msScan table and related tables (for each scan in the ms2 file)
        if (runFileName.equals("1.ms2")) checkScansFor_1ms2(runId);
        else checkScansFor_2ms2(runId);
        
        return runId;
    }
    
    private void checkScansFor_1ms2(int runId) {
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        assertEquals(22, scanIds.size());
        Collections.sort(scanIds);
        MsScanDAO<MS2Scan, MS2ScanDb> ms2scanDo = DAOFactory.instance().getMS2FileScanDAO();
        
        for (int id = 0; id < scanIds.size(); id++) {
            MS2ScanDb scan = ms2scanDo.load(scanIds.get(id));
            assertEquals(-1, scan.getPrecursorScanNum());
            assertEquals(0, scan.getPrecursorScanId());
            assertNull(scan.getFragmentationType());
            assertEquals(DataConversionType.UNKNOWN, scan.getDataConversionType());
            assertEquals(2, scan.getMsLevel());
            assertNull(scan.getRetentionTime());
            assertEquals(0, scan.getChargeIndependentAnalysisList().size());
        }
        int i = 0;
        MS2ScanDb scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(2, scan.getStartScanNum());
        assertEquals(2, scan.getEndScanNum());
        assertEquals(535.96, scan.getPrecursorMz().doubleValue());
        assertEquals(20, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(3, scan.getStartScanNum());
        assertEquals(3, scan.getEndScanNum());
        assertEquals(447.03, scan.getPrecursorMz().doubleValue());
        assertEquals(19, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(4, scan.getStartScanNum());
        assertEquals(4, scan.getEndScanNum());
        assertEquals(434.09, scan.getPrecursorMz().doubleValue());
        assertEquals(12, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 2));
        assertEquals(26, scan.getStartScanNum());
        assertEquals(26, scan.getEndScanNum());
        assertEquals(441.4, scan.getPrecursorMz().doubleValue());
        assertEquals(15, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 1));
        assertEquals(27, scan.getStartScanNum());
        assertEquals(27, scan.getEndScanNum());
        assertEquals(451.23, scan.getPrecursorMz().doubleValue());
        assertEquals(14, scan.getPeakCount());
        
    }
    
    private void checkScansFor_2ms2(int runId) {
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        assertEquals(13, scanIds.size());
        Collections.sort(scanIds);
        MsScanDAO<MS2Scan, MS2ScanDb> ms2scanDo = DAOFactory.instance().getMS2FileScanDAO();
        
        for (int id = 0; id < scanIds.size(); id++) {
            MS2ScanDb scan = ms2scanDo.load(scanIds.get(id));
            assertEquals(0, scan.getPrecursorScanId());
            assertEquals("CID", scan.getFragmentationType());
            assertEquals(DataConversionType.CENTROID, scan.getDataConversionType());
            assertEquals(2, scan.getMsLevel());
            assertNotNull(scan.getRetentionTime());
            assertNotSame(0, scan.getChargeIndependentAnalysisList().size());
        }
        int i = 0;
        MS2ScanDb scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(2, scan.getStartScanNum());
        assertEquals(2, scan.getEndScanNum());
        assertEquals(1, scan.getPrecursorScanNum());
        assertEquals(0.01, scan.getRetentionTime().doubleValue());
        assertEquals(475.42, scan.getPrecursorMz().doubleValue());
        assertEquals(109, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(9, scan.getStartScanNum());
        assertEquals(9, scan.getEndScanNum());
        assertEquals(7, scan.getPrecursorScanNum());
        assertEquals(0.03, scan.getRetentionTime().doubleValue());
        assertEquals(1372.55, scan.getPrecursorMz().doubleValue());
        assertEquals(357, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(10, scan.getStartScanNum());
        assertEquals(10, scan.getEndScanNum());
        assertEquals(7, scan.getPrecursorScanNum());
        assertEquals(0.04, scan.getRetentionTime().doubleValue());
        assertEquals(717.62, scan.getPrecursorMz().doubleValue());
        assertEquals(293, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 2));
        assertEquals(24, scan.getStartScanNum());
        assertEquals(24, scan.getEndScanNum());
        assertEquals(19, scan.getPrecursorScanNum());
        assertEquals(0.09, scan.getRetentionTime().doubleValue());
        assertEquals(1374.58, scan.getPrecursorMz().doubleValue());
        assertEquals(711, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 1));
        assertEquals(26, scan.getStartScanNum());
        assertEquals(26, scan.getEndScanNum());
        assertEquals(25, scan.getPrecursorScanNum());
        assertEquals(0.1, scan.getRetentionTime().doubleValue());
        assertEquals(817.33, scan.getPrecursorMz().doubleValue());
        assertEquals(319, scan.getPeakCount());
    }
}
