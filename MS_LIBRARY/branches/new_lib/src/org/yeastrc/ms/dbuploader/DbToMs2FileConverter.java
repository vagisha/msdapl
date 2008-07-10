package org.yeastrc.ms.dbuploader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.domain.ms2File.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.domain.ms2File.MS2FileHeader;
import org.yeastrc.ms.domain.ms2File.MS2FileRun;
import org.yeastrc.ms.domain.ms2File.MS2FileScan;
import org.yeastrc.ms.domain.ms2File.MS2FileScanCharge;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;

public class DbToMs2FileConverter {

    private BufferedWriter outFile = null;
    
    public void convertToMs2(int dbRunId, String output) throws IOException {
        
        try {
            outFile = new BufferedWriter(new FileWriter(output));

            MsRunDAO<MS2FileRun> runDao = DAOFactory.instance().getMS2FileRunDAO();
            MS2FileRun run = runDao.loadRun(dbRunId);
            if (run == null) {
                System.err.println("No run found with id: "+dbRunId);
                return;
            }
            printMs2Header(run);
            outFile.write("\n");

            MsScanDAO<MS2FileScan> scanDao = DAOFactory.instance().getMS2FileScanDAO();
            List<Integer> scanIds = scanDao.loadScanIdsForRun(dbRunId);

            for (Integer scanId: scanIds) {
                MS2FileScan scan = scanDao.load(scanId);
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
    
    private void printMs2Scan(MS2FileScan scan) throws IOException {
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

    private List<ScanCharge> getChargeStates(MS2FileScan scan) {
        MS2FileScanChargeDAO scDao = DAOFactory.instance().getMS2FileScanChargeDAO();
        List <MS2FileScanCharge> msChgStates = scDao.loadScanChargesForScan(scan.getId());
        List <ScanCharge> chgStates = new ArrayList<ScanCharge>(msChgStates.size());
        for (MS2FileScanCharge msChg: msChgStates) {
            ScanCharge chg = new ScanCharge();
            chg.setCharge(msChg.getCharge());
            chg.setMass(msChg.getMass());
            chgStates.add(chg);
            
            // add any charge dependent analysis for this charge state
            MS2FileChargeDependentAnalysisDAO chgDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
            List <MS2FileChargeDependentAnalysis> analysisList = chgDao.loadAnalysisForScanCharge(msChg.getId());
            for (MS2FileChargeDependentAnalysis analysis: analysisList) {
                chg.addAnalysisItem(analysis.getName(), analysis.getValue());
            }
        }
        return chgStates;
    }

    private void printMs2Header(MS2FileRun run) throws IOException {
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
        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        List<MS2FileHeader> headers = headerDao.loadHeadersForRun(run.getId());
        for (MS2FileHeader header: headers) {
            ms2Header.addHeaderItem(header.getName(), header.getValue());
        }
        outFile.write(ms2Header.toString());
        
       
    }
    
    public static void main (String[] args) {
        DbToMs2FileConverter converter = new DbToMs2FileConverter();
        try {
            long start = System.currentTimeMillis();
            converter.convertToMs2(1, "db2ms2_string.ms2");
            long end = System.currentTimeMillis();
            long timeElapsed = (end - start)/1000;
            System.out.println("Seconds to convert to MS2: "+timeElapsed);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
