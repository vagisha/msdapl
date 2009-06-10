/**
 * MsDigestionEnzymeDAOImpl.java
 * @author Vagisha Sharma
 * Jul 1, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.general.dualdb;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.general.MsEnzymeUploadDAO;

/**
 * Deals with the tables: 
 * 1. msSequenceDatabaseDetail
 * 2. msRunEnzyme
 * 3. msSearchEnzyme
 */
public class MsEnzymeUploadDAOImpl implements MsEnzymeUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsEnzymeUploadDAOImpl.class.getName());
    
    private final MsEnzymeUploadDAO mainEnzymeDao;
    private final MsEnzymeUploadDAO enzymeDao;
    private final boolean useTempTable;
    
    /**
     * @param mainEnzymeDao -- DAO for the MAIN database table
     * @param enzymeDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsEnzymeUploadDAOImpl(MsEnzymeUploadDAO mainEnzymeDao, MsEnzymeUploadDAO enzymeDao, boolean useTempTable) {
        this.mainEnzymeDao = mainEnzymeDao;
        if(enzymeDao == null)
            this.enzymeDao = mainEnzymeDao;
        else
            this.enzymeDao = enzymeDao;
        this.useTempTable = useTempTable;
    }

    //------------------------------------------------------------------------------------------------
    // SAVE, LOAD and DELETE enzymes (msDigestionEnzyme table)
    //------------------------------------------------------------------------------------------------
    public MsEnzyme loadEnzyme(int enzymeId) {
        return mainEnzymeDao.loadEnzyme(enzymeId);
    }

    public List<MsEnzyme> loadEnzymes(String name) {
        return mainEnzymeDao.loadEnzymes(name);
    }

    public List<MsEnzyme> loadEnzymes(String name, Sense sense, String cut,
            String nocut) {
       return mainEnzymeDao.loadEnzymes(name, sense, cut, nocut);
    }

    public int saveEnzyme(MsEnzymeIn enzyme) {
        return saveEnzyme(enzyme, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzyme(MsEnzymeIn enzyme, List<EnzymeProperties> params) {
        return mainEnzymeDao.saveEnzyme(enzyme, params);
    }
    
    public void deleteEnzymeById(int enzymeId) {
        mainEnzymeDao.deleteEnzymeById(enzymeId);
    }
    
    //------------------------------------------------------------------------------------------------
    // Enzymes for a RUN
    //------------------------------------------------------------------------------------------------

    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId) {
        return saveEnzymeforRun(enzyme, runId, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId, List<EnzymeProperties> properties) {
        int enzymeId = saveEnzyme(enzyme, properties);
        saveEnzymeForRun(enzymeId, runId);
        return enzymeId;
    }

    public void saveEnzymeForRun(int enzymeId, int runId) {
       enzymeDao.saveEnzymeForRun(enzymeId, runId);
    }
    
    
    //------------------------------------------------------------------------------------------------
    // Enzymes for a SEARCH
    //------------------------------------------------------------------------------------------------
    
    @Override
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId) {
        return saveEnzymeforSearch(enzyme, searchId, Arrays.asList(EnzymeProperties.values()));
    }

    @Override
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId,
            List<EnzymeProperties> properties) {
        
        int enzymeId = saveEnzyme(enzyme, properties); // uses the main table
        saveEnzymeForSearch(enzymeId, searchId);
        return enzymeId;
    }
    
    public void saveEnzymeForSearch(int enzymeId, int searchId) {
       enzymeDao.saveEnzymeForSearch(enzymeId, searchId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the msSearchEnzyme table
            copier.copyToMainTableDirect("msSearchEnzyme");
            // copy entries from the msRunEnzyme table
            copier.copyToMainTableDirect("msRunEnzyme");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
