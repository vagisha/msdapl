package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dto.MsExperiment;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;

public class MsExperimentUploader {

    private MsRun.RunFileFormat runFormat;
    
    public MsExperimentUploader(MsRun.RunFileFormat runFormat) {
        this.runFormat = runFormat;
    }
    
    public void uploadExperiment(String remoteServer, String remoteDirectory, String fileDirectory) throws Exception {
        
        // right now we only know how to save runs in the .ms2 file format.
        if (!runFormat.equals(RunFileFormat.MS2))
            throw new Exception("Don't know how to save runs in format: "+runFormat);
        
        int expId = saveExperiment(remoteServer, remoteDirectory, fileDirectory);
        saveMs2RunsInDirectory(expId, fileDirectory);
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
    
    private void saveMs2RunsInDirectory(int experimentId, String fileDirectory) throws Exception {
        
        File directory = new File (fileDirectory);
        if (!directory.exists())
            throw new Exception ("Invalid directory name.");
        
        Set<String> filenames = getMs2FileNamesInDirectory(directory);
        
        // If we didn't find anything, just leave
        if (filenames.size() == 0)
            return;
        
        Ms2FileToDbUploader ms2Uploader = new Ms2FileToDbUploader();
        
        for (String filename: filenames) {
            
            File file = new File (fileDirectory, filename + ".ms2");
            ms2Uploader.uploadMs2File(file.getAbsolutePath());
        }
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
