

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.service.MsExperimentUploader;
import org.yeastrc.ms.service.YatesCycleDownloader;


/**
 * 
 */
public class YatesCycleConverter {

    private static final Logger log = Logger.getLogger(YatesCycleConverter.class);
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, ZipException, IOException {
        
        Connection connect = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        
        // get a list of runIds from tblYatesCycles
//        String sql = "SELECT distinct runID FROM tblYatesCycles ORDER BY runID DESC limit 10";
        String sql = "SELECT distinct runID FROM tblYatesCycles WHERE runID < 2978 ORDER BY runID DESC limit 50";
        statement = connect.createStatement();
        rs = statement.executeQuery(sql);
        
        List<Integer> yatesRunIds = new ArrayList<Integer>();
        while(rs.next()) {
            yatesRunIds.add(rs.getInt("runID"));
        }

        rs.close();
        statement.close();
        connect.close();
        
       
        log.info("STARTED UPLOAD: "+new Date());
        String dataDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/UploadTest/dataDir";
        for (Integer runId: yatesRunIds) {
            log.info("------UPLOADING YATES runID: "+runId);
            List<YatesCycle> cycles = getCyclesForRun(runId);
            // download the files first
            for (YatesCycle cycle: cycles) {
                YatesCycleDownloader downloader = new YatesCycleDownloader();
                downloader.downloadMS2File(cycle.cycleId, dataDir, cycle.cycleName+".ms2");
                downloader.downloadSQTFile(cycle.cycleId, dataDir, cycle.cycleName+".sqt");
            }
            
            // upload data to msData database
            MsExperimentUploader uploader = new MsExperimentUploader();
            int experimentId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dataDir);
            
            // delete the files
            for (YatesCycle cycle: cycles) {
                log.info("Deleting yates cycle files......");
                new File(dataDir+File.separator+cycle.cycleName+".ms2").delete();
                new File(dataDir+File.separator+cycle.cycleName+".sqt").delete();
            }
            // make sure not ms2 or sqt files are left in the directory;
            String[] files = new File(dataDir).list();
            if (files.length > 0)
                throw new IllegalStateException("Files for previous experiment were not all deleted. Cannot continue...");
            log.info("------UPLOADED EXPERIMENT: "+experimentId+" for yates run: "+runId+"\n\n");
        }
        log.info("FINISHED UPLOAD: "+new Date());
    }

    public static List<YatesCycle> getCyclesForRun(int runId) throws ClassNotFoundException, SQLException {
        
        Connection connect = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        
        // get a list of runIds from tblYatesCycles
        String sql = "SELECT runID, cycleID, cycleFileName FROM tblYatesCycles where runID="+runId;
        statement = connect.createStatement();
        rs = statement.executeQuery(sql);
        
        List<YatesCycle> cycles = new ArrayList<YatesCycle>();
        while(rs.next()) {
            cycles.add(new YatesCycle(rs.getInt("runID"), rs.getInt("cycleID"), rs.getString("cycleFileName")));
        }
        
        rs.close();
        statement.close();
        connect.close();
        return cycles;
        
    }
    
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/yrc";
        return DriverManager.getConnection( URL, "root", "");
    }
    
    private static class YatesCycle {
        private int runId;
        private String cycleName;
        private int cycleId;

        public YatesCycle(int runId, int cycleId, String cycleName) {
            this.runId = runId;
            this.cycleId = cycleId;
            this.cycleName = cycleName;
        }
    }
    
//    public static void main(String[] args) {
//        int cycleId = 9686;
//        String fileName = "PARC_073105-smt3-wt-02.sqt";
//        String downloadDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/test";
//        YatesCycleConverter converter = new YatesCycleConverter();
//        converter.downloadSQTFile(cycleId, fileName, downloadDir);
//    }
}
