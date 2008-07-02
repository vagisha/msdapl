package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsDigestionEnzyme;

public interface MsDigestionEnzymeDAO {

    public MsDigestionEnzyme loadEnzyme(int enzymeId);
    
    public MsDigestionEnzyme loadEnzyme(String name);
    
    public MsDigestionEnzyme loadEnzyme(String name, int sense, String cut, String nocut);
    
    public int saveEnzyme(MsDigestionEnzyme enzyme);
    
    public List<MsDigestionEnzyme> loadEnzymesForRun(int runId);
    
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId);
    
    public boolean saveEnzymeForRun(String enzymeName, int runId);
    
    public void deleteEnzymeById(int enzymeId);
    
    public void deleteEnzymesByRunId(int runId);
    
    public void deleteEnzymesByRunIds(List<Integer> runIds);
}
