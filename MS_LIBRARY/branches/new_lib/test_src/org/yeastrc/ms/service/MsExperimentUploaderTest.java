package org.yeastrc.ms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.MsLibTests;
import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public class MsExperimentUploaderTest extends BaseDAOTestCase {

    private MsExperimentUploader uploader = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        uploader = new MsExperimentUploader();
        MsLibTests.resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testUploadExperimentToDbInvalidDirectory() {
        String dir = "dummy/directory";
        try {uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false); fail("Directory "+dir+" does not exist");}
        catch(UploadException e) {
            assertEquals(ERROR_CODE.DIRECTORY_NOT_FOUND, e.getErrorCode());
        }
    }

    public void testUploadExperimentToDbEmptyDirectory() {
        String dir = "test_resources/empty_dir";
        int expId = 0;
        try {
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
            fail("Upload directory is empty");
        }
        catch (UploadException e) {
            assertEquals(ERROR_CODE.EMPTY_DIRECTORY, e.getErrorCode());
        }
        assertEquals(0, expId);
    }

    public void testUploadExperimentToDbMissingMS2Files() {
        String dir = "test_resources/missingMS2_dir";
        int expId = 0;
        try {
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
            fail("Upload directory has missing ms2 files");
        }
        catch (UploadException e) {
            assertEquals(ERROR_CODE.MISSING_MS2, e.getErrorCode());
        }
        assertEquals(0, expId);
    }
    
    
    public void testUploadValidData() {
        String dir = "test_resources/validData_dir";
        int expId = 0;
        try {
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
        }
        catch (UploadException e1) {
            fail("Data is valid. Error message: "+e1.getMessage());
        }
        assertEquals(1, expId);
        
        // read from database and make sure files are identical (MS2 files)
        String outputTest = "test_resources/validData_dir/fromDb.ms2";
        // remove the output if it already exists
        new File(outputTest).delete();
        DbToMs2FileConverter ms2Converter = new DbToMs2FileConverter();
        // compare the first ms2 file uploaded
        try {
            ms2Converter.convertToMs2(1, outputTest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.ms2"));
        // remove the output file
        assertTrue(new File(outputTest).delete());
        // compare the second ms2 file uploaded
        try {
            ms2Converter.convertToMs2(2, outputTest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.ms2"));
        // remove the output file
        assertTrue(new File(outputTest).delete());
        
        // read from database and make sure files are identical (SQT files)
        outputTest = "test_resources/validData_dir/fromDb.sqt";
        // remove the output if it already exists
        new File(outputTest).delete();
        DbToSqtFileConverter sqtConverter = new DbToSqtFileConverter();
        // compare the first sqt file uploaded
        try {
            sqtConverter.convertToSqt(1, outputTest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.sqt"));
        // remove the output file
        assertTrue(new File(outputTest).delete());
        // compare the second sqt file uploaded
        try {
            sqtConverter.convertToSqt(2, outputTest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.sqt"));
        // remove the output file
        assertTrue(new File(outputTest).delete());
        
        
        // upload the same experiment.  This time the MS2 file should not be uploaded
        // we should have a new experiment id and there should be two entries in the msExperimentRun
        // table for the runs uploaded before.
        List<Integer> runIds = expDao.loadRunIdsForExperiment(expId);
        int expId2 = 0;
        try {
            expId2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
        }
        catch (UploadException e) {
            fail("Data is valid. Error message: "+e.getMessage());
        }
        assertEquals(2, expId2);
        
        for (Integer runId: runIds) {
            List<Integer> expIds = expDao.loadExperimentIdsForRun(runId);
            assertEquals(2, expIds.size());
            Collections.sort(expIds);
            assertEquals(expId, expIds.get(0).intValue());
            assertEquals(expId2, expIds.get(1).intValue());
        }
    }

    public void testUploadInvalidMS2_S() {
        String dir = "test_resources/invalid_ms2_S_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
            fail("2.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid 'Z' line.\n\tLINE NUMBER: 37\n\tLINE: Z\t1\t1372.55laksdjflkasf;a";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertEquals(0, expDao.selectAllExperimentIds().size());
        assertNull(runDao.loadRun(1));
        assertNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidMS2_peak() {
        String dir = "test_resources/invalid_ms2_peak_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
            fail("1.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid MS2 scan -- no valid peaks and/or charge states found for scan: 11"+
            "\n\tLINE NUMBER: 61\n\tLINE: S\t000012\t000012\t1394.58000";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertEquals(0, expDao.selectAllExperimentIds().size());
        assertNull(runDao.loadRun(1));
        assertNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidMS2_Z() {
        String dir = "test_resources/invalid_ms2_Z_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
            fail("1.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid 'Z' line.\n\tLINE NUMBER: 60\n\tLINE: Z\t1\t1394.58 invalid Z line";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertEquals(0, expDao.selectAllExperimentIds().size());
        assertNull(runDao.loadRun(1));
        assertNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidSQTFiles() {
      String dir = "test_resources/invalid_sqt_dir";
      try {
          uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
      }
      catch (UploadException e1) {
         fail("Valid ms2 files in directory");
      }
      List<UploadException> exceptionList = uploader.getUploadExceptionList();
      String warnings = "WARNING: Non-SEQUEST sqt files are not supported"+
                          "\n\tFile: test_resources/invalid_sqt_dir/percolator.sqt"+
                          "\n\nWARNING: Non-SEQUEST sqt files are not supported"+
                          "\n\tFile: test_resources/invalid_sqt_dir/pepprobe.sqt"+
                          "\n\nWARNING: Non-SEQUEST sqt files are not supported"+
                          "\n\tFile: test_resources/invalid_sqt_dir/prolucid.sqt";
      
      assertEquals(warnings, uploader.getUploadWarnings().trim());
      
      assertEquals(3, exceptionList.size());
      assertEquals(3, runDao.loadExperimentRuns(1).size());
      assertEquals(0, searchDao.loadSearchIdsForExperiment(1).size());
      
    }
    
    public void testUploadExperimentInvalidSQTHeader() {
        String dir = "test_resources/invalidSQTHeader_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
        }
        catch (UploadException e) {
            fail("Valid ms2 file in directory");
        }
        
        List<UploadException> exceptions = uploader.getUploadExceptionList();
        assertEquals(1, exceptions.size());
        
        assertEquals(1, exceptions.size());
        assertEquals(1, runDao.loadExperimentRuns(1).size());
        assertEquals(0, searchDao.loadSearchIdsForExperiment(1).size());
    }
    
    public void testUploadExperimntNoScanIdFound() {
        String dir = "test_resources/noScanIdFound_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, false);
        }
        catch (UploadException e) {
            fail("Valid ms2 file in directory");
        }
        
        List<UploadException> exceptions = uploader.getUploadExceptionList();
        assertEquals(1, exceptions.size());
        System.out.println(uploader.getUploadWarnings());
        assertEquals(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN, exceptions.get(0).getErrorCode());
        
        assertEquals(1, exceptions.size());
        assertEquals(1, runDao.loadExperimentRuns(1).size());
        assertEquals(0, searchDao.loadSearchIdsForExperiment(1).size());
    }

    public void testDeleteExperiment() {
        String exp1Dir = "test_resources/deleteExperiment_dir/one"; //has ONE ms2, sqt pair
        String exp2Dir = "test_resources/deleteExperiment_dir/two"; //has TWO ms2, sqt pair (one of them is the same as above)
        
        int expID1 = 0;
        int expID2 = 0;
        try {
            expID1 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", exp1Dir, false);
            expID2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", exp2Dir, false);
        }
        catch (UploadException e) {
            fail("Valid files in both directories. Error is: "+e.getMessage());
        }
        
        assertEquals(1, expID1);
        assertEquals(2, expID2);
        
        // make sure everything got uploaded
        assertEquals(1, searchDao.loadSearchIdsForExperiment(expID1).size());
        List<MsRunDb> runs1 = runDao.loadExperimentRuns(expID1);
        assertEquals(1, runs1.size());
        
        assertEquals(2, searchDao.loadSearchIdsForExperiment(expID2).size());
        List<MsRunDb> runs2 = runDao.loadExperimentRuns(expID2);
        assertEquals(2, runs2.size());
        
        Set<Integer> distinctRunIds = new HashSet<Integer>();
        for (MsRunDb run: runs1)
            distinctRunIds.add(run.getId());
        for (MsRunDb run: runs2)
            distinctRunIds.add(run.getId());
        assertEquals(2, distinctRunIds.size());
        
        // delete the second experiment
        uploader.deleteExperiment(expID2);
        
        // make sure run common to both experiments is still there
        List<MsRunDb> runs = runDao.loadExperimentRuns(expID1);
        assertEquals(1, runs.size());
        assertEquals("771_5489.ms2", runs.get(0).getFileName());
        
        // make sure runs for deleted experiment are gone
        assertEquals(0, runDao.loadExperimentRuns(expID2).size());
        assertEquals(0, searchDao.loadSearchIdsForExperiment(expID2).size());
    }
    
    public void testCheckNonSqtFilesFirst() {
        String dir = "test_resources/invalid_sqt_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, true);
            fail("We care checking for non-sequest SQT's first. We have those in the directory");
        }
        catch (UploadException e) {
            assertEquals(ERROR_CODE.UNSUPPORTED_SQT, e.getErrorCode());
        }
        // make sure nothing got uploaded
        assertEquals(0, expDao.selectAllExperimentIds().size());
    }
    
    private boolean filesIdentical(String output, String input) {
        BufferedReader orig = null;
        BufferedReader fromDb = null;
        try {
           orig = new BufferedReader(new FileReader(input)); 
           fromDb = new BufferedReader(new FileReader(output));
           String origL = orig.readLine();
           String fromDbL = fromDb.readLine();
           while(origL != null) {
               origL = origL.trim().replaceAll("\\s+", " ");
               fromDbL = fromDbL.trim().replaceAll("\\s+", " ");
               assertEquals(origL, fromDbL);
               origL = orig.readLine();
               fromDbL = fromDb.readLine();
           }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
            
        }
        finally {
            try {
                orig.close();
                fromDb.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
