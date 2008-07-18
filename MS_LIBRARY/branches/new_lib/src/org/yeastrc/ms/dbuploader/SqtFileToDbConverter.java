package org.yeastrc.ms.dbuploader;

import java.io.File;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.ScanResult;

public class SqtFileToDbConverter {


    public void convertSQTFile(String filePath, int runId) throws Exception {

        String justFileName = new File(filePath).getName();
        SQTFileReader reader = new SQTFileReader();
        reader.open(filePath);
        convertSQTFile(justFileName, reader, runId);
    }

    private void convertSQTFile(String file, SQTFileReader reader, int runId) throws Exception {
        SQTHeader header = reader.getHeader();
        if (!header.isValid())
            throw new Exception("Invalid header section for SQT file");
            
        // insert a search into the database and get the search Id
        int searchId = saveSQTSearch(header, runId);

        while (reader.hasScans()) {
            ScanResult scan = reader.getNextScan();
            saveScan(scan, searchId, runId);
        }
    }


    private void saveScan(ScanResult scan, int searchId, int runId) {
        
        // first get the database scan id for the given scan
        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scan.getStartScan(), runId);
        if (scanId == 0)
            throw new IllegalArgumentException("No scanId found for scan number: "+scan.getStartScan()+" and runId: "+runId);
        
        saveSpectrumData(scan, searchId, scanId);
        // save all the results for the scan
        for (SQTSearchResult result: scan.getScanResults()) {
            savePeptideResult(result, searchId, scanId);
        }
    }

    private int savePeptideResult(SQTSearchResult result, int searchId, int scanId) {
        MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> resultDao = DAOFactory.instance().getSqtResultDAO();
        return resultDao.save(result, searchId, scanId);
    }

    private void saveSpectrumData(ScanResult scan, int searchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, searchId, scanId);
    }

    private int saveSQTSearch(SQTHeader header, int runId) {
        // save and return id
        MsSearchDAO<SQTSearch, SQTSearchDb> searchDao = DAOFactory.instance().getSqtSearchDAO();
        return searchDao.saveSearch(header, runId);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SqtFileToDbConverter uploader = new SqtFileToDbConverter();
        String file = "./resources/PARC_p75_01_itms.sqt";
//      String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
        long start = System.currentTimeMillis();
        try {
//            uploader.convertSQTFile(file, 565);
            MsSearchDAO<SQTSearch, SQTSearchDb> searchDao = DAOFactory.instance().getSqtSearchDAO();
            searchDao.deleteSearch(152);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long timeElapsed = (end - start)/1000;
        System.out.println("Seconds to upload: "+timeElapsed);

    }

}
