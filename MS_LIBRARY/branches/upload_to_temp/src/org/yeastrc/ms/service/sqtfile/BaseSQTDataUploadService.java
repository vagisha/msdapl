/**
 * BaseSQTDataUploadService.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.BaseSQTFileReader;
import org.yeastrc.ms.parser.sqtFile.PeptideResultBuilder;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAO;

/**
 * 
 */
public class BaseSQTDataUploadService extends AbstractSQTDataUploadService {

    private final SearchParamsDataProvider paramsProvider;
    private final PeptideResultBuilder peptideResultBuilder;
    private final Program searchProgram;
    private final SearchFileFormat sqtType;
    
    private MsSearchDatabaseIn db = null;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    

    public BaseSQTDataUploadService(SearchParamsDataProvider paramsProvider, 
            PeptideResultBuilder peptideResultBuilder,
            Program searchProgram, SearchFileFormat sqtType) {
        super();
        this.paramsProvider = paramsProvider;
        this.peptideResultBuilder = peptideResultBuilder;
        this.searchProgram = searchProgram;
        this.sqtType = sqtType;
    }

    @Override
    int uploadSearchParameters(int experimentId, String paramFileDirectory,
            String remoteServer, String remoteDirectory, java.util.Date searchDate)
    throws UploadException {

        SearchParamsDataProvider parser = parseParams(paramFileDirectory, remoteServer);
        
        db = parser.getSearchDatabase();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        
        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(parser.getSearchDatabase());
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            if(paramsProvider instanceof SequestParamsParser) {
                SequestSearchUploadDAO searchDAO = UploadDAOFactory.getInstance().getSequestSearchDAO();
                return searchDAO.saveSearch(SequestSQTDataUploadService.makeSearchObject((SequestParamsParser) parser, 
                                                                                        this.searchProgram,
                                                                                        remoteDirectory, 
                                                                                        searchDate), 
                                              experimentId, sequenceDatabaseId);
            }
            else if(paramsProvider instanceof ProlucidParamsParser) {
                ProlucidSearchUploadDAO searchDAO = UploadDAOFactory.getInstance().getProlucidSearchDAO();
                return searchDAO.saveSearch(ProlucidSQTDataUploadService.makeSearchObject((ProlucidParamsParser) parser, 
                                                                                            remoteDirectory, 
                                                                                            searchDate), 
                                              experimentId, sequenceDatabaseId);
            }
            else {
                throw new UploadException(ERROR_CODE.UNKNOWN_PARAMS);
            }
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }

    private SearchParamsDataProvider parseParams(String fileDirectory, final String remoteServer) throws UploadException {

        log.info("BEGIN SQT search UPLOAD -- parsing parameters file: "+paramsProvider.paramsFileName());
        // parse the parameters file
        try {
            paramsProvider.parseParams(remoteServer, fileDirectory);
            return paramsProvider;
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(fileDirectory+File.separator+paramsProvider.paramsFileName());
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }

    @Override
    MsSearchDatabaseIn getSearchDatabase() {
        return db;
    }

    @Override
    Program getSearchProgram() {
        return searchProgram;
    }

    @Override
    int uploadSqtFile(String filePath, int runId) throws UploadException {
        
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        long startTime = System.currentTimeMillis();
        BaseSQTFileReader provider = new BaseSQTFileReader(peptideResultBuilder); 
        
        // If we are uploading MacCoss lab data we need to look for "Placeholder" peptide matches
        // in the SQT files. 
        if(this.doScanChargeMassCheck)
            provider.doPercolatorMLineCheck();
        
        
        try {
            provider.open(filePath);
            provider.setDynamicResidueMods(this.dynaResidueMods);
            provider.setDynamicTerminalMods(this.dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        int runSearchId;
        try {
            runSearchId = uploadBaseSqtFile(provider, searchId, runId, sequenceDatabaseId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            ex.appendErrorMessage("\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
        return runSearchId;
    }
    
    // parse and upload a sqt file
    private int uploadBaseSqtFile(BaseSQTFileReader provider, int searchId, int runId, int searchDbId) throws UploadException {
        
        int runSearchId;
        try {
            runSearchId = uploadSearchHeader(provider, runId, searchId);
            log.info("Uploaded top-level info for sqt file. runSearchId: "+runSearchId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }

        // upload the search results for each scan + charge combination
        int numResults = 0;
        while (provider.hasNextSearchScan()) {
            SQTSearchScanIn<MsSearchResultIn> scan = null;
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
            if(uploadSearchScan(scan, runSearchId, scanId)) {
                // save all the search results for this scan
                for (MsSearchResultIn result: scan.getScanResults()) {
                    uploadSearchResult(result, runSearchId, scanId);
                    numResults++;
                }
            }
            else {
                log.info("Ignoring search scan: "+scan.getScanNumber()+"; scanId: "+scanId+"; charge: "+scan.getCharge()+"; mass: "+scan.getObservedMass());
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");
        
        return runSearchId;
    }
    
    void uploadSearchResult(MsSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        super.uploadBaseSearchResult(result, runSearchId, scanId);
    }

    @Override
    SearchFileFormat getSearchFileFormat() {
        return this.sqtType;
    }

    @Override
    String searchParamsFile() {
        return this.paramsProvider.paramsFileName();
    }

    @Override
    protected void copyFiles(int experimentId) throws UploadException {
        // Does nothing
    }
    
}
