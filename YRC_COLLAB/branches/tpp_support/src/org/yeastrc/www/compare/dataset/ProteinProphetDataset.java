/**
 * ProteinProphetDataset.java
 * @author Vagisha Sharma
 * Nov 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;

/**
 * 
 */
public class ProteinProphetDataset extends FilterableDataset {

    private ProteinProphetFilterCriteria filterCriteria;
    private ProteinProphetROC roc;
    
    public ProteinProphetDataset() {}
    
    public ProteinProphetDataset(Dataset dataset) {
        super(dataset);
        this.filterCriteria = new ProteinProphetFilterCriteria();
        filterCriteria.setMinProbability(0.9);
        filterCriteria.setMaxProbability(1.0);
    }

    @Override
    public ProteinProphetFilterCriteria getProteinFilterCrteria() {
        return filterCriteria;
    }

    public void setProteinFilterCriteria(ProteinProphetFilterCriteria filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
    
    public ProteinProphetROC getRoc() {
        return roc;
    }
    
    public void setRoc(ProteinProphetROC roc) {
        this.roc = roc;
    }
    
    public double getProbabilityForDefaultError() {
        return roc.getMinProbabilityForError(0.01);
    }
}
