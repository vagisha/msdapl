/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.MsSearchModificationUploadDAO;

/**
 * Deals with the tables:
 * 1. msSearchStaticMod
 * 2. msSearchTerminalStaticMod
 * 3. msSearchDynamicMod
 * 4. msSearchTerminalDynamicMod
 * 5. msDynamicModResult
 * 6. msTerminalDynamicModResult
 */
public class MsSearchModificationUploadDAOImpl implements MsSearchModificationUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsSearchModificationUploadDAOImpl.class.getName());
    
    private final MsSearchModificationUploadDAO modDao;
    private final MsSearchModificationUploadDAO mainModDao;
    private final boolean useTempTable;
    
    /**
     * @param mainModDao -- DAO for the MAIN database table
     * @param modDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchModificationUploadDAOImpl(MsSearchModificationUploadDAO mainModDao, MsSearchModificationUploadDAO modDao,
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
    public void saveStaticResidueMod(MsResidueModification mod) {
        modDao.saveStaticResidueMod(mod);
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

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public void saveStaticTerminalMod(MsTerminalModification mod) {
        modDao.saveStaticTerminalMod(mod);
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

    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList) {
        modDao.saveAllDynamicResidueModsForResult(modList);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    public void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList) {
       modDao.saveAllDynamicTerminalModsForResult(modList);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableDirect("msSearchStaticMod"); // static mods at the search level
            copier.copyToMainTableDirect("msSearchTerminalStaticMod"); // terminal static mods at the search level
            copier.copyToMainTableDirect("msSearchDynamicMod"); // dynamic mods at the search level
            copier.copyToMainTableDirect("msSearchTerminalDynamicMod"); // terminal dynamic mods at the search level
            
            copier.copyToMainTableFromFile("msDynamicModResult");          // dynamic mods at the result level
            copier.copyToMainTableFromFile("msTerminalDynamicModResult");  // terminal dynamic mods at the result level
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }

}
