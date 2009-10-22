/**
 * XtandemSearchUploadDAOImpl.java
 * @author Vagisha Sharma
 * Oct 21, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.xtandem.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearch;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchUploadDAO;

/**
 * Deals with the tables:
 * 1. XtandemParams
 * 2. msSearch (only for the loadSearch method)
 */
public class XtandemSearchUploadDAOImpl implements XtandemSearchUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(XtandemSearchUploadDAOImpl.class.getName());
    
    private final XtandemSearchUploadDAO searchDao;
    private final boolean useTempTable;
    
    
    /**
     * @param searchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public XtandemSearchUploadDAOImpl(XtandemSearchUploadDAO searchDao, boolean useTempTable) {
        this.searchDao = searchDao;
        this.useTempTable = useTempTable;
    }
    
    public XtandemSearch loadSearch(int searchId) {
        return searchDao.loadSearch(searchId);
    }
    
    public int saveSearch(XtandemSearchIn search, int experimentId, int sequenceDatabaseId) {
       return searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
    }
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        return searchDao.getSearchIdsForExperiment(experimentId);
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchProgramVersion(searchId, versionStr);
    }
    
    @Override
    public int updateSearchProgram(int searchId, Program program) {
        return searchDao.updateSearchProgram(searchId, program);
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableDirect("XtandemParams"); 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("XtandemParams", "id"))
            return false;
        return true;
    }
}
