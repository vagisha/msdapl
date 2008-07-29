package org.yeastrc.ms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.yeastrc.ms.dao.BaseDAOTestCase;

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
        String script = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/src/resetDatabase.sh";
        try {
            Process proc = runtime.exec("sh "+script);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//            String line = reader.readLine();
//            while(line != null) {
//                System.out.println(line);
//                line = reader.readLine();
//            }
//            reader.close();
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
        catch(IllegalArgumentException e) {
            assertEquals("Directory does not exist: "+dir, e.getMessage());
        }
    }

    public void testUploadExperimentToDbEmptyDirectory() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/empty_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }

    public void testUploadExperimentToDbMissingMS2Files() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/missingMS2_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }
    
    public void testUploadExperimentToDbPercolatorSQTFiles() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/percolatorSQT_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }
    
    public void testUploadExperimentToDbProlucidSQTFiles() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/prolucidSQT_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }
    
    public void testUploadExperimentToDbPepProbeSQTFiles() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/pepprobeSQT_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }
    
    public void testUploadExperimentInvalidSQTHeader() {
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/invalidSQTHeader_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
    }
    
//    public void testUploadExperimentNoValidMS2Scans() {
//        resetDatabase();
//        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/noValidMS2Scans_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentNoScanIdFound() {
//        resetDatabase();
//        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/noScanIdFound_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
    
    public void testUploadValidData() {
        resetDatabase();
        String dir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/validData_dir";
        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(1, expId);
        
        // read from database and make sure files are identical
        String outputTest = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/validData_dir/fromDb.ms2";
        DbToMs2FileConverter ms2Converter = new DbToMs2FileConverter();
        try {
            ms2Converter.convertToMs2(1, outputTest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.ms2"));
        // remove the output file
        assertTrue(new File(outputTest).delete());
        
        // upload the same experiment.  This time the MS2 file should not be uploaded
        // so the returned experimentID should be 0;
        expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
        assertEquals(0, expId);
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
