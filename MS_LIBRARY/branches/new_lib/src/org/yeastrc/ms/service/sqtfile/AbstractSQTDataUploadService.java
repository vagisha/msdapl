package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsResultDynamicTerminalModDb;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.impl.MsSearchResultProteinDbImpl;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ResultResidueModification;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.TerminalModification;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public abstract class AbstractSQTDataUploadService {

    static final Logger log = Logger.getLogger(AbstractSQTDataUploadService.class);

    static final DAOFactory daoFactory = DAOFactory.instance();

    DynamicModLookupUtil dynaModLookup;

    static final int BUF_SIZE = 1000;

    // these are the things we will cache and do bulk-inserts
    LinkedHashSet<MsSearchResultProteinDb> proteinMatchSet;
    List<MsResultDynamicResidueModDb> resultResidueModList;
    List<MsResultDynamicTerminalModDb> resultTerminalModList;


    List<UploadException> uploadExceptionList = new ArrayList<UploadException>();

    int numSearchesToUpload = 0;
    int numSearchesUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msSearch and msSequenceDatabaseDetail table.
//  private long dbSequenceLength;
//  private int dbProteinCount;
    String programVersion = "uninit";

    int lastUploadedRunSearchId;

    public AbstractSQTDataUploadService() {
        this.proteinMatchSet = new LinkedHashSet<MsSearchResultProteinDb>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultDynamicResidueModDb>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultDynamicTerminalModDb>(BUF_SIZE);
        this.uploadExceptionList = new ArrayList<UploadException>();
    }
    
    final void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesToUpload = 0;
        numSearchesUploaded = 0;

        resetCaches();

        uploadExceptionList.clear();
    }

    void resetCaches() {
        
        proteinMatchSet.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();

        lastUploadedRunSearchId = 0;
        //      dbSequenceLength = 0;
        //      dbProteinCount = 0;
        programVersion = "uninit";
    }

    public final List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }

    public final int getNumSearchesToUpload() {
        return numSearchesToUpload;
    }

    public final int getNumSearchesUploaded() {
        return numSearchesUploaded;
    }

    static int getScanId(int runId, int scanNumber)
            throws UploadException {

        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }

    static int getNumFilesToUpload(String fileDirectory, Set<String> fileNames) {
        int num = 0;
        for (String file: fileNames) {
            if ((new File(fileDirectory+File.separator+file+".sqt")).exists())
                num++;
        }
        return num;
    }

    static void updateProgramVersion(int searchId, String programVersion) {
        try {
            MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchAnalysisProgramVersion(searchId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating prolucid version for searchID: "+searchId, e);
        }
    }

    final int uploadSearchHeader(SQTSearchDataProvider provider, int runId, int searchId)
        throws DataProviderException {

        SQTRunSearch search = provider.getSearchHeader();
        if (search instanceof SQTHeader) {
            SQTHeader header = (SQTHeader)search;
            // this is the first time we are assigning a value to prolucidVersion
            if ("uninit".equals(programVersion))
                this.programVersion = header.getSearchEngineVersion();

            // make sure the prolucidVersion value is same in all sqt header
            // if not we set Version to null so that the analysisProgramVersion field does
            // not get updated. 
            if (programVersion != null &&
                    !programVersion.equals(header.getSearchEngineVersion())) {
                this.programVersion = null;
            }
        }
        // save the run search and return the database id
        MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> runSearchDao = daoFactory.getSqtRunSearchDAO();
        return runSearchDao.saveRunSearch(search, runId, searchId);
    }

    final void uploadSearchScan(SQTSearchScan scan, int runSearchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, runSearchId, scanId);
    }

    final void uploadProteinMatches(MsSearchResult result, final int resultId, int databaseId)
        throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchSet.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            int proteinId = 0;
            try {proteinId = NrSeqLookupUtil.getProteinId(databaseId, match.getAccession());}
            catch(NrSeqLookupException e) {
                UploadException ex = new UploadException(ERROR_CODE.PROTEIN_NOT_FOUND, e);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            // NOTE: we are using a Set for the proteinMatches.  ONLY UNIQUE ENTRIES WILL BE ADDED.
            MsSearchResultProteinDbImpl prMatch = new MsSearchResultProteinDbImpl();
            prMatch.setProteinId(proteinId);
            prMatch.setResultId(resultId);
            proteinMatchSet.add(prMatch);
        }
    }
    
    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        List<MsSearchResultProteinDb> list = new ArrayList<MsSearchResultProteinDb>(proteinMatchSet.size());
        list.addAll(proteinMatchSet);
        matchDao.saveAll(list);
        proteinMatchSet.clear();
    }

    // RESIDUE DYNAMIC MODIFICATION
    void uploadResultResidueMods(MsSearchResult result, int resultId, int searchId) {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultDynamicResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicResidueModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass()); // throws a RuntimeException
            resultResidueModList.add(new ResultResidueMod(mod.getModifiedResidue(), 
                    mod.getModificationSymbol(),
                    mod.getModificationMass(), 
                    mod.getModifiedPosition(),
                    resultId,
                    modId));
        }
    }

    private void uploadResultResidueModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicResidueModsForResult(resultResidueModList);
        resultResidueModList.clear();
    }
    
    // TERMINAL DYNAMIC MODIFICATION
    void uploadResultTerminalMods(MsSearchResult result, int resultId, int searchId) {
        // upload the result dynamic terminal modifications if the cache has enough entries
        if (resultTerminalModList.size() >= BUF_SIZE) {
            uploadResultTerminalModBuffer();
        }
        // add the dynamic terminal modifications for this result to the cache
        for (MsTerminalModification mod: result.getResultPeptide().getDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicTerminalModificationId(searchId, 
                    mod.getModifiedTerminal(), mod.getModificationMass()); // throws a RuntimeException
            resultTerminalModList.add(new ResultTerminalMod(mod.getModifiedTerminal(), 
                    mod.getModificationSymbol(),
                    mod.getModificationMass(), 
                    resultId,
                    modId));
        }
    }

    private void uploadResultTerminalModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicTerminalModsForResult(resultTerminalModList);
        resultTerminalModList.clear();
    }

    void flush() {
        if (proteinMatchSet.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (resultResidueModList.size() > 0) {
            uploadResultResidueModBuffer();
        }
        if (resultTerminalModList.size() > 0) {
            uploadResultTerminalModBuffer();
        }
    }

    final void deleteLastUploadedRunSearch() {
        if (lastUploadedRunSearchId == 0)
            return;
        MsRunSearchDAO<MsRunSearch, MsRunSearchDb> runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        runSearchDao.deleteRunSearch(lastUploadedRunSearchId);
    }

    public static void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        searchDao.deleteSearch(searchId);
    }

    private static final class ResultResidueMod extends ResultResidueModification implements MsResultDynamicResidueModDb {

        private final int resultId;
        private final int modId;
        public ResultResidueMod(char modResidue, char modSymbol,
                BigDecimal modMass, int position, int resultId, int modId) {
            super(modResidue, modSymbol, modMass, position);
            this.resultId = resultId;
            this.modId = modId;
        }
        public int getModificationId() {
            return modId;
        }
        public int getResultId() {
            return resultId;
        }
    }
    
    private static final class ResultTerminalMod extends TerminalModification implements MsResultDynamicTerminalModDb {

        private final int resultId;
        private final int modId;
        public ResultTerminalMod(Terminal modifiedTerminal, char modSymbol,
                BigDecimal modMass, int resultId, int modId) {
            super(modifiedTerminal, modMass, modSymbol);
            this.resultId = resultId;
            this.modId = modId;
        }
        public int getModificationId() {
            return modId;
        }
        public int getResultId() {
            return resultId;
        }
    }
}