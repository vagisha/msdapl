/**
 * MsDigestionEnzymeDAOImpl.java
 * @author Vagisha Sharma
 * Jul 1, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

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

    public MsDigestionEnzyme loadEnzyme(int enzymeId) {
        return (MsDigestionEnzyme) queryForObject("MsDigestionEnzyme.selectEnzymeById", enzymeId);
    }

    public MsDigestionEnzyme loadEnzyme(String name) {
        return (MsDigestionEnzyme) queryForObject("MsDigestionEnzyme.selectEnzymeByName", name);
    }

    public MsDigestionEnzyme loadEnzyme(String name, int sense, String cut,
            String nocut) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("name", name);
        map.put("sense", sense);
        map.put("cut", cut);
        map.put("nocut", nocut);
        return (MsDigestionEnzyme) queryForObject("MsDigestionEnzyme.selectEnzyme", map);
    }

    public int saveEnzyme(MsDigestionEnzyme enzyme) {
        return saveAndReturnId("MsDigestionEnzyme.insert", enzyme);
    }
    
    public int saveEnzymeforRun(MsDigestionEnzyme enzyme, int runId) {
        
        // check if the enzyme already exists in the database
        MsDigestionEnzyme enzymeFromDb = loadEnzyme(enzyme.getName(), enzyme.getSense(), enzyme.getCut(), enzyme.getNocut());
        
        int enzymeId = 0;
        if (enzymeFromDb == null)
            // if the enzyme does not exist in the database save it first
            enzymeId = saveEnzyme(enzyme);
        else 
            enzymeId = enzymeFromDb.getId();
        
        // now save an entry in the msRunEnzyme table liking this enzyme to the given runId
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runID", runId);
        map.put("enzymeID", enzymeId);
        save("MsDigestionEnzyme.insertRunEnzyme", map);
        
        return enzymeId;
    }

    /**
     * First checks if an enzyme by the given name exists in the database. 
     * If it does, it saves an entry on the msRunEnzyme table linking the enzyme and the run
     * 
     * @return true if the an entry was saved in the msRunEnzyme table. This method returns false
     * if no enzyme by the given name exists in the database.
     */
    public boolean saveEnzymeForRun(String enzymeName, int runId) {
        
        // check if the enzyme by the given name already exists in the database
        MsDigestionEnzyme enzymeFromDb = loadEnzyme(enzymeName);
        
        if (enzymeFromDb == null)   return false;
        
        int enzymeId = enzymeFromDb.getId();
        
        // now save an entry in the msRunEnzyme table liking this enzyme to the given runId
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runID", runId);
        map.put("enzymeID", enzymeId);
        save("MsDigestionEnzyme.insertRunEnzyme", map);
        
        return true;
    }

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
