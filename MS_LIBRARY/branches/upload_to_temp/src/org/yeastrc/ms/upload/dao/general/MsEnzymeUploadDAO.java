package org.yeastrc.ms.upload.dao.general;

import java.util.List;

import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

public interface MsEnzymeUploadDAO {

    //------------------------------------------------------------------------------------------------
    // SAVE, LOAD and DELETE enzymes (msDigestionEnzyme table)
    //------------------------------------------------------------------------------------------------
    public abstract MsEnzyme loadEnzyme(int enzymeId);

    public abstract List<MsEnzyme> loadEnzymes(String name);

    public abstract List<MsEnzyme> loadEnzymes(String name, Sense sense,
            String cut, String nocut);

    public abstract int saveEnzyme(MsEnzymeIn enzyme);

    public abstract int saveEnzyme(MsEnzymeIn enzyme,
            List<EnzymeProperties> params);

    public abstract void deleteEnzymeById(int enzymeId);

    //------------------------------------------------------------------------------------------------
    // Enzymes for a RUN
    //------------------------------------------------------------------------------------------------
    public abstract int saveEnzymeforRun(MsEnzymeIn enzyme, int runId);

    public abstract int saveEnzymeforRun(MsEnzymeIn enzyme, int runId,
            List<EnzymeProperties> properties);

    public abstract void saveEnzymeForRun(int enzymeId, int runId);

    //------------------------------------------------------------------------------------------------
    // Enzymes for a SEARCH
    //------------------------------------------------------------------------------------------------

//    public List<MsEnzyme> loadEnzymesForSearch(int searchId);
    
    public abstract int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId);

    public abstract int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId,
            List<EnzymeProperties> properties);

    public abstract void saveEnzymeForSearch(int enzymeId, int searchId);
    
//    public void deleteEnzymesForSearch(int searchId);
    
//    public void deleteEnzymesForSearches(List<Integer> searchIds);

}