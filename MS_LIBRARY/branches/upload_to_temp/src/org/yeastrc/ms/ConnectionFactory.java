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
import org.yeastrc.ms.dao.UploadDAOFactory;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class ConnectionFactory {

    
    private static String mainDbName;
    private static String tempDbName;
    
    private static DataSource mainMsData = null;
    private static DataSource tempMsData = null;
    
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
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        
        String tempDbUrl = props.getProperty("db.temp.url");
        tempDbName = props.getProperty("db.temp.name");
        
        mainMsData = setupDataSource(mainDbUrl, user, password);
        tempMsData = setupDataSource(tempDbUrl, user, password);
        
    }
    private ConnectionFactory() {}
    
    public static Connection getConnection() throws SQLException {
//        return UploadDAOFactory.getInstance().getConnection();
        return mainMsData.getConnection();
    }
    
    public static Connection getTempDbConnection() throws SQLException {
//        return UploadDAOFactory.getInstance().getTempDbConnection();
        return tempMsData.getConnection();
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
    
    
}
