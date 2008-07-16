/**
 * SQTDataUploadService.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.ibatis.MsSearchResultProteinDAOImpl.MsResultProteinSqlMapParam;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.MsSearchResultModification;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.MsSearchModification.ModificationType;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;

/**
 * 
 */
class SQTDataUploadService {

    private static final Logger log = Logger.getLogger(SQTDataUploadService.class);
    
    private static final DAOFactory daoFactory = DAOFactory.instance();
    
    private static final DynamicModLookupUtil dynaModLookup = DynamicModLookupUtil.instance();
    
    public static final int BUF_SIZE = 1000;
    
    // these are the things we will cache and do bulk-inserts
    List<MsSearchResultProteinDb> proteinMatchList; // protein matches
    
    
    public SQTDataUploadService() {
        proteinMatchList = new ArrayList<MsSearchResultProteinDb>();
    }
    
    public int uploadSearch(SQTSearch search, int runId) {
        
        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup.reset();
        
        // clean up any cached data
        proteinMatchList.clear();
        
        // save the search and return the database id
        MsSearchDAO<SQTSearch, SQTSearchDb> searchDao = daoFactory.getSqtSearchDAO();
        return searchDao.saveSearch(search, runId);
    }
    
    public void uploadSearchScan(SQTSearchScan scan, int searchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, searchId, scanId);
    }
    
    public int uploadSearchResult(SQTSearchResult result, int searchId, int scanId) {
        
        try {
            result.getResultPeptide(); // parse the peptide sequence to get the pre, post residues, modifications etc.
        }
        catch(IllegalArgumentException e) {
            log.error(("!!!Peptide sequence appears to be invalid. Unlable to upload result... Skipping: "+e.getMessage()));
            return 0;
        }
        
        MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> resultDao = DAOFactory.instance().getSqtResultDAO();
        int resultId = resultDao.saveResultOnly(result, searchId, scanId);
        
        // upload dynamic mods for this result
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        for (MsSearchResultModification mod: result.getResultPeptide().getDynamicModifications()) {
            if (mod == null || mod.getModificationType() == ModificationType.STATIC)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass());
            modDao.saveDynamicModificationForSearchResult(mod, resultId, modId);
        }
        
        
        // upload the protein matches
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            proteinMatchList.add(new MsResultProteinSqlMapParam(resultId, match.getAccession(), match.getDescription()));
        }
        return resultId;
    }

    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        matchDao.saveAll(proteinMatchList);
        proteinMatchList.clear();
    }
    
    public void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
    }
}
