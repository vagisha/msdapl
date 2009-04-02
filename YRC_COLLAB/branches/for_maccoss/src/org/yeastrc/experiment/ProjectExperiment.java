/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.yates.YatesRun;


/**
 * 
 */
public class ProjectExperiment implements MsExperiment, Comparable<ProjectExperiment> {

    private final MsExperiment experiment;
    private List<ExperimentSearch> searches;
    private List<SearchAnalysis> analyses;
    private YatesRun dtaSelect;
    
    public ProjectExperiment(MsExperiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public int getId() {
        return experiment.getId();
    }

    @Override
    public Timestamp getLastUpdateDate() {
        return experiment.getLastUpdateDate();
    }

    @Override
    public String getServerAddress() {
        return experiment.getServerAddress();
    }

    @Override
    public String getServerDirectory() {
        return experiment.getServerDirectory();
    }

    public List<ExperimentSearch> getSearches() {
        return searches;
    }

    public void setSearches(List<ExperimentSearch> searches) {
        this.searches = searches;
    }

    public List<SearchAnalysis> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(List<SearchAnalysis> analyses) {
        this.analyses = analyses;
    }

    public YatesRun getDtaSelect() {
        return dtaSelect;
    }

    public void setDtaSelect(YatesRun dtaSelect) {
        this.dtaSelect = dtaSelect;
    }

    @Override
    public Date getUploadDate() {
        return experiment.getUploadDate();
    }

    @Override
    public int compareTo(ProjectExperiment o) {
        if(o == null)
            return -1;
        return Integer.valueOf(experiment.getId()).compareTo(o.getId());
    }
    
}
