/**
 * DatasetBuilder.java
 * @author Vagisha Sharma
 * Apr 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;


/**
 * 
 */
public class DatasetBuilder {

    private static DatasetBuilder instance;
    
    private ProteinferDAOFactory fact;
    private ProteinferRunDAO runDao;
    private ProteinferSpectrumMatchDAO specDao;
    
    
    private DatasetBuilder() {
        fact = ProteinferDAOFactory.instance();
        runDao = fact.getProteinferRunDao();
        specDao = fact.getProteinferSpectrumMatchDao();
    }
    
    public static DatasetBuilder instance() {
        if(instance == null)
            instance = new DatasetBuilder();
        return instance;
    }
    
    public Dataset buildDataset(int datasetId) {
        
        ProteinferRun run = runDao.loadProteinferRun(datasetId);
        if(run == null)
            return null;
        DatasetSource source = DatasetSource.getSourceForProtinferProgram(run.getProgram());
        Dataset dataset = new Dataset(datasetId, source);
        dataset.setDatasetComments(run.getComments());
        initDataset(dataset);
        return dataset;
    }
    
    private void initDataset(Dataset dataset) {
        dataset.setSpectrumCount(specDao.getSpectrumCountForPinferRun(dataset.getDatasetId()));
        dataset.setMaxProteinSpectrumCount(specDao.getMaxSpectrumCountForPinferRunProtein(dataset.getDatasetId()));
        dataset.setMinProteinSpectrumCount(specDao.getMinSpectrumCountForPinferRunProtein(dataset.getDatasetId()));
    }
    
    public SelectableDataset buildSelectableDataset(int datasetId) {
        ProteinferRun run = runDao.loadProteinferRun(datasetId);
        if(run == null)
            return null;
        DatasetSource source = DatasetSource.getSourceForProtinferProgram(run.getProgram());
        SelectableDataset dataset = new SelectableDataset(datasetId, source);
        dataset.setSelected(false);
        dataset.setDatasetComments(run.getComments());
        return dataset;
    }
    
    public FilterableDataset buildFilterableDataset(int datasetId) {
        Dataset dataset = buildDataset(datasetId);
        if(dataset == null)
            return null;
        
        if(dataset.getSource() == DatasetSource.PROTINFER)
            return buildProtInferFilterableDataset(dataset);
        else if (dataset.getSource() == DatasetSource.PROTEIN_PROPHET)
            return buildProteinProphetFilterableDataset(dataset);
        return null;
    }

    private ProteinferDataset buildProtInferFilterableDataset(Dataset dataset) {
        ProteinferDataset prDataset = new ProteinferDataset(dataset);
        return prDataset;
    }
    
    private ProteinProphetDataset buildProteinProphetFilterableDataset(Dataset dataset) {
        ProteinProphetDataset prDataset = new ProteinProphetDataset(dataset);
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        prDataset.setRoc(fact.getProteinProphetRocDao().loadRoc(dataset.getDatasetId()));
        return prDataset;
    }
}
