/**
 * MsRunUploadService.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.ms2file;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.run.MsRunLocationDb;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MS2RunDataProvider;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class MS2DataUploadService {

    private static final Logger log = Logger.getLogger(MS2DataUploadService.class);

    private static final DAOFactory daoFactory = DAOFactory.instance();

    public static final int BUF_SIZE = 1000;
    
    private List<MS2ChargeDependentAnalysisDb> dAnalysisList;
    private List<MS2ChargeIndependentAnalysisDb> iAnalysisList;
    
    public MS2DataUploadService() {
        dAnalysisList = new ArrayList<MS2ChargeDependentAnalysisDb>();
        iAnalysisList = new ArrayList<MS2ChargeIndependentAnalysisDb>();
    }

    private void reset() {
        // clean up any cached data
        dAnalysisList.clear();
        iAnalysisList.clear();
    }
    
    /**
     * provider should be closed after this method returns
     * @param provider
     * @param experimentId
     * @param sha1Sum
     * @return
     * @throws UploadException 
     */
    public int uploadMS2Run(MS2RunDataProvider provider, String sha1Sum, 
            final String serverAddress, final String serverDirectory) throws UploadException  {

        log.info("BEGIN MS2 FILE UPLOAD: "+provider.getFileName());
        long startTime = System.currentTimeMillis();
        
        // reset all caches etc.
        reset();
        
        MsRunDAO<MS2Run, MS2RunDb> runDao = daoFactory.getMS2FileRunDAO();

        // determine if this run is already in the database
        // if a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload it
        int runId = getMatchingRunId(provider.getFileName(), sha1Sum);

        // if run is already in the database return the runId of the existing run
        if (runId > 0)  {
            // first save the original location (on remote server) of the MS2 file if the location is new.
            List<MsRunLocationDb> runLocs = runDao.loadMatchingRunLocations(runId, serverAddress, serverDirectory);
            if (runLocs.size() == 0) {
                runDao.saveRunLocation(serverAddress, serverDirectory, runId);
            }
            log.info("Run with name: "+provider.getFileName()+" and sha1Sum: "+sha1Sum+
                    " found in the database; runID: "+runId);
            log.info("END MS2 FILE UPLOAD: "+provider.getFileName());
            return runId;
        }

        // if run is NOT in the database get the top-level run information and upload it
        MS2Run header;
        try {
            header = provider.getRunHeader();
        }
        catch (DataProviderException e) { // this should only happen if there was an IOException while reading the file
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_MS2);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        runId = runDao.saveRun(header, serverAddress, serverDirectory);
        log.info("Uploaded top-level run information with runId: "+runId);

        // upload each of the scans
        MsScanDAO<MsScan, MsScanDb> scanDao = daoFactory.getMsScanDAO();
        int all = 0;
        int uploaded = 0;
        while(provider.hasNextScan()) {
            MS2Scan scan;
            try {
                scan = provider.getNextScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            // MS2 file scans may have a precursor scan number but the precursor scans are not in the database
            // so we do not have a database id for the precursor scan. We still do the check, though
            int precursorScanId = scanDao.loadScanIdForScanNumRun(scan.getPrecursorScanNum(), runId);
            int scanId = scanDao.save(scan, runId, precursorScanId); 

            // save charge independent analysis
            saveChargeIndependentAnalysis(scan, scanId);

            // save the scan charge states for this scan
            MS2ScanChargeDAO chargeDao = daoFactory.getMS2FileScanChargeDAO();
            for (MS2ScanCharge scanCharge: scan.getScanChargeList()) {
                int scanChargeId = chargeDao.saveScanChargeOnly(scanCharge, scanId);
                saveChargeDependentAnalysis(scanCharge, scanChargeId);
            }

            uploaded++;
            all++;
        }
        
        
        // if no scans were uploaded for this run throw an exception
        if (uploaded == 0) {
            log.error("END MS2 FILE UPLOAD: !!!No scans were uploaded for file: "+provider.getFileName()+"("+runId+")");
            UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
            ex.setErrorMessage("No scans were uploaded for runID: "+runId);
        }
        
        flush(); // save any cached data
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded "+uploaded+" out of "+all+" scans for runId: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END MS2 FILE UPLOAD: "+provider.getFileName());
        return runId;
    }

    private void saveChargeDependentAnalysis(MS2ScanCharge scanCharge, final int scanChargeId) {
        if (dAnalysisList.size() > BUF_SIZE) {
            saveChargeDependentAnalysis();
        }
        
        for (final MS2Field dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisList.add(new MS2ChargeDependentAnalysisDb() {
                @Override
                public int getId() {return 0;}
                @Override
                public int getScanChargeId() {return scanChargeId;}
                @Override
                public String getName() {return dAnalysis.getName();}
                @Override
                public String getValue() {return dAnalysis.getValue();}
            });
        }
    }

    private void saveChargeDependentAnalysis() {
        MS2ChargeDependentAnalysisDAO dao = daoFactory.getMs2FileChargeDAnalysisDAO();
        dao.saveAll(dAnalysisList);
        dAnalysisList.clear();
    }

    private void saveChargeIndependentAnalysis(MS2Scan scan, final int scanId) {
        if (iAnalysisList.size() > BUF_SIZE) {
            saveChargeIndependentAnalysis();
        }
        
        for (final MS2Field iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalysisList.add(new MS2ChargeIndependentAnalysisDb() {
                @Override
                public int getId() {return 0;}
                @Override
                public int getScanId() {return scanId;}
                @Override
                public String getName() {return iAnalysis.getName();}
                @Override
                public String getValue() {return iAnalysis.getValue();}
            });
        }
    }

    private void saveChargeIndependentAnalysis() {
        MS2ChargeIndependentAnalysisDAO dao = daoFactory.getMs2FileChargeIAnalysisDAO();
        dao.saveAll(iAnalysisList);
        iAnalysisList.clear();
    }
    
    private void flush() {
        if (iAnalysisList.size() > 0)
            saveChargeIndependentAnalysis();
        if (dAnalysisList.size() > 0)
            saveChargeDependentAnalysis();
    }
    
    int getMatchingRunId(String fileName, String sha1Sum) {

        MsRunDAO<MS2Run, MS2RunDb> runDao = daoFactory.getMS2FileRunDAO();
        List <Integer> runIds = runDao.loadRunIdsForFileNameAndSha1Sum(fileName, sha1Sum);

        // return the database of the first matching run found
        if (runIds.size() > 0)
            return runIds.get(0);
        return 0;
    }
    
    public static void deleteRun(Integer runId) {
        MsRunDAO<MS2Run, MS2RunDb> runDao = daoFactory.getMS2FileRunDAO();
        runDao.delete(runId);
    }
}
