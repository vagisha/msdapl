/**
 * SQTDataUploadService.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.ibatis.MsSearchResultProteinDAOImpl.MsResultProteinSqlMapParam;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.MsSearchResultModification;
import org.yeastrc.ms.domain.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.ValidationStatus;
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
    List<SQTSearchResultDb> sqtResultList; 
    
    
    public SQTDataUploadService() {
        proteinMatchList = new ArrayList<MsSearchResultProteinDb>();
        sqtResultList = new ArrayList<SQTSearchResultDb>();
    }
    
    public int uploadSearch(SQTSearch search, int runId) {
        
        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup.reset();
        
        // clean up any cached data
        proteinMatchList.clear();
        sqtResultList.clear();
        
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
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
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
        uploadProteinMatches(result, resultId);
        // upload the SQT file specific information for this result.
        uploadSQTResult(result, resultId);
        
        return resultId;
    }

    private void uploadProteinMatches(SQTSearchResult result, int resultId) {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            proteinMatchList.add(new MsResultProteinSqlMapParam(resultId, match.getAccession(), match.getDescription()));
        }
    }

    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        matchDao.saveAll(proteinMatchList);
        proteinMatchList.clear();
    }
    
    private void uploadSQTResult(SQTSearchResult result, int resultId) {
        // upoad the SQT file specific result information if the cache has enough entries
        if (sqtResultList.size() >= BUF_SIZE) {
            uploadSqtResultBuffer();
        }
        // add the SQT file specific information for this result to the cache
        SQTSearchResultLite sqtResultOnly = new SQTSearchResultLite();
        sqtResultOnly.resultId = resultId;
        sqtResultOnly.xcorrRank = result.getxCorrRank();
        sqtResultOnly.spRank = result.getSpRank();
        sqtResultOnly.deltaCN = result.getDeltaCN();
        sqtResultOnly.xcorr = result.getxCorr();
        sqtResultOnly.sp = result.getSp();
        sqtResultList.add(sqtResultOnly);
    }
    
    private void uploadSqtResultBuffer() {
        SQTSearchResultDAO sqtResultDao = daoFactory.getSqtResultDAO();
        sqtResultDao.saveAllSqtResultOnly(sqtResultList);
        sqtResultList.clear();
    }
    
    public void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (sqtResultList.size() > 0) {
            uploadSqtResultBuffer();
        }
    }
    
    private static final class SQTSearchResultLite implements SQTSearchResultDb{

        int resultId;
        int xcorrRank;
        int spRank;
        BigDecimal deltaCN;
        BigDecimal xcorr;
        BigDecimal sp;
        
        public int getId() {
            return resultId;
        }

        @Override
        public BigDecimal getDeltaCN() {
            return deltaCN;
        }

        @Override
        public BigDecimal getSp() {
            return sp;
        }

        @Override
        public int getSpRank() {
            return spRank;
        }

        @Override
        public BigDecimal getxCorr() {
            return xcorr;
        }

        @Override
        public int getxCorrRank() {
            return xcorrRank;
        }
        
        @Override
        public int getSearchId() {
            return 0;
        }
        
        public List<MsSearchResultProteinDb> getProteinMatchList() {
            return null;
        }

        public MsSearchResultPeptideDb getResultPeptide() {
            return null;
        }

        public int getScanId() {
            return 0;
        }

        public BigDecimal getCalculatedMass() {
            return null;
        }

        @Override
        public int getCharge() {
            return 0;
        }

        @Override
        public int getNumIonsMatched() {
            return 0;
        }

        @Override
        public int getNumIonsPredicted() {
            return 0;
        }

        @Override
        public ValidationStatus getValidationStatus() {
            return null;
        }
    }
}
