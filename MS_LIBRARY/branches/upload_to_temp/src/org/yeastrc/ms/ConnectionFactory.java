/**
 * ConnectionFactory.java
 * @author Vagisha Sharma
 * Apr 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 
 */
public class ConnectionFactory {

    
    private static DataSource ds = null;
    
    static {
        ds = setupDataSource("jdbc:mysql://localhost/msData_test");
    }
    private ConnectionFactory() {}
    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private static DataSource setupDataSource(String dbUrl) {
        
        BasicDataSource bds = new BasicDataSource();;
        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUsername("root");
        bds.setPassword("");
        bds.setUrl(dbUrl);
        return bds;
    }
}
