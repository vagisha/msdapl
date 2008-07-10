package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsDigestionEnzyme;

public interface MsDigestionEnzymeDAO {

    
    public enum EnzymeProperties {NAME, SENSE, CUT, NOTCUT};
    
    
    public MsDigestionEnzyme loadEnzyme(int enzymeId);
    
    public List<MsDigestionEnzyme> loadEnzymes(String name);
    
    public List<MsDigestionEnzyme> loadEnzymes(String name, int sense, String cut, String nocut);
    
    public List<MsDigestionEnzyme> loadEnzymesForRun(int runId);
    
    
    /**
     * Saves the enzyme if it does not already exist in the database. 
     * All enzyme properties (name, sense, cut, nocut) are used to to look for a matching enzyme
     * before saving
     * @param enzyme
     * @return
     */
    public int saveEnzyme(MsDigestionEnzyme enzyme);
    
    /**
     * Saves the enzyme if it does not already exist in the database.
     * @param enzyme
     * @param properties -- list of properties used to look for a matching enzyme before saving
     * @return
     */
    public int saveEnzyme(MsDigestionEnzyme enzyme, List<EnzymeProperties> properties);
    
    
    /**
     * Saves an entry in msRunEnzyme linking the enzyme with the runId.
     * If enzyme does not exist in the database it is saved
     * All enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the run is linked to the first enzyme
     * @param enzyme
     * @param runId
     * @return
     */
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId);
    
    /**
     * Saves an entry in msRunEnzyme linking the enzyme with the runId.
     * If enzyme does not exist in the database it is saved
     * The given enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the run is linked to the first enzyme
     * @param enzyme
     * @param runId
     * @param properties
     * @return
     */
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId, List<EnzymeProperties> properties);
    
    
    
    public void deleteEnzymeById(int enzymeId);
    
    public void deleteEnzymesByRunId(int runId);
    
    public void deleteEnzymesByRunIds(List<Integer> runIds);
}
