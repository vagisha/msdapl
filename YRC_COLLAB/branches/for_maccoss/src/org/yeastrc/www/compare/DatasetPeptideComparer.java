/**
 * DatasetPeptideComparer.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;

/**
 * 
 */
public class DatasetPeptideComparer {

    private static DatasetPeptideComparer instance;
    private static final ProteinferDAOFactory daoFactory = ProteinferDAOFactory.instance();
    private static final ProteinferPeptideDAO peptDao = daoFactory.getProteinferPeptideDao();
    private static final ProteinferProteinDAO protDao = daoFactory.getProteinferProteinDao();
    
    
    private DatasetPeptideComparer() {}
    
    public static DatasetPeptideComparer instance() {
        if(instance == null)
            instance = new DatasetPeptideComparer();
        return instance;
    }
    
    public int getMaxPeptidesForProtein(ComparisonProtein protein) {
        
        int max = 0;
        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
            
            // If this dataset does not contain this protein move on.
            if(!dpi.isPresent()) continue;
            
            max = Math.max(max, getPeptideCount(protein.getNrseqId(), dpi.getDataset()));
        }
        return max;
    }
    
    private int getPeptideCount(int nrseqProteinId, Dataset dataset) {
        
        int count = 0;
        if(dataset.getSource() == DatasetSource.DTA_SELECT) {
            
            
        }
        else if(dataset.getSource() == DatasetSource.PROT_INFER) {
            
            ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
            nrseqIds.add(nrseqProteinId);
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            for(int piProteinId: piProteinIds) {
                count += peptDao.getPeptideIdsForProteinferProtein(piProteinId).size();
            }
            return count;
        }
        return 0;
    }
    
    public PeptideComparisonDataset getComparisonPeptides(int nrseqProteinId, List<Dataset> datasets) {
        
        Map<String, ComparisonPeptide> peptSeqMap = new HashMap<String, ComparisonPeptide>();
        
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(nrseqProteinId);
        
        for(Dataset dataset: datasets) {
            
            if(dataset.getSource() == DatasetSource.PROT_INFER) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
                
                for(int piProteinId: piProteinIds) {
                    List<ProteinferPeptide> peptides = peptDao.loadPeptidesForProteinferProtein(piProteinId);
                    
                    for(ProteinferPeptide pept: peptides) {
                        ComparisonPeptide compPept = peptSeqMap.get(pept.getSequence());
                        if(compPept != null) {
                           compPept = new ComparisonPeptide(nrseqProteinId, pept.getSequence());
                           peptSeqMap.put(pept.getSequence(), compPept);
                        }
                        DatasetPeptideInformation dpi = new DatasetPeptideInformation(dataset);
                        dpi.setPresent(true);
                        dpi.setSpectrumCount(pept.getSpectrumCount());
                        dpi.setUnique(pept.isUniqueToProtein());
                    }
                }
            }
        }
        
        PeptideComparisonDataset pd = new PeptideComparisonDataset();
        pd.setDatasets(datasets);
        pd.setPeptides(new ArrayList<ComparisonPeptide>(peptSeqMap.values()));
        return pd;
    }
}
