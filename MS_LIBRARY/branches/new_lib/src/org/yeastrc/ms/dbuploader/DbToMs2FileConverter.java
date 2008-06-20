package org.yeastrc.ms.dbuploader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanChargeDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;
import org.yeastrc.ms.dto.MsScanCharge;
import org.yeastrc.ms.dto.Peak;
import org.yeastrc.ms.dto.Peaks;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;

public class DbToMs2FileConverter {

    private BufferedWriter outFile = null;
    
    public void convertToMs2(int runId) {
        
        try {
            outFile = new BufferedWriter(new FileWriter("ms2test.ms2"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        MsRun run = runDao.load(runId);
        
        try {
            printMs2Header(run);
            outFile.write("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        
        for (Integer scanId: scanIds) {
            MsScan scan = scanDao.load(scanId);
            try {
                printMs2Header(scan);
                outFile.write("\n");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
        try {
            outFile.flush();
            outFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void printMs2Header(MsScan scan) throws IOException {
       Scan ms2scan = new Scan();
       ms2scan.setStartScan(scan.getStartScanNum());
       ms2scan.setEndScan(scan.getEndScanNum());
       ms2scan.setPrecursorMz(scan.getPrecursorMz());
       
       ms2scan.addAnalysisItem(Scan.ACTIVATION_TYPE, scan.getFragmentationType());
       ms2scan.addAnalysisItem(Scan.PRECURSOR_SCAN, scan.getPrecursorScanNum()+"");
       ms2scan.addAnalysisItem(Scan.RET_TIME, scan.getRetentionTime()+"");
       ms2scan.addAnalysisItem("PrecursorFile", "");
       
       List <ScanCharge> charges = getChargeStates(scan);
       for (ScanCharge ch: charges) 
           ms2scan.addChargeState(ch);
       
       Peaks peaks = scan.getPeaks();
       Iterator <Peak> pIt = peaks.getIterator();
       while(pIt.hasNext()) {
           Peak p = pIt.next();
           ms2scan.addPeak(p.getMz(), p.getIntensity());
       }
       
       outFile.write(ms2scan.toString());
    }

    private List<ScanCharge> getChargeStates(MsScan scan) {
        MsScanChargeDAO scDao = DAOFactory.instance().getMsScanChargeDAO();
        List <MsScanCharge> chargeStates = scDao.loadChargesForScan(scan.getId());
        List <ScanCharge> cStates = new ArrayList<ScanCharge>(chargeStates.size());
        for (MsScanCharge mscharge: chargeStates) {
            ScanCharge sc = new ScanCharge();
            sc.setCharge(mscharge.getCharge());
            sc.setMass(mscharge.getMass());
            cStates.add(sc);
        }
        return cStates;
    }

    private void printMs2Header(MsRun run) throws IOException {
        Header ms2Header = new Header();
        ms2Header.addHeaderItem(Header.CREATION_DATE, run.getCreationDate());
        ms2Header.addHeaderItem(Header.EXTRACTOR, run.getConversionSW());
        ms2Header.addHeaderItem(Header.EXTRACTOR_VERSION, run.getConversionSWVersion());
        ms2Header.addHeaderItem(Header.EXTRACTOR_OPTIONS, run.getConversionSWOptions());
        ms2Header.addHeaderItem("Comments", run.getComment());
        ms2Header.addHeaderItem(Header.INSTRUMENT_TYPE, run.getInstrumentModel());
        ms2Header.addHeaderItem("AcquisitionMethod", "");
        ms2Header.addHeaderItem("ScanType", "");
        ms2Header.addHeaderItem("DataType", "");
        ms2Header.addHeaderItem("IsolationWindow", "");
        ms2Header.addHeaderItem("FirstScan", "");
        ms2Header.addHeaderItem("LastScan", "");
        outFile.write(ms2Header.toString());
        
    }
    
    
    public static void main (String[] args) {
        DbToMs2FileConverter converter = new DbToMs2FileConverter();
        converter.convertToMs2(1);
    }
}
