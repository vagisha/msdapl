package org.yeastrc.ms.service.ms2file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2HeaderDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanChargeDb;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanDb;
import org.yeastrc.ms.parser.ms2File.MS2Header;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.parser.ms2File.ScanCharge;
import org.yeastrc.ms.util.PeakConverterString;

public class DbToMs2FileConverter {

    private BufferedWriter outFile = null;
    
    public void convertToMs2(int dbRunId, String outputFile) throws IOException {
        
        try {
            outFile = new BufferedWriter(new FileWriter(outputFile));

            MsRunDAO<MS2Run, MS2RunDb> runDao = DAOFactory.instance().getMS2FileRunDAO();
            MS2RunDb run = runDao.loadRun(dbRunId);
            if (run == null) {
                System.err.println("No run found with id: "+dbRunId);
                return;
            }
            printMs2Header(run);
            outFile.write("\n");

            MsScanDAO<MS2Scan, MS2ScanDb> scanDao = DAOFactory.instance().getMS2FileScanDAO();
            List<Integer> scanIds = scanDao.loadScanIdsForRun(dbRunId);

            for (Integer scanId: scanIds) {
                MS2ScanDb scan = scanDao.load(scanId);
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
    
    private void printMs2Scan(MS2ScanDb scan) throws IOException {
       Scan ms2scan = new Scan();
       ms2scan.setStartScan(scan.getStartScanNum());
       ms2scan.setEndScan(scan.getEndScanNum());
       ms2scan.setPrecursorMz(scan.getPrecursorMz().toString());
       
       // add predicted charge states for the scan
       for (MS2ScanChargeDb scanCharge: scan.getScanChargeList()) {
           ScanCharge sc = new ScanCharge();
           sc.setCharge(scanCharge.getCharge());
           sc.setMass(scanCharge.getMass());
           for (MS2Field dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
               sc.addAnalysisItem(dAnalysis.getName(), dAnalysis.getValue());
           }
           ms2scan.addChargeState(sc);
       }
       
       // add charge independent analysis
       for (MS2Field item: scan.getChargeIndependentAnalysisList()) {
            ms2scan.addAnalysisItem(item.getName(), item.getValue());
       }
       
       // finally, the peak data!
       PeakConverterString converter = new PeakConverterString();
       List<String[]> peaks = converter.convert(scan.peakDataString());
       for(String[] peak: peaks) {
           ms2scan.addPeak(String.valueOf(peak[0]), String.valueOf(peak[1]));
       }
       
       outFile.write(ms2scan.toString());
    }

    private void printMs2Header(MS2RunDb run) throws IOException {
        MS2Header ms2Header = new MS2Header();
        List<MS2HeaderDb> headerList = run.getHeaderList();
        Collections.sort(headerList, new Comparator<MS2HeaderDb>() {
            public int compare(MS2HeaderDb o1, MS2HeaderDb o2) {
                return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }});
        
        for (MS2HeaderDb header: headerList) {
            ms2Header.addHeaderItem(header.getName(), header.getValue());
        }
        outFile.write(ms2Header.toString());
    }
    
    public static void main (String[] args) {
        DbToMs2FileConverter converter = new DbToMs2FileConverter();
        try {
            long start = System.currentTimeMillis();
            converter.convertToMs2(565, "db2ms2_string.ms2");
            long end = System.currentTimeMillis();
            long timeElapsed = (end - start)/1000;
            System.out.println("Seconds to convert to MS2: "+timeElapsed);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
