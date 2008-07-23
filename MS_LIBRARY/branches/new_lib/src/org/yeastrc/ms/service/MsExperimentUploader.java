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
        try {
            experimentId =  uploadExperiment(remoteServer, remoteDirectory, fileDirectory);
            uploadRunAndSearchFilesToDb(experimentId, fileDirectory);
        }
        catch(Exception e) {
            log.error("ERROR UPLOADING EXPERIMENT "+experimentId+" (runs and/or search results). ABORTING...", e);
            triggerSearchDelete(searchIdList);
            triggerExperimentDelete(experimentId);
            log.error("DELETED search results (and runs)");
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

    private void uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory) throws Exception {

        Set<String> filenames = getFileNamePrefixes(fileDirectory);

        // If we didn't find anything throw an exception so that the experiment is deleted.
        if (filenames.size() == 0) {
            throw new IllegalArgumentException("No files found to upload in directory: "+fileDirectory);
        }

        // Make sure matching .ms2 and sqt file pairs are found
        if (!requiredFilesExist(fileDirectory, filenames)) {
            throw new IllegalArgumentException("Missing required files in directory: "+fileDirectory);
        }

        
        int runExpId = -1;
        for (String filename: filenames) {

            // We are only uploading SEQUEST .sqt files. We don't know how to upload other formats: 
            // e.g. Percolator, ProLuCID, PepProbe etc.
            if (!SQTFileReader.isSequestSQT(fileDirectory+File.separator+filename+".sqt")) {
                throw new IllegalArgumentException("Don't know how to convert non-SEQUEST .sqt files");
            }

            // upload the run first
            int runId = uploadMS2Run(fileDirectory+File.separator+filename+".ms2",experimentId);
            // get the experimentId for this run
            int tempId = MS2DataUploadService.getExperimentIdForRun(runId);
            if (runExpId == -1) runExpId = tempId;
            if (runExpId != tempId) {
                throw new Exception("Runs in an experiment upload cannot have different experimentIds!");
            }

            // now upload the search result and remember the searchId in case we have to abort upload
            int searchId = uploadSQTSearch(fileDirectory+File.separator+filename+".sqt", runId);
            searchIdList.add(searchId);
        }
        
        // if the runs in this experiment upload were already uploaded as part of another
        // experiment upload, delete the entry we created earlier in the msExperiment table
        if (runExpId != experimentId) {
            triggerExperimentDelete(experimentId);
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
        return uploadService.uploadSQTSearch(sqtProvider, runId);
    }
    
    private boolean requiredFilesExist(String fileDirectory, Set<String> filenames) {
        for (String filePrefix: filenames) {
            if (!(new File(fileDirectory+File.separator+filePrefix+".ms2").exists())) {
                log.error("Required file: "+filePrefix+".ms2 not found");
                return false;
            }
            if (!(new File(fileDirectory+File.separator+filePrefix+".sqt").exists())) {
                log.error("Required file: "+filePrefix+".sqt not found");
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
    
    private void triggerExperimentDelete(int experimentId) {
        MS2DataUploadService.deleteExperiment(experimentId);
        log.error("DELETED EXPERIMENT "+experimentId);
    }
    
    private void triggerSearchDelete(List<Integer> searchIdList) {
        for (Integer searchId: searchIdList) {
            SQTDataUploadService.deleteSearch(searchId);
            log.error("DELETED searchID: "+searchId);
        }
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        MsExperimentUploader uploader = new MsExperimentUploader();
        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/silmaril/WORK/UW/MS_LIBRARY/YATES_CYCLE_DUMP/1542/temp");
//      uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/MacCossData/sequest");
        //uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/new_lib/resources/PARC/TEST");
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
