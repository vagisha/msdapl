/**
 * MsDigestionEnzymeDAOImpl.java
 * @author Vagisha Sharma
 * Jul 1, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dto.MsDigestionEnzyme;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsDigestionEnzymeDAOImpl extends BaseSqlMapDAO implements MsDigestionEnzymeDAO {

    public MsDigestionEnzymeDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsDigestionEnzyme> loadEnzymesForRun(int runId) {
        return queryForList("MsDigestionEnzyme.selectEnzymesForRun", runId);
    }

    //------------------------------------------------------------------------------------------------
    // LOAD methods
    //------------------------------------------------------------------------------------------------
    public MsDigestionEnzyme loadEnzyme(int enzymeId) {
        return (MsDigestionEnzyme) queryForObject("MsDigestionEnzyme.selectEnzymeById", enzymeId);
    }

    public List<MsDigestionEnzyme> loadEnzymes(String name) {
//        return (MsDigestionEnzyme) queryForObject("MsDigestionEnzyme.selectEnzymeByName", name);
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put("name", name);
        return loadEnzymes(properties);
    }

    public List<MsDigestionEnzyme> loadEnzymes(String name, int sense, String cut,
            String nocut) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("name", name);
        map.put("sense", sense);
        map.put("cut", cut);
        map.put("nocut", nocut);
        return loadEnzymes(map);
    }

    private List<MsDigestionEnzyme> loadEnzymes(Map<String, Object> properties) {
        if (properties == null || properties.size() == 0)
            return null;
        return queryForList("MsDigestionEnzyme.selectEnzymes", properties);
    }
    
    
    //------------------------------------------------------------------------------------------------
    // SAVE methods
    //------------------------------------------------------------------------------------------------
    public int saveEnzyme(MsDigestionEnzyme enzyme) {
        return saveEnzyme(enzyme, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzyme(MsDigestionEnzyme enzyme, List<EnzymeProperties> params) {
        
        // TODO if the enzyme given to us already has a database id
        // exucute an update.  If no database entry was found for the id
        // throw an exception
        
        Map<String, Object> properties = new HashMap<String, Object>(params.size());
        for (EnzymeProperties param: params) {
            if (param == EnzymeProperties.NAME)
                properties.put("name", enzyme.getName());
            else if (param == EnzymeProperties.SENSE)
                properties.put("sense", enzyme.getSense());
            else if (param == EnzymeProperties.CUT)
                properties.put("cut", enzyme.getCut());
            else if (param == EnzymeProperties.NOTCUT)
                properties.put("nocut", enzyme.getNocut());
        }
        
        List<MsDigestionEnzyme> enzymesFromDb = loadEnzymes(properties);
        // if we found an enzyme return its database id
        if (enzymesFromDb.size() > 0)   
            return enzymesFromDb.get(0).getId();
        
        // otherwise save the enzyme and returns its database id
        return saveAndReturnId("MsDigestionEnzyme.insert", enzyme);
    }
    
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId) {
        
        return saveEnzymeforRun(enzyme, runId, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId, List<EnzymeProperties> properties) {
        
        int enzymeId = saveEnzyme(enzyme, properties);
        
        // now save an entry in the msRunEnzyme table liking this enzyme to the given runId
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runID", runId);
        map.put("enzymeID", enzymeId);
        save("MsDigestionEnzyme.insertRunEnzyme", map);
        
        return enzymeId;
    }

    
    
    //------------------------------------------------------------------------------------------------
    // DELETE methods
    //------------------------------------------------------------------------------------------------
    public void deleteEnzymeById(int enzymeId) {
        delete("MsDigestionEnzyme.deleteEnzymeById", enzymeId);
    }

    public void deleteEnzymesByRunId(int runId) {
        delete("MsDigestionEnzyme.deleteEnzymesByRunId", runId);
    }

    public void deleteEnzymesByRunIds(List<Integer> runIds) {
        if (runIds == null || runIds.size() == 0) return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("runIdList", runIds);
        delete("MsDigestionEnzyme.deleteEnzymesByRunIds", map);
    }
}
