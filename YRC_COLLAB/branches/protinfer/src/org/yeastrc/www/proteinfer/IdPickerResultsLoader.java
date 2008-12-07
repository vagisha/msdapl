package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptide;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideIon;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideIonWSpectra;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
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
    private static final MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
    private static final SequestSearchResultDAO seqResDao = msDataDaoFactory.getSequestResultDAO();
    private static final ProlucidSearchResultDAO plcResDao = msDataDaoFactory.getProlucidResultDAO();
    private static final MsSearchResultDAO resDao = msDataDaoFactory.getMsSearchResultDAO();
    
    private static final IdPickerSpectrumMatchDAO specDao = pinferDaoFactory.getIdPickerSpectrumMatchDao();
    private static final IdPickerPeptideDAO peptDao = pinferDaoFactory.getIdPickerPeptideDao();
    private static final IdPickerProteinDAO protDao = pinferDaoFactory.getIdPickerProteinDao();
    private static final IdPickerRunDAO pinferRunDao = pinferDaoFactory.getIdPickerRunDao();
    
    private static final Logger log = Logger.getLogger(IdPickerResultsLoader.class);
    
    private IdPickerResultsLoader(){}
    
//    public static IdPickerParams getIDPickerParams(int pinferId) {
//        IdPickerParams params = new IdPickerParams();
//        
//        BaseProteinferRun<T> inferRun = pinferRunDao.getProteinferRun(pinferId);
//        List<IdPickerFilter> filters = inferRun.getFilters();
//        for(IdPickerFilter filter: filters) {
//            if(filter.getFilterName().equalsIgnoreCase("decoyRatio"))
//                params.setDecoyRatio(Float.parseFloat(filter.getFilterValue()));
//            else if (filter.getFilterName().equalsIgnoreCase("decoyPrefix"))
//                params.setDecoyPrefix(filter.getFilterValue());
//            else if (filter.getFilterName().equalsIgnoreCase("parsimonyAnalysis"))
//                params.setDoParsimonyAnalysis(Boolean.parseBoolean(filter.getFilterValue()));
//            else if (filter.getFilterName().equalsIgnoreCase("maxAbsFDR"))
//                params.setMaxAbsoluteFdr(Float.parseFloat(filter.getFilterValue()));
//            else if (filter.getFilterName().equalsIgnoreCase("maxRelativeFDR"))
//                params.setMaxRelativeFdr(Float.parseFloat(filter.getFilterValue()));
//                
//        }
//        return params;
//    }
    
//    public static IdPickerSummary getIDPickerInputSummary(int pinferId) {
//        
//        IdPickerSummary summary = new IdPickerSummary();
//        
//        GenericProteinferRun inferRunDao = pinferDaoFactory.getProteinferRunDao();
//        BaseProteinferRun<T> inferRun = inferRunDao.getProteinferRun(pinferId);
//        
//        summary.setNumTotalProteins(inferRun.getUnfilteredProteins());
//        
//        GenericProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
//        summary.setFilteredProteinsMinPeptCount(protDao.getFilteredProteinCount(pinferId));
//        summary.setNumFilteredProteinsParsimony(protDao.getFilteredParsimoniousProteinCount(pinferId));
//        
//        
//        List<ProteinferInput> inputList = inferRun.getInputSummaryList();
//        MsRunSearchDAO runSearchDao = msDataDaoFactory.getMsRunSearchDAO();
//        MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
//        
//        for(ProteinferInput input: inputList) {
//            MsRunSearch runSearch = runSearchDao.loadRunSearch(input.getRunSearchId());
//            String filename = runDao.loadFilenameNoExtForRun(runSearch.getRunId());
//            RunSearchSummary rs = new RunSearchSummary();
//            rs.setRunSearchId(input.getRunSearchId());
//            rs.setRunName(filename);
//            rs.setTotalTargetHits(input.getNumTargetHits());
//            rs.setTotalDecoyHits(input.getNumDecoyHits());
//            rs.setFilteredTargetHits(input.getNumFilteredTargetHits());
//            rs.setIsSelected(true);
//            summary.addRunSearch(rs);
//        }
//        return summary;
//    }
    
