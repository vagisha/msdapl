package org.yeastrc.ms.dbuploader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileRunHeadersDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;
import org.yeastrc.ms.dto.MsScanCharge;
import org.yeastrc.ms.dto.ms2File.Ms2FileChargeDependentAnalysis;
import org.yeastrc.ms.dto.ms2File.Ms2FileHeaders;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;

public class DbToMs2FileConverter {

    private BufferedWriter outFile = null;
    
    public void convertToMs2(int dbRunId, String output) throws IOException {
        
        try {
            outFile = new BufferedWriter(new FileWriter(output));

            MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
            MsRun run = runDao.load(dbRunId);
            if (run == null) {
                System.err.println("No run found with id: "+dbRunId);
                return;
            }
            printMs2Header(run);
            outFile.write("\n");

            MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
            List<Integer> scanIds = scanDao.loadScanIdsForRun(dbRunId);

            for (Integer scanId: scanIds) {
                MsScan scan = scanDao.load(scanId);
                printMs2Scan(scan);
                outFile.write("\n");
            }

            outFile.flush();
        }
        finally {
            if (outFile != null)
                outFile.close();
        }
        
    }
    
    private void printMs2Scan(MsScan scan) throws IOException {
       Scan ms2scan = new Scan();
       ms2scan.setStartScan(scan.getStartScanNum());
       ms2scan.setEndScan(scan.getEndScanNum());
       ms2scan.setPrecursorMz(scan.getPrecursorMz());
       
       ms2scan.addAnalysisItem(Scan.ACTIVATION_TYPE, scan.getFragmentationType());
       ms2scan.addAnalysisItem(Scan.PRECURSOR_SCAN, scan.getPrecursorScanNum()+"");
       ms2scan.addAnalysisItem(Scan.RET_TIME, scan.getRetentionTime()+"");
       
       // TODO remove this
       ms2scan.addAnalysisItem("PrecursorFile", "p75_01_itms.ms1");
       
       // add predicted charge states for the scan
       List <ScanCharge> charges = getChargeStates(scan);
       for (ScanCharge ch: charges) 
           ms2scan.addChargeState(ch);
       
       // finally, the peak data!
       ms2scan.setPeaks(scan.getPeaks());
       
       outFile.write(ms2scan.toString());
    }

    private List<ScanCharge> getChargeStates(MsScan scan) {
        MS2FileScanChargeDAO scDao = DAOFactory.instance().getMsScanChargeDAO();
        List <MsScanCharge> msChgStates = scDao.loadChargesForScan(scan.getId());
        List <ScanCharge> chgStates = new ArrayList<ScanCharge>(msChgStates.size());
        for (MsScanCharge msChg: msChgStates) {
            ScanCharge chg = new ScanCharge();
            chg.setCharge(msChg.getCharge());
            chg.setMass(msChg.getMass());
            chgStates.add(chg);
            
            // add any charge dependent analysis for this charge state
            Ms2FileChargeDependentAnalysisDAO chgDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
            List <Ms2FileChargeDependentAnalysis> analysisList = chgDao.loadAnalysisForScanCharge(msChg.getId());
            for (Ms2FileChargeDependentAnalysis analysis: analysisList) {
                chg.addAnalysisItem(analysis.getHeader(), analysis.getValue());
            }
        }
        return chgStates;
    }

    private void printMs2Header(MsRun run) throws IOException {
        Header ms2Header = new Header();
        ms2Header.addHeaderItem(Header.CREATION_DATE, run.getCreationDate());
        ms2Header.addHeaderItem(Header.EXTRACTOR, run.getConversionSW());
        ms2Header.addHeaderItem(Header.EXTRACTOR_VERSION, run.getConversionSWVersion());
        ms2Header.addHeaderItem(Header.EXTRACTOR_OPTIONS, run.getConversionSWOptions());
        ms2Header.addHeaderItem(Header.COMMENTS, run.getComment());
        ms2Header.addHeaderItem(Header.INSTRUMENT_TYPE, run.getInstrumentModel());
        ms2Header.addHeaderItem("ScanType", "MS2");
        
        // TODO remove these
        ms2Header.addHeaderItem("AcquisitionMethod", "Data-Dependent");
        ms2Header.addHeaderItem("DataType", "Centroid");
        ms2Header.addHeaderItem("IsolationWindow", "");
        ms2Header.addHeaderItem("FirstScan", "1");
        ms2Header.addHeaderItem("LastScan", "17903");
        
        // add any MS2 file specific IAnalyzer and DAnalyzer headers
        MS2FileRunHeadersDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        Ms2FileHeaders headers = headerDao.loadHeadersForRun(run.getId());
        ms2Header.addHeaderItem(Header.IANALYZER, headers.getiAnalyzer());
        ms2Header.addHeaderItem(Header.IANALYZER_VERSION, headers.getiAnalyzerVersion());
        ms2Header.addHeaderItem(Header.IANALYZER_OPTIONS, headers.getiAnalyzerOptions());
        ms2Header.addHeaderItem(Header.DANALYZER, headers.getdAnalyzer());
        ms2Header.addHeaderItem(Header.DANALYZER_VERSION, headers.getdAnalyzerVersion());
        ms2Header.addHeaderItem(Header.DANALYZER_OPTIONS, headers.getdAnalyzerOptions());
        
        outFile.write(ms2Header.toString());
        
       
    }
    
    public static void main (String[] args) {
        DbToMs2FileConverter converter = new DbToMs2FileConverter();
        try {
            converter.convertToMs2(1, "db2ms2.ms2");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
