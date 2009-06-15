/**
 * DatasetBuilder.java
 * @author Vagisha Sharma
 * Apr 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;


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
        if(source == DatasetSource.PROT_INFER)
            return buildProtInferDataset(datasetId);
        else if(source == DatasetSource.DTA_SELECT)
            return buildDtaSelectDatasource(datasetId);
        return null;
    }

    private Dataset buildDtaSelectDatasource(int datasetId) {
        return new Dataset(datasetId, DatasetSource.DTA_SELECT);
    }

    private Dataset buildProtInferDataset(int datasetId) {
        Dataset dataset = new Dataset(datasetId, DatasetSource.PROT_INFER);
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferSpectrumMatchDAO specDao = fact.getProteinferSpectrumMatchDao();
        dataset.setSpectrumCount(specDao.getSpectrumCountForPinferRun(datasetId));
        dataset.setMaxProteinSpectrumCount(specDao.getMaxSpectrumCountForPinferRunProtein(datasetId));
        dataset.setMinProteinSpectrumCount(specDao.getMinSpectrumCountForPinferRunProtein(datasetId));
        
        return dataset;
    }
    
    
}
