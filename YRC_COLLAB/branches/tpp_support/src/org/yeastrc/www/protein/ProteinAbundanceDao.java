package org.yeastrc.www.protein;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;

/**
 * ProteinAbundanceDao.java
 * @author Vagisha Sharma
 * May 9, 2010
 * @version 1.0
 */

/**
 * 
 */
public class ProteinAbundanceDao {

	private static ProteinAbundanceDao instance = null;
	
	private ProteinAbundanceDao() {}
	
	public static synchronized ProteinAbundanceDao getInstance() {
		if(instance == null)
			instance = new ProteinAbundanceDao();
		return instance;
	}
	
	public double getAbundance(int nrseqProteinId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT abundance from yeastProteinAbundance WHERE proteinID = "+nrseqProteinId;
			conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				return rs.getDouble("abundance");
			}
			return -1.0;
			
		} finally {

			if (rs != null) {
				try { rs.close(); rs = null; } catch (Exception e) { ; }
			}
			
			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
	}
}
