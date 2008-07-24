package org.yeastrc.ms.service;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.domain.impl.MsExperimentDbImpl;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);


    private int experimentId = 0;
    private List<Integer> searchIdList = new ArrayList<Integer>();
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return true if experiment was uploaded to the database successfully, false otherwise
     */
    public int uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) {

        log.info("BEGIN EXPERIMENT UPLOAD: directory: "+fileDirectory);
        long start = System.currentTimeMillis();
        
        // get the file names
        Set<String> filenames = getFileNamePrefixes(fileDirectory);
        
        // If we didn't find anything print warning and return.
        if (filenames.size() == 0) {
            log.error("ERROR UPLOADING EXPERIMENT -- No files found to upload in directory: "+fileDirectory);
            return 0;
        }
        // make sure .ms2 files are present
        if (!requiredFilesExist(fileDirectory, filenames)) {
            log.error("ERROR UPLOADING EXPERIMENT -- Missing required ms2 files in directory: "+fileDirectory);
            return 0;
        }
        
        try {
            experimentId =  uploadExperiment(remoteServer, remoteDirectory, fileDirectory);
            uploadRunAndSearchFilesToDb(experimentId, fileDirectory, filenames);
        }
        catch(Exception e) {
            log.error("ERROR UPLOADING EXPERIMENT "+experimentId+" (runs and/or search results). ABORTING...", e);
            deleteSearch(searchIdList);
            deleteExperiment(experimentId);
            return 0;
        }
        long end = System.currentTimeMillis();
        log.info("---------------------- EXPERIMENT UPLOADED IN: "+((end - start)/(1000L))+"seconds ----------------------");
        return experimentId;
    }

    private int uploadExperiment(String remoteServer, String remoteDirectory, String fileDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperimentDbImpl experiment = new MsExperimentDbImpl();
        experiment.setDate(new Date(new java.util.Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }

    private void uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory, Set<String> filenames) throws Exception {
        
        int runExpId = -1;
        for (String filename: filenames) {

            // upload the run first
            int runId = uploadMS2Run(fileDirectory+File.separator+filename+".ms2",experimentId);
            // get the experimentId for this run
            int tempId = MS2DataUploadService.getExperimentIdForRun(runId);
            if (runExpId == -1) runExpId = tempId; // in the first iteration
            if (runExpId != tempId) {
                throw new Exception("Runs in an experiment upload cannot have different experimentIds!");
            }

            // if the sqt file does not exist go on to the next run
            String sqtFile = fileDirectory+File.separator+filename+".sqt";
            if (!(new File(sqtFile).exists()))
                continue;
            
            // We are only uploading SEQUEST .sqt files. We don't know how to upload other formats: 
            // e.g. Percolator, ProLuCID, PepProbe etc.
            if (!SQTFileReader.isSequestSQT(sqtFile)) {
                log.error("Non-SEQUEST sqt file not supported: "+sqtFile);
                throw new Exception("Non-SEQUEST sqt file not supported: "+sqtFile);
            }
            
            // now upload the search result 
            uploadSQTSearch(fileDirectory+File.separator+filename+".sqt", runId);
        }
        
        // if the runs in this experiment upload were already uploaded as part of another
        // experiment upload, delete the entry we created earlier in the msExperiment table
        if (runExpId != experimentId) {
            deleteExperiment(experimentId);
        }
    }

    private int uploadMS2Run(String filePath, int experimentId) throws Exception {
        Ms2FileReader ms2Provider = new Ms2FileReader();
        MS2DataUploadService uploadService = new MS2DataUploadService();
        ms2Provider.open(filePath);
        return uploadService.uploadMS2Run(ms2Provider,experimentId);
    }
    
    private int uploadSQTSearch(String filePath, int runId) throws Exception {
        SQTFileReader sqtProvider = new SQTFileReader();
        SQTDataUploadService uploadService = new SQTDataUploadService();
        sqtProvider.open(filePath);
        int searchId = 0;
        try { 
            searchId = uploadService.uploadSQTSearch(sqtProvider, runId);
        }
        catch(Exception e){
            searchId = uploadService.getUploadedSearchId(); 
            throw e; // throw the exception again
        }
        finally {
            if (searchId != 0)
                searchIdList.add(searchId);
        }
        return searchId;
    }
    
    /**
     * Check for .ms2 files. 
     * @param fileDirectory
     * @param filenames
     * @return
     */
    private boolean requiredFilesExist(String fileDirectory, Set<String> filenames) {
        for (String filePrefix: filenames) {
            if (!(new File(fileDirectory+File.separator+filePrefix+".ms2").exists())) {
                log.error("Required file: "+filePrefix+".ms2 not found");
                return false;
            }
        }
        return true;
    }

    private Set<String> getFileNamePrefixes(String fileDirectory) {
        File directory = new File (fileDirectory);
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist: "+fileDirectory);
        }
        return getFileNamesInDirectory(directory);
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
    
    private void deleteExperiment(int experimentId) {
        MS2DataUploadService.deleteExperiment(experimentId);
        log.error("DELETED RUNS, SEARCHES and EXPERIMENT for experimentID: "+experimentId);
    }
    
    private void deleteSearch(List<Integer> searchIdList) {
        for (Integer searchId: searchIdList) {
            SQTDataUploadService.deleteSearch(searchId);
            log.error("DELETED searchID: "+searchId);
        }
        
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        MsExperimentUploader uploader = new MsExperimentUploader();
        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/1542/");
//      uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/MacCossData/sequest");
//        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/new_lib/resources/PARC/TEST");
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
