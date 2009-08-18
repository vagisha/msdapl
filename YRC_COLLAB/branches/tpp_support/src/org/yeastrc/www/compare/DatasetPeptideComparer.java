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

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;

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
        
        List<Integer> datasetIds = new ArrayList<Integer>(protein.getDatasetInfo().size());
        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
            // If this dataset does not contain this protein move on.
            if(!dpi.isPresent()) continue;
            datasetIds.add(dpi.getDatasetId());
        }
        return protDao.getPeptideCountForProtein(protein.getNrseqId(), datasetIds);
        
//        Set<String> peptides = new HashSet<String>();
//        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
//            
//            // If this dataset does not contain this protein move on.
//            if(!dpi.isPresent()) continue;
//            
//            List<String> protPeptides = getPeptideSequences(protein.getNrseqId(), dpi.getDataset());
//            peptides.addAll(protPeptides);
////            max = Math.max(max, getPeptideCount(protein.getNrseqId(), dpi.getDataset()));
//        }
//        return peptides.size();
    }
    
    private int getPeptideCount(int nrseqProteinId, Dataset dataset) {
        
        int count = 0;
        if(dataset.getSource() == DatasetSource.DTA_SELECT) {
            
            
        }
        else if(dataset.getSource() == DatasetSource.PROTINFER || dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            
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
    
    private List<String> getPeptideSequences(int nrseqProteinId, Dataset dataset) {
        
        List<String> peptides = new ArrayList<String>();
        if(dataset.getSource() == DatasetSource.DTA_SELECT) {
            
            // TODO
        }
        else if(dataset.getSource() == DatasetSource.PROTINFER || dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            
            ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
            nrseqIds.add(nrseqProteinId);
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            for(int piProteinId: piProteinIds) {
                List<ProteinferPeptide> piPeptides = peptDao.loadPeptidesForProteinferProtein(piProteinId);
                for(ProteinferPeptide pept: piPeptides) {
                    peptides.add(pept.getSequence());
                }
            }
        }
        return peptides;
    }
    
    public PeptideComparisonDataset getComparisonPeptides(int nrseqProteinId, List<Dataset> datasets) {
        
        Map<String, ComparisonPeptide> peptSeqMap = new HashMap<String, ComparisonPeptide>();
        
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(nrseqProteinId);
        
        for(Dataset dataset: datasets) {
            
            if(dataset.getSource() != DatasetSource.DTA_SELECT) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
                
                for(int piProteinId: piProteinIds) {
                    List<ProteinferPeptide> peptides = peptDao.loadPeptidesForProteinferProtein(piProteinId);
                    
                    for(ProteinferPeptide pept: peptides) {
                        ComparisonPeptide compPept = peptSeqMap.get(pept.getSequence());
                        if(compPept == null) {
                           compPept = new ComparisonPeptide(nrseqProteinId, pept.getSequence());
                           peptSeqMap.put(pept.getSequence(), compPept);
                        }
                        DatasetPeptideInformation dpi = new DatasetPeptideInformation(dataset);
                        dpi.setPresent(true);
                        dpi.setSpectrumCount(pept.getSpectrumCount());
                        dpi.setUnique(pept.isUniqueToProtein());
                        compPept.addDatasetInformation(dpi);
                    }
                }
            }
        }
        
        PeptideComparisonDataset pd = new PeptideComparisonDataset(nrseqProteinId);
        pd.setDatasets(datasets);
        pd.setPeptides(new ArrayList<ComparisonPeptide>(peptSeqMap.values()));
        return pd;
    }
}
