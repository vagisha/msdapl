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

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanChargeDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileRunHeadersDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;
import org.yeastrc.ms.dto.MsScanCharge;
import org.yeastrc.ms.dto.ms2File.Ms2FileChargeDependentAnalysis;
import org.yeastrc.ms.dto.ms2File.Ms2FileChargeIndependentAnalysis;
import org.yeastrc.ms.dto.ms2File.Ms2FileHeaders;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.ms2File.Ms2FileReaderException;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;


/**
 * 
 */
public class Ms2FileToDbConverter {

    public void uploadMs2File(String filePath) {
        
        Ms2FileReader reader = new Ms2FileReader();
        try {
            reader.open(filePath);
            uploadMs2File(filePath, reader);
            
        }
        catch (Ms2FileReaderException e) {
            e.printStackTrace();
        }
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
        run.setComment(header.getComments());
        
        // save the run
        MsRunDAO rundao = DAOFactory.instance().getMsRunDAO();
        int runID = rundao.save(run);
        
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
            MS2FileRunHeadersDAO headersDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
            headersDao.save(headers);
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
        scan.setPeaksBinary(ms2Scan.getPeaksBinary());
        
        // save the scan
        MsScanDAO scanDAO = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDAO.save(scan);
        
        // save the scan charges
        for (ScanCharge scanCharge: ms2Scan.getChargeStates()) {
            saveScanCharge(scanCharge, scanId);
        }
        
        // save the charge independent analysis
        Ms2FileChargeIndependentAnalysisDAO analysisDAO = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        HashMap<String, String> analysisItems = ms2Scan.getAnalysisItems();
        for (String label: analysisItems.keySet()) {
            Ms2FileChargeIndependentAnalysis analysis = new Ms2FileChargeIndependentAnalysis();
            analysis.setScanId(scanId);
            analysis.setHeader(label);
            analysis.setValue(analysisItems.get(label));
            analysisDAO.save(analysis);
        }
    }
    
    private void saveScanCharge(ScanCharge ms2ScanCharge, int scanId) {
        MsScanCharge scanCharge = new MsScanCharge();
        scanCharge.setScanId(scanId);
        scanCharge.setCharge(ms2ScanCharge.getCharge());
        scanCharge.setMass(ms2ScanCharge.getMass());
        
        // save the scan charge
        MsScanChargeDAO sChgDAO = DAOFactory.instance().getMsScanChargeDAO();
        int sChgId = sChgDAO.save(scanCharge);
        
        // save the charge dependent analysis associated with this charge state
        Ms2FileChargeDependentAnalysisDAO analysisDAO = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
        HashMap<String, String> analysisItems = ms2ScanCharge.getAnalysisItems();
        for (String label: analysisItems.keySet()) {
            Ms2FileChargeDependentAnalysis analysis = new Ms2FileChargeDependentAnalysis();
            analysis.setScanChargeId(sChgId);
            analysis.setHeader(label);
            analysis.setValue(analysisItems.get(label));
            analysisDAO.save(analysis);
        }
    }
    
    
    public static void main(String[] args) {
       Ms2FileToDbConverter uploader = new Ms2FileToDbConverter();
       //String file = "./resources/sample.ms2";
       //String file = "./resources/PARC_p75_01_itms.ms2";
       String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
//       String file = "./resources/NE063005ph8s01.ms2";
       uploader.uploadMs2File(file);
    }
}
