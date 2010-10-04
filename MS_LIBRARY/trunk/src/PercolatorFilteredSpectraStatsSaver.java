import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraDistributionCalculator;

/**
 * PercolatorFilteredSpectraStatsSaver.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */

/**
 * 
 */
public class PercolatorFilteredSpectraStatsSaver {

	public static void main(String[] args) throws SQLException {

		List<Integer> searchAnalysisIds = getSearchAnalysisIds();
		
		DAOFactory fact = DAOFactory.instance();
		PercolatorFilteredSpectraResultDAO filtPsmDao = fact.getPrecolatorFilteredSpectraResultDAO();
		
		for(Integer saId: searchAnalysisIds) {

			System.out.println("Saving results for searchAnalysisID: "+saId);
			PercolatorFilteredSpectraDistributionCalculator calc = new PercolatorFilteredSpectraDistributionCalculator(saId, 0.01);
			calc.calculate();
			List<PercolatorFilteredSpectraResult> filteredResults = calc.getFilteredResults();
			if(filteredResults == null || filteredResults.size() == 0) {
				System.out.println("No results for searchAnalysisID: "+saId+". Skipping....");
				continue;
			}
			for(PercolatorFilteredSpectraResult res: filteredResults) {
				System.out.println("\tSaving for: "+res.getRunSearchAnalysisId());
				filtPsmDao.save(res);
			}
		}
	}
	
	private static List<Integer> getSearchAnalysisIds() throws SQLException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DAOFactory.instance().getConnection();
			String sql = "SELECT id FROM msSearchAnalysis ORDER BY id DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			List<Integer> pinferIds = new ArrayList<Integer>();
			while(rs.next()) {
				pinferIds.add(rs.getInt("id"));
			}
			return pinferIds;
		}
		finally {
			if(conn != null) try {conn.close();}catch(Exception e){}
			if(stmt != null) try {stmt.close();}catch(Exception e){}
			if(rs != null) try {rs.close();}catch(Exception e){}
		}
	}
}