//    public static List<ProteinferProtein> getProteinferProteins(int pinferId) {
//        List<ProteinferProtein> proteins = protDao.getProteins(pinferId);
//        // set the description for the proteins.  This requires querying the 
//        // NRSEQ database
//        for(BaseProteinferProtein<T> prot: proteins) {
//            prot.getPeptides();
//            prot.getUniquePeptideCount();
//            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(prot.getNrseqProteinId());
//            prot.setDescription(dbProt.getDescription());
//        }
//        return proteins;
//    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferId, int pinferProteinId) {
        IdPickerProtein protein = protDao.getProtein(pinferProteinId);
        WIdPickerProtein wProt = new WIdPickerProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinAccessionDescription(wProt);
        return wProt;
    }

    private static void assignProteinAccessionDescription(WIdPickerProtein wProt) {
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        NRProtein nrseqProt = null;
        try {
            nrseqProt = (NRProtein)(nrpf.getProtein(wProt.getProtein().getNrseqProteinId()));
            wProt.setAccession(nrseqProt.getListing());
            wProt.setDescription(nrseqProt.getDescription());
        }
        catch (Exception e) {
            log.error("Exception getting nrseq protein for protein Id: "+wProt.getProtein().getNrseqProteinId(), e);
        }
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

            // separate by ion type(charge + modifications)
            String ionKey = modifiedSeq+"_chg"+charge;
            WIdPickerPeptideIonWSpectra<T> wion = ionMap.get(ionKey);
            if(wion == null) {
                IdPickerPeptideIon ion = new IdPickerPeptideIon();
                ion.setCharge(charge);
                ion.setGroupId(peptide.getPeptide().getGroupId());
                ion.setSequence(modifiedSeq);
                wion = new WIdPickerPeptideIonWSpectra<T>(ion);
                wion.setScanId(res.getScanId());
                wion.setIsUniqueToProteinGroup(peptide.getIsUniqueToProteinGroup());
                ionMap.put(ionKey, wion);
            }
            wion.getIon().addSpectrumMatch(psm);
            wion.addMsSearchResult((T) res);
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
    
    public static List<WIdPickerProteinGroup> getProteinferProteinGroups(int pinferId) {
        
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
        }
        return wProtGrps;
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


    
//    public static InferredProtein<SequestSpectrumMatch> getInferredProtein(int pinferId, int nrseqProtId) {
//        
//        BaseProteinferProtein<T> pProt = protDao.getProteinferProtein(pinferId, nrseqProtId);
//        Protein prot = new Protein(pProt.getAccession(), pProt.getNrseqProteinId());
//        prot.setAccepted(pProt.getIsParsimonious());
//        prot.setProteinClusterId(pProt.getClusterId());
//        prot.setProteinGroupId(pProt.getGroupId());
//        InferredProtein<SequestSpectrumMatch> iProt = new InferredProtein<SequestSpectrumMatch>(prot);
//        iProt.setPercentCoverage((float) pProt.getCoverage());
//        
//        
//        // add the peptide evidences
//        for(BaseProteinferPeptide<T> pPept: pProt.getPeptides()) {
//            
//            Peptide pept = new Peptide(getModifiedSequenceForPeptide(pPept), pPept.getId());
//            pept.setPeptideGroupId(pPept.getGroupId());
//            
//            PeptideEvidence<SequestSpectrumMatch> pev = new PeptideEvidence<SequestSpectrumMatch>(pept);
//            pev.setBestFdr(pPept.getBestFdr());
//            pev.setProteinMatchCount(pPept.getMatchingProteinIds().size());
//            
//            for(ProteinferSpectrumMatch psm: pPept.getSpectrumMatchList()) {
//                SequestSpectrumMatch ssm = getSequestSearchResult(psm, null); // SearchSource = null
//                pev.addSpectrumMatch(ssm);
//            }
//            
//            // get the description for this protein
//            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(pProt.getNrseqProteinId());
//            iProt.setDescription(dbProt.getDescription());
//            
//            iProt.addPeptideEvidence(pev);
//        }
//        return iProt;
//    }
    
//    public static List<ProteinferProtein> getGroupProteins(int pinferId, int groupId) {
//        return protDao.getProteinferGroupProteins(pinferId, groupId);
//    }
//    
//    public static IdPickerCluster getProteinferCluster(int pinferId, int clusterId) {
//        IdPickerCluster cluster = protDao.getProteinferCluster(pinferId, clusterId);
//        // set the sequence on each of the peptides in this cluster
//        List<IdPickerPeptideGroup> peptGrps = cluster.getPeptideGroups();
//        for(IdPickerPeptideGroup grp: peptGrps) {
//            List<ProteinferPeptide> peptList = grp.getPeptides();
//            for(BaseProteinferPeptide<T> pept: peptList) {
//                pept.setSequence(getModifiedSequenceForPeptide(pept));
//            }
//        }
//        return cluster;
//    }
    
//    public static List<SequestSpectrumMatch> getSpectrummatchesForRunSearch(int pinferId, int runSearchId) {
//        
//        // get the filename
//        String filename = runDao.loadFilenameNoExtForRun(rsDao.loadRunSearch(runSearchId).getRunId());
//        
//        List<Integer> resultIds = seqResDao.loadTopResultIdsForRunSearch(runSearchId);
//        
//        List<Integer> resultIdsForRunSearch = specDao.getSpectrumMatchIdsForPinferRun(pinferId);
//        Collections.sort(resultIdsForRunSearch);
//        
//        List<Integer> resultIdsForPinferAndRunSearch = new ArrayList<Integer>();
//        for(Integer id: resultIds) {
//            int idx = Collections.binarySearch(resultIdsForRunSearch, id);
//            if (idx >= 0)
//                resultIdsForPinferAndRunSearch.add(id);
//        }
//        
//        List<ProteinferSpectrumMatch> psmList = new ArrayList<ProteinferSpectrumMatch>(resultIdsForPinferAndRunSearch.size());
//        for(Integer id: resultIdsForPinferAndRunSearch) {
//            ProteinferSpectrumMatch psm = specDao.getSpectrumMatchForMsResult(pinferId, id);
//            if(psm == null) {
//                System.out.println("No match found for pinferId: "+pinferId+" and msRunSearchResultId: "+id);
//            }
//            else {
//                psmList.add(psm);
//            }
//        }
//        
//        List<SequestSpectrumMatch> seqMatchList = new ArrayList<SequestSpectrumMatch>(psmList.size());
//        SearchSource source = new SearchSource(filename, runSearchId);
//        
//        
//        for(ProteinferSpectrumMatch psm: psmList) {
//            seqMatchList.add(getSequestSearchResult(psm, source));
//        }
//        
//        return seqMatchList;
//    }
    
    
//    private static SequestSpectrumMatch getSequestSearchResult(ProteinferSpectrumMatch psm, SearchSource source) {
//        SequestSearchResult seqRes = seqResDao.load(psm.getMsRunSearchResultId());
//        SequestResultData data = seqRes.getSequestResultData();
//        int scanNumber = scanDao.load(seqRes.getScanId()).getStartScanNum();
//        String modiSeq = getModifiedSequenceForSpectrumMatch(psm);
////        String modiSeq = seqRes.getResultPeptide().getModifiedPeptideSequence();
//        
//        SequestSpectrumMatch seqM = new SequestSpectrumMatch(source, scanNumber, seqRes.getCharge(), modiSeq);
//        seqM.setFdr(psm.getFdrRounded());
//        seqM.setXcorr(data.getxCorr());
//        seqM.setDeltaCn(data.getDeltaCN());
//        seqM.setHitId(psm.getMsRunSearchResultId());
//        seqM.setScanId(seqRes.getScanId());
//        return seqM;
//    }
//
//    public static Map<Integer, SequestSpectrumMatch> getBestSpectrumMatches(List<IdPickerProteinGroup> proteinGroups) {
//        Map<Integer, SequestSpectrumMatch> psmMap = new HashMap<Integer, SequestSpectrumMatch>();
//        for(IdPickerProteinGroup grp: proteinGroups) {
//            for(IdPickerPeptideGroup peptGrp: grp.getMatchingPeptideGroups()) {
//                for(BaseProteinferPeptide<T> pept: peptGrp.getPeptides()) {
//                    ProteinferSpectrumMatch bestPsm = pept.getBestSpectrumMatch();
//                    if(psmMap.get(bestPsm.getMsRunSearchResultId()) == null) {
//                        SequestSpectrumMatch ssm = getSequestSearchResult(bestPsm, null);
//                        psmMap.put(bestPsm.getMsRunSearchResultId(), ssm);
//                    }
//                }
//            }
//        }
//        return psmMap;
//    }
}
