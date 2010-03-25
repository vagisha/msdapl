/**
 * ProteinPropertiesStore.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

/**
 * 
 */
public class ProteinPropertiesStore {

    // This maps protein inference run IDs to a map of its protein IDs (protein inference IDs) and protein properties
    private LinkedHashMap<Integer, Map<Integer, ProteinProperties>> store;
    private final int size = 3;
    
    private static ProteinPropertiesStore instance = null;
    
    private ProteinPropertiesStore() {
        int capacity = (int)Math.ceil(size/0.75f) + 1;
        store = new LinkedHashMap<Integer, Map<Integer,ProteinProperties>>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry (Map.Entry<Integer, Map<Integer, ProteinProperties>> eldest) {
                // This method is invoked by put and putAll after inserting a new entry into the map.
                return store.size() > size;  
            }
        };
    }
    
    public static synchronized ProteinPropertiesStore getInstance() {
        if(instance == null)
            instance = new ProteinPropertiesStore();
        return instance;
    }
    
    public synchronized Map<Integer, ProteinProperties> getPropertiesMapForAccession(int pinferId) {
        return getPropertiesMapForAccession(pinferId, true);
    }
    
    public synchronized Map<Integer, ProteinProperties> getPropertiesMapForPi(int pinferId) {
        return getPropertiesMapForPi(pinferId, true);
    }
    
    public synchronized Map<Integer, ProteinProperties> getPropertiesMapForMolecularWt(int pinferId) {
        return getPropertiesMapForMolWt(pinferId, true);
    }
    
    synchronized Map<Integer, ProteinProperties> getPropertiesMapForAccession(int pinferId, boolean createNew) {
    	
        Map<Integer, ProteinProperties> map = store.get(pinferId);
        if(map == null) {
            if(createNew) {
            	ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
            	builder.setGetAccession(true);
                map = builder.buildMap(pinferId);
                store.put(pinferId, map);
            }
        }
        else {
        	// we have a map; make sure the right information is available
        	// check one of the entries in the map 
        	for(Integer piProteinId: map.keySet()) {
        		ProteinProperties props = map.get(piProteinId);
        		if(!props.accessionInitialized()) {
        			
        			// If we are not told to create a new map return null
        			if(!createNew)
        				return null;
        		
        			ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
        			builder.setGetAccession(true);
        			builder.updateMap(pinferId, map);
        		}
        		break;
        	}
        }
        return map;
    }

    private Map<Integer, ProteinProperties> getPropertiesMapForPi(int pinferId, boolean createNew) {
    	
        Map<Integer, ProteinProperties> map = store.get(pinferId);
        if(map == null) {
            if(createNew) {
            	ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
            	builder.setGetPi(true);
                map = builder.buildMap(pinferId);
                store.put(pinferId, map);
            }
        }
        else {
        	// we have a map; make sure the right information is available
        	// check one of the entries in the map 
        	for(Integer piProteinId: map.keySet()) {
        		ProteinProperties props = map.get(piProteinId);
        		if(!props.piInitialized()) {
        			
        			// If we are not told to create a new map return null
        			if(!createNew)
        				return null;
        			
        			ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
        			builder.setGetPi(true);
        			builder.updateMap(pinferId, map);
        		}
        		break;
        	}
        	
        }
        return map;
    }
    
    private Map<Integer, ProteinProperties> getPropertiesMapForMolWt(int pinferId, boolean createNew) {
    	
    	Map<Integer, ProteinProperties> map = store.get(pinferId);
    	if(map == null) {
    		if(createNew) {
    			ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
    			builder.setGetMolWt(true);
    			map = builder.buildMap(pinferId);
    			store.put(pinferId, map);
    		}
    	}
    	else {
    		// we have a map; make sure the right information is available
    		// check one of the entries in the map 
    		for(Integer piProteinId: map.keySet()) {
    			ProteinProperties props = map.get(piProteinId);
    			if(!props.molecularWtInitialized()) {

    				// If we are not told to create a new map return null
    				if(!createNew)
    					return null;

    				ProteinPropertiesMapBuilder builder = new ProteinPropertiesMapBuilder();
    				builder.setGetMolWt(true);
    				builder.updateMap(pinferId, map);
    			}
    			break;
    		}

    	}
    	return map;
    }
    
    public synchronized ProteinProperties getProteinMolecularWtPi(int pinferId, GenericProteinferProtein<?> protein) {
    	
        Map<Integer, ProteinProperties> map = this.store.get(pinferId);
        
        ProteinPropertiesBuilder builder = new ProteinPropertiesBuilder();
        builder.setGetMolWt(true);
        builder.setGetPi(true);
        if(map == null) {
            return builder.build(pinferId, protein);
        }
        else {
            ProteinProperties props = map.get(protein.getId());
            if(!props.molecularWtInitialized() || !props.piInitialized()) {
            	builder.update(props);
            }
            return props;
        }
    }
}
