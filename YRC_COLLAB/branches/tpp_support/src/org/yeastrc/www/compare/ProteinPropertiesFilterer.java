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
			nrseqIds = ProteinAccessionFilter.getInstance().filterNrseqIdsByName(nrseqIds, filters.getAccessionLike());
			e = System.currentTimeMillis();
			log.info("Time to filter on accession: "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description LIKE filter
		List<Integer> fastaDatabaseIds = null;
		if(filters.hasDescriptionLikeFilter()) {
			s = System.currentTimeMillis();
			fastaDatabaseIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets);
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsByDescription(nrseqIds, fastaDatabaseIds,
					filters.getDescriptionLike(), true);
			e = System.currentTimeMillis();
			log.info("Time to filter on description (LIKE): "+TimeUtils.timeElapsedSeconds(e, e)+" seconds");
		}
		
		
		// apply description NOT LIKE filter
		if(filters.hasDescriptionNotLikeFilter()) {
			s = System.currentTimeMillis();
			if(fastaDatabaseIds == null)
				fastaDatabaseIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets);
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsByDescription(nrseqIds, fastaDatabaseIds,
					filters.getDescriptionLike(), false);
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
	
	public void applyProteinPropertiesFiltersToGroup(List<ComparisonProteinGroup> proteins, 
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets) {
		
	}

//	public void applySearchNameFilter(ProteinComparisonDataset dataset, String searchString) throws SQLException {
//
//		if(searchString == null || searchString.trim().length() == 0)
//			return;
//
//		// get the protein ids for the names the user is searching for
//		Set<Integer> proteinIds = new HashSet<Integer>();
//		String tokens[] = searchString.split(",");
//
//		List<String> notFound = new ArrayList<String>();
//
//		// Do a common name lookup first
//		for(String token: tokens) {
//			String name = token.trim();
//			if(name.length() > 0) {
//				List<Integer> ids = CommonNameLookupUtil.getInstance().getProteinIds(name);
//				proteinIds.addAll(ids);
//
//				if(ids.size() == 0) {
//					notFound.add(name);
//				}
//			}
//		}
//
//		// Now look at the accession strings in tblProteinDatabase;
//		if(notFound.size() > 0) {
//			for(String name: notFound) {
//				List<Integer> ids = NrSeqLookupUtil.getProteinIdsForAccession(name);
//				if(ids != null)
//					proteinIds.addAll(ids);
//			}
//		}
//
//		// sort the matching protein ids.
//		List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
//		sortedIds.addAll(proteinIds);
//		Collections.sort(sortedIds);
//
//		// Remove the ones that do not match
//		Iterator<ComparisonProtein> iter = dataset.getProteins().iterator();
//		while(iter.hasNext()) {
//			ComparisonProtein prot = iter.next();
//			if(Collections.binarySearch(sortedIds, prot.getNrseqId()) < 0)
//				iter.remove();
//		}
//	}
//
//
//	public void applyDescriptionFilter(ProteinComparisonDataset dataset, String searchString) throws SQLException {
//
//		if(searchString == null || searchString.trim().length() == 0)
//			return;
//
//		// get the protein ids for the descriptions the user is searching for
//		Set<Integer> proteinIds = new HashSet<Integer>();
//		String tokens[] = searchString.split(",");
//
//		List<String> notFound = new ArrayList<String>();
//
//		for(String token: tokens) {
//			String description = token.trim();
//			if(description.length() > 0) {
//				List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteinsForDescription(dataset.getFastaDatabaseIds(), description);
//				for(NrDbProtein protein: proteins)
//					proteinIds.add(protein.getProteinId());
//			}
//		}
//
//		// sort the matching protein ids.
//		List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
//		sortedIds.addAll(proteinIds);
//		Collections.sort(sortedIds);
//
//		// Remove the ones that do not match
//		Iterator<ComparisonProtein> iter = dataset.getProteins().iterator();
//		while(iter.hasNext()) {
//			ComparisonProtein prot = iter.next();
//			if(Collections.binarySearch(sortedIds, prot.getNrseqId()) < 0)
//				iter.remove();
//		}
//	}
}
