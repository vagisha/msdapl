/**
 * ConnectionFactory.java
 * @author Vagisha Sharma
 * Apr 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.yeastrc.ms.service.MsDataUploadProperties;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class ConnectionFactory {

    
    private static String mainDbName;
    private static String tempDbName;
    
    private static DataSource mainMsData = null;
    private static DataSource tempMsData = null;
    
    private static String nrseqDbName;
    
    private static DataSource nrseq = null;
    
    private static String user;
    private static String password;
    
    private static final Logger log = Logger.getLogger(ConnectionFactory.class.getName());
    
    static {
        Properties props = new Properties();
        try {
            Reader reader = Resources.getResourceAsReader("msDataDB.properties");
            props.load(reader);
        }
        catch (IOException e) {
            log.error("Error reading properties file msDataDB.properties", e);
        }
        
        String mainDbUrl = props.getProperty("db.url");
        mainDbName = props.getProperty("db.name");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");
        
        String tempDbUrl = props.getProperty("db.temp.url");
        tempDbName = props.getProperty("db.temp.name");
        
        mainMsData = setupDataSource(mainDbUrl, user, password);
        tempMsData = setupDataSource(tempDbUrl, user, password);
        
        nrseqDbName = props.getProperty("db.nrseq.name");
        String nrseqUrl = props.getProperty("db.nrseq.url");
        nrseq = setupDataSource(nrseqUrl, user, password);
        
    }
    private ConnectionFactory() {}
    
    public static Connection getMainMsDataConnection() throws SQLException {
//        return UploadDAOFactory.getInstance().getConnection();
        return mainMsData.getConnection();
    }
    
    public static Connection getTempMsDataConnection() throws SQLException {
//        return UploadDAOFactory.getInstance().getTempDbConnection();
        return tempMsData.getConnection();
    }
    
    public static Connection getNrseqConnection() throws SQLException {
//      return UploadDAOFactory.getInstance().getConnection();
      return nrseq.getConnection();
  }
  
    public static Connection getMsDataConnection() throws SQLException {
        if(MsDataUploadProperties.uploadToTempTables())
            return getTempMsDataConnection();
        else
            return getMainMsDataConnection();
    }
    
    private static DataSource setupDataSource(String dbUrl, String user, String password) {
        
        BasicDataSource bds = new BasicDataSource();;
        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUsername(user);
        bds.setPassword(password);
        bds.setUrl(dbUrl);
        bds.setMaxActive(30);
        bds.setMaxIdle(10);
        bds.setMaxWait(10000);
        bds.setDefaultAutoCommit(true);
        bds.setValidationQuery("SELECT 1");
        return bds;
    }
    
    public static String masterDbName() {
        return mainDbName;
    }
    
    public static String tempDbName() {
        return tempDbName;
    }
    
    public static String nrseqDbName() {
        return nrseqDbName;
    }
    
    public static DataSource getDataSource(String dbName) {
        String dbUrl = "jdbc:mysql://localhost/"+dbName;
        DataSource ds = setupDataSource(dbUrl, user, password);
        return ds;
    }
    
    public static Connection getConnection(String dbName) throws SQLException {
        String dbUrl = "jdbc:mysql://localhost/"+dbName;
        DataSource ds = setupDataSource(dbUrl, user, password);
        return ds.getConnection();
    }
}
