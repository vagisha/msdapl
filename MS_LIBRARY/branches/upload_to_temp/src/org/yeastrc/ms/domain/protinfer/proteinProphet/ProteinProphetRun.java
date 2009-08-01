/**
 * ProteinProphetRun.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;

/**
 * 
 */
public class ProteinProphetRun extends GenericProteinferRun<ProteinferInput> {

    private List<ProteinProphetParam> params;
    private ProteinProphetROC roc;
    
    public ProteinProphetRun() {
        params = new ArrayList<ProteinProphetParam>();
    }

    public List<ProteinProphetParam> getParams() {
        return params;
    }

    public void setParams(List<ProteinProphetParam> params) {
        this.params = params;
    }

    public ProteinProphetROC getRoc() {
        return roc;
    }

    public void setRoc(ProteinProphetROC roc) {
        this.roc = roc;
    }
    
}
