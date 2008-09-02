

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.service.UploadException;

/**
 * 
 */
public class YatesCycleConverter {

    private static final Logger log = Logger.getLogger(YatesCycleConverter.class);
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, ZipException, IOException, UploadException {
        
//        List<Integer> yatesRunIds = YatesTablesUtils.getAllYatesRunIds("ORDER BY runID DESC limit 50");
        List<Integer> yatesRunIds = new ArrayList<Integer>(1);
        yatesRunIds.add(2930);
        
        if (yatesRunIds.size() == 0) {
            log.error("No runIds found!!");
            return;
        }
       
        log.info("STARTED UPLOAD: "+new Date());
//        String dataDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/UploadTest/dataDir";
        String dataDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/2930";
        for (Integer runId: yatesRunIds) {
            log.info("------UPLOADING YATES runID: "+runId);
//            List<YatesTablesUtils.YatesCycle> cycles = YatesTablesUtils.getCyclesForRun(runId);
//            // download the files first
//            for (YatesTablesUtils.YatesCycle cycle: cycles) {
//                YatesCycleDownloader downloader = new YatesCycleDownloader();
//                downloader.downloadMS2File(cycle.cycleId, dataDir, cycle.cycleName+".ms2");
//                downloader.downloadSQTFile(cycle.cycleId, dataDir, cycle.cycleName+".sqt");
//            }
            
            // upload data to msData database
            MsDataUploader uploader = new MsDataUploader();
            int experimentId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dataDir, new Date());
            
            // delete the files
//            for (YatesTablesUtils.YatesCycle cycle: cycles) {
//                log.info("Deleting yates cycle files......");
//                new File(dataDir+File.separator+cycle.cycleName+".ms2").delete();
//                new File(dataDir+File.separator+cycle.cycleName+".sqt").delete();
//            }
//            // make sure not ms2 or sqt files are left in the directory;
//            String[] files = new File(dataDir).list();
//            if (files.length > 0)
//                throw new IllegalStateException("Files for previous experiment were not all deleted. Cannot continue...");
            log.info("------UPLOADED EXPERIMENT: "+experimentId+" for yates run: "+runId+"\n\n");
        }
        log.info("FINISHED UPLOAD: "+new Date());
    }
}
