package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.sqtFile.SQTPeptideSearchDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAO;
import org.yeastrc.ms.dto.MsProteinMatch;
import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.sqtFile.SQTPeptideSearch;
import org.yeastrc.ms.dto.sqtFile.SQTSearchHeader;
import org.yeastrc.ms.dto.sqtFile.SQTSearchResult;
import org.yeastrc.ms.dto.sqtFile.SQTSpectrumData;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.DynamicModification;
import org.yeastrc.ms.parser.sqtFile.Header;
import org.yeastrc.ms.parser.sqtFile.PeptideResult;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.ScanResult;
import org.yeastrc.ms.parser.sqtFile.StaticModification;
import org.yeastrc.ms.parser.sqtFile.Header.HeaderItem;

public class SqtFileToDbConverter {


    private void convertSQTFile(String filePath, int runId) throws Exception {

        String justFileName = new File(filePath).getName();
        SQTFileReader reader = new SQTFileReader();
        reader.open(filePath);
        convertSQTFile(justFileName, reader, runId);
    }

    private void convertSQTFile(String file, SQTFileReader reader, int runId)
    throws Exception {
        Header header = reader.getHeader();

        // insert a search into the database and get the search Id
        int searchId = saveSQTSearch(header, file, runId);

        int scanId = 1;
        while (reader.hasScans()) {
            ScanResult scan = reader.getNextScan();
            
            // TODO get the scanId from the database using the runId and scan number
            // insert a scan into the database for the given run
            saveScan(scan, searchId, scanId++);
        }
    }


    private void saveScan(ScanResult scan, int searchId, int scanId) {
        
        saveSpectrumData(scan, searchId, scanId);
        for (PeptideResult result: scan.getResultList()) {
            savePeptideResult(result, searchId, scanId, scan.getCharge());
        }
        
    }

    private int savePeptideResult(PeptideResult result, int searchId,
            int scanId, int charge) {
        SQTSearchResult sqtResult = new SQTSearchResult();
        sqtResult.setSearchId(searchId);
        sqtResult.setScanId(scanId);
        sqtResult.setCharge(charge);
        String sequence = result.getSequence();
        sqtResult.setPeptide(getOnlyPeptide(sequence));
        sqtResult.setPreResidue(sequence.charAt(0));
        sqtResult.setPostResidue(sequence.charAt(sequence.length() -1));
        sqtResult.setCalculatedMass(result.getMass());
        sqtResult.setNumIonsMatched(result.getNumMatchingIons());
        sqtResult.setNumIonsPredicted(result.getNumPredictedIons());
        sqtResult.setValidationStatus(result.getValidationStatus());
        
        sqtResult.setxCorrRank(result.getXcorrRank());
        sqtResult.setxCorr(result.getXcorr());
        sqtResult.setSpRank(result.getSpRank());
        sqtResult.setSp(result.getSp());
        sqtResult.setDeltaCN(result.getDeltaCN());
        
        // save matching protein information for this result
        List<DbLocus> loci = result.getMatchingLoci();
        for (DbLocus l: loci) {
            MsProteinMatch match = new MsProteinMatch();
            match.setAccession(l.getAccession());
            match.setDescription(l.getDescription());
            sqtResult.addProteinMatch(match);
        }
        
        // TODO add dynamic modifications
        
        
        SQTSearchResultDAO resultDao = DAOFactory.instance().getSqtResultDAO();
        return resultDao.save(sqtResult);
    }

    private String getOnlyPeptide(String sequence) {
        int f = sequence.indexOf('.');
        if (f < 0) 
            throw new RuntimeException("Sequence does not have a .(dot)"+sequence);
        int e = sequence.lastIndexOf('.');
        if (f == e)
            throw new RuntimeException("First and last index of .(dot) cannot be the same: "+sequence);
        return sequence.substring(f+1, e);
        
    }
    
    private void saveSpectrumData(ScanResult scan, int searchId, int scanId) {
        SQTSpectrumData scanData = new SQTSpectrumData();
        scanData.setSearchId(searchId);
        scanData.setCharge(scan.getCharge());
        scanData.setProcessTime(scan.getProcessingTime());
        scanData.setScanId(scanId);
        scanData.setServerName(scan.getServer());
        scanData.setTotalIntensity(scan.getTotalIntensity());
        scanData.setLowestSp(scan.getLowestSp());
        SQTSpectrumDataDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scanData);
    }

    private int saveSQTSearch(Header header, String file, int runId) {
        SQTPeptideSearch search = new SQTPeptideSearch();
        search.setRunId(runId);
        search.setOriginalFileType("SQT");
        search.setSearchEngineName(header.getSqtGenerator());
        search.setSearchEngineVersion(header.getSqtGeneratorVersion());
        search.setPrecursorMassType(header.getPrecursorMassType());
        search.setPrecursorMassTolerance(header.getPrecursorMassTolerance());
        search.setFragmentMassType(header.getFragmentMassType());
        search.setFragmentMassTolerance(header.getFragmentMassTolerance());
        
        // add headers
        for (HeaderItem item: header.getHeaderItems()) {
            SQTSearchHeader h = new SQTSearchHeader();
            h.setName(item.getName());
            h.setValue(item.getValue());
            search.addHeader(h);
        }
        
        //TODO add enzyme information
        
        //TODO add search database information
        
        
        // save and return id
        SQTPeptideSearchDAO searchDao = DAOFactory.instance().getSqtSearchDAO();
        int searchId = searchDao.saveSearch(search);
        
        
        // add static modifications
        for (StaticModification sMod: header.getStaticMods()) {
            MsSearchMod mod = new MsSearchMod();
            mod.setModifiedResidue(sMod.getModificationChar());
            mod.setModificationMass(sMod.getModificationMass());
            search.addStaticModification(mod);
        }
        // add dynamic modifications
        for (DynamicModification dMod: header.getDynaMods()) {
            MsSearchDynamicMod mod = new MsSearchDynamicMod();
            mod.setModifiedResidue(dMod.getModificationChar());
            mod.setModificationMass(dMod.getModificationMass());
            mod.setModificationSymbol(dMod.getModificationSymbol());
            search.addDynamicModification(mod);
        }
        
        return searchId;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SqtFileToDbConverter uploader = new SqtFileToDbConverter();
        String file = "./resources/PARC_p75_01_itms.sqt";
//      String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
        long start = System.currentTimeMillis();
        try {
            uploader.convertSQTFile(file, 10);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long timeElapsed = (end - start)/1000;
        System.out.println("Seconds to upload: "+timeElapsed);

    }



}
