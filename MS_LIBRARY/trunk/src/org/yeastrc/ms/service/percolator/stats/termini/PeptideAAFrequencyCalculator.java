/**
 * PeptideAaFrequencyCalculator.java
 * @author Vagisha Sharma
 * Feb 25, 2011
 */
package org.yeastrc.ms.service.percolator.stats.termini;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.impl.EnzymeBean;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.service.EnzymeRules.ENZYME;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PeptideAAFrequencyCalculator {

	private double scoreCutoff;
	private MsEnzyme enzyme;
	private EnzymeRule rule;
	
	private static final Logger log = Logger.getLogger(PeptideAAFrequencyCalculator.class);
	
	public PeptideAAFrequencyCalculator (MsEnzyme enzyme, double minQvalue) {
		
		this.enzyme = enzyme;
		// If we are not given an enzyme; assume it is Trypsin.
		if(this.enzyme == null) {
			EnzymeBean enz = new EnzymeBean();
			enz.setName(ENZYME.TRYPSIN.name());
	        enz.setCut(ENZYME.TRYPSIN.getCut());
	        enz.setNocut(ENZYME.TRYPSIN.getNoCut());
	        enz.setSense(ENZYME.TRYPSIN.getSense());
	        this.enzyme = enz;
		}
		if(this.enzyme != null)
			this.rule = new EnzymeRule(this.enzyme);
		
		this.scoreCutoff = minQvalue;
	}
	
	public PeptideTerminalResidueResult calculateForAnalysis(int analysisId) {
		
		DAOFactory daoFactory = DAOFactory.instance();
		
		MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
		
		PeptideTerminalResidueResult result = calculateForRunSearchAnalysisIds(runSearchAnalysisIds);
		result.setEnzyme(enzyme);
		return result;
		
	}

	public PeptideTerminalResidueResult calculateForRunSearchAnalysisIds(List<Integer> runSearchAnalysisIds) {
		
		
		long s = System.currentTimeMillis();
		
		PeptideTerminalResidueResult result = new PeptideTerminalResidueResult();
		for(Integer rsaId: runSearchAnalysisIds) {
			
			PeptideTerminalResidueResult rsaResult = calculateForRunSearchAnalysis(rsaId);
			result.combineWith(rsaResult);
		}
		
		long e = System.currentTimeMillis();
		
		log.info("Total time to get results "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("Total filtered results: "+result.getTotalResultCount());
		
		result.setEnzyme(enzyme);
		return result;
	}
	
	
	public PeptideTerminalResidueResult calculateForRunSearchAnalysis(int runSearchAnalysisId) {
		
		PercolatorResultDAO percDao = DAOFactory.instance().getPercolatorResultDAO();
		
		PercolatorResultFilterCriteria filterCriteria = new PercolatorResultFilterCriteria();
		filterCriteria.setMaxQValue(scoreCutoff);
		
		long s = System.currentTimeMillis();
		
		PeptideTerminalResidueResult result = new PeptideTerminalResidueResult();
		int totalResults = 0;
			
		List<Integer> percResultIds = percDao.loadIdsForRunSearchAnalysis(runSearchAnalysisId, filterCriteria, null);
		totalResults += percResultIds.size();

		log.info("Found "+percResultIds.size()+" Percolator results at qvalue <= "+scoreCutoff+
				" for runSearchAnalysisID "+runSearchAnalysisId);

		
		for(Integer percResultId: percResultIds) {

			PercolatorResult pres = percDao.loadForPercolatorResultId(percResultId);
			MsSearchResultPeptide peptide = pres.getResultPeptide();
			String seq = peptide.getPeptideSequence();

			char ntermMinusOne = peptide.getPreResidue(); // nterm - 1 residue
			result.addNtermMinusOneCount(ntermMinusOne);

			char nterm = seq.charAt(0); // nterm residue
			result.addNtermCount(nterm);

			char cterm = seq.charAt(seq.length() - 1); // cterm residue
			result.addCtermCount(cterm);

			char ctermPlusOne = peptide.getPostResidue(); // cterm + 1 residue
			result.addCtermPlusOneCount(ctermPlusOne);

			int numEnzTerm = 0;
			if(this.rule != null)
				numEnzTerm = rule.getNumEnzymaticTermini(seq, ntermMinusOne, ctermPlusOne);
			result.addEnzymaticTerminiCount(numEnzTerm);
		}
			
		long e = System.currentTimeMillis();
		
		log.info("Time to get results for runSearchAnalysisId "+runSearchAnalysisId+" "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("# filtered results: "+totalResults);
		
		result.setTotalResultCount(totalResults);
		result.setEnzyme(enzyme);
		
		return result;
	}
	
}
