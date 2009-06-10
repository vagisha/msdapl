/**
 * BaseJDBCUploadDAO.java
 * @author Vagisha Sharma
 * Jun 9, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 */
public class BaseJDBCUploadDAO {

    protected void close(Connection conn) {
        close(conn, null, null);
    }
    
    protected void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
    
    protected void close(Connection conn, Statement stmt, ResultSet rs) {
        
        if(rs != null) try { rs.close(); } catch (SQLException e){}
        if(stmt != null) try { stmt.close(); } catch (SQLException e){}
        if(conn != null) try { conn.close(); } catch (SQLException e){}
    }
}
