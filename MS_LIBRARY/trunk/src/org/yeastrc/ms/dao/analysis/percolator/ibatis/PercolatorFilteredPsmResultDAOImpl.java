/**
 * PercolatorPsmFilteredResultDAOImpl.java
 * @author Vagisha Sharma
 * Sep 29, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredPsmResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedPsmResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredPsmResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PercolatorFilteredPsmResultDAOImpl extends BaseSqlMapDAO implements
		PercolatorFilteredPsmResultDAO {

	private static final String namespace = "PercolatorFilteredPsmResult";
	
	public PercolatorFilteredPsmResultDAOImpl(SqlMapClient sqlMap) {
		super(sqlMap);
	}

	@Override
	public PercolatorFilteredPsmResult load(int runSearchAnalysisId) {
		
		return (PercolatorFilteredPsmResult) queryForObject(namespace+".select", runSearchAnalysisId);
	}

	@Override
	public void save(PercolatorFilteredPsmResult result) {
		
		try {
			save(namespace+".insert",result);
		
			for(PercolatorBinnedPsmResult binnedResult: result.getBinnedResults()) {
				save(namespace+".insertBinnedResult",binnedResult);
			}
		}
		catch(RuntimeException e) {
			delete(result.getRunSearchAnalysisId());
			throw e;
		}
	}

	@Override
	public void delete(int runSearchAnalysisId) {
		delete(namespace+".delete",runSearchAnalysisId);
	}

}
