/**
 * ProteinDatasetComparer.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.dataset.FilterableDataset;

/**
 * 
 */
public class ProteinDatasetComparer {

    private static final Logger log = Logger.getLogger(ProteinDatasetComparer.class.getName());
    
    private static ProteinDatasetComparer instance;
    
    public static enum PARSIM{
		NONE(0), ONE(1), ALL(2);
		
		private int numericVal;
		private PARSIM(int count) {
			this.numericVal = count;
		}
		
		public int getNumericValue() {return numericVal;}
		
		public static PARSIM getForValue(int val) {
			switch (val) {
				case(0):  return NONE;
				case(1):  return ONE;
				case(2):  return ALL;
				default: return null;
			}
		}
	}
    
    private ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
    private IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
    private ProteinProphetProteinDAO ppProtDao = fact.getProteinProphetProteinDao();
    
    private ProteinDatasetComparer() {}
    
    public static ProteinDatasetComparer instance() {
        if(instance == null) 
            instance = new ProteinDatasetComparer();
        return instance;
    }
    
    public ProteinComparisonDataset compareDatasets(List<FilterableDataset> datasets, 
    		PARSIM parsimoniousCount) {
        
        // First create ProteinComparisionDataset
        ProteinComparisonDataset comparison = createProteinComparisionDataset(
				datasets, parsimoniousCount);
        
        
        return comparison;
        
    }

	private ProteinComparisonDataset createProteinComparisionDataset(List<FilterableDataset> datasets, 
			PARSIM parsimoniousCount) {
		
		Map<Integer, ComparisonProtein> proteinMap = new HashMap<Integer, ComparisonProtein>();
		
        
		// This will get ONLY the parsimonious proteins from each dataset that pass through the filter criteria
		// since the default ProteinFilterCriteria in the FilterableDataset has sets nonParsimonious to false
		// NOTE: The filter criteria, so far, is used individually only on ProteinProphet datasets (filtering on 
        // protein and group probabilities).  Other filters in the filter criteria (mol. wt., pI, accession, description terms)
        // are applied after the ProteinComparisionDataset has been created. This is because these filters are not 
        // specific to a particular dataset (unlike protein probabilities etc.).
        for(FilterableDataset dataset: datasets) {
            
        	log.info("Getting proteins from pinferID: "+dataset.getDatasetId()+ " for comparison "+
        			"; parsimonious: "+dataset.getProteinFilterCrteria().getParsimonious()+", "+
        			" non-parsimonious: "+dataset.getProteinFilterCrteria().getNonParsimonious());
        	
            List<Integer> nrseqProteinIds = new ArrayList<Integer>(0);
            
            if(dataset.getSource() != DatasetSource.DTA_SELECT)
                nrseqProteinIds = getProteinIdsForDataset(dataset);
            
            log.info("Got "+nrseqProteinIds.size()+" nrseqIds for pinferId: "+dataset.getDatasetId());
            for(int nrseqId: nrseqProteinIds) {
                ComparisonProtein protein = proteinMap.get(nrseqId);
                if(protein == null) {
                    protein = new ComparisonProtein(nrseqId);
                    proteinMap.put(nrseqId, protein);
                }
                DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                dpi.setParsimonious(true);
                dpi.setPresent(true);
                protein.addDatasetInformation(dpi);
            }
        }
        
        // If we want proteins that are parsimonious in at least one but not all the datasets we get
        // the list of non-parsimonious proteins in each dataset and keep the ones we have already seen
        // If a proteins was parsimonious in one of the datasets (and passed through any filters in the
        // filtering criteria) it should already be in the map.
        if(parsimoniousCount == PARSIM.ONE) {
            
        	log.info("Getting proteins parsimonious in one or more datasets");
            for(FilterableDataset dataset: datasets) {
                
            	// get only non-parsimonious
                List<Integer> nrseqProteinIds = getAllNonParsimoniousProteinIdsForDataset(dataset); 
                log.info("Got "+nrseqProteinIds.size()+" nrseqIds for pinferId: "+dataset.getDatasetId());
                
                for(int nrseqId: nrseqProteinIds) {
                    ComparisonProtein protein = proteinMap.get(nrseqId);
                    // This protein will not be in the map if: 
                    // 1. It did not make it through the filtering criteria in the previous step
                    // 2. It was not parsimonious in any of the datasets being compared. 
                    // Ignore this protein in such cases.
                    if(protein == null) {
                        continue; 
                    }
                    DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                    dpi.setParsimonious(false);
                    dpi.setPresent(true);
                    protein.addDatasetInformation(dpi);
                }
            }
        }
        // If we want ALL proteins regardless of parsimonious state, get all the filtered
        // non-parsimonious proteins for each dataset now.
        else if (parsimoniousCount == PARSIM.NONE) {
        	
        	log.info("Getting all non-parsimonious proteins that pass through the filter criteria");
        	for(FilterableDataset dataset: datasets) {
                
        		dataset.getProteinFilterCrteria().setParsimonious(false);
        		dataset.getProteinFilterCrteria().setNonParsimonious(true);
        		
        		List<Integer> nrseqProteinIds = new ArrayList<Integer>(0);
                
                if(dataset.getSource() != DatasetSource.DTA_SELECT)
                    nrseqProteinIds = getProteinIdsForDataset(dataset);
                log.info("Got "+nrseqProteinIds.size()+" nrseqIds for pinferId: "+dataset.getDatasetId());
                
                for(int nrseqId: nrseqProteinIds) {
                    ComparisonProtein protein = proteinMap.get(nrseqId);
                    if(protein == null) {
                        protein = new ComparisonProtein(nrseqId);
                        proteinMap.put(nrseqId, protein);
                    }
                    DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                    dpi.setParsimonious(false);
                    dpi.setPresent(true);
                    protein.addDatasetInformation(dpi);
                }
                
                // reset
                dataset.getProteinFilterCrteria().setParsimonious(true);
        		dataset.getProteinFilterCrteria().setNonParsimonious(false);
            }
        }
        
        ProteinComparisonDataset comparison = new ProteinComparisonDataset();
        comparison.setDatasets(datasets);
        for(ComparisonProtein protein: proteinMap.values())
            comparison.addProtein(protein);
		return comparison;
	}
    

    private List<Integer> getProteinIdsForDataset(FilterableDataset dataset) {
        
        if(dataset.getSource() == DatasetSource.PROTINFER) {
        	
        	List<Integer> ids = protDao.getFilteredNrseqIds(dataset.getDatasetId(), 
        			dataset.getProteinFilterCrteria());
        	return ids;
        }
        
        else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
           
        	List<Integer> ids = ppProtDao.getFilteredNrseqIds(dataset.getDatasetId(), 
        			(ProteinProphetFilterCriteria) dataset.getProteinFilterCrteria());

        	return ids;
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }
    
    private List<Integer> getAllNonParsimoniousProteinIdsForDataset(Dataset dataset) {
        
        if(dataset.getSource() == DatasetSource.PROTINFER) {
            return protDao.getNrseqProteinIds(dataset.getDatasetId(), false, true); // non-parsimonious only
        }
        else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            return ppProtDao.getNrseqProteinIds(dataset.getDatasetId(), false, true); // non-parsimonious only
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }
}
