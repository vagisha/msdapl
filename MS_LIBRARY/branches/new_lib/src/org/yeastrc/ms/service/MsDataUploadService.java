/**
 * MsRunUploadService.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.impl.MsExperimentDbImpl;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;

/**
 * 
 */
public class MsDataUploadService {

    private static final Logger log = Logger.getLogger(MsDataUploadService.class);

    private static final DAOFactory daoFactory = DAOFactory.instance();

    private MsDataUploadService() {}

    public static int uploadExperiment(String remoteServer, String remoteDirectory, String fileDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperimentDbImpl experiment = new MsExperimentDbImpl();
        experiment.setDate(new Date(new java.util.Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }

    public static int uploadMS2Run(MS2RunDataProvider provider, int experimentId) {

        log.info("BEGIN MS2 FILE UPLOAD: "+provider.getFileName()+"; EXPERIMENT_ID: "+experimentId);

        MsRunDAO<MS2Run, MS2RunDb> runDao = daoFactory.getMS2FileRunDAO();

        // determine if this run is already in the database
        // if a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload it
        int runId = getMatchingRunId(provider.getFileName(), provider.getSha1Sum());

        // if run is already in the database return the runId of the existing run
        if (runId > 0)  {
            log.info("Run with name: "+provider.getFileName()+" and sha1Sum: "+provider.getSha1Sum()+
                    " found in the database; runID: "+runId);
            log.info("END MS2 FILE UPLOAD: "+provider.getFileName()+"; EXPERIMENT_ID: "+experimentId);
            return runId;
        }

        // if run is NOT in the database get the top-level run information and upload it
        runId = runDao.saveRun(provider.getRunData(), experimentId);
        log.info("Uploaded top-level run information with runId: "+runId);

        // upload each of the scans
        MsScanDAO<MS2Scan, MS2ScanDb> scanDao = daoFactory.getMS2FileScanDAO();
        Iterator<MS2Scan> scanIterator = provider.scanIterator();
        int i = 0;
        while(scanIterator.hasNext()) {
            MS2Scan scan = scanIterator.next();
            scanDao.save(scan, runId, 0);   // MS2 file scans may have a precursor scan number
            // but the precursor scans are not in the database
            // so we do not have a database id for the precursor scan.
            i++;
        }
        log.info("Uploaded "+i+" scans for runId: "+runId);
        log.info("END MS2 FILE UPLOAD: "+provider.getFileName()+"; EXPERIMENT_ID: "+experimentId);
        return runId;
    }

    static int getMatchingRunId(String fileName, String sha1Sum) {

        MsRunDAO<MS2Run, MS2RunDb> runDao = daoFactory.getMS2FileRunDAO();
        List <Integer> runIds = runDao.runIdsFor(fileName, sha1Sum);

        // return the database of the first matching run found
        if (runIds.size() > 0)
            return runIds.get(0);
        return 0;

    }

    public static void uploadSQTSearch(SQTSearchDataProvider provider, int runId) {

        log.info("BEGIN SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId);
        
        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        DynamicModLookupUtil.instance().reset();

        MsSearchDAO<SQTSearch, SQTSearchDb> searchDao = daoFactory.getSqtSearchDAO();
        int searchId = searchDao.saveSearch(provider.getSearchData(), runId);
        log.info("Uploaded top-level info for search with searchId: "+searchId);

        // upload the search results for each scan + charge combination
        Iterator<SQTSearchScan> searchScanIterator = provider.scanResultIterator();
        int i = 0;
        while (searchScanIterator.hasNext()) {
            i+= saveScan(searchScanIterator.next(), searchId, runId);
        }
        log.info("Uploaded "+i+" results for searchId: "+searchId);
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId);
    }

    private static int saveScan(SQTSearchScan scan, int searchId, int runId) {

        // first get the database scan id for the given scan
        List<? extends SQTSearchResult> resultList = scan.getScanResults();
        if (resultList == null || resultList.size() == 0)
            throw new IllegalArgumentException("No search results found for scan");
        
        int scanNumber = resultList.get(0).getScanNumber();
        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0)
            throw new IllegalArgumentException("No scanId found for scan number: "+scanNumber+" and runId: "+runId);

        saveSpectrumData(scan, searchId, scanId);
        // save all the results for the scan
        for (SQTSearchResult result: resultList) {
            savePeptideResult(result, searchId, scanId);
        }
        
        // upload the protein match data and SQT related search data that was appended to temp files
        
        // delete temp files. 
        
        
        return resultList.size();
    }

    private static int savePeptideResult(SQTSearchResult result, int searchId, int scanId) {
        
        MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> resultDao = DAOFactory.instance().getSqtResultDAO();
        return resultDao.saveResultOnly(result, searchId, scanId);
        
        // save the protein matches (append to existing temp file)
        
        // save the SQT related search result data (append to existing temp file)
        
    }

    private static void saveSpectrumData(SQTSearchScan scan, int searchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, searchId, scanId);
    }

    public static void deleteExperiment(int experimentId) {
        MsExperimentDAO expDao = daoFactory.getMsExperimentDAO();
        log.info("DELETING EXPERIMENT: "+experimentId);
        expDao.delete(experimentId);
    }

    public static void deleteExperiment(int experimentId, Throwable t) {
        MsExperimentDAO expDao = daoFactory.getMsExperimentDAO();
        log.info("DELETING EXPERIMENT: "+experimentId, t);
        expDao.delete(experimentId);
    }
}
