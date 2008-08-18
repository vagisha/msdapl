package org.yeastrc.ms.dao.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;

public class DynamicModLookupUtil {

    private static int currentSearchId = 0;
    private static DynamicModLookupUtil instance = new DynamicModLookupUtil();
    
    private MsSearchModificationDAO modDao;
    
    private static Map<String, Integer> modMap;
    
    private DynamicModLookupUtil(){
        modDao = DAOFactory.instance().getMsSearchModDAO();
        modMap = new HashMap<String, Integer>();
    }

    public static DynamicModLookupUtil instance() {
        return instance;
    }
    
    public void reset() {
        modMap.clear();
        currentSearchId = 0;
    }
    
    private synchronized void buildLookup(int searchId) {
        
        if (currentSearchId == searchId)    return;
        
        currentSearchId = searchId;
        modMap.clear();
        List<MsSearchModificationDb> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);
        String key = null;
        for (MsSearchModificationDb mod: dynaMods) {
            key = mod.getModifiedResidue()+""+mod.getModificationMass().doubleValue();
            modMap.put(key, mod.getId());
        }
    }
    
    /**
     * 
     * @param searchId
     * @param modChar
     * @param modMass
     * @return
     * @throws RuntimeException if no matching modification was found
     */
    public synchronized int getDynamicModificationId(int searchId, char modChar, BigDecimal modMass) {
        buildLookup(searchId);
        Integer modId = modMap.get(modChar+""+modMass.doubleValue());
        if (modId != null)  return modId;
        throw new RuntimeException("No matching modification found for: searchId: "+
                searchId+"; modChar: "+
                modChar+"; modMass: "+modMass.doubleValue());
    }
    
}
