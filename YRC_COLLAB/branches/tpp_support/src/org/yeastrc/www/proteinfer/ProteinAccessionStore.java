/**
 * ProteinAccesionStore.java
 * @author Vagisha Sharma
 * Aug 28, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class ProteinAccessionStore {

    private LinkedHashMap<Integer, Map<Integer, String>> store;
    private final int size = 3;
    private final ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    
    private final Logger log = Logger.getLogger(ProteinAccessionStore.class.getName());
    
    private static ProteinAccessionStore instance = null;
    
    private ProteinAccessionStore() {
        int capacity = (int)Math.ceil(size/0.75f) + 1;
        store = new LinkedHashMap<Integer, Map<Integer,String>>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry (Map.Entry<Integer, Map<Integer, String>> eldest) {
                // This method is invoked by put and putAll after inserting a new entry into the map.
                return store.size() > size;  
            }
        };
    }
    
    public static ProteinAccessionStore getInstance() {
        if(instance == null)
            instance = new ProteinAccessionStore();
        return instance;
    }
    
    public Map<Integer, String> getAccessionMapForProteinInference(int pinferId) {
        return getAccessionMapForProteinInference(pinferId, true);
    }
    
    public Map<Integer, String> getAccessionMapForProteinInference(int pinferId, boolean createNew) {
        Map<Integer, String> map = store.get(pinferId);
        if(map == null) {
            if(createNew) {
                map = buildMap(pinferId);
                store.put(pinferId, map);
            }
        }
        return map;
    }

    private Map<Integer, String> buildMap(int pinferId) {
        
        Map<Integer, String> map;
        log.info("Building Protein Accession map");
        long s = System.currentTimeMillis();
        List<ProteinferProtein> proteins = protDao.loadProteins(pinferId);
        map = new HashMap<Integer, String>((int) (proteins.size() * 1.5));
        long e = System.currentTimeMillis();
        log.info("Time to get all proteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        s = System.currentTimeMillis();
        
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        for(ProteinferProtein protein: proteins) {
        	List<NrDbProtein> matchingProteins = NrSeqLookupUtil.getDbProteins(protein.getNrseqProteinId(), dbIds);
        	Set<String> accessions = new HashSet<String>();
        	for(NrDbProtein prot: matchingProteins)
        		accessions.add(prot.getAccessionString());
        	
        	ArrayList<String> accList = new ArrayList<String>(accessions);
        	Collections.sort(accList);
            map.put(protein.getId(), StringUtils.makeCommaSeparated(accList));
        }
        e = System.currentTimeMillis();
        log.info("Time to assign protein accessions: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return map;
    }
}
