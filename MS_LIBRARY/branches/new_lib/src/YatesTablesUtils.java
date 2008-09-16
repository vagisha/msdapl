import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms2.data.DTAPeptide;
import org.yeastrc.ms2.data.DTAPeptideLoader;
import org.yeastrc.ms2.data.DTAPeptideSaver;


public class YatesTablesUtils {

    public static final Logger log = Logger.getLogger(YatesTablesUtils.class);
    
    private YatesTablesUtils() {}
    
    public static List<Integer> getAllYatesRunIds(String queryQualifier) {
        
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        // get a list of runIds from tblYatesCycles
        // String sql = "SELECT distinct runID FROM tblYatesCycles ORDER BY runID DESC limit 10";
        String sql = "SELECT distinct runID FROM tblYatesCycles "+queryQualifier;
        try {
            connect = getConnection();
            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<Integer> yatesRunIds = new ArrayList<Integer>();
            while(rs.next()) {
                yatesRunIds.add(rs.getInt("runID"));
            }
            return yatesRunIds;
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (connect != null) connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static List<YatesCycle> getCyclesForRun(int runId) {
        
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            connect = getConnection();
            // get a list of runIds from tblYatesCycles
            String sql = "SELECT runID, cycleID, cycleFileName FROM tblYatesCycles where runID="+runId;
            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<YatesCycle> cycles = new ArrayList<YatesCycle>();
            while(rs.next()) {
                cycles.add(new YatesCycle(rs.getInt("runID"), rs.getInt("cycleID"), rs.getString("cycleFileName")));
            }
            return cycles;
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<YatesCycle>(0);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<YatesCycle>(0);
        }
        finally {
            try {
                rs.close();
                statement.close();
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Integer> getYatesResultPeptideIds(int yatesRunId) {
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            connect = getConnection();
            // get a list of runIds from tblYatesCycles
            String sql = "select pep.id from tblYatesRun as run, tblYatesRunResult as res, tblYatesResultPeptide as pep where run.id = "+
                            yatesRunId+
                            " and run.id = res.runID and res.id =  pep.resultID";

            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<Integer> idList = new ArrayList<Integer>();
            while(rs.next()) {
                idList.add(rs.getInt("id"));
            }
            return idList;
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        finally {
            try {
                rs.close();
                statement.close();
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static DTAPeptide loadDTAPeptide(int id) throws Exception {
        Connection connect = null;
        
        try {
            connect = getConnection();
            return DTAPeptideLoader.getInstance().load(id, connect);
            
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        finally {
            try {
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static void updateDTAPeptide(DTAPeptide peptide) throws Exception {
        Connection connect = null;
        
        try {
            connect = getConnection();
            DTAPeptideSaver.getInstance().update(peptide, connect);
            
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            try {
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/yrc";
        return DriverManager.getConnection( URL, "root", "");
    }
    
    public static final class YatesCycle {
        public int runId;
        public String cycleName;
        public int cycleId;

        public YatesCycle(int runId, int cycleId, String cycleName) {
            this.runId = runId;
            this.cycleId = cycleId;
            this.cycleName = cycleName;
        }
    }
}
