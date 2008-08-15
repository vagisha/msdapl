package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsSearchDb;

public interface MsExperimentDAO {

    public abstract MsSearchDb load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract int save(MsExperiment experiment);
    
    public abstract void delete(int msExperimentId);
}