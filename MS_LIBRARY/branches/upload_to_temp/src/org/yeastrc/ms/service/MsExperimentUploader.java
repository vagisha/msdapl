/**
 * MsExperimentUploader.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.UploadDAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.domain.general.impl.ExperimentBean;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    private String remoteServer;
    private String remoteDirectory;
    private String uploadDirectory;
    private String comments;
    
    private SpectrumDataUploadService rdus;
    private SearchDataUploadService sdus;
    private AnalysisDataUploadService adus;
    
    private boolean do_rdupload = true;
    private boolean do_sdupload = false;
    private boolean do_adupload = false;
    
    
    private StringBuilder preUploadCheckMsg;
    
    private int searchId = 0; // uploaded searchId
    private int experimentId = 0; // uploaded experimentId
    
    public MsExperimentUploader () {
        this.preUploadCheckMsg = new StringBuilder();
    }
    
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setSpectrumDataUploader(SpectrumDataUploadService rdus) {
        this.rdus = rdus;
    }

    public void setSearchDataUploader(SearchDataUploadService sdus) {
        this.sdus = sdus;
        this.do_sdupload = true;
    }

    public void setAnalysisDataUploader(AnalysisDataUploadService adus) {
        this.adus = adus;
        this.do_adupload = true;
    }

    public void setDirectory(String directory) {
        this.uploadDirectory = directory;
    }
    
    public boolean preUploadCheckPassed() {
        boolean passed = true;
        
        log.info("Doing pre-upload check for spectrum data uploader....");
        // Spectrum data uploader check
        if(rdus == null) {
            appendToMsg("SpectrumDataUploader was null");
            passed = false;
        }
        else if(!rdus.preUploadCheckPassed())  {
            appendToMsg(rdus.getPreUploadCheckMsg());
            passed = false;
        }
        if(!passed) log.info("...FAILED");
        else        log.info("...PASSED");
        
        
        
        // Search data uploader check
        if(do_sdupload) {
            log.info("Doing pre-upload check for search data uploader....");
            if(sdus == null) {
                appendToMsg("SearchDataUploader was null");
                passed = false;
            }
            else {
                if(!sdus.preUploadCheckPassed()) {
                    appendToMsg(sdus.getPreUploadCheckMsg());
                    passed = false;
                }
            }
            if(!passed) log.info("...FAILED");
            else        log.info("...PASSED");
        }
        
        // Analysis data uploader check
        if(do_adupload) {
            log.info("Doing pre-upload check for analysis data uploader....");
            if(!do_sdupload) {
                appendToMsg("No search results uploader found. Cannot upload analysis results without uploading search results first.");
                passed = false;
            }
            if(adus == null) {
                appendToMsg("AnalysisDataUploader was null");
                passed = false;
            }
            else {
                if(!adus.preUploadCheckPassed()) {
                    appendToMsg(adus.getPreUploadCheckMsg());
                    passed = false;
                }
            }
            if(!passed) log.info("...FAILED");
            else        log.info("...PASSED");
        }
        return passed;
    }
    
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }
    
    
    public String getUploadSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n\tExperiment ID: "+experimentId+"\n");
        summary.append("\tRemote server: "+remoteServer+"\n");
        summary.append("\tRemote directory: "+remoteDirectory+"\n");
        summary.append(rdus.getUploadSummary()+"\n");
        if(do_sdupload)
            summary.append(sdus.getUploadSummary()+"\n");
        if(do_adupload)
            summary.append(adus.getUploadSummary()+"\n");
        summary.append("\n");
        return summary.toString();
    }
    
    private void appendToMsg(String msg) {
        preUploadCheckMsg.append(msg+"\n");
    }
    
    public int uploadSpectrumData() throws UploadException {
        
        // first create an entry in the msExperiment table
        experimentId = saveExperiment();
        log.info("\n\nAdded entry for experiment ID: "+experimentId+"\n\n");
        
        uploadSpectrumData(experimentId);
        return experimentId;
    }
    
    public void uploadSpectrumData(int experimentId) throws UploadException {
        
        this.experimentId = experimentId;
        
        // upload raw data
        rdus.setExperimentId(experimentId);
        try {
            rdus.upload();
        }
        catch(UploadException ex) {
            deleteExperiment(experimentId);
            throw ex;
        }
    }
    
    public int uploadSearchData(int experimentId) throws UploadException {
        
        // if we have search data upload that next
        int searchId;
        if(do_sdupload) {
            sdus.setExperimentId(experimentId);
            sdus.setSpectrumFileNames(rdus.getFileNames());
            searchId = sdus.upload();
        }
        else {
            log.error("No SearchDataUploadService found");
            return 0;
        }
        return searchId;
        
    }
    
    public int uploadAnalysisData(int searchId) throws UploadException {
        
        int analysisId;
        // if we have post-search analysis data upload that next
        if(do_adupload) {
            adus.setSearchId(searchId);
            analysisId = adus.upload();
        }
        else {
            log.error("No AnalysisDataUploadService found");
            return 0;
        }
        return analysisId;
    }
    
    private int saveExperiment() throws UploadException {
        MsExperimentDAO experimentDao = UploadDAOFactory.getInstance().getMsExperimentDAO();
        ExperimentBean experiment = new ExperimentBean();
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        experiment.setComments(comments);
        experiment.setUploadDate(new java.sql.Date(new Date().getTime()));
        try { return experimentDao.saveExperiment(experiment);}
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.CREATE_EXPT_ERROR);
            ex.appendErrorMessage("!!!\n\tERROR CREATING EXPERIMENT. EXPERIMENT WILL NOT BE UPLOADED\n!!!");
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private void deleteExperiment(int experimentId) {
        log.error("\n\tDELETING EXPERIMENT: "+experimentId);
        MsExperimentDAO exptDao = UploadDAOFactory.getInstance().getMsExperimentDAO();
        exptDao.deleteExperiment(experimentId);
    }
    
    private void logEndExperimentUpload(long start, long end) {
        log.info("END EXPERIMENT UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+"\n"+
                getUploadSummary());
    }

    private void logBeginExperimentUpload() {
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+uploadDirectory+
                "\n\tTime: "+(new Date().toString())+
                "\n\tSPECTRUM DATA UPLOAD: "+do_rdupload+
                "\n\tSEARCH DATA UPLOAD: "+do_sdupload+
                "\n\tANALYSIS DATA UPLOAD: "+do_adupload);
    }
}
