/**
 * Ms2FileToDbUploader.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms;

import java.io.File;

import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileRunHeadersDAO;
import org.yeastrc.ms.ms2File.db.Ms2FileHeaders;
import org.yeastrc.ms.ms2File.parser.Ms2FileHeader;
import org.yeastrc.ms.ms2File.parser.Ms2FileReader;
import org.yeastrc.ms.ms2File.parser.Ms2FileReaderException;
import org.yeastrc.ms.ms2File.parser.Ms2FileScan;


/**
 * 
 */
public class Ms2FileToDbUploader {

    public Ms2FileToDbUploader() {
        
    }
    
    private void uploadMs2File(String file) {
        
        Ms2FileReader reader = new Ms2FileReader();
        try {
            reader.open(file);
            Ms2FileHeader header = reader.getHeader();
            // insert a run into the database and get the run Id
            int runId = insertMs2Header(header, 18, file);
            
            while (reader.hasScans()) {
                Ms2FileScan scan = reader.getNextScan();
                // insert a scan into the database for the given run
                insertScan(scan, runId);
            }
            
        }
        catch (Ms2FileReaderException e) {
            e.printStackTrace();
        }
    }
    
    private int insertMs2Header(Ms2FileHeader header, int experimentId, String fileName) {
        
        MsRun run = new MsRun();
        run.setId(0); // new run set id to 0
        run.setMsExperimentId(experimentId);
        run.setFileName(new File(fileName).getName());
        run.setFileFormat(MsRun.RunFileFormat.MS2.name());
        run.setCreationDate(header.getCreationDate());
        run.setConversionSW(header.getExtractor());
        run.setConversionSWVersion(header.getExtractorVersion());
        run.setConversionSWOptions(header.getExtractorOptions());
//        run.setInstrumentVendor("");
        run.setInstrumentModel(header.getInstrumentType());
        run.setInstrumentSN(header.getInstrumentSN());
        run.setFragmentationType(header.getActivationType());
        run.setComment(header.getComments());
        
        // save the run
        MsRunDAO rundao = new MsRunDAO();
        int runID = rundao.insertMsRun(run);
        if (runID != 0) {
            // save the other headers from the ms2 file
            Ms2FileHeaders headers = new Ms2FileHeaders();
            headers.setiAnalyzer(header.getIAnalyzer());
            headers.setiAnalyzerVersion(header.getIAnalyzerVersion());
            headers.setiAnalyzerOptions(header.getIAnalyzerOptions());
            headers.setdAnalyzer(header.getDAnalyzer());
            headers.setdAnalyzerVersion(header.getDAnalyzerVersion());
            headers.setdAnalyzerOptions(header.getDAnalyzerOptions());
            headers.setRunId(runID);
            Ms2FileRunHeadersDAO headersDao = new Ms2FileRunHeadersDAO();
            headersDao.insert(headers);
        }
        return runID;
    }
    
    private void insertScan(Ms2FileScan scan, int runId) {
        
    }
    
    
    public static void main(String[] args) {
       Ms2FileToDbUploader uploader = new Ms2FileToDbUploader();
       String file = "./resources/sample.ms2";
       uploader.uploadMs2File(file);
    }
}
