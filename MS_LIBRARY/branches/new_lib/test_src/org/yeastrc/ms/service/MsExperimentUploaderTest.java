package org.yeastrc.ms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public class MsExperimentUploaderTest extends BaseDAOTestCase {

    private MsExperimentUploader uploader = null;
    private static final Runtime runtime = Runtime.getRuntime();
    
    protected void setUp() throws Exception {
        super.setUp();
        uploader = new MsExperimentUploader();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void resetDatabase() {
        System.out.println("Resetting database");
        String script = "src/resetDatabase.sh";
        try {
            Process proc = runtime.exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void testUploadExperimentToDbInvalidDirectory() {
        String dir = "dummy/directory";
        try {uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir); fail("Directory "+dir+" does not exist");}
        catch(UploadException e) {
            assertEquals(ERROR_CODE.DIRECTORY_NOT_FOUND, e.getErrorCode());
        }
    }

    public void testUploadExperimentToDbEmptyDirectory() {
        String dir = "test_resources/empty_dir";
        int expId = 0;
        try {
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
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
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
            fail("Upload directory has missing ms2 files");
        }
        catch (UploadException e) {
            assertEquals(ERROR_CODE.MISSING_MS2, e.getErrorCode());
        }
        assertEquals(0, expId);
    }
    
    
    public void testUploadValidData() {
        resetDatabase();
        String dir = "test_resources/validData_dir";
        int expId = 0;
        try {
            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
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
            expId2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
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
        resetDatabase();
        String dir = "test_resources/invalid_ms2_S_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
            fail("2.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid 'Z' line.\n\tLINE NUMBER: 37\n\tLINE: Z\t1\t1372.55laksdjflkasf;a";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertTrue(DAOFactory.instance().getMsExperimentDAO().selectAllExperimentIds().size() == 0);
        assertNull(DAOFactory.instance().getMsRunDAO().loadRun(1));
        assertNull(DAOFactory.instance().getMsSearchDAO().loadSearch(1));
    }
    
    public void testUploadInvalidMS2_peak() {
        resetDatabase();
        String dir = "test_resources/invalid_ms2_peak_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
            fail("1.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid MS2 scan -- no valid peaks and/or charge states found for scan: 11"+
            "\n\tLINE NUMBER: 61\n\tLINE: S\t000012\t000012\t1394.58000";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertTrue(DAOFactory.instance().getMsExperimentDAO().selectAllExperimentIds().size() == 0);
        assertNull(DAOFactory.instance().getMsRunDAO().loadRun(1));
        assertNull(DAOFactory.instance().getMsSearchDAO().loadSearch(1));
    }
    
    public void testUploadInvalidMS2_Z() {
        resetDatabase();
        String dir = "test_resources/invalid_ms2_Z_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
            fail("1.ms2 is invalid");
        }
        catch (UploadException e1) {
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid 'Z' line.\n\tLINE NUMBER: 60\n\tLINE: Z\t1\t1394.58 invalid Z line";
            System.out.println(e1.getMessage());
            assertTrue(e1.getMessage().contains(msg));
        }
        assertTrue(DAOFactory.instance().getMsExperimentDAO().selectAllExperimentIds().size() == 0);
        assertNull(DAOFactory.instance().getMsRunDAO().loadRun(1));
        assertNull(DAOFactory.instance().getMsSearchDAO().loadSearch(1));
    }
    
    public void testUploadInvalidSQTFiles() {
      resetDatabase();
      String dir = "test_resources/invalid_sqt_dir";
      try {
          uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
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
      assertEquals(3, DAOFactory.instance().getMsRunDAO().loadExperimentRuns(1).size());
      assertEquals(0, DAOFactory.instance().getMsSearchDAO().loadSearchIdsForExperiment(1).size());
      
    }
    
    public void testUploadExperimentInvalidSQTHeader() {
        resetDatabase();
        String dir = "test_resources/invalidSQTHeader_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        }
        catch (UploadException e) {
            fail("Valid ms2 file in directory");
        }
        
        List<UploadException> exceptions = uploader.getUploadExceptionList();
        assertEquals(1, exceptions.size());
        
        assertEquals(1, exceptions.size());
        assertEquals(1, DAOFactory.instance().getMsRunDAO().loadExperimentRuns(1).size());
        assertEquals(0, DAOFactory.instance().getMsSearchDAO().loadSearchIdsForExperiment(1).size());
    }
    
    public void testUploadExperimntNoScanIdFound() {
        resetDatabase();
        String dir = "test_resources/noScanIdFound_dir";
        try {
            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        }
        catch (UploadException e) {
            fail("Valid ms2 file in directory");
        }
        
        List<UploadException> exceptions = uploader.getUploadExceptionList();
        assertEquals(1, exceptions.size());
        System.out.println(uploader.getUploadWarnings());
        assertEquals(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN, exceptions.get(0).getErrorCode());
        
        assertEquals(1, exceptions.size());
        assertEquals(1, DAOFactory.instance().getMsRunDAO().loadExperimentRuns(1).size());
        assertEquals(0, DAOFactory.instance().getMsSearchDAO().loadSearchIdsForExperiment(1).size());
    }

    
//    public void testUploadValidDataWWarnings() {
//        resetDatabase();
//        String dir = "test_resources/validData_w_warnings_dir";
//        int expId = 0;
//        try {
//            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        }
//        catch (UploadException e1) {
//            e1.printStackTrace();
//        }
//        assertEquals(1, expId);
//        
//        // read from database and make sure files are identical (MS2 files)
//        String outputTest = "test_resources/validData_w_warnings_dir/fromDb.ms2";
//        // remove the output if it already exists
//        new File(outputTest).delete();
//        DbToMs2FileConverter ms2Converter = new DbToMs2FileConverter();
//        // compare the first ms2 file uploaded
//        try {
//            ms2Converter.convertToMs2(1, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.ms2.valid"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//    }
    
    
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
