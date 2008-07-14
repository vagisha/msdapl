package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.MsSearch.SearchFileFormat;
import org.yeastrc.ms.domain.impl.MsExperimentDbImpl;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    private RunFileFormat runFormat;
    private SearchFileFormat searchFormat;
    
    public MsExperimentUploader(RunFileFormat runFormat, SearchFileFormat searchFormat) {
        this.runFormat = runFormat;
        this.searchFormat = searchFormat;
    }
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return true if experiment was uploaded to the database successfully, false otherwise
     */
    public boolean uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) {
        
        // right now we only know how to save runs in the .ms2 file format.
        if (runFormat != RunFileFormat.MS2) {
            log.error("Don't know how to save runs in format: "+runFormat);
            return false;
        }
        if (searchFormat != SearchFileFormat.SQT) {
            log.error("Don't know how to save search in format: "+searchFormat);
            return false;
        }
        
        
        int expId = 0;
        try {expId = saveExperiment(remoteServer, remoteDirectory, fileDirectory);}
        catch(Exception e) {log.error("ERROR SAVING EXPERIMENT", e); return false;}
        
        try {
            uploadRunAndSearchFilesToDb(expId, fileDirectory);
        }
        catch (Exception e) {
            log.error("ERROR UPLOADING EXPERIMENT (runs and/or search results). ABORTING...", e);
            return false;
        }
        
        return true;
    }

    private int saveExperiment(String remoteServer, String remoteDirectory,
            String fileDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperimentDbImpl experiment = new MsExperimentDbImpl();
        experiment.setDate(new Date(new java.util.Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }
    
    private boolean uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory) throws Exception {
        
        File directory = new File (fileDirectory);
        if (!directory.exists()) {
            log.error ("Invalid directory name.");
            return false;
        }
        
        Set<String> filenames = getFileNamesInDirectory(directory);
        
        // If we didn't find anything, just leave
        if (filenames.size() == 0) {
            log.error("No files found to upload");
            return false;
        }
        
        Ms2FileToDbConverter ms2Uploader = new Ms2FileToDbConverter();
        SqtFileToDbConverter sqtUploader = new SqtFileToDbConverter();
        
        for (String filename: filenames) {
            File file = new File (fileDirectory, filename + ".ms2");
            int runId = ms2Uploader.convertMs2File(file.getAbsolutePath(), experimentId);
            file = new File(fileDirectory, filename+".sqt");
            sqtUploader.convertSQTFile(file.getAbsolutePath(), runId);
        }
        return true;
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
        MsExperimentUploader uploader = new MsExperimentUploader(RunFileFormat.MS2, SearchFileFormat.SQT);
        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "./resources/PARC");
    }
}
