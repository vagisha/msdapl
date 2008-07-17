/**
 * YatesCycleConverter.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchDataProviderImpl;
import org.yeastrc.ms.parser.sqtFile.SQTSearchDataProviderImpl.ScanResultIterator;
import org.yeastrc.ms2.utils.Decompresser;


/**
 * 
 */
public class YatesCycleConverter {

    private static final Logger log = Logger.getLogger(YatesCycleConverter.class);
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, ZipException, IOException {
        
        YatesCycleConverter converter = new YatesCycleConverter();
        
        Connection connect = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        
        // get a list of runIds from tblYatesCycles
        String sql = "SELECT runID, cycleID, cycleFileName FROM tblYatesCycles ORDER BY runID";
        statement = connect.createStatement();
        rs = statement.executeQuery(sql);
        
        List<YatesCycle> experiments = new ArrayList<YatesCycle>();
        while(rs.next()) {
            experiments.add(new YatesCycle(rs.getInt("runID"), rs.getInt("cycleID"), rs.getString("cycleFileName")));
        }
        Collections.sort(experiments, new Comparator<YatesCycle>() {
            public int compare(YatesCycle o1, YatesCycle o2) {
                return Integer.valueOf(o2.runId).compareTo(Integer.valueOf(o1.runId));
            }});
        
        rs.close();
        statement.close();
        connect.close();
        
        
        int lastRunId = 0;
        int numExp = 0;
        String dataDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/SQTParserTest";
        MsExperimentUploader expUploader = new MsExperimentUploader();
        for (YatesCycle exp: experiments) {
//            if (lastRunId != exp.runId) {
//                
//                // convert the experiment
//                if (numExp != 0)
//                    expUploader.uploadExperimentToDb("my_computer", dataDir, dataDir);
//                
//                // TODO delete the downloaded files
//                if (numExp != 0)
//                    break;
//                
//                lastRunId = exp.runId;
//                numExp++;
//            }
            // download the MS2 file for this experiment
//            converter.downloadMS2File(exp.cycleId, exp.cycleName+".ms2", dataDir);
            
            // download the SQT file for this experiment
            if (converter.downloadSQTFile(exp.cycleId, exp.cycleName+".sqt", dataDir)) {
                converter.parseSQTFile(dataDir+File.separator+exp.cycleName+".sqt");
                numExp++;
                new File(dataDir+File.separator+exp.cycleName+".sqt").delete();
            }
        }
        
        // upload the last experiment
//        expUploader.uploadExperimentToDb("my_computer", dataDir, dataDir);
        
        System.out.println("Number of experiments found: "+numExp);
    }
    
    public boolean downloadMS2File(int cycleId, String fileName, String downloadDir) throws ClassNotFoundException, SQLException, ZipException, IOException {
        Connection conn = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        System.out.println("CycleID is: "+cycleId);
        String sql = "SELECT data from tblYatesCycleMS2Data WHERE cycleID="+cycleId;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        BufferedReader reader = null;
        boolean downloaded = false;
        BufferedWriter writer = new BufferedWriter(new FileWriter(downloadDir+File.separator+fileName));
        if (rs.next()) {
            byte[] bytes = rs.getBytes("data");
            InputStream instr = Decompresser.getInstance().decompressString(bytes);
            reader = new BufferedReader(new InputStreamReader(instr));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
            downloaded = true;
        }
        reader.close();
        writer.close();
        rs.close();
        statement.close();
        conn.close();
        return downloaded;
    }
    
    public void parseSQTFile(String filePath) {
        SQTSearchDataProviderImpl provider = new SQTSearchDataProviderImpl();
        try {
            provider.setSQTSearch(filePath);
            try {
                provider.getSearchData();
            }
            catch (DataFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
        catch (ParserException e) {
            e.printStackTrace();
        }
        ScanResultIterator scanIt = provider.scanResultIterator();
        while(scanIt.hasNext()) {
            try {
                scanIt.next();
            }
            catch (DataFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    public boolean downloadSQTFile(int cycleId, String fileName, String downloadDir) throws ClassNotFoundException, SQLException, ZipException, IOException {
        Connection conn = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        log.debug("CycleID is: "+cycleId+"; fileName: "+fileName);
        String sql = "SELECT data from tblYatesCycleSQTData WHERE cycleID="+cycleId;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        BufferedReader reader = null;
        boolean downloaded = false;
        BufferedWriter writer = new BufferedWriter(new FileWriter(downloadDir+File.separator+fileName));
        if (rs.next()) {
            byte[] bytes = rs.getBytes("data");
            InputStream instr = Decompresser.getInstance().decompressString(bytes);
            reader = new BufferedReader(new InputStreamReader(instr));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
            reader.close();
            downloaded = true;
        }
        writer.close();
        rs.close();
        statement.close();
        conn.close();
        return downloaded;
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
    
//    public static void main(String[] args) throws ZipException, ClassNotFoundException, SQLException, IOException {
//        int cycleId = 9686;
//        String fileName = "PARC_073105-smt3-wt-02.sqt";
//        String downloadDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/test";
//        YatesCycleConverter converter = new YatesCycleConverter();
//        converter.downloadSQTFile(cycleId, fileName, downloadDir);
//    }
}
