/**
 * MS2DataUploadService2.java
 * @author Vagisha Sharma
 * Jun 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.ms2file;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MS2RunDataProvider;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2RunUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAO;

/**
 * 
 */
public class MS2DataUploadService2 extends MS2DataUploadService {

    private static final Logger log = Logger.getLogger(MS2DataUploadService2.class);

    private static final UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();

    public static final int SCAN_BUF_SIZE = 100;
    
 
    
    public MS2DataUploadService2() {}


    /**
     * provider should be closed after this method returns
     * @param provider
     * @param experimentId
     * @param sha1Sum
     * @return
     * @throws UploadException 
     */
    protected int uploadMS2Run(MS2RunDataProvider provider, final String serverDirectory) throws UploadException  {

        log.info("BEGIN MS2 FILE UPLOAD: "+provider.getFileName());
        long startTime = System.currentTimeMillis();
        
        // reset all caches.
        resetCaches();
        
        MS2RunUploadDAO runDao = daoFactory.getMS2FileRunDAO();


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
        int uploaded = 0;
        
        // maintain a list of scans
        List<MS2ScanIn> scans = new ArrayList<MS2ScanIn>(SCAN_BUF_SIZE);
        
        while(true) {
            MS2ScanIn scan;
            try {
                scan = provider.getNextScan();
                if(scan == null)
                    break;
                scans.add(scan);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            if(scans.size() == SCAN_BUF_SIZE) {
                uploaded += uploadScans(scans, runId);
                scans.clear();
            }
            
            uploaded++;
        }
        
        // upload any remaining scans
        if(scans.size() > 0)
            uploaded += uploadScans(scans, runId);
        scans.clear();
        
        // if no scans were uploaded for this run throw an exception
        if (uploaded == 0) {
            log.error("END MS2 FILE UPLOAD: !!!No scans were uploaded for file: "+provider.getFileName()+"("+runId+")"+"\n");
            UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
            ex.setErrorMessage("No scans were uploaded for runID: "+runId);
        }
        
        flush(); // save any cached data
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded "+uploaded+" scans for runId: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END MS2 FILE UPLOAD: "+provider.getFileName()+"\n");
        return runId;
    }
    
    

    private int uploadScans(List<MS2ScanIn> scans, int runId) {
        
        MsScanUploadDAO scanDao = daoFactory.getMsScanDAO();
        List<Integer> autoIncrIds = scanDao.save(scans, runId);
        
        for(int i = 0; i < scans.size(); i++) {
            MS2ScanIn scan = scans.get(i);
            int scanId = autoIncrIds.get(i);
            
            saveChargeIndependentAnalysis(scan, scanId);
            
            // save the scan charge states for this scan
            MS2ScanChargeUploadDAO chargeDao = daoFactory.getMS2FileScanChargeDAO();
            for (MS2ScanCharge scanCharge: scan.getScanChargeList()) {
                int scanChargeId = chargeDao.saveScanChargeOnly(scanCharge, scanId);
                saveChargeDependentAnalysis(scanCharge, scanChargeId);
            }
        }
        return scans.size();
    }
    
}
