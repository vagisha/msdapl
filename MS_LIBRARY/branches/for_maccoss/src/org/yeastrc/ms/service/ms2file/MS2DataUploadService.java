/**
 * MsRunUploadService.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.ms2file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeDependentAnalysisWrap;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeIndependentAnalysisWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MS2RunDataProvider;
import org.yeastrc.ms.parser.ms2File.Cms2FileReader;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MS2DataUploadService {

    private static final Logger log = Logger.getLogger(MS2DataUploadService.class);

    private static final DAOFactory daoFactory = DAOFactory.instance();

    public static final int BUF_SIZE = 1000;
    
    // these are the things we will cache and do bulk-inserts
    private List<MS2ChargeDependentAnalysisWId> dAnalysisList;
    private List<MS2ChargeIndependentAnalysisWId> iAnalysisList;
    
    private int lastUploadedRunId = 0;
    private List<UploadException> uploadExceptionList;
    
    private int numRunsToUpload = 0;
    private int numRunsUploaded = 0;
    
    public MS2DataUploadService() {
        dAnalysisList = new ArrayList<MS2ChargeDependentAnalysisWId>();
        iAnalysisList = new ArrayList<MS2ChargeIndependentAnalysisWId>();
        
        uploadExceptionList = new ArrayList<UploadException>();
    }

    private void reset() {
        // clean up any cached data
        resetCaches();
        
        uploadExceptionList.clear();
        
        numRunsToUpload = 0;
        numRunsUploaded = 0;
    }

    private void resetCaches() {
        dAnalysisList.clear();
        iAnalysisList.clear();
        
        lastUploadedRunId = 0;
    }
    
    private void deleteLastUploadedRun() {
        if (lastUploadedRunId != 0)
            deleteRun(lastUploadedRunId);
    }
    
    private static void deleteRun(Integer runId) {
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        runDao.delete(runId);
    }
    
    public List<UploadException> getUploadExceptionList() {
        return uploadExceptionList;
    }
    
    public int getNumRunsToUpload() {
        return numRunsToUpload;
    }
    
    public int getNumRunsUploaded() {
        return numRunsUploaded;
    }
    
    /**
     * Uploaded the ms2 files in the directory to the database.  Returns a mapping of uploaded filenames to database runIds. 
     * @param fileDirectory
     * @param filenames
     * @param serverDirectory
     * @return
     * @throws UploadException
     */
    public Map<String, Integer> uploadRuns(int experimentId, String fileDirectory, Set<String> filenames, RunFileFormat format,
                    String serverDirectory) throws UploadException {
        
        reset(); // reset all caches etc. 
        
        this.numRunsToUpload = filenames.size();
        
        Map<String, Integer> runIdMap = new HashMap<String, Integer>(filenames.size());
        for (String filename: filenames) {
            int runId = 0;
            try {
                String filepath = fileDirectory+File.separator+filename+"."+format.name().toLowerCase();
                if(!(new File(filepath).exists()))
                    filepath = fileDirectory+File.separator+filename+"."+format.name().toUpperCase();
                
                runId = uploadMS2Run(experimentId, filepath, format, serverDirectory);
                // link experiment and run
                linkExperimentAndRun(experimentId, runId);
                numRunsUploaded++;
            }
            catch (UploadException e) {
                deleteLastUploadedRun();
                throw e;
            }
            runIdMap.put(filename, runId);
        }
        return runIdMap;
    }
    
    private int uploadMS2Run(int experimentId, String filePath, RunFileFormat format, String serverDirectory) throws UploadException {
        
        // first check if the file in already in the database. If it is, return its database id
        // If a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload it
        String sha1Sum = calculateSha1Sum(filePath);
        String fileName = new File(filePath).getName();
        int runId = getMatchingRunId(fileName, sha1Sum);
        if (runId > 0) {
            // If this run was uploaded from a different location, upload the location
            saveRunLocation(serverDirectory, runId);
            log.info("Run with name: "+fileName+" and sha1Sum: "+sha1Sum+
                    " found in the database; runID: "+runId);
            log.info("END MS2 FILE UPLOAD: "+fileName+"\n");
            return runId;
        }
        
        // this is a new file so we will upload it.
        MS2RunDataProvider ms2Provider = getMS2DataProvider(format);
        try {
            ms2Provider.open(filePath, sha1Sum);
            runId = uploadMS2Run(ms2Provider, serverDirectory);
            return runId;
        }
        catch (DataProviderException e) {
            UploadException ex = logAndAddUploadException(ERROR_CODE.READ_ERROR_MS2, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = logAndAddUploadException(ERROR_CODE.RUNTIME_MS2_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch(UploadException e) {
            e.setFile(filePath);
            throw e;
        }
        finally {
            ms2Provider.close();
        }
    }

    private MS2RunDataProvider getMS2DataProvider(RunFileFormat format) {
        if(format == RunFileFormat.MS2) {
            return new Ms2FileReader();
        }
        else if(format == RunFileFormat.CMS2) {
            return new Cms2FileReader();
        }
        return null;
    }
    
    private void saveRunLocation(String serverDirectory, int runId) {
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        // Save the original location (on remote server) of the MS2 file, if the location is not in the database already.
        int runLocs = runDao.loadMatchingRunLocations(runId, serverDirectory);
        if (runLocs == 0) {
            runDao.saveRunLocation(serverDirectory, runId);
        }
    }
    
    private void linkExperimentAndRun(int experimentId, int runId) {
        MsExperimentDAO exptDao = daoFactory.getMsExperimentDAO();
        // an entry will be made in the msExperimentRun table only if 
        // it does not already exists. 
        exptDao.saveExperimentRun(experimentId, runId);
    }
    
    private String calculateSha1Sum(String filePath) throws UploadException {
        String sha1Sum;
        try {
            sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        }
        catch (Exception e) {
            UploadException ex = logAndAddUploadException(ERROR_CODE.SHA1SUM_CALC_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        return sha1Sum;
    }
    
    int getMatchingRunId(String fileName, String sha1Sum) {

        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        List <Integer> runIds = runDao.loadRunIdsForFileNameAndSha1Sum(fileName, sha1Sum);

        // return the database of the first matching run found
        if (runIds.size() > 0)
            return runIds.get(0);
        return 0;
    }
    
    /**
     * provider should be closed after this method returns
     * @param provider
     * @param experimentId
     * @param sha1Sum
     * @return
     * @throws UploadException 
     */
    private int uploadMS2Run(MS2RunDataProvider provider, final String serverDirectory) throws UploadException  {

        log.info("BEGIN MS2 FILE UPLOAD: "+provider.getFileName());
        long startTime = System.currentTimeMillis();
        
        // reset all caches.
        resetCaches();
        
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();


        // Get the top-level run information and upload it
        MS2RunIn header;
        try {
            header = provider.getRunHeader();
        }
        catch (DataProviderException e) { // this should only happen if there was an IOException while reading the file
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_MS2);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        int runId = runDao.saveRun(header, serverDirectory);
        lastUploadedRunId = runId;
        log.info("Uploaded top-level run information with runId: "+runId);

        // upload each of the scans
        MsScanDAO scanDao = daoFactory.getMsScanDAO();
        int all = 0;
        int uploaded = 0;
        while(true) {
            MS2ScanIn scan;
            try {
                scan = provider.getNextScan();
                if(scan == null)
                    break;
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
            log.error("END MS2 FILE UPLOAD: !!!No scans were uploaded for file: "+provider.getFileName()+"("+runId+")"+"\n");
            UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
            ex.setErrorMessage("No scans were uploaded for runID: "+runId);
        }
        
        flush(); // save any cached data
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded "+uploaded+" out of "+all+" scans for runId: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END MS2 FILE UPLOAD: "+provider.getFileName()+"\n");
        return runId;
    }

    private void saveChargeDependentAnalysis(MS2ScanCharge scanCharge, final int scanChargeId) {
        if (dAnalysisList.size() > BUF_SIZE) {
            saveChargeDependentAnalysis();
        }
        
        for (final MS2NameValuePair dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisList.add(new MS2ChargeDependentAnalysisWrap(dAnalysis, scanChargeId));
        }
    }

    private void saveChargeDependentAnalysis() {
        MS2ChargeDependentAnalysisDAO dao = daoFactory.getMs2FileChargeDAnalysisDAO();
        dao.saveAll(dAnalysisList);
        dAnalysisList.clear();
    }

    private void saveChargeIndependentAnalysis(MS2ScanIn scan, final int scanId) {
        if (iAnalysisList.size() > BUF_SIZE) {
            saveChargeIndependentAnalysis();
        }
        
        for (final MS2NameValuePair iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalysisList.add(new MS2ChargeIndependentAnalysisWrap(iAnalysis, scanId));
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
    
    private UploadException logAndAddUploadException(ERROR_CODE errCode, Exception sourceException, String file, String directory, String message) {
        UploadException ex = null;
        if (sourceException == null)
            ex = new UploadException(errCode);
        else
            ex = new UploadException(errCode, sourceException);
        ex.setFile(file);
        ex.setDirectory(directory);
        ex.setErrorMessage(message);
        uploadExceptionList.add(ex);
        log.error(ex.getMessage(), ex);
        return ex;
    }
}
