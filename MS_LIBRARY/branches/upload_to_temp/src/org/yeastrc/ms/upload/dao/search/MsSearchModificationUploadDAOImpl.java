/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msSearchStaticMod
 * 2. msSearchTerminalStaticMod
 * 3. msSearchDynamicMod
 * 4. msSearchTerminalDynamicMod
 * 5. msDynamicModResult
 * 6. msTerminalDynamicModResult
 */
public class MsSearchModificationUploadDAOImpl extends AbstractTableCopier implements MsSearchModificationDAO {

    private final MsSearchModificationDAO modDao;
    private final MsSearchModificationDAO mainModDao;
    private final boolean useTempTable;
    
    /**
     * @param mainModDao -- DAO for the MAIN database table
     * @param modDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchModificationUploadDAOImpl(MsSearchModificationDAO mainModDao, MsSearchModificationDAO modDao,
            boolean useTempTable) {
        this.mainModDao = mainModDao;
        if(modDao == null)
            this.modDao = mainModDao;
        else
            this.modDao = modDao;
        this.useTempTable = useTempTable;
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsResidueModification> loadStaticResidueModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }

    public void saveStaticResidueMod(MsResidueModification mod) {
        modDao.saveStaticResidueMod(mod);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteStaticResidueModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId) {
        List<MsResidueModification> mods = modDao.loadDynamicResidueModsForSearch(searchId);
        if(mods == null || mods.size() == 0) {
            if(useTempTable) {
                // Look in the main table if nothing was found in the temp table
                mods = mainModDao.loadDynamicResidueModsForSearch(searchId);
            }
        }
        return mods;
    }

    public int saveDynamicResidueMod(MsResidueModification mod) {
        return modDao.saveDynamicResidueMod(mod);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteDynamicResidueModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsTerminalModification> loadStaticTerminalModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }

    public void saveStaticTerminalMod(MsTerminalModification mod) {
        modDao.saveStaticTerminalMod(mod);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteStaticTerminalModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId) {
        List<MsTerminalModification> mods = modDao.loadDynamicTerminalModsForSearch(searchId);
        if(mods == null || mods.size() == 0) {
            if(useTempTable) {
                // Look in the main table if nothing was found in the temp table
                mods = mainModDao.loadDynamicTerminalModsForSearch(searchId);
            }
        }
        return mods;
    }

    public  int saveDynamicTerminalMod(MsTerminalModification mod) {
        return modDao.saveDynamicTerminalMod(mod);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public  void deleteDynamicTerminalModsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsResultResidueMod> loadDynamicResidueModsForResult(
            int resultId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int loadMatchingDynamicResidueModId(MsResidueModification mod) {
        return modDao.loadMatchingDynamicResidueModId(mod);
    }
    
    public void saveDynamicResidueModForResult(int resultId,
            int modificationId, int modifiedPosition) {
        modDao.saveDynamicResidueModForResult(resultId, modificationId, modifiedPosition);
    }
    
    public void saveDynamicResidueModForResult(MsResultResidueModIds modIdentifier) {
        modDao.saveDynamicResidueModForResult(modIdentifier);
    }
    
    public void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList) {
        modDao.saveAllDynamicResidueModsForResult(modList);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteDynamicResidueModsForResult(int resultId) {
        throw new UnsupportedOperationException();
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsResultTerminalMod> loadDynamicTerminalModsForResult(
            int resultId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int loadMatchingDynamicTerminalModId(
            MsTerminalModification mod) {
       return modDao.loadMatchingDynamicTerminalModId(mod);
    }
    
    public void saveDynamicTerminalModForResult(int resultId, int modificationId) {
        modDao.saveDynamicTerminalModForResult(resultId, modificationId);
    }
    
    public void saveDynamicTerminalModForResult(MsResultTerminalModIds modIdentifier) {
        modDao.saveDynamicTerminalModForResult(modIdentifier);
    }
    
    public void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList) {
       modDao.saveAllDynamicTerminalModsForResult(modList);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteDynamicTerminalModsForResult(int resultId) {
        throw new UnsupportedOperationException();
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableDirect("msSearchStaticMod"); // static mods at the search level
            copyToMainTableDirect("msSearchTerminalStaticMod"); // terminal static mods at the search level
            copyToMainTableDirect("msSearchDynamicMod"); // dynamic mods at the search level
            copyToMainTableDirect("msSearchTerminalDynamicMod"); // terminal dynamic mods at the search level
            
            copyToMainTableFromFile("msDynamicModResult");          // dynamic mods at the result level
            copyToMainTableFromFile("msTerminalDynamicModResult");  // terminal dynamic mods at the result level
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }

}
