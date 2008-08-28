/**
 * DbToSqtFileconverter.java
 * @author Vagisha Sharma
 * Jul 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.NrSeqLookupUtil;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SearchScan;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResult;

/**
 * 
 */
public class DbToSqtFileConverter {

    private BufferedWriter outFile = null;

    public void convertToSqt(int runSearchId, String outputFile) throws IOException {

        try {
            outFile = new BufferedWriter(new FileWriter(outputFile));

            MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> searchDao = DAOFactory.instance().getSqtRunSerachDAO();
            SQTRunSearchDb runSearch = searchDao.loadRunSearch(runSearchId);
            if (runSearch == null) {
                System.err.println("No run search found with id: "+runSearchId);
                return;
            }
            
            int searchDatabaseId = getSearchDatabaseId(runSearch.getSearchId());
            
            printSqtHeader(runSearch);
            outFile.write("\n");
            SearchFileFormat origFileType = runSearch.getSearchFileFormat();
            if (origFileType == SearchFileFormat.SQT_SEQ || 
                origFileType == SearchFileFormat.SQT_NSEQ) {
                printSequestSQTData(runSearch, searchDatabaseId, outFile);
            }
            else if (origFileType == SearchFileFormat.SQT_PLUCID) {
                // TODO
            }
            
            outFile.flush();
        }
        finally {
            if (outFile != null)
                outFile.close();
        }
    }

    private int getSearchDatabaseId(int searchId) {
        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearchDb search = searchDao.loadSearch(searchId);
        List<MsSearchDatabaseDb> db = search.getSearchDatabases();
        if (db.size() == 0)
            return 0;
        return NrSeqLookupUtil.getDatabaseId(db.get(0).getServerPath());
    }
    
