/**
 * Ms2FileToDbUploader.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeIndependentAnalysis;
import org.yeastrc.ms.dto.ms2File.MS2FileHeader;
import org.yeastrc.ms.dto.ms2File.MS2FileRun;
import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;
import org.yeastrc.ms.util.Sha1SumCalculator;


/**
 * 
 */
public class Ms2FileToDbConverter {

    private static final Logger log = Logger.getLogger(Ms2FileToDbConverter.class);
    
    /**
     * @param filePath
     * @param experimentId
     * @return true if file was uploaded to the database; false otherwise
     * @throws Ms2FileReaderException if an error occurs while parsing the file
     */
    public boolean convertMs2File(String filePath, int experimentId) throws Exception {
        
        String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        
        String justFileName = new File(filePath).getName();
        if (fileIsInDb(justFileName, sha1Sum)) {
            log.warn("Aborting upload of file: "+filePath+". This run already exists in the database");
            return false;
        }
        
        Ms2FileReader reader = new Ms2FileReader();
        reader.open(filePath);
        
        convertMs2File(filePath, reader, experimentId, sha1Sum);
        return true;
    }

    public boolean convertMs2File(InputStream inStream, String fileName, int experimentId) throws Exception {
        
        String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(inStream);
        
        if (fileIsInDb(fileName, sha1Sum)) {
            log.warn("Aborting upload of file: "+fileName+". This run already exists in the database");
            return false;
        }
        
        Ms2FileReader reader = new Ms2FileReader();
        reader.open(inStream);
        
        convertMs2File(fileName, reader, experimentId, sha1Sum);
        return true;  
    }
    
    boolean fileIsInDb(String fileName, String sha1Sum) {
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        List <MsRun> runs = runDao.loadRuns(fileName, sha1Sum);
        // if a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload this run
        
        if (runs.size() > 0)
            return true;
        return false;
        
    }

    private void convertMs2File(String file, Ms2FileReader reader, int experimentId, String sha1Sum)
            throws Exception {
        Header header = reader.getHeader();
        // insert a run into the database and get the run Id
        int runId = saveMs2Header(header, experimentId, file, sha1Sum);
        
        while (reader.hasScans()) {
            Scan scan = reader.getNextScan();
            // insert a scan into the database for the given run
            saveScan(scan, runId);
        }
    }
    
    private int saveMs2Header(Header header, int experimentId, String fileName, String sha1Sum) {
        
        MS2FileRun run = new MS2FileRun();
        run.setId(0); // new run; set id to 0
        run.setMsExperimentId(experimentId);
        run.setFileName(new File(fileName).getName());
        run.setFileFormat(MsRun.RunFileFormat.MS2.name());
        run.setSha1Sum(sha1Sum);
        run.setCreationDate(header.getCreationDate());
        run.setConversionSW(header.getExtractor());
        run.setConversionSWVersion(header.getExtractorVersion());
        run.setConversionSWOptions(header.getExtractorOptions());
        // MS2 files don't have instrument vendor information, AFAIK
//        run.setInstrumentVendor("");
        run.setInstrumentModel(header.getInstrumentType());
        run.setInstrumentSN(header.getInstrumentSN());
        run.setComment(header.getComments());
        
        // save the run
        MsRunDAO rundao = DAOFactory.instance().getMsRunDAO();
        int runID = rundao.saveRun(run);
        
        if (runID != 0) {
            // save all the headers from the MS2 file (some headers are already a part of the MsRun object created above)
            MS2FileHeaderDAO headersDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
            Iterator<Entry<String,String>> headerIterator = header.iterator();
            while(headerIterator.hasNext()) {
                Entry<String, String> headerEntry = headerIterator.next();
                MS2FileHeader h = new MS2FileHeader();
                h.setHeaderName(headerEntry.getKey());
                h.setValue(headerEntry.getValue());
                h.setRunId(runID);
                headersDao.save(h);
            }
        }
        return runID;
    }
    
    private void saveScan(Scan ms2Scan, int runId) {
        MsScan scan = new MsScan();
        scan.setRunId(runId);
        scan.setStartScanNum(ms2Scan.getStartScan());
        scan.setEndScanNum(ms2Scan.getEndScan());
        scan.setMsLevel(2);
        scan.setRetentionTime(ms2Scan.getRetentionTime());
        scan.setFragmentationType(ms2Scan.getActivationType());
        scan.setPrecursorMz(ms2Scan.getPrecursorMz());
        scan.setPrecursorScanNum(ms2Scan.getPrecursorScanNumber());
        scan.setPeaks(ms2Scan.getPeaks());
        
        // save the scan
        MsScanDAO scanDAO = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDAO.save(scan);
        
        // save the scan charges
        for (ScanCharge scanCharge: ms2Scan.getChargeStates()) {
            saveScanCharge(scanCharge, scanId);
        }
        
        // save the charge independent analysis
        MS2FileChargeIndependentAnalysisDAO analysisDAO = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        HashMap<String, String> analysisItems = ms2Scan.getAnalysisItems();
        for (String label: analysisItems.keySet()) {
            MS2FileChargeIndependentAnalysis analysis = new MS2FileChargeIndependentAnalysis();
            analysis.setScanId(scanId);
            analysis.setHeader(label);
            analysis.setValue(analysisItems.get(label));
            analysisDAO.save(analysis);
        }
    }
    
    private void saveScanCharge(ScanCharge ms2ScanCharge, int scanId) {
        MS2FileScanCharge scanCharge = new MS2FileScanCharge();
        scanCharge.setScanId(scanId);
        scanCharge.setCharge(ms2ScanCharge.getCharge());
        scanCharge.setMass(ms2ScanCharge.getMass());
        
        // save the scan charge
        MS2FileScanChargeDAO sChgDAO = DAOFactory.instance().getMsScanChargeDAO();
        int sChgId = sChgDAO.save(scanCharge);
        
        // save the charge dependent analysis associated with this charge state
        MS2FileChargeDependentAnalysisDAO analysisDAO = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
        HashMap<String, String> analysisItems = ms2ScanCharge.getAnalysisItems();
        for (String label: analysisItems.keySet()) {
            MS2FileChargeDependentAnalysis analysis = new MS2FileChargeDependentAnalysis();
            analysis.setScanChargeId(sChgId);
            analysis.setHeader(label);
            analysis.setValue(analysisItems.get(label));
            analysisDAO.save(analysis);
        }
    }
    
    
    public static void main(String[] args) {
       Ms2FileToDbConverter uploader = new Ms2FileToDbConverter();
//       String file = "./resources/sample.ms2";
       String file = "./resources/PARC_p75_01_itms.ms2";
//       String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
//       String file = "./resources/NE063005ph8s01.ms2";
       long start = System.currentTimeMillis();
       try {
           uploader.convertMs2File(file, 18);
       }
       catch (Exception e) {
           e.printStackTrace();
       }
       long end = System.currentTimeMillis();
       long timeElapsed = (end - start)/1000;
       System.out.println("Seconds to upload: "+timeElapsed);
       
    }
}
