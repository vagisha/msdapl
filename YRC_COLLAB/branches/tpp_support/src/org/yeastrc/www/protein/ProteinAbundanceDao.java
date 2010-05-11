package org.yeastrc.www.protein;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<YeastOrfAbundance> getAbundance(int nrseqProteinId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT orfName, abundance from yeastProteinAbundance WHERE proteinID = "+nrseqProteinId;
			conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			List<YeastOrfAbundance> orfs = new ArrayList<YeastOrfAbundance>();
			while(rs.next()) {
				YeastOrfAbundance oa = new YeastOrfAbundance();
				oa.setAbundance(rs.getDouble("abundance"));
				oa.setOrfName(rs.getString("orfName"));
				orfs.add(oa);
			}
			return orfs;
			
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
	
	public static final class YeastOrfAbundance {
		
		private String orfName;
		private double abundance;
		public String getOrfName() {
			return orfName;
		}
		public void setOrfName(String orfName) {
			this.orfName = orfName;
		}
		public double getAbundance() {
			return abundance;
		}
		public String getAbundanceToPrint() {
			if(abundance == Math.round(abundance)) {
				return String.valueOf((int)abundance);
			}
			else
				return String.valueOf(abundance);
		}
		public String getAbundanceAndOrfNameToPrint() {
			return orfName+":"+getAbundanceToPrint();
		}
		public void setAbundance(double abundance) {
			this.abundance = abundance;
		}
	}
}
