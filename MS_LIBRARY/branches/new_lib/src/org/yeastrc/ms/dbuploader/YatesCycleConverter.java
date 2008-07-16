/**
 * YatesCycleConverter.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dbuploader;

import java.io.BufferedReader;
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
import java.util.zip.ZipException;

import org.yeastrc.ms2.utils.Decompresser;


/**
 * 
 */
public class YatesCycleConverter {

    
    // runID will become the experimentId in msData 
    // cycleID will become the runID in msData
    public void convertYatesCycle(int cycleId, int runId) {
        
    }
    
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, ZipException, IOException {
        
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
        int experimentID = 0;
        for (YatesCycle exp: experiments) {
            if (lastRunId != exp.runId) {
                lastRunId = exp.runId;
                numExp++;
                // upload experiment
            }
            // get the MS2 file for this cycleID and upload it
            //uploadMS2File(exp.cycleId, exp.cycleName+".ms2", experimentID);
            
            // get the SQT file for this cycleID and upload it
            uploadSQTFile(exp.cycleId, exp.cycleName+".sqt", experimentID);
            break;
        }
        
        
        System.out.println("Number of experiments found: "+numExp);
    }
    
    public static void uploadMS2File(int cycleId, String fileName, int experimentId) throws ClassNotFoundException, SQLException, ZipException, IOException {
        Connection conn = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        System.out.println("CycleID is: "+cycleId);
        String sql = "SELECT data from tblYatesCycleMS2Data WHERE cycleID="+cycleId;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        if (rs.next()) {
            byte[] bytes = rs.getBytes("data");
            InputStream instr = Decompresser.getInstance().decompressString(bytes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instr));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        
        rs.close();
        statement.close();
        conn.close();
        
    }
    
    public static void uploadSQTFile(int cycleId, String fileName, int runId) throws ClassNotFoundException, SQLException, ZipException, IOException {
        Connection conn = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        System.out.println("CycleID is: "+cycleId);
        String sql = "SELECT data from tblYatesCycleSQTData WHERE cycleID="+cycleId;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        if (rs.next()) {
            byte[] bytes = rs.getBytes("data");
            InputStream instr = Decompresser.getInstance().decompressString(bytes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instr));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        
        rs.close();
        statement.close();
        conn.close();
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
}
