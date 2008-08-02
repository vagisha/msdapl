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
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDb;

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
    
//    public void testUploadExperimentToDbInvalidDirectory() {
//        String dir = "dummy/directory";
//        try {uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir); fail("Directory "+dir+" does not exist");}
//        catch(UploadException e) {
//            assertEquals("Directory does not exist: "+dir, e.getMessage());
//        }
//    }
//
//    public void testUploadExperimentToDbEmptyDirectory() {
//        String dir = "test_resources/empty_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//
//    public void testUploadExperimentToDbMissingMS2Files() {
//        String dir = "test_resources/missingMS2_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentToDbPercolatorSQTFiles() {
//        String dir = "test_resources/percolatorSQT_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentToDbProlucidSQTFiles() {
//        String dir = "test_resources/prolucidSQT_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentToDbPepProbeSQTFiles() {
//        String dir = "test_resources/pepprobeSQT_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentInvalidSQTHeader() {
//        String dir = "test_resources/invalidSQTHeader_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentNoValidMS2Scans() {
//        resetDatabase();
//        String dir = "test_resources/noValidMS2Scans_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadExperimentNoScanIdFound() {
//        resetDatabase();
//        String dir = "test_resources/noScanIdFound_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(0, expId);
//    }
//    
//    public void testUploadValidData() {
//        resetDatabase();
//        String dir = "test_resources/validData_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(1, expId);
//        
//        // read from database and make sure files are identical (MS2 files)
//        String outputTest = "test_resources/validData_dir/fromDb.ms2";
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
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.ms2"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        // compare the second ms2 file uploaded
//        try {
//            ms2Converter.convertToMs2(2, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.ms2"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        
//        // read from database and make sure files are identical (SQT files)
//        outputTest = "test_resources/validData_dir/fromDb.sqt";
//        // remove the output if it already exists
//        new File(outputTest).delete();
//        DbToSqtFileConverter sqtConverter = new DbToSqtFileConverter();
//        // compare the first sqt file uploaded
//        try {
//            sqtConverter.convertToSqt(1, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.sqt"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        // compare the second sqt file uploaded
//        try {
//            sqtConverter.convertToSqt(2, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.sqt"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        
//        
//        // upload the same experiment.  This time the MS2 file should not be uploaded
//        // so the returned experimentID should be the same as before. No new experiment
//        // should have been created.
//        expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        assertEquals(1, expId);
//    }
//
//    public void testUploadValidDataWWarnings() {
//        resetDatabase();
//        String dir = "test_resources/validData_w_warnings_dir";
//        int expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
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
//    
//    public void testUploadTwoSearchGroups() {
//        resetDatabase();
//        String dir = "test_resources/validData_dir";
//        
//        int expId1 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        int searchGroupId1 = uploader.getSearchGroupId();
//        List<Integer> searchIdList1 = uploader.getSearchIdList();
//        Collections.sort(searchIdList1);
//        assertEquals(2, searchIdList1.size());
//        
//        int expId2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir);
//        int searchGroupId2 = uploader.getSearchGroupId();
//        List<Integer> searchIdList2 = uploader.getSearchIdList();
//        Collections.sort(searchIdList2);
//        assertEquals(2, searchIdList2.size());
//        
//        assertEquals(expId1, expId2);
//        assertEquals(1, searchGroupId1);
//        assertEquals(searchGroupId1+1, searchGroupId2);
//        
//        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
//        for (Integer searchId: searchIdList1) {
//            MsSearchDb search = searchDao.loadSearch(searchId);
//            assertEquals(search.getSearchGroupId(), searchGroupId1);
//        }
//        
//        for (Integer searchId: searchIdList2) {
//            MsSearchDb search = searchDao.loadSearch(searchId);
//            assertEquals(search.getSearchGroupId(), searchGroupId2);
//        }
//        
//    }
    
    public void testGetSearchResultIdFor() {
        String peptide = "R.WAESGSGTSPESGDEEVSGAGS*SPVSGGVNLFANDGSFLELFKR.K";
        String filename = "NE063005ph8s13.18352.18352.3";
        int experimentId = 1;
        int searchGroupId = 1;
        int searchResultId = MsExperimentUploader.getSearchResultIdFor(experimentId, searchGroupId, filename, peptide);
        System.out.println("Serach resultid found: "+searchResultId);
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
