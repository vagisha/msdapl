/**
 * ProteinProphetRunFormBean.java
 * @author Vagisha Sharma
 * Sep 8, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;

/**
 * 
 */
public class ProteinProphetRunFormBean extends ProteinferRunFormBean {

    private ProteinProphetROC roc;
    private double errorRate;
    private double probability;
    
    
    public ProteinProphetRunFormBean() {
        super();
    }
    
    public ProteinProphetRunFormBean(ProteinferRun run, int projectId) {
        this(projectId, run.getId(), run.getDate(), run.getComments(), run.getProgram());
    }
    
    protected ProteinProphetRunFormBean(int projectId, int runId, Date runDate, String comments, ProteinInferenceProgram program) {
        this(projectId, runId, (java.util.Date)runDate, comments, program);
    }
    
    protected ProteinProphetRunFormBean(int projectId, int runId, java.util.Date runDate, String comments, ProteinInferenceProgram program) {
        super(projectId, runId, runDate, comments, program);
    }
    
    public ProteinProphetRunFormBean(ProteinferRun run, List<Integer> projectIds) {
        this(projectIds, run.getId(), run.getDate(), run.getComments(), run.getProgram());
    }
    
    protected ProteinProphetRunFormBean(List<Integer> projectIds, int runId, Date runDate, String comments, ProteinInferenceProgram program) {
        this(projectIds, runId, (java.util.Date)runDate, comments, program);
    }
    
    protected ProteinProphetRunFormBean(List<Integer> projectIds, int runId, java.util.Date runDate, String comments, ProteinInferenceProgram program) {
        super(projectIds, runId, runDate, comments, program);
    }
    
    public ProteinProphetROC getRoc() {
        return roc;
    }
    public void setRoc(ProteinProphetROC roc) {
        this.roc = roc;
    }
    public double getErrorRate() {
        return errorRate;
    }
    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }
    public double getProbability() {
        return probability;
    }
    public void setProbability(double probability) {
        this.probability = probability;
    }
    
}
