package org.yeastrc.ms.service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return true if experiment was uploaded to the database successfully, false otherwise
     */
    public void uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) {
        
        log.info("BEGIN EXPERIMENT UPLOAD: directory: "+fileDirectory);
        long start = System.currentTimeMillis();
        int experimentId = 0;
        try {
           experimentId =  MsDataUploadService.uploadExperiment(remoteServer, remoteDirectory, fileDirectory);
           uploadRunAndSearchFilesToDb(experimentId, fileDirectory);
        }
        catch(Exception e) {
            triggerExperimentDelete(experimentId, e);
            return;
        }
        long end = System.currentTimeMillis();
        log.info("---------------------- EXPERIMENT UPLOADED IN: "+((end - start)/(1000L))+"seconds ----------------------");
    }

    private void triggerExperimentDelete(int experimentId, Throwable t) {
        log.error("", t);
        log.error("ERROR UPLOADING EXPERIMENT "+experimentId+" (runs and/or search results). ABORTING...");
        MsDataUploadService.deleteExperiment(experimentId);
        log.error("DELETED EXPERIMENT "+experimentId);
        
    }
    
    private void uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory) throws Exception {
        
        File directory = new File (fileDirectory);
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist: "+fileDirectory);
        }
        
        Set<String> filenames = getFileNamesInDirectory(directory);
        
        // If we didn't find anything throw an exception so that the experiment is deleted.
        if (filenames.size() == 0) {
            throw new RuntimeException("No files found to upload in directory: "+fileDirectory);
        }
        
        Ms2FileReader ms2Provider = null;
        SQTFileReader sqtProvider = null;
        
        for (String filename: filenames) {
            // upload the run first
            ms2Provider = new Ms2FileReader();
            ms2Provider.open(fileDirectory+File.separator+filename+".ms2");
            int runId = MsDataUploadService.uploadMS2Run(ms2Provider,experimentId);
            
            // now upload the search result
            sqtProvider = new SQTFileReader();
            sqtProvider.open(fileDirectory+File.separator+filename+".sqt");
            MsDataUploadService.uploadSQTSearch(sqtProvider, runId);
        }
    }

    private Set<String> getFileNamesInDirectory(File directory) {
        Set<String> filenames = new HashSet<String>();
        File[] files = directory.listFiles();
        String name = null;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (fileName.endsWith(".ms2") || fileName.endsWith(".sqt")) {
                name = files[i].getName();
                name = name.substring(0, name.indexOf('.'));
                filenames.add(name);
            }
        }
        return filenames;
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        MsExperimentUploader uploader = new MsExperimentUploader();
        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "./resources/PARC/");
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
