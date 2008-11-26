package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferCluster;
import edu.uwpr.protinfer.database.dto.ProteinferFilter;
import edu.uwpr.protinfer.database.dto.ProteinferInput;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferPeptideGroup;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferProteinGroup;
import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.SearchSummary;
import edu.uwpr.protinfer.idpicker.SearchSummary.RunSearch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.SearchSource;

public class ProteinferLoader {

    private static final DAOFactory pinferDaoFactory = DAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
    private static final SequestSearchResultDAO seqResDao = msDataDaoFactory.getSequestResultDAO();
    private static final MsSearchResultDAO resDao = msDataDaoFactory.getMsSearchResultDAO();
    private static final ProteinferSpectrumMatchDAO specDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final ProteinferPeptideDAO peptDao = pinferDaoFactory.getProteinferPeptideDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final ProteinferRunDAO pinferRunDao = pinferDaoFactory.getProteinferRunDao();
    
    private ProteinferLoader(){}
    
    public static IDPickerParams getIDPickerParams(int pinferId) {
        IDPickerParams params = new IDPickerParams();
        
        ProteinferRun inferRun = pinferRunDao.getProteinferRun(pinferId);
        List<ProteinferFilter> filters = inferRun.getFilters();
        for(ProteinferFilter filter: filters) {
            if(filter.getFilterName().equalsIgnoreCase("decoyRatio"))
                params.setDecoyRatio(Float.parseFloat(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("decoyPrefix"))
                params.setDecoyPrefix(filter.getFilterValue());
            else if (filter.getFilterName().equalsIgnoreCase("parsimonyAnalysis"))
                params.setDoParsimonyAnalysis(Boolean.parseBoolean(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("maxAbsFDR"))
                params.setMaxAbsoluteFdr(Float.parseFloat(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("maxRelativeFDR"))
                params.setMaxRelativeFdr(Float.parseFloat(filter.getFilterValue()));
                
        }
        return params;
    }
    
    public static SearchSummary getIDPickerInputSummary(int pinferId) {
        
        SearchSummary summary = new SearchSummary();
        
        ProteinferRunDAO inferRunDao = pinferDaoFactory.getProteinferRunDao();
        ProteinferRun inferRun = inferRunDao.getProteinferRun(pinferId);
        
        summary.setAllProteins(inferRun.getUnfilteredProteins());
        
        ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
        summary.setFilteredProteinsMinPeptCount(protDao.getFilteredProteinCount(pinferId));
        summary.setFilteredProteinsParsimony(protDao.getFilteredParsimoniousProteinCount(pinferId));
        
        
        List<ProteinferInput> inputList = inferRun.getInputSummaryList();
        MsRunSearchDAO runSearchDao = msDataDaoFactory.getMsRunSearchDAO();
        MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
        
        for(ProteinferInput input: inputList) {
            MsRunSearch runSearch = runSearchDao.loadRunSearch(input.getRunSearchId());
            String filename = runDao.loadFilenameNoExtForRun(runSearch.getRunId());
            RunSearch rs = new RunSearch();
            rs.setRunSearchId(input.getRunSearchId());
            rs.setRunName(filename);
            rs.setTotalTargetHits(input.getNumTargetHits());
            rs.setTotalDecoyHits(input.getNumDecoyHits());
            rs.setFilteredTargetHits(input.getNumFilteredTargetHits());
            rs.setIsSelected(true);
            summary.addRunSearch(rs);
        }
        return summary;
    }
    
    public static List<ProteinferProtein> getProteinferProteins(int pinferId) {
        List<ProteinferProtein> proteins = protDao.getProteinferProteins(pinferId);
        // set the description for the proteins.  This requires querying the 
        // NRSEQ database
        for(ProteinferProtein prot: proteins) {
            prot.getPeptides();
            prot.getUniquePeptideCount();
            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(prot.getNrseqProteinId());
            prot.setDescription(dbProt.getDescription());
        }
        return proteins;
    }
    
    public static List<ProteinferProteinGroup> getProteinferProteinGroups(int pinferId) {
        List<ProteinferProteinGroup> proteinGrps = protDao.getProteinferProteinGroups(pinferId);
        
        Map<Integer, String> peptSeqMap = new HashMap<Integer, String>();
        
        for(ProteinferProteinGroup protGrp: proteinGrps) {
            for(ProteinferProtein prot: protGrp.getProteins()) {
                //prot.getUniquePeptideCount();
                // set the description for the proteins.  This requires querying the 
                // NRSEQ database
                NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(prot.getNrseqProteinId());
                prot.setDescription(dbProt.getDescription());
            }
            
            // set the modified sequence for each peptide in the group
            List<ProteinferPeptideGroup> peptGrps = protGrp.getMatchingPeptideGroups();
            for(ProteinferPeptideGroup peptGrp: peptGrps) {
                for(ProteinferPeptide pept: peptGrp.getPeptides()) {
                    String seq = peptSeqMap.get(pept.getId());
                    if(seq == null) {
                        seq = getModifiedSequenceForPeptide(pept);
                    }
                    pept.setSequence(seq);

                    // get the best peptide spectrum match and set its scanID
                    MsSearchResult res = getMsSearchResult(pept.getBestSpectrumMatch());
                    pept.getBestSpectrumMatch().setScanId(res.getScanId());
                }
            }
        }
        return proteinGrps;
    }
    
    public static List<String> getUnmodifiedPeptidesForProtein(int pinferId, int nrseqProtId) {
        ProteinferProtein protein = protDao.getProteinferProtein(pinferId, nrseqProtId);
        Set<String> peptides = new HashSet<String>(protein.getPeptides().size());
        for(ProteinferPeptide peptide: protein.getPeptides()) {
            peptides.add(getUnModifiedSequenceForPeptide(peptide));
        }
        return new ArrayList<String>(peptides);
    }
    
    public static String getModifiedSequenceForPeptide(ProteinferPeptide peptide) {
        // get the first hit
        ProteinferSpectrumMatch psm = peptide.getSpectrumMatchList().get(0);
        return getModifiedSequenceForSpectrumMatch(psm);
    }
    
    public static String getModifiedSequenceForSpectrumMatch(ProteinferSpectrumMatch psm) {
        MsSearchResult res = seqResDao.load(psm.getMsRunSearchResultId());
        String seq = res.getResultPeptide().getModifiedPeptideSequence();
        int f = seq.indexOf('.');
        int l = seq.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? seq.length() : l;
        return seq.substring(f, l);
    }
    
    public static String getUnModifiedSequenceForPeptide(ProteinferPeptide peptide) {
        // get the first hit
        ProteinferSpectrumMatch psm = peptide.getSpectrumMatchList().get(0);
        MsSearchResult res = seqResDao.load(psm.getMsRunSearchResultId());
        String seq = res.getResultPeptide().getPeptideSequence();
        int f = seq.indexOf('.');
        int l = seq.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? seq.length() : l;
        return seq.substring(f, l);
    }
    
    public static InferredProtein<SequestSpectrumMatch> getInferredProtein(int pinferId, int nrseqProtId) {
        
        ProteinferProtein pProt = protDao.getProteinferProtein(pinferId, nrseqProtId);
        Protein prot = new Protein(pProt.getAccession(), pProt.getNrseqProteinId());
        prot.setAccepted(pProt.getIsParsimonious());
        prot.setProteinClusterId(pProt.getClusterId());
        prot.setProteinGroupId(pProt.getGroupId());
        InferredProtein<SequestSpectrumMatch> iProt = new InferredProtein<SequestSpectrumMatch>(prot);
        iProt.setPercentCoverage((float) pProt.getCoverage());
        
        
        // add the peptide evidences
        for(ProteinferPeptide pPept: pProt.getPeptides()) {
            
            Peptide pept = new Peptide(getModifiedSequenceForPeptide(pPept), pPept.getId());
            pept.setPeptideGroupId(pPept.getGroupId());
            
            PeptideEvidence<SequestSpectrumMatch> pev = new PeptideEvidence<SequestSpectrumMatch>(pept);
            pev.setBestFdr(pPept.getBestFdr());
            pev.setProteinMatchCount(pPept.getMatchingProteinIds().size());
            
            for(ProteinferSpectrumMatch psm: pPept.getSpectrumMatchList()) {
                SequestSpectrumMatch ssm = getSequestSearchResult(psm, null); // SearchSource = null
                pev.addSpectrumMatch(ssm);
            }
            
            // get the description for this protein
            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(pProt.getNrseqProteinId());
            iProt.setDescription(dbProt.getDescription());
            
            iProt.addPeptideEvidence(pev);
        }
        return iProt;
    }
    
    public static List<ProteinferProtein> getGroupProteins(int pinferId, int groupId) {
        return protDao.getProteinferGroupProteins(pinferId, groupId);
    }
    
    public static ProteinferCluster getProteinferCluster(int pinferId, int clusterId) {
        ProteinferCluster cluster = protDao.getProteinferCluster(pinferId, clusterId);
        // set the sequence on each of the peptides in this cluster
        List<ProteinferPeptideGroup> peptGrps = cluster.getPeptideGroups();
        for(ProteinferPeptideGroup grp: peptGrps) {
            List<ProteinferPeptide> peptList = grp.getPeptides();
            for(ProteinferPeptide pept: peptList) {
                pept.setSequence(getModifiedSequenceForPeptide(pept));
            }
        }
        return cluster;
    }
    
    public static List<SequestSpectrumMatch> getSpectrummatchesForRunSearch(int pinferId, int runSearchId) {
        
        // get the filename
        String filename = runDao.loadFilenameNoExtForRun(rsDao.loadRunSearch(runSearchId).getRunId());
        
        List<Integer> resultIds = seqResDao.loadTopResultIdsForRunSearch(runSearchId);
        
        List<Integer> resultIdsForRunSearch = specDao.getSpectrumMatchIdsForProteinferRun(pinferId);
        Collections.sort(resultIdsForRunSearch);
        
        List<Integer> resultIdsForPinferAndRunSearch = new ArrayList<Integer>();
        for(Integer id: resultIds) {
            int idx = Collections.binarySearch(resultIdsForRunSearch, id);
            if (idx >= 0)
                resultIdsForPinferAndRunSearch.add(id);
        }
        
        List<ProteinferSpectrumMatch> psmList = new ArrayList<ProteinferSpectrumMatch>(resultIdsForPinferAndRunSearch.size());
        for(Integer id: resultIdsForPinferAndRunSearch) {
            ProteinferSpectrumMatch psm = specDao.getSpectrumMatchForMsResult(pinferId, id);
            if(psm == null) {
                System.out.println("No match found for pinferId: "+pinferId+" and msRunSearchResultId: "+id);
            }
            else {
                psmList.add(psm);
            }
        }
        
        List<SequestSpectrumMatch> seqMatchList = new ArrayList<SequestSpectrumMatch>(psmList.size());
        SearchSource source = new SearchSource(filename, runSearchId);
        
        
        for(ProteinferSpectrumMatch psm: psmList) {
            seqMatchList.add(getSequestSearchResult(psm, source));
        }
        
        return seqMatchList;
    }
    
    private static MsSearchResult getMsSearchResult(ProteinferSpectrumMatch psm) {
        return resDao.load(psm.getMsRunSearchResultId());
    }
    
    private static SequestSpectrumMatch getSequestSearchResult(ProteinferSpectrumMatch psm, SearchSource source) {
        SequestSearchResult seqRes = seqResDao.load(psm.getMsRunSearchResultId());
        SequestResultData data = seqRes.getSequestResultData();
        int scanNumber = scanDao.load(seqRes.getScanId()).getStartScanNum();
        String modiSeq = getModifiedSequenceForSpectrumMatch(psm);
//        String modiSeq = seqRes.getResultPeptide().getModifiedPeptideSequence();
        
        SequestSpectrumMatch seqM = new SequestSpectrumMatch(source, scanNumber, seqRes.getCharge(), modiSeq);
        seqM.setFdr(psm.getFdrRounded());
        seqM.setXcorr(data.getxCorr());
        seqM.setDeltaCn(data.getDeltaCN());
        seqM.setHitId(psm.getMsRunSearchResultId());
        seqM.setScanId(seqRes.getScanId());
        return seqM;
    }

    public static Map<Integer, SequestSpectrumMatch> getBestSpectrumMatches(List<ProteinferProteinGroup> proteinGroups) {
        Map<Integer, SequestSpectrumMatch> psmMap = new HashMap<Integer, SequestSpectrumMatch>();
        for(ProteinferProteinGroup grp: proteinGroups) {
            for(ProteinferPeptideGroup peptGrp: grp.getMatchingPeptideGroups()) {
                for(ProteinferPeptide pept: peptGrp.getPeptides()) {
                    ProteinferSpectrumMatch bestPsm = pept.getBestSpectrumMatch();
                    if(psmMap.get(bestPsm.getMsRunSearchResultId()) == null) {
                        SequestSpectrumMatch ssm = getSequestSearchResult(bestPsm, null);
                        psmMap.put(bestPsm.getMsRunSearchResultId(), ssm);
                    }
                }
            }
        }
        return psmMap;
    }
}
