package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptide;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideIon;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideIonWSpectra;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerSpectrumMatch;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerCluster;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInputSummary;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideIon;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final SequestSearchResultDAO seqResDao = msDataDaoFactory.getSequestResultDAO();
    private static final ProlucidSearchResultDAO plcResDao = msDataDaoFactory.getProlucidResultDAO();
//    private static final MsSearchResultDAO resDao = msDataDaoFactory.getMsSearchResultDAO();
    
    private static final IdPickerSpectrumMatchDAO specDao = pinferDaoFactory.getIdPickerSpectrumMatchDao();
    private static final IdPickerPeptideDAO peptDao = pinferDaoFactory.getIdPickerPeptideDao();
    private static final IdPickerProteinDAO protDao = pinferDaoFactory.getIdPickerProteinDao();
    private static final IdPickerInputDAO inputDao = pinferDaoFactory.getIdPickerInputDao();
//    private static final IdPickerRunDAO pinferRunDao = pinferDaoFactory.getIdPickerRunDao();
    
//    private static final Logger log = Logger.getLogger(IdPickerResultsLoader.class);
    
    private IdPickerResultsLoader(){}
    
    
    public static List<WIdPickerInputSummary> getIDPickerInputSummary(int pinferId) {
        
        List<IdPickerInputSummary> inputSummary = inputDao.getProteinferInputList(pinferId);
        List<WIdPickerInputSummary> wInputList = new ArrayList<WIdPickerInputSummary>(inputSummary.size());
        
        for(IdPickerInputSummary input: inputSummary) {
            String filename = rsDao.loadFilenameForRunSearch(input.getRunSearchId());
            WIdPickerInputSummary winput = new WIdPickerInputSummary(input);
            winput.setFileName(filename);
            wInputList.add(winput);
        }
        return wInputList;
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferId, int pinferProteinId) {
        IdPickerProtein protein = protDao.getProtein(pinferProteinId);
        WIdPickerProtein wProt = new WIdPickerProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinAccessionDescription(wProt);
        return wProt;
    }

    private static void assignProteinAccessionDescription(WIdPickerProtein wProt) {
        
        NrDbProtein nrDbProt = NrSeqLookupUtil.getDbProtein(wProt.getProtein().getNrseqProteinId());
        wProt.setAccession(nrDbProt.getAccessionString());
        wProt.setDescription(nrDbProt.getDescription());
        
//        NRProteinFactory nrpf = NRProteinFactory.getInstance();
//        NRProtein nrseqProt = null;
//        try {
//            nrseqProt = (NRProtein)(nrpf.getProtein(wProt.getProtein().getNrseqProteinId()));
//            wProt.setAccession(nrseqProt.getListing());
//            wProt.setDescription(nrseqProt.getDescription());
//        }
//        catch (Exception e) {
//            log.error("Exception getting nrseq protein for protein Id: "+wProt.getProtein().getNrseqProteinId(), e);
//        }
    }
    
    public static List<WIdPickerProtein> getGroupProteins(int pinferId, int groupId) {
        List<IdPickerProtein> proteins = protDao.getIdPickerGroupProteins(pinferId, groupId);
        List<WIdPickerProtein> wProteins = new ArrayList<WIdPickerProtein>(proteins.size());
        for(IdPickerProtein prot: proteins) {
            WIdPickerProtein wProt = new WIdPickerProtein(prot);
            assignProteinAccessionDescription(wProt);
            wProteins.add(wProt);
        }
        return wProteins;
    }
    
    
    public static List<WIdPickerPeptideIon> getPeptideIonsForProteinGroup(int pinferId, int pinferProteinGroupId) {
        
        IdPickerProteinGroup protGrp = protDao.getIdPickerProteinGroup(pinferId, pinferProteinGroupId);
        List<IdPickerPeptideGroup> peptGrps = protGrp.getMatchingPeptideGroups();
        
        
        List<WIdPickerPeptide> allPeptides = new ArrayList<WIdPickerPeptide>();
        // get all the peptides in all the groups
        for(IdPickerPeptideGroup peptGrp: peptGrps) {
            for (IdPickerPeptide peptide: peptGrp.getPeptides()) {
                WIdPickerPeptide wPept = new WIdPickerPeptide(peptide);
                wPept.setIsUniqueToProteinGroup(peptGrp.isUniqueToProteinGroup());
                allPeptides.add(wPept);
            }
        }
        
        List<WIdPickerPeptideIon> allIonList = new ArrayList<WIdPickerPeptideIon>();
        
        // get all the matching spectra for each peptide
        for(WIdPickerPeptide wpeptide: allPeptides) {
            List<WIdPickerPeptideIon> wionList = getIonsForWPeptide(wpeptide);
            allIonList.addAll(wionList);
        }
        
        // sort by by modified peptide sequence
        Collections.sort(allIonList, new Comparator<WIdPickerPeptideIon>() {
            public int compare(WIdPickerPeptideIon o1, WIdPickerPeptideIon o2) {
                return o1.getIon().getSequence().compareTo(o2.getIon().getSequence());
            }});
        return allIonList;
    }
    
    public static List<WIdPickerPeptideIon> getIonsForWPeptide(WIdPickerPeptide peptide) {
        
        Map<String, WIdPickerPeptideIon> ionMap = new HashMap<String, WIdPickerPeptideIon>();
        List<IdPickerSpectrumMatch> psmList = peptide.getPeptide().getSpectrumMatchList();

        // for each spectrum match
        for(IdPickerSpectrumMatch psm: psmList) {
            // get the underlying search result 
            MsSearchResult res = seqResDao.load(psm.getMsRunSearchResultId());
            int charge = res.getCharge();
            String modifiedSeq = res.getResultPeptide().getModifiedPeptideSequence();
            modifiedSeq = removeTerminalResidues(modifiedSeq);

            // separate by ion type(charge + modifications)
            String ionKey = modifiedSeq+"_chg"+charge;
            WIdPickerPeptideIon wion = ionMap.get(ionKey);
            if(wion == null) {
                IdPickerPeptideIon ion = new IdPickerPeptideIon();
                ion.setCharge(charge);
                ion.setGroupId(peptide.getPeptide().getGroupId());
                ion.setSequence(modifiedSeq);
                wion = new WIdPickerPeptideIon(ion);
                wion.setScanId(res.getScanId());
                wion.setIsUniqueToProteinGroup(peptide.getIsUniqueToProteinGroup());
                ionMap.put(ionKey, wion);
            }
            wion.getIon().addSpectrumMatch(psm);
        }
        return new ArrayList<WIdPickerPeptideIon>(ionMap.values());
    }
    
    public static List<IdPickerPeptideIon> getIonsForPeptide(IdPickerPeptide peptide, PeptideDefinition peptideDef) {
        
        Map<String, IdPickerPeptideIon> ionMap = new HashMap<String, IdPickerPeptideIon>();
        List<IdPickerSpectrumMatch> psmList = peptide.getSpectrumMatchList();

        // for each spectrum match
        for(IdPickerSpectrumMatch psm: psmList) {
            // get the underlying search result 
            MsSearchResult res = seqResDao.load(psm.getMsRunSearchResultId());
            int charge = res.getCharge();
            
            String ionseq = null;
            if(peptideDef.isUseMods()) {
                ionseq = res.getResultPeptide().getModifiedPeptideSequence();
            }
            else {
                ionseq = res.getResultPeptide().getPeptideSequence();
            }
            ionseq = removeTerminalResidues(ionseq);

            // separate by ion type(based on given peptide definition)
            String ionKey = ionseq;
            if(peptideDef.isUseCharge()) {
                ionKey = ionKey+"_chg"+charge;
            }
            IdPickerPeptideIon ion = ionMap.get(ionKey);
            if(ion == null) {
                ion = new IdPickerPeptideIon();
                ion.setCharge(charge);
                ion.setGroupId(peptide.getGroupId());
                ion.setSequence(ionseq);
                ionMap.put(ionKey, ion);
            }
            ion.addSpectrumMatch(psm);
        }
        return new ArrayList<IdPickerPeptideIon>(ionMap.values());
    }
    
    public static <T extends MsSearchResult> List<WIdPickerPeptideIonWSpectra<T>> 
            getIonsForWPeptide(WIdPickerPeptide peptide, SearchProgram program) {
        
        Map<String, WIdPickerPeptideIonWSpectra<T>> ionMap = new HashMap<String, WIdPickerPeptideIonWSpectra<T>>();
        List<IdPickerSpectrumMatch> psmList = peptide.getPeptide().getSpectrumMatchList();

        // for each spectrum match
        for(IdPickerSpectrumMatch psm: psmList) {
            
            // get the underlying search result 
            MsSearchResult res = null;
            if(program == SearchProgram.SEQUEST || program == SearchProgram.EE_NORM_SEQUEST) {
                res = seqResDao.load(psm.getMsRunSearchResultId());
            }
            else if (program == SearchProgram.PROLUCID) {
                res = plcResDao.load(psm.getMsRunSearchResultId());
            }
            else {
                res = seqResDao.load(psm.getMsRunSearchResultId());
            }
            int charge = res.getCharge();
            String modifiedSeq = res.getResultPeptide().getModifiedPeptideSequence();
            modifiedSeq = removeTerminalResidues(modifiedSeq);
            int scanNum = scanDao.load(res.getScanId()).getStartScanNum();
            

            // separate by ion type(charge + modifications)
            String ionKey = modifiedSeq+"_chg"+charge;
            WIdPickerPeptideIonWSpectra<T> wion = ionMap.get(ionKey);
            if(wion == null) {
                IdPickerPeptideIon ion = new IdPickerPeptideIon();
                ion.setCharge(charge);
                ion.setGroupId(peptide.getPeptide().getGroupId());
                ion.setSequence(modifiedSeq);
                wion = new WIdPickerPeptideIonWSpectra<T>(ion);
                wion.setIdUniqueToProteinGroup(peptide.getIsUniqueToProteinGroup());
                ionMap.put(ionKey, wion);
            }
            WIdPickerSpectrumMatch<T> wpsm = new WIdPickerSpectrumMatch<T>();
            wpsm.setScanNumber(scanNum);
            wpsm.setIdPickerSpectrumMatch(psm);
            wpsm.setSpectrumMatch((T) res);
           
            wion.addMsSearchResult(wpsm);
        }
        return new ArrayList<WIdPickerPeptideIonWSpectra<T>>(ionMap.values());
    }
    
    public static List<WIdPickerPeptideIonWSpectra<SequestSearchResult>> getPeptideIonsWithSequestResults(int pinferProteinId) {
        
        IdPickerProtein protein = protDao.getProtein(pinferProteinId);
        List<IdPickerPeptide> peptides = protein.getPeptides();
        List<WIdPickerPeptideIonWSpectra<SequestSearchResult>> wIons = 
            new ArrayList<WIdPickerPeptideIonWSpectra<SequestSearchResult>>(peptides.size());
        for(IdPickerPeptide peptide: peptides) {
            WIdPickerPeptide wPept = new WIdPickerPeptide(peptide);
            List<Integer> matchingProteinGrpIds = peptDao.getMatchingProtGroupIds(protein.getProteinferId(), peptide.getGroupId());
            wPept.setIsUniqueToProteinGroup(matchingProteinGrpIds.size() == 1 ? true : false);
            List<WIdPickerPeptideIonWSpectra<SequestSearchResult>> ionList = getIonsForWPeptide(wPept, SearchProgram.SEQUEST);
            wIons.addAll(ionList);
        }
        return wIons;
    }
    
    public static List<WIdPickerPeptideIonWSpectra<ProlucidSearchResult>> getPeptideIonsWithProlucidResults(int pinferProteinId) {
        IdPickerProtein protein = protDao.getProtein(pinferProteinId);
        List<IdPickerPeptide> peptides = protein.getPeptides();
        List<WIdPickerPeptideIonWSpectra<ProlucidSearchResult>> wIons = 
            new ArrayList<WIdPickerPeptideIonWSpectra<ProlucidSearchResult>>(peptides.size());
        for(IdPickerPeptide peptide: peptides) {
            WIdPickerPeptide wPept = new WIdPickerPeptide(peptide);
            List<Integer> matchingProteinGrpIds = peptDao.getMatchingProtGroupIds(protein.getProteinferId(), peptide.getGroupId());
            wPept.setIsUniqueToProteinGroup(matchingProteinGrpIds.size() == 1 ? true : false);
            List<WIdPickerPeptideIonWSpectra<ProlucidSearchResult>> ionList = getIonsForWPeptide(wPept, SearchProgram.PROLUCID);
            wIons.addAll(ionList);
        }
        return wIons;
    }
    
    public static List<WIdPickerProteinGroup> getProteinferProteinGroups(int pinferId, PeptideDefinition peptideDef) {
        
        List<IdPickerProteinGroup> proteinGrps = protDao.getIdPickerProteinGroups(pinferId);
        
        List<WIdPickerProteinGroup> wProtGrps = new ArrayList<WIdPickerProteinGroup>(proteinGrps.size());
        
        for(IdPickerProteinGroup protGrp: proteinGrps) {
            
            WIdPickerProteinGroup wProtGrp = new WIdPickerProteinGroup(protGrp);
            wProtGrps.add(wProtGrp);
            
            for(WIdPickerProtein wProt: wProtGrp.getProteins()) {
                // set the description for the proteins.  This requires querying the 
                // NRSEQ database
                assignProteinAccessionDescription(wProt);
            }
            
            satisfyPeptideDefinition(wProtGrp, protGrp, peptideDef);
        }
        return wProtGrps;
    }
    
    private static void satisfyPeptideDefinition(WIdPickerProteinGroup wGroup, 
            IdPickerProteinGroup group, PeptideDefinition peptideDef) {
        // if we are using sequence only to define unique peptides don't do anything
        if(!peptideDef.isUseCharge() && !peptideDef.isUseMods())
            return;
        
        // update the peptide count and unique peptide counts based on the peptide definition
        int numPeptides = 0;
        int numUniqPeptides = 0;
        for(IdPickerPeptideGroup peptideGrp: group.getMatchingPeptideGroups()) {
            for(IdPickerPeptide peptide: peptideGrp.getPeptides()) {
                List<IdPickerPeptideIon> ions = getIonsForPeptide(peptide, peptideDef);
                numPeptides += ions.size();
                if(peptideGrp.isUniqueToProteinGroup())
                    numUniqPeptides += ions.size();
            }
        }
        wGroup.setMatchingPeptideCount(numPeptides);
        wGroup.setUniqMatchingPeptideCount(numUniqPeptides);
    }
    
    public static String getModifiedSequenceForSpectrumMatch(ProteinferSpectrumMatch psm) {
        MsSearchResult res = seqResDao.load(psm.getMsRunSearchResultId());
        String seq = res.getResultPeptide().getModifiedPeptideSequence();
        return removeTerminalResidues(seq);
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }


    public static WIdPickerCluster getProteinferCluster(int pinferId, int clusterId) {
        IdPickerCluster cluster = protDao.getIdPickerCluster(pinferId, clusterId);
        WIdPickerCluster wCluster = new WIdPickerCluster(pinferId, clusterId);
        wCluster.setPeptideGroups(cluster.getPeptideGroups());
        
        // set the accession and description for the proteins in this cluster
        List<WIdPickerProteinGroup> wProtGrps = new ArrayList<WIdPickerProteinGroup>(cluster.getProteinGroups().size());
        for(IdPickerProteinGroup protGrp: cluster.getProteinGroups()) {
            
            WIdPickerProteinGroup wProtGrp = new WIdPickerProteinGroup(protGrp);
            wProtGrps.add(wProtGrp);
            
            for(WIdPickerProtein wProt: wProtGrp.getProteins()) {
                // set the description for the proteins.  This requires querying the 
                // NRSEQ database
                assignProteinAccessionDescription(wProt);
            }
            
        }
        wCluster.setProteinGroups(wProtGrps);
        return wCluster;
    }
    
    public static List<WIdPickerSpectrumMatch<SequestSearchResult>> getSequestSpectrumMmatchesForRunSearch(int pinferId, int runSearchId) {
        
        List<Integer> psmIdList = specDao.getSpectrumMatchIdsForPinferRunAndRunSearch(pinferId, runSearchId);
        List<WIdPickerSpectrumMatch<SequestSearchResult>> wIdpPsmList = new ArrayList<WIdPickerSpectrumMatch<SequestSearchResult>>(psmIdList.size());
        for(Integer psmId: psmIdList) {
            IdPickerSpectrumMatch idpPsm = specDao.getSpectrumMatch(psmId);
            SequestSearchResult seqPsm = seqResDao.load(idpPsm.getMsRunSearchResultId());
            MsScan scan = scanDao.load(seqPsm.getScanId());
            WIdPickerSpectrumMatch<SequestSearchResult> widpPsm = new WIdPickerSpectrumMatch<SequestSearchResult>();
            widpPsm.setIdPickerSpectrumMatch(idpPsm);
            widpPsm.setScanNumber(scan.getStartScanNum());
            widpPsm.setSpectrumMatch(seqPsm);
            wIdpPsmList.add(widpPsm);
        }
        return wIdpPsmList;
    }
    
    public static List<WIdPickerSpectrumMatch<ProlucidSearchResult>> getProlucidSpectrumMmatchesForRunSearch(int pinferId, int runSearchId) {
        
        List<Integer> psmIdList = specDao.getSpectrumMatchIdsForPinferRunAndRunSearch(pinferId, runSearchId);
        List<WIdPickerSpectrumMatch<ProlucidSearchResult>> wIdpPsmList = new ArrayList<WIdPickerSpectrumMatch<ProlucidSearchResult>>(psmIdList.size());
        
        for(Integer psmId: psmIdList) {
            IdPickerSpectrumMatch idpPsm = specDao.getSpectrumMatch(psmId);
            ProlucidSearchResult seqPsm = plcResDao.load(idpPsm.getMsRunSearchResultId());
            MsScan scan = scanDao.load(seqPsm.getScanId());
            WIdPickerSpectrumMatch<ProlucidSearchResult> widpPsm = new WIdPickerSpectrumMatch<ProlucidSearchResult>();
            widpPsm.setIdPickerSpectrumMatch(idpPsm);
            widpPsm.setScanNumber(scan.getStartScanNum());
            widpPsm.setSpectrumMatch(seqPsm);
            wIdpPsmList.add(widpPsm);
        }
        return wIdpPsmList;
    }
    
}