    private void printSequestSQTData(SQTRunSearchDb runSearch, int searchDatabaseId, BufferedWriter outFile) throws IOException {
        
        List<MsResidueModificationDb> dynaResidueModsDb = getDynaResidueModsForSearch(runSearch.getSearchId());
        
        SQTSearchScanDAO scanDao = DAOFactory.instance().getSqtSpectrumDAO();
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearch.getId());
        int currCharge = -1;
        int currScanId = -1;
        SearchScan currScan = null;
        for (Integer resultId: resultIds) {
            SequestSearchResultDb result = resultDao.load(resultId);
            if (result.getScanId() != currScanId || result.getCharge() != currCharge) {
                if (currScan != null) {
                    outFile.write(currScan.toString());
                    outFile.write("\n");
                }
                currScanId = result.getScanId();
                currCharge = result.getCharge();
                SQTSearchScanDb scanDb = scanDao.load(runSearch.getId(), currScanId, currCharge);
                currScan = makeScanResult(scanDb);
            }
            List<MsResidueModification> dynaResidueMods = new ArrayList<MsResidueModification>();
            for (MsResidueModificationDb modDb: dynaResidueModsDb) {
                dynaResidueMods.add(modDb);
            }
            SequestResult peptResult = new SequestResult(dynaResidueMods);
            SequestResultData data = result.getSequestResultData();
            peptResult.setCharge(result.getCharge());
            peptResult.setDeltaCN(data.getDeltaCN());
            peptResult.setMass(data.getCalculatedMass());
            peptResult.setNumMatchingIons(data.getMatchingIons());
            peptResult.setNumPredictedIons(data.getPredictedIons());
            peptResult.setOriginalPeptideSequence(reconstructSequestPeptideSequence(runSearch.getSearchId(), result));
            peptResult.setScanNumber(currScan.getScanNumber());
            peptResult.setSp(data.getSp());
            peptResult.setSpRank(data.getSpRank());
            peptResult.setValidationStatus(result.getValidationStatus().getStatusChar());
            peptResult.setXcorr(data.getxCorr());
            peptResult.setxCorrRank(data.getxCorrRank());
            peptResult.setEvalue(data.getEvalue());
            
            
            List<MsSearchResultProteinDb> proteins = getProteinsForResultId(resultId);
            for (MsSearchResultProteinDb pr: proteins) {
                peptResult.addMatchingLocus(NrSeqLookupUtil.getProteinAccession(searchDatabaseId, pr.getProteinId()), null);
            }
            currScan.addPeptideResult(peptResult);
        }
        // print the last one
        if (currScan != null) {
            outFile.write(currScan.toString());
            outFile.write("\n");
        }
    }

    private List<MsSearchResultProteinDb> getProteinsForResultId(Integer resultId) {
        MsSearchResultProteinDAO proteinDao = DAOFactory.instance().getMsProteinMatchDAO();
        return proteinDao.loadResultProteins(resultId);
    }

    private String reconstructSequestPeptideSequence(int searchId, SequestSearchResultDb resultDb) {
        // dynamic modifications for the search
        MsSearchResultPeptideDb peptideSeq = resultDb.getResultPeptide();
        List<MsResultDynamicResidueModDb> resultMods = peptideSeq.getResultDynamicResidueModifications();
        Collections.sort(resultMods, new Comparator<MsResultDynamicResidueModDb>() {
            public int compare(MsResultDynamicResidueModDb o1,
                    MsResultDynamicResidueModDb o2) {
                return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
            }});
        
        String justSeq = peptideSeq.getPeptideSequence();
        StringBuilder fullSeq = new StringBuilder();
        fullSeq.append(peptideSeq.getPreResidue()+".");
        int lastIdx = 0;
        for (MsResultDynamicResidueModDb mod: resultMods) {
            int pos = mod.getModifiedPosition();
            fullSeq.append(justSeq.substring(lastIdx, pos+1));
            fullSeq.append(mod.getModificationSymbol());
            lastIdx = pos+1;
        }
        if (lastIdx < justSeq.length()) {
            fullSeq.append(justSeq.substring(lastIdx, justSeq.length()));
        }
        fullSeq.append("."+peptideSeq.getPostResidue());
        return fullSeq.toString();
    }
    
    private List<MsResidueModificationDb> getDynaResidueModsForSearch(int dbSearchId) {
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        List<MsResidueModificationDb> dynaMods = modDao.loadDynamicResidueModsForSearch(dbSearchId);
        return dynaMods;
    }
    
    private List<MsTerminalModificationDb> getDynaTermModsForSearch(int dbSearchId) {
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        List<MsTerminalModificationDb> dynaMods = modDao.loadDynamicTerminalModsForSearch(dbSearchId);
        return dynaMods;
    }

    private SearchScan makeScanResult(SQTSearchScanDb resultScan) {
        SearchScan scanResult = new SearchScan();
        MsScanDb msScan = getScanForId(resultScan.getScanId());
        scanResult.setStartScan(msScan.getStartScanNum());
        scanResult.setEndScan(msScan.getStartScanNum());
        scanResult.setCharge(resultScan.getCharge());
        scanResult.setLowestSp(resultScan.getLowestSp());
        scanResult.setObservedMass(msScan.getPrecursorMz());
        scanResult.setProcessingTime(resultScan.getProcessTime());
        scanResult.setSequenceMatches(resultScan.getSequenceMatches());
        scanResult.setServer(resultScan.getServerName());
        scanResult.setTotalIntensity(resultScan.getTotalIntensity());
        return scanResult;
    }

    private MsScanDb getScanForId(int scanId) {
        MsScanDAO<MsScan, MsScanDb>scanDao = DAOFactory.instance().getMsScanDAO();
        return scanDao.load(scanId);
    }
    
    private void printSqtHeader(SQTRunSearchDb search) throws IOException {
        SQTHeader sqtHeader = new SQTHeader();
        List<SQTHeaderDb> headerList = search.getHeaders();
        Collections.sort(headerList, new Comparator<SQTHeaderDb>() {
            public int compare(SQTHeaderDb o1, SQTHeaderDb o2) {
                return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }});

        for (SQTHeaderDb header: headerList) {
            try {
                sqtHeader.addHeaderItem(header.getName(), header.getValue());
            }
            catch (SQTParseException e) {
                e.printStackTrace();
            }
        }
        outFile.write(sqtHeader.toString());
    }
}
