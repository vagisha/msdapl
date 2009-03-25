/**
 * MsExperimentUploader.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 */
public class MsExperimentUploader implements UploadService {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    private String remoteServer;
    private String remoteDirectory;
    private String uploadDirectory;
    
    private int experimentId;

    private RawDataUploadService rdus;
    private SearchDataUploadService sdus;
    private AnalysisDataUploadService adus;
    
    private boolean do_rdupload = true;
    private boolean do_sdupload = false;
    private boolean do_adupload = false;
    
    
    private StringBuilder preUploadCheckMsg;
    
    public MsExperimentUploader () {
        this.preUploadCheckMsg = new StringBuilder();
    }
    
    public int getExperimentId() {
        return experimentId;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public void setRawDataUploader(RawDataUploadService rdus) {
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

    @Override
    public void setDirectory(String directory) {
        this.uploadDirectory = directory;
    }
    
    @Override
    public boolean preUploadCheckPassed() {
        boolean passed = true;
        
        // Raw data uploader check
        if(rdus == null) {
            appendToMsg("RawDataUploader was null");
            passed = false;
        }
        
        // Search data uploader check
        if(do_sdupload) {
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
        }
        
        // Analysis data uploader check
        if(do_adupload) {
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
        }
        return passed;
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }
    
    
    public String getUploadSummary() {
        
    }
    
    private void appendToMsg(String msg) {
        preUploadCheckMsg.append(msg+"\n");
    }
    
    @Override
    public int upload() throws UploadException {
        return 0;
    }
    
    private void logEndExperimentUpload(long start, long end) {
        log.info("END EXPERIMENT UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+
                "\n\tExperiment ID: "+uploadedExptId+
                "\n\t#Runs in Directory: "+numRunsToUpload+"; #Uploaded: "+numRunsUploaded+
                "\n\tSEARCH ID: "+this.uploadedSearchId+
                "\n\t#Searches in Directory: "+numSearchesToUpload+"; #Uploaded: "+numSearchesUploaded+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tUpload Directory: "+uploadDirectory+
                "\n\n");
    }

    private void logBeginExperimentUpload() {
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+uploadDirectory+
                "\n\tTime: "+(new Date().toString())+
                "\n\tRAW DATA UPLOAD: "+do_rdupload+
                "\n\tSEARCH DATA UPLOAD: "+do_sdupload+
                "\n\tANALYSIS DATA UPLOAD: "+do_adupload);
                
    }
}
