/**
 * DatasetBuilder.java
 * @author Vagisha Sharma
 * Apr 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.List;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;


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
        IdPickerRunDAO idpRunDao = fact.getIdPickerRunDao();
        IdPickerRun idpRun = idpRunDao.loadProteinferRun(datasetId);
        
        // Get the total number of hits in this dataset
//        List<IdPickerInput> inputs = idpRun.getInputList();
//        int totalHitCount = 0;
//        for(IdPickerInput input: inputs) {
//            totalHitCount += input.getNumFilteredTargetHits();
//        }
//        dataset.setSpectrumCount(totalHitCount);
        
        
        // Get the maximum number of hits for a protein in this dataset
        
        return dataset;
    }
    
    
}
