/**
 * ProteinDatasetFilterer.java
 * @author Vagisha Sharma
 * Mar 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.util.FastaDatabaseLookupUtil;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;
import org.yeastrc.www.proteinfer.ProteinPropertiesFilter;

/**
 * 
 */
public class ProteinPropertiesFilterer {

	private static ProteinPropertiesFilterer instance = null;

	private static final Logger log = Logger.getLogger(ProteinPropertiesFilterer.class.getName());
	
	private ProteinPropertiesFilterer () {}

	public static ProteinPropertiesFilterer getInstance() {
		if(instance == null)
			instance = new ProteinPropertiesFilterer();
		return instance;
	}

	public void applyProteinPropertiesFilters(List<ComparisonProtein> proteins, 
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets) 
		throws SQLException {
		
		List<Integer> nrseqIds = new ArrayList<Integer>(proteins.size());
		for(ComparisonProtein protein: proteins) {
			nrseqIds.add(protein.getNrseqId());
		}
		
		long s, e;
		
		// apply accession filter
		if(filters.hasAccessionFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets);
			
			nrseqIds = ProteinAccessionFilter.getInstance().filterNrseqIdsByAccession(nrseqIds, 
					filters.getAccessionLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on accession: "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description LIKE filter
		if(filters.hasDescriptionLikeFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets, true); // add standard databases
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsMatchingDescription(nrseqIds,
					filters.getDescriptionLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on description (LIKE): "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description NOT LIKE filter
		if(filters.hasDescriptionNotLikeFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets, true); // add standard databases
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsNotMatchingDescription(nrseqIds,
					filters.getDescriptionNotLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on description NOT LIKE): "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply molecular wt and pI filters
		if(filters.hasMolecularWtFilter() || filters.hasPiFilter()) {
			s = System.currentTimeMillis();
			ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
			filterCriteria.setMinMolecularWt(filters.getMinMolecularWt());
			filterCriteria.setMaxMolecularWt(filters.getMaxMolecularWt());
			filterCriteria.setMinPi(filters.getMinPi());
			filterCriteria.setMaxPi(filters.getMaxPi());
			nrseqIds = ProteinPropertiesFilter.getInstance().filterNrseqIdsyMolecularWtAndPi(nrseqIds, filterCriteria);
			e = System.currentTimeMillis();
			log.info("Time to filter on Mol. Wt. and pI: "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// sort the filtered Ids
		Collections.sort(nrseqIds);
		// keep the ComparisonProteins that are in the filtered IDs
		Iterator<ComparisonProtein> iter = proteins.iterator();
		while(iter.hasNext()) {
			ComparisonProtein prot = iter.next();
			if(Collections.binarySearch(nrseqIds, prot.getNrseqId()) < 0)
				iter.remove();
		}
	}
	
	public void applyProteinPropertiesFiltersToGroup(List<ComparisonProteinGroup> proteinGroups, 
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets) throws SQLException {
		
		List<Integer> nrseqIds = new ArrayList<Integer>(proteinGroups.size());
		for(ComparisonProteinGroup proteinGrp: proteinGroups) {
			for(ComparisonProtein protein: proteinGrp.getProteins())
				nrseqIds.add(protein.getNrseqId());
		}
		
		long s, e;
		
		// apply accession filter
		if(filters.hasAccessionFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets);
			
			nrseqIds = ProteinAccessionFilter.getInstance().filterNrseqIdsByAccession(nrseqIds, 
					filters.getAccessionLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on accession: "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description LIKE filter
		if(filters.hasDescriptionLikeFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets, true); // add standard databases
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsMatchingDescription(nrseqIds,
					filters.getDescriptionLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on description (LIKE): "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description NOT LIKE filter
		if(filters.hasDescriptionNotLikeFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets, true); // add standard databases
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsNotMatchingDescription(nrseqIds,
					filters.getDescriptionNotLike(), fastaDbIds);
			e = System.currentTimeMillis();
			log.info("Time to filter on description NOT LIKE): "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply molecular wt and pI filters
		if(filters.hasMolecularWtFilter() || filters.hasPiFilter()) {
			s = System.currentTimeMillis();
			ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
			filterCriteria.setMinMolecularWt(filters.getMinMolecularWt());
			filterCriteria.setMaxMolecularWt(filters.getMaxMolecularWt());
			filterCriteria.setMinPi(filters.getMinPi());
			filterCriteria.setMaxPi(filters.getMaxPi());
			nrseqIds = ProteinPropertiesFilter.getInstance().filterNrseqIdsyMolecularWtAndPi(nrseqIds, filterCriteria);
			e = System.currentTimeMillis();
			log.info("Time to filter on Mol. Wt. and pI: "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// sort the filtered Ids
		Collections.sort(nrseqIds);
		// keep the ComparisonProteins that are in the filtered IDs
		Iterator<ComparisonProteinGroup> iter = proteinGroups.iterator();
		while(iter.hasNext()) {
			ComparisonProteinGroup protGrp = iter.next();
			
			boolean matches = false;
			for(ComparisonProtein prot: protGrp.getProteins()) {
				if(Collections.binarySearch(nrseqIds, prot.getNrseqId()) >= 0) {
					matches = true;
					break; // found a match
				}
			}
			if(!matches)
				iter.remove();
		}
	}
}
