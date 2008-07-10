package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dto.MsExperiment;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    private MsRun.RunFileFormat runFormat;
    
    public MsExperimentUploader(MsRun.RunFileFormat runFormat) {
        this.runFormat = runFormat;
    }
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return true if experiment was uploaded to the database successfully, false otherwise
     */
    public boolean uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) {
        
        // right now we only know how to save runs in the .ms2 file format.
        if (!runFormat.equals(RunFileFormat.MS2)) {
            log.error("Don't know how to save runs in format: "+runFormat);
            return false;
        }
        
        
        int expId = 0;
        try {expId = saveExperiment(remoteServer, remoteDirectory, fileDirectory);}
        catch(Exception e) {log.error("ERROR SAVING EXPERIMENT", e); return false;}
        
        uploadMs2FilesToDb(expId, fileDirectory);
        
        return true;
    }

    private int saveExperiment(String remoteServer, String remoteDirectory,
            String fileDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperiment experiment = new MsExperiment();
        experiment.setDate(new Date(new java.util.Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }
    
    private boolean uploadMs2FilesToDb(int experimentId, String fileDirectory) {
        
        File directory = new File (fileDirectory);
        if (!directory.exists()) {
            log.error ("Invalid directory name.");
            return false;
        }
        
        Set<String> filenames = getMs2FileNamesInDirectory(directory);
        
        // If we didn't find anything, just leave
        if (filenames.size() == 0) {
            log.error("No files found to upload");
            return false;
        }
        
        Ms2FileToDbConverter ms2Uploader = new Ms2FileToDbConverter();
        
        for (String filename: filenames) {
            
            File file = new File (fileDirectory, filename + ".ms2");
           // ms2Uploader.uploadMs2File(file.getAbsolutePath());
        }
        return true;
    }

    private Set<String> getMs2FileNamesInDirectory(File directory) {
        Set<String> filenames = new HashSet<String>();
        File[] files = directory.listFiles();
        String name = null;
        for (int i = 0; i < files.length; i++) {
            
            if (!files[i].getName().endsWith(".ms2"))
                continue;
            
            name = files[i].getName();
            name = name.replaceAll("\\.ms2", "");
            
            filenames.add(name);
        }
        return filenames;
    }
}
