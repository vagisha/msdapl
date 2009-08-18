/**
 * DatasetBuilder.java
 * @author Vagisha Sharma
 * Apr 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;


/**
 * 
 */
public class DatasetBuilder {

    private static DatasetBuilder instance;
    private DatasetBuilder() {}
    
    public static DatasetBuilder instance() {
        if(instance == null)
            instance = new DatasetBuilder();
        return instance;
    }
    
    public Dataset buildDataset(int datasetId, DatasetSource source) {
        if(source == DatasetSource.PROTINFER)
            return buildProtInferDataset(datasetId);
        else if (source == DatasetSource.PROTEIN_PROPHET)
            return buildProteinProphetDataset(datasetId);
        else if(source == DatasetSource.DTA_SELECT)
            return buildDtaSelectDatasource(datasetId);
        return null;
    }

    private Dataset buildDtaSelectDatasource(int datasetId) {
        return new Dataset(datasetId, DatasetSource.DTA_SELECT);
    }

    private Dataset buildProtInferDataset(int datasetId) {
        Dataset dataset = new Dataset(datasetId, DatasetSource.PROTINFER);
        initDataset(datasetId, dataset);
        return dataset;
    }
    
    private Dataset buildProteinProphetDataset(int datasetId) {
        Dataset dataset = new Dataset(datasetId, DatasetSource.PROTEIN_PROPHET);
        initDataset(datasetId, dataset);
        return dataset;
    }

    private void initDataset(int datasetId, Dataset dataset) {
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferSpectrumMatchDAO specDao = fact.getProteinferSpectrumMatchDao();
        dataset.setSpectrumCount(specDao.getSpectrumCountForPinferRun(datasetId));
        dataset.setMaxProteinSpectrumCount(specDao.getMaxSpectrumCountForPinferRunProtein(datasetId));
        dataset.setMinProteinSpectrumCount(specDao.getMinSpectrumCountForPinferRunProtein(datasetId));
        dataset.setDatasetComments(fact.getProteinferRunDao().loadProteinferRun(datasetId).getComments());
    }
}
