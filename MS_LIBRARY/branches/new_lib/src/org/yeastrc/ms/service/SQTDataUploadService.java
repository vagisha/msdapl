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
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.ibatis.MsSearchModificationDAOImpl.MsResultResidueModSqlMapParam;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultProteinDAOImpl.MsResultProteinSqlMapParam;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class SQTDataUploadService {

    private static final Logger log = Logger.getLogger(SQTDataUploadService.class);
    
    private static final DAOFactory daoFactory = DAOFactory.instance();
    
    private static final DynamicModLookupUtil dynaModLookup = DynamicModLookupUtil.instance();
    
    public static final int BUF_SIZE = 1000;
    
    private int uploadedSearchId = 0;
    
    // these are the things we will cache and do bulk-inserts
    List<MsSearchResultProteinDb> proteinMatchList; // protein matches
    List<SQTSearchResultScoresDb> sqtResultScoresList; // sequest scores
    List<MsResultResidueModSqlMapParam> resultModList; // dynamic modifications
    
    
    public SQTDataUploadService() {
        proteinMatchList = new ArrayList<MsSearchResultProteinDb>();
        sqtResultScoresList = new ArrayList<SQTSearchResultScoresDb>();
        resultModList = new ArrayList<MsResultResidueModSqlMapParam>();
    }
    
    /**
     * provider should be closed after this method returns. 
     * @param provider
     * @param runId
     * @param experimentId
     * @return
     * @throws UploadException 
     */
    public int uploadSQTSearch(SQTSearchDataProvider provider, int runId, int experimentId) 
    throws UploadException {

        log.info("BEGIN SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+"; EXPERIMENT_ID: "+experimentId);
        long startTime = System.currentTimeMillis();
        
        // reset all caches etc.
        reset();

        try {
            uploadedSearchId = uploadSearchHeader(provider, runId, experimentId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        
        log.info("Uploaded top-level info for search with searchId: "+uploadedSearchId);

        // upload the search results for each scan + charge combination
        int numResults = 0;
        int numProteins = 0;
        while (provider.hasNextSearchScan()) {
            SQTSearchScan scan = null;
            try {
                scan = provider.getNextSearchScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            int scanId = getScanId(runId, scan.getScanNumber());
            // save spectrum data
            uploadSearchScan(scan, uploadedSearchId, scanId); 

            // save all the search results for this scan
            for (SequestSearchResult result: scan.getScanResults()) {
                uploadSearchResult(result, uploadedSearchId, scanId);
                numResults++;
                numProteins += result.getProteinMatchList().size();
            }
            
        }
        flush(); // save any cached data
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results, "+numProteins+" protein matches. (searchId: "+uploadedSearchId+")"
                + " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId);
        
        return uploadedSearchId;
    }
    
    public int getUploadedSearchId() {
        return uploadedSearchId;
    }
    
    private void reset() {
        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup.reset();
        
        // clean up any cached data
        proteinMatchList.clear();
        sqtResultScoresList.clear();
        resultModList.clear();
        
        uploadedSearchId = 0;
    }
    
    private int uploadSearchHeader(SQTSearchDataProvider provider, int runId, int experimentId) throws DataProviderException {
        
        SQTRunSearch search = provider.getSearchHeader();
        // save the search and return the database id
        MsSearchDAO<SQTRunSearch, SQTRunSearchDb> searchDao = daoFactory.getSqtSearchDAO();
        return searchDao.saveRunSearch(search, runId, experimentId);
    }
    
    private void uploadSearchScan(SQTSearchScan scan, int searchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, searchId, scanId);
    }
    
    private int uploadSearchResult(SequestSearchResult result, int searchId, int scanId) {
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, searchId, scanId);
        
        // upload dynamic mods for this result
        uploadResultMods(result, resultId, searchId);
        
        // upload the protein matches
        uploadProteinMatches(result, resultId);
        
        // upload the SQT file specific information for this result.
        uploadSQTResult(result, resultId);
        
        return resultId;
    }

    private void uploadProteinMatches(SequestSearchResult result, int resultId) {
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
    
    private void uploadResultMods(SequestSearchResult result, int resultId, int searchId) {
        // upload the result dynamic modifications if the cache has enough entries
        if (resultModList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the dynamic modifications for this result to the cache
        for (MsSearchResultModification mod: result.getResultPeptide().getDynamicResidueModifications()) {
            if (mod == null || mod.getModificationType() == ModificationType.STATIC)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicResidueModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass());
            resultModList.add(new MsResultResidueModSqlMapParam(resultId, modId, mod.getModifiedPosition()));
        }
    }

    private void uploadResultModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicResidueModsForResult(resultModList);
        resultModList.clear();
    }
    
    private void uploadSQTResult(SequestSearchResult result, int resultId) {
        // upload the SQT file specific result information if the cache has enough entries
        if (sqtResultScoresList.size() >= BUF_SIZE) {
            uploadSqtResultBuffer();
        }
        // add the SQT file specific information for this result to the cache
        SQTSearchResultScores sqtResultOnly = new SQTSearchResultScores();
        sqtResultOnly.resultId = resultId;
        sqtResultOnly.xcorrRank = result.getxCorrRank();
        sqtResultOnly.spRank = result.getSpRank();
        sqtResultOnly.deltaCN = result.getDeltaCN();
        sqtResultOnly.xcorr = result.getxCorr();
        sqtResultOnly.sp = result.getSp();
        sqtResultScoresList.add(sqtResultOnly);
    }
    
    private void uploadSqtResultBuffer() {
        SequestSearchResultDAO sqtResultDao = daoFactory.getSequestResultDAO();
        sqtResultDao.saveAllSequestResultData(sqtResultScoresList);
        sqtResultScoresList.clear();
    }
    
    private void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (sqtResultScoresList.size() > 0) {
            uploadSqtResultBuffer();
        }
        if (resultModList.size() > 0) {
            uploadResultModBuffer();
        }
    }
    
    private static int getScanId(int runId, int scanNumber) throws UploadException {
        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    public static List<Integer> getSearchIdsForExperiment(int experimentId) {
        MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        return searchDao.loadRunSearchIdsForSearch(experimentId);
    }
    
    public static void deleteSearch(int searchId) {
        MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        searchDao.deleteSearch(searchId);
    }
    
    private static final class SQTSearchResultScores implements SQTSearchResultScoresDb{

        int resultId;
        int xcorrRank;
        int spRank;
        BigDecimal deltaCN;
        BigDecimal xcorr;
        BigDecimal sp;
        
        public int getResultId() {
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
    }
}
