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
import java.util.Map.Entry;

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
import org.yeastrc.ms.parser.ms2File.Ms2FileReaderException;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;


/**
 * 
 */
public class Ms2FileToDbConverter {

    public void uploadMs2File(String filePath) throws Ms2FileReaderException {
        
        Ms2FileReader reader = new Ms2FileReader();
        reader.open(filePath);
        uploadMs2File(filePath, reader);
            
    }

    public void uploadMs2File(InputStream inStream, String fileName) {
        Ms2FileReader reader = new Ms2FileReader();
        try {
            reader.open(inStream);
            uploadMs2File(fileName, reader);
            
        }
        catch (Ms2FileReaderException e) {
            e.printStackTrace();
        }
    }
    
    private void uploadMs2File(String file, Ms2FileReader reader)
            throws Ms2FileReaderException {
        Header header = reader.getHeader();
        // insert a run into the database and get the run Id
        int runId = saveMs2Header(header, 18, file);
        
        while (reader.hasScans()) {
            Scan scan = reader.getNextScan();
            // insert a scan into the database for the given run
            saveScan(scan, runId);
        }
    }
    
    private int saveMs2Header(Header header, int experimentId, String fileName) {
        
        MS2FileRun run = new MS2FileRun();
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
        run.setComment(header.getComments());
        
        // save the run
        MsRunDAO rundao = DAOFactory.instance().getMsRunDAO();
        int runID = rundao.save(run);
        
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
       //String file = "./resources/PARC_p75_01_itms.ms2";
       String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
//       String file = "./resources/NE063005ph8s01.ms2";
       try {
           uploader.uploadMs2File(file);
       }
       catch (Ms2FileReaderException e) {
           e.printStackTrace();
       }
    }
}
