/**
 * ProteinDatasetComparer.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;

/**
 * 
 */
public class ProteinDatasetComparer {

    private static final Logger log = Logger.getLogger(ProteinDatasetComparer.class.getName());
    
    private static ProteinDatasetComparer instance;
    
    private ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
    private IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
    
    private ProteinDatasetComparer() {}
    
    public static ProteinDatasetComparer instance() {
        if(instance == null) 
            instance = new ProteinDatasetComparer();
        return instance;
    }
    
    public ProteinComparisonDataset compareDatasets(List<Dataset> datasets, boolean parsimoniousOnly) throws Exception {
        
        Map<Integer, ComparisonProtein> proteinMap = new HashMap<Integer, ComparisonProtein>();
        
        // First get the parsimonious proteins
        for(Dataset dataset: datasets) {
            
            List<Integer> nrseqProteinIds = new ArrayList<Integer>(0);
            
            if(dataset.getSource() == DatasetSource.PROT_INFER)
                nrseqProteinIds = getProteinIdsForDataset(dataset, true, false); // get only parsimonious
            
            else if (dataset.getSource() == DatasetSource.DTA_SELECT) 
                nrseqProteinIds = getDtaSelectProteinIds(dataset);
                
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
        
        // now get the non-parsimonious proteins, if required (Protein Inference ONLY)
        if(!parsimoniousOnly) {
            
            for(Dataset dataset: datasets) {
                
                if(dataset.getSource() != DatasetSource.PROT_INFER)
                    continue;
                
                List<Integer> nrseqProteinIds = getProteinIdsForDataset(dataset, false, true); // get only non-parsimonious
                
                for(int nrseqId: nrseqProteinIds) {
                    ComparisonProtein protein = proteinMap.get(nrseqId);
                    if(protein == null) {
                        continue; // Ignore this proteins if it was not already listed as 
                                  // parsimonious for one or more of the datasets being compared.
                    }
                    DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                    dpi.setParsimonious(false);
                    dpi.setPresent(true);
                    protein.addDatasetInformation(dpi);
                }
            }
        }
        
        ProteinComparisonDataset comparison = new ProteinComparisonDataset();
        comparison.setDatasets(datasets);
        for(ComparisonProtein protein: proteinMap.values())
            comparison.addProtein(protein);
        
        return comparison;
        
    }
    
    private List<Integer> getDtaSelectProteinIds(Dataset dataset) throws InvalidIDException, SQLException, Exception {
        YatesRun run = new YatesRun();
        run.load(dataset.getDatasetId());
        return run.getNrseqIds();
    }

    private List<Integer> getProteinIdsForDataset(Dataset dataset, boolean parsimonious, boolean nonParsimonious) {
        
        if(dataset.getSource() == DatasetSource.PROT_INFER) {
            return protDao.getNrseqProteinIds(dataset.getDatasetId(), parsimonious, nonParsimonious);
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }
}
