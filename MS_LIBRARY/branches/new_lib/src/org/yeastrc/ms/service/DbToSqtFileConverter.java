/**
 * DbToSqtFileconverter.java
 * @author Vagisha Sharma
 * Jul 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;
import org.yeastrc.ms.domain.search.MsSearchResultDynamicModDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;
import org.yeastrc.ms.parser.sqtFile.PeptideResult;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.ScanResult;

/**
 * 
 */
public class DbToSqtFileConverter {

    private BufferedWriter outFile = null;

    public void convertToSqt(int dbSearchId, String outputFile) throws IOException {

        try {
            outFile = new BufferedWriter(new FileWriter(outputFile));

            MsSearchDAO<SQTRunSearch, SQTRunSearchDb> searchDao = DAOFactory.instance().getSqtSearchDAO();
            SQTRunSearchDb run = searchDao.loadSearch(dbSearchId);
            if (run == null) {
                System.err.println("No search found with id: "+dbSearchId);
                return;
            }
            printSqtHeader(run);
            outFile.write("\n");

            List<MsSearchModification> dynaMods = getDynaModsForSearch(dbSearchId);
            
            SQTSearchScanDAO scanDao = DAOFactory.instance().getSqtSpectrumDAO();
            SQTSearchResultDAO resultDao = DAOFactory.instance().getSqtResultDAO();
            List<Integer> resultIds = resultDao.loadResultIdsForSearch(dbSearchId);
            int currCharge = -1;
            int currScanId = -1;
            ScanResult currScan = null;
            for (Integer resultId: resultIds) {
                SQTSearchResultDb result = resultDao.load(resultId);
                if (result.getScanId() != currScanId || result.getCharge() != currCharge) {
                    if (currScan != null) {
                        outFile.write(currScan.toString());
                        outFile.write("\n");
                    }
                    currScanId = result.getScanId();
                    currCharge = result.getCharge();
                    SQTSearchScanDb scanDb = scanDao.load(dbSearchId, currScanId, currCharge);
                    currScan = makeScanResult(scanDb);
                }
                PeptideResult peptResult = new PeptideResult(dynaMods);
                peptResult.setCharge(result.getCharge());
                peptResult.setDeltaCN(result.getDeltaCN());
                peptResult.setMass(result.getCalculatedMass());
                peptResult.setNumMatchingIons(result.getNumIonsMatched());
                peptResult.setNumPredictedIons(result.getNumIonsPredicted());
                peptResult.setResultSequence(reconstructPeptideSequence(dbSearchId, result));
                peptResult.setScanNumber(currScan.getScanNumber());
                peptResult.setSp(result.getSp());
                peptResult.setSpRank(result.getSpRank());
                peptResult.setValidationStatus(result.getValidationStatus().getStatusChar());
                peptResult.setXcorr(result.getxCorr());
                peptResult.setxCorrRank(result.getxCorrRank());
                
                List<MsSearchResultProteinDb> proteins = getProteinsForResultId(resultId);
                for (MsSearchResultProteinDb pr: proteins) {
                    peptResult.addMatchingLocus(pr.getAccession(), pr.getDescription());
                }
                currScan.addPeptideResult(peptResult);
            }
            // print the last one
            if (currScan != null) {
                outFile.write(currScan.toString());
                outFile.write("\n");
            }
            outFile.flush();
        }
        finally {
            if (outFile != null)
                outFile.close();
        }

    }

    private List<MsSearchResultProteinDb> getProteinsForResultId(Integer resultId) {
        MsSearchResultProteinDAO proteinDao = DAOFactory.instance().getMsProteinMatchDAO();
        return proteinDao.loadResultProteins(resultId);
    }

    private String reconstructPeptideSequence(int dbSearchId, SQTSearchResultDb resultDb) {
        // dynamic modifications for the search
        MsSearchResultPeptideDb peptideSeq = resultDb.getResultPeptide();
        List<MsSearchResultDynamicModDb> resultMods = peptideSeq.getDynamicModifications();
        Collections.sort(resultMods, new Comparator<MsSearchResultDynamicModDb>() {
            public int compare(MsSearchResultDynamicModDb o1,
                    MsSearchResultDynamicModDb o2) {
                return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
            }});
        
        String justSeq = peptideSeq.getPeptideSequence();
        StringBuilder fullSeq = new StringBuilder();
        fullSeq.append(peptideSeq.getPreResidue()+".");
        int lastIdx = 0;
        for (MsSearchResultDynamicModDb mod: resultMods) {
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
    
    private List<MsSearchModification> getDynaModsForSearch(int dbSearchId) {
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        List<MsSearchModificationDb> dynaMods = modDao.loadDynamicModificationsForSearch(dbSearchId);
        List<MsSearchModification> mods = new ArrayList<MsSearchModification>(dynaMods.size());
        for (MsSearchModificationDb mod: dynaMods) {
            mods.add(mod);
        }
        return mods;
    }

    private ScanResult makeScanResult(SQTSearchScanDb resultScan) {
        ScanResult scanResult = new ScanResult();
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
