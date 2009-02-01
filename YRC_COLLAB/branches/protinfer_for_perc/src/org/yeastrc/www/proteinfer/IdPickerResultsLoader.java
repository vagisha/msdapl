package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIon;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIonWAllSpectra;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerSpectrumMatch;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerIon;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideBase;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.util.TimeUtils;

public class IdPickerResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = msDataDaoFactory.getMsRunSearchAnalysisDAO();
    
    private static final SequestSearchResultDAO seqResDao = msDataDaoFactory.getSequestResultDAO();
    private static final ProlucidSearchResultDAO plcResDao = msDataDaoFactory.getProlucidResultDAO();
    private static final PercolatorResultDAO percResDao = msDataDaoFactory.getPercolatorResultDAO();
    
    private static final ProteinferSpectrumMatchDAO psmDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final IdPickerSpectrumMatchDAO idpPsmDao = pinferDaoFactory.getIdPickerSpectrumMatchDao();
    private static final IdPickerPeptideDAO idpPeptDao = pinferDaoFactory.getIdPickerPeptideDao();
    private static final IdPickerPeptideBaseDAO idpPeptBaseDao = pinferDaoFactory.getIdPickerPeptideBaseDao();
    private static final IdPickerProteinBaseDAO idpProtBaseDao = pinferDaoFactory.getIdPickerProteinBaseDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final IdPickerInputDAO inputDao = pinferDaoFactory.getIdPickerInputDao();
    private static final IdPickerRunDAO idpRunDao = pinferDaoFactory.getIdPickerRunDao();
    
    private static final Logger log = Logger.getLogger(IdPickerResultsLoader.class);
    
    private IdPickerResultsLoader(){}
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given filtering criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
        long s = System.currentTimeMillis();
        List<Integer> proteinIds = idpProtBaseDao.getFilteredSortedProteinIds(pinferId, filterCriteria);
        log.info("Returned "+proteinIds.size()+" protein IDs for protein inference ID: "+pinferId);
        long e = System.currentTimeMillis();
        log.info("Time: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteinIds;
    }
    
    public static List<Integer> filterByProteinAccession(
            List<Integer> allProteinIds,
            Map<Integer, String> proteinAccessionMap, String accessionLike) {
        
        Set<String> reqAcc = new HashSet<String>();
        String[] tokens = accessionLike.split(",");
        for(String tok: tokens)
            reqAcc.add(tok.trim().toLowerCase());
        
        List<Integer> filtered = new ArrayList<Integer>();
        for(int id: allProteinIds) {
            String acc = proteinAccessionMap.get(id);
            if(acc != null) acc = acc.toLowerCase();
            // first check if the exact accession is given to us
            if(reqAcc.contains(acc)) {
                filtered.add(id);
                continue;
            }
            // we may have a partial accession
            for(String ra: reqAcc) {
                if(acc.contains(ra)) {
                    filtered.add(id);
                    break;
                }
            }
        }
        return filtered;
    }

    public static List<Integer> filterByProteinDescription(int pinferId,
            List<Integer> storedProteinIds, String descriptionLike) {
        
//        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
//        List<IdPickerInput> inputList = run.getInputList();
//        Set<Integer> searchIds = new HashSet<Integer>();
//        for(IdPickerInput input: inputList) {
//            if(input.getInputType() == InputType.SEARCH) { // this is a runSearchID
//                MsRunSearch rs = rsDao.loadRunSearch(input.getInputId());
//                if(rs == null) {
//                    log.error("No run search found with ID: "+input.getInputId());
//                    throw new IllegalArgumentException("No run search found with ID: "+input.getInputId());
//                }
//                searchIds.add(rs.getSearchId());
//            }
//            else if(input.getInputType() == InputType.ANALYSIS) { // this is a runSearchAnalysisID
//                MsRunSearchAnalysis rsa = rsaDao.load(input.getInputId());
//                if(rsa == null) {
//                    log.error("No run search analysis found with ID: "+input.getInputId());
//                    throw new IllegalArgumentException("No run search analysis found with ID: "+input.getInputId());
//                }
//            }
//        }
        
        List<ProteinferProtein> proteins = protDao.loadProteins(pinferId);
        Set<Integer> accepted = new HashSet<Integer>();
        descriptionLike = descriptionLike.toLowerCase();
        for(ProteinferProtein protein: proteins) {
            String[] acc_descr = getProteinAccessionDescription(protein.getNrseqProteinId());
            if(acc_descr[1] != null) {
                String protDescr = acc_descr[1].toLowerCase();
                if(protDescr.contains(descriptionLike))
                    accepted.add(protein.getId());
            }
        }
        
        List<Integer> acceptedProteinIds = new ArrayList<Integer>(accepted.size());
        for(int id: storedProteinIds) {
            if(accepted.contains(id))
                acceptedProteinIds.add(id);
        }
        return acceptedProteinIds;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given sorting criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getSortedProteinIds(int pinferId, PeptideDefinition peptideDef, 
            List<Integer> proteinIds, SORT_BY sortBy, boolean groupProteins) {
        
        long s = System.currentTimeMillis();
        List<Integer> allIds = null;
        if(sortBy == SORT_BY.CLUSTER_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByCluster(pinferId);
        }
        else if (sortBy == SORT_BY.GROUP_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByGroup(pinferId);
        }
        else if (sortBy == SORT_BY.COVERAGE) {
            allIds = idpProtBaseDao.sortProteinIdsByCoverage(pinferId);
        }
        else if (sortBy == SORT_BY.NUM_PEPT) {
            allIds = idpProtBaseDao.sortProteinIdsByPeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_UNIQ_PEPT) {
            allIds = idpProtBaseDao.sortProteinIdsByUniquePeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_SPECTRA) {
            allIds = idpProtBaseDao.sortProteinIdsBySpectrumCount(pinferId, groupProteins);
        }
//        else if (sortBy == SORT_BY.ACCESSION) {
//            allIds = sortIdsByAccession(proteinIds);
//        }
        if(allIds == null) {
            log.warn("Could not get sorted order for all protein IDs for protein inference run: "+pinferId);
        }
        
        // we want the sorted order from allIds but only want to keep the ids in the current 
        // filtered list.
        // remove the ones from allIds that are not in the current filtered list. 
        Set<Integer> currentOrder = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        currentOrder.addAll(proteinIds);
        Iterator<Integer> iter = allIds.iterator();
        while(iter.hasNext()) {
            Integer protId = iter.next();
            if(!currentOrder.contains(protId))
                iter.remove();
        }
        
        long e = System.currentTimeMillis();
        log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return allIds;
    }
    
    public static Map<Integer, String> getProteinAccessionMap(int pinferId) {
        List<ProteinferProtein> proteins = protDao.loadProteins(pinferId);
        Map<Integer, String> map = new HashMap<Integer, String>((int) (proteins.size() * 1.5));
        
        for(ProteinferProtein protein: proteins) {
            String[] acc_descr = getProteinAccessionDescription(protein.getNrseqProteinId());
            map.put(protein.getId(), acc_descr[0]);
        }
        return map;
    }
    
    public static List<Integer> sortIdsByAccession(List<Integer> proteinIds, Map<Integer, String> proteinAccessionMap) {
        
        List<ProteinIdAccession> accMap = new ArrayList<ProteinIdAccession>(proteinIds.size());
        
        for(int id: proteinIds) {
            accMap.add(new ProteinIdAccession(id, proteinAccessionMap.get(id)));
        }
        Collections.sort(accMap, new Comparator<ProteinIdAccession>() {
            public int compare(ProteinIdAccession o1, ProteinIdAccession o2) {
                return o1.accession.compareTo(o2.accession);
            }});
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        for(ProteinIdAccession pa: accMap) {
            sortedIds.add(pa.proteinId);
        }
        return sortedIds;
    }
    
    
    
    private static class ProteinIdAccession {
        int proteinId;
        String accession;
        public ProteinIdAccession(int proteinId, String accession) {
            this.proteinId = proteinId;
            this.accession = accession;
        }
    }

    //---------------------------------------------------------------------------------------------------
    // Get a list of proteins
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getProteins(int pinferId, List<Integer> proteinIds, PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(proteinIds.size());
        for(int id: proteinIds) 
            proteins.add(getIdPickerProtein(pinferId, id, peptideDef));
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerProteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteins;
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferId, int pinferProteinId, PeptideDefinition peptideDef) {
        IdPickerProteinBase protein = idpProtBaseDao.loadProtein(pinferProteinId);
        protein.setPeptideDefinition(peptideDef);
        return getWIdPickerProtein(protein);
    }
    
    private static WIdPickerProtein getWIdPickerProtein(IdPickerProteinBase protein) {
        WIdPickerProtein wProt = new WIdPickerProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinAccessionDescription(wProt);
        return wProt;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all the proteins in a group
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getGroupProteins(int pinferId, int groupId, PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        
        List<IdPickerProteinBase> groupProteins = idpProtBaseDao.loadIdPickerGroupProteins(pinferId, groupId);
        
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(groupProteins.size());
        
        for(IdPickerProteinBase prot: groupProteins) {
            prot.setPeptideDefinition(peptideDef);
            proteins.add(getWIdPickerProtein(prot));
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get proteins in a group: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein groups
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProteinGroup> getProteinGroups(int pinferId, List<Integer> proteinIds, PeptideDefinition peptideDef) {
        return getProteinGroups(pinferId, proteinIds, true, peptideDef);
    }
    
    public static List<WIdPickerProteinGroup> getProteinGroups(int pinferId, List<Integer> proteinIds, boolean append, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = getProteins(pinferId, proteinIds, peptideDef);
        
        if(proteins.size() == 0) {
            return new ArrayList<WIdPickerProteinGroup>(0);
        }
        
        if(append) {
            // protein Ids should be sorted by groupID. If the proteins at the top of the list
            // does not have all members of the group in the list, add them
            int groupId_top = proteins.get(0).getProtein().getGroupId();
            List<IdPickerProteinBase> groupProteins = idpProtBaseDao.loadIdPickerGroupProteins(pinferId, groupId_top);
            for(IdPickerProteinBase prot: groupProteins) {
                if(!proteinIds.contains(prot.getId())) {
                    prot.setPeptideDefinition(peptideDef);
                    proteins.add(0, getWIdPickerProtein(prot));
                }
            }

            // protein Ids should be sorted by groupID. If the proteins at the bottom of the list
            // does not have all members of the group in the list, add them
            int groupId_last = proteins.get(proteins.size() - 1).getProtein().getGroupId();
            if(groupId_last != groupId_top) {
                groupProteins = idpProtBaseDao.loadIdPickerGroupProteins(pinferId, groupId_last);
                for(IdPickerProteinBase prot: groupProteins) {
                    if(!proteinIds.contains(prot.getId())) {
                        prot.setPeptideDefinition(peptideDef);
                        proteins.add(getWIdPickerProtein(prot));
                    }
                }
            }
        }
       
        if(proteins.size() == 0)
            return new ArrayList<WIdPickerProteinGroup>(0);
        
        int currGrpId = -1;
        List<WIdPickerProtein> grpProteins = null;
        List<WIdPickerProteinGroup> groups = new ArrayList<WIdPickerProteinGroup>();
        for(WIdPickerProtein prot: proteins) {
            if(prot.getProtein().getGroupId() != currGrpId) {
                if(grpProteins != null && grpProteins.size() > 0) {
                    WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
                    groups.add(grp);
                }
                currGrpId = prot.getProtein().getGroupId();
                grpProteins = new ArrayList<WIdPickerProtein>();
            }
            grpProteins.add(prot);
        }
        if(grpProteins != null && grpProteins.size() > 0) {
            WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
            groups.add(grp);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerProteinsGroups: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return groups;
    }
    
    //---------------------------------------------------------------------------------------------------
    // NR_SEQ lookup 
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinAccessionDescription(WIdPickerProtein wProt) {
        
        String[] acc_descr = getProteinAccessionDescription(wProt.getProtein().getNrseqProteinId());
        wProt.setAccession(acc_descr[0]);
        wProt.setDescription(acc_descr[1]);
    }
    
    private static String[] getProteinAccessionDescription(int nrseqProteinId) {
        
        NrDbProtein nrDbProt = NrSeqLookupUtil.getDbProtein(nrseqProteinId);
        return new String[] {nrDbProt.getAccessionString(), nrDbProt.getDescription()};
        
//      NRProteinFactory nrpf = NRProteinFactory.getInstance();
//      NRProtein nrseqProt = null;
//      try {
//          nrseqProt = (NRProtein)(nrpf.getProtein(wProt.getProtein().getNrseqProteinId()));
//          wProt.setAccession(nrseqProt.getListing());
//          wProt.setDescription(nrseqProt.getDescription());
//      }
//      catch (Exception e) {
//          log.error("Exception getting nrseq protein for protein Id: "+wProt.getProtein().getNrseqProteinId(), e);
//      }
        
    }
    
    //---------------------------------------------------------------------------------------------------
    // IDPicker input summary
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerInputSummary> getIDPickerInputSummary(int pinferId) {
        
        List<IdPickerInput> inputSummary = inputDao.loadProteinferInputList(pinferId);
        List<WIdPickerInputSummary> wInputList = new ArrayList<WIdPickerInputSummary>(inputSummary.size());
        
        for(IdPickerInput input: inputSummary) {
            String filename = rsDao.loadFilenameForRunSearch(input.getInputId());
            WIdPickerInputSummary winput = new WIdPickerInputSummary(input);
            winput.setFileName(filename);
            wInputList.add(winput);
        }
        return wInputList;
    }
    
    //---------------------------------------------------------------------------------------------------
    // IDPicker input summary
    //---------------------------------------------------------------------------------------------------
    public static WIdPickerResultSummary getIdPickerResultSummary(int pinferId, List<Integer> proteinIds) {
        
        long s = System.currentTimeMillis();
        
        WIdPickerResultSummary summary = new WIdPickerResultSummary();
//        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
//        summary.setUnfilteredProteinCount(run.getNumUnfilteredProteins());
        summary.setFilteredProteinCount(proteinIds.size());
        // parsimonious protein IDs
        List<Integer> parsimProteinIds = idpProtBaseDao.getIdPickerProteinIds(pinferId, true);
        Map<Integer, Integer> protGroupMap = idpProtBaseDao.getProteinGroupIds(pinferId, false);
        
        
        Set<Integer> groupIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        for(int id: proteinIds) {
            groupIds.add(protGroupMap.get(id));
        }
        summary.setFilteredProteinGroupCount(groupIds.size());
        
        groupIds.clear();
        int parsimCount = 0;
        Set<Integer> myIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        myIds.addAll(proteinIds);
        for(int id: parsimProteinIds) {
            if(myIds.contains(id))  {
                parsimCount++;
                groupIds.add(protGroupMap.get(id));
            }
        }
        summary.setFilteredParsimoniousProteinCount(parsimCount);
        summary.setFilteredParsimoniousProteinGroupCount(groupIds.size());
        
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerResultSummary: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return summary;
    }

    //---------------------------------------------------------------------------------------------------
    // Cluster Ids in the given protein inference run
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getClusterIds(int pinferId) {
        return idpProtBaseDao.getClusterIds(pinferId);
    }

    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a protein group (sorted by sequence, modification state and charge
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerIon> getPeptideIonsForProteinGroup(int pinferId, int pinferProteinGroupId) {
        
        long s = System.currentTimeMillis();
        
        List<WIdPickerIon> ionList = new ArrayList<WIdPickerIon>();
        
        // get the id of one of the proteins in the group. All proteins in a group match the same peptides
        int proteinId = idpProtBaseDao.getIdPickerGroupProteinIds(pinferId, pinferProteinGroupId).get(0);
        
        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        
        if(pinferProgram == ProteinInferenceProgram.IDPICKER) {
            List<IdPickerPeptide> peptides = idpPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptide peptide: peptides) {
                List<IdPickerIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(IdPickerIon ion: ions) {
                    WIdPickerIon wIon = makeWIdPickerIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else if (pinferProgram == ProteinInferenceProgram.IDPICKER_PERC) {
            List<IdPickerPeptideBase> peptides = idpPeptBaseDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptideBase peptide: peptides) {
                List<ProteinferIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinferIon ion: ions) {
                    WIdPickerIon wIon = makeWIdPickerIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions for pinferID: "+pinferId+
                ", proteinGroupID: "+pinferProteinGroupId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }

    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
            WIdPickerIon makeWIdPickerIon(I ion, Program inputGenerator) {
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = getOriginalResult(psm.getMsRunSearchResultId(), inputGenerator);
        return new WIdPickerIon(ion, origResult);
    }

    private static void sortIonList(List<? extends GenericProteinferIon<?>> ions) {
        Collections.sort(ions, new Comparator<GenericProteinferIon<?>>() {
            public int compare(GenericProteinferIon<?> o1, GenericProteinferIon<?> o2) {
                if(o1.getModificationStateId() < o2.getModificationStateId())   return -1;
                if(o1.getModificationStateId() > o2.getModificationStateId())   return 1;
                if(o1.getCharge() < o2.getCharge())                             return -1;
                if(o2.getCharge() > o2.getCharge())                             return 1;
                return 0;
            }});
    }
    
    private static MsSearchResult getOriginalResult(int msRunSearchResultId, Program inputGenerator) {
        if(inputGenerator == Program.SEQUEST || inputGenerator == Program.EE_NORM_SEQUEST) {
            return seqResDao.load(msRunSearchResultId);
        }
        else if (inputGenerator == Program.PROLUCID) {
            return plcResDao.load(msRunSearchResultId);
        }
        else if (inputGenerator == Program.PERCOLATOR) {
            return percResDao.load(msRunSearchResultId);
        }
        else {
            log.warn("Unrecognized input generator for protein inference: "+inputGenerator);
            return null;
        }
    }
    
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a protein group (sorted by sequence, modification state and charge
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerIonWAllSpectra> getPeptideIonsForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WIdPickerIonWAllSpectra> ionList = new ArrayList<WIdPickerIonWAllSpectra>();
        
        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        
        if(pinferProgram == ProteinInferenceProgram.IDPICKER) {
            List<IdPickerPeptide> peptides = idpPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptide peptide: peptides) {
                List<IdPickerIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(IdPickerIon ion: ions) {
                    WIdPickerIonWAllSpectra wIon = makeWIdPickerIonWAllSpectra(ion, inputGenerator, pinferProgram);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else if (pinferProgram == ProteinInferenceProgram.IDPICKER_PERC) {
            List<IdPickerPeptideBase> peptides = idpPeptBaseDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptideBase peptide: peptides) {
                List<ProteinferIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinferIon ion: ions) {
                    WIdPickerIonWAllSpectra wIon = makeWIdPickerIonWAllSpectra(ion, inputGenerator, pinferProgram);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions (with ALL spectra) for pinferID: "+pinferId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }
    
    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
        WIdPickerIonWAllSpectra makeWIdPickerIonWAllSpectra(I ion, Program inputGenerator,
                                                            ProteinInferenceProgram pinferProgram) {
        
        List<? extends ProteinferSpectrumMatch> psmList = null;
        if(pinferProgram == ProteinInferenceProgram.IDPICKER) {
            psmList = idpPsmDao.loadSpectrumMatchesForIon(ion.getId());
        }
        else if (pinferProgram == ProteinInferenceProgram.IDPICKER_PERC) {
            psmList = psmDao.loadSpectrumMatchesForIon(ion.getId());
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        List<WIdPickerSpectrumMatch> wPsmList = new ArrayList<WIdPickerSpectrumMatch>(psmList.size());
        for(ProteinferSpectrumMatch psm: psmList) {
            MsSearchResult origResult = getOriginalResult(psm.getMsRunSearchResultId(), inputGenerator);
            WIdPickerSpectrumMatch wPsm = new WIdPickerSpectrumMatch(psm, origResult);
            int scanNum = scanDao.load(origResult.getScanId()).getStartScanNum();
            wPsm.setScanNumber(scanNum);
            wPsmList.add(wPsm);
        }
        
        return new WIdPickerIonWAllSpectra(ion, wPsmList);
    }
    

    
    //---------------------------------------------------------------------------------------------------
    // Protein and Peptide groups for a cluster
    //--------------------------------------------------------------------------------------------------- 
    public static WIdPickerCluster getIdPickerCluster(int pinferId, int clusterId, PeptideDefinition peptideDef) {
       
        List<Integer> protGroupIds = idpProtBaseDao.getGroupIdsForCluster(pinferId, clusterId);
        
        Map<Integer, WIdPickerProteinGroup> proteinGroups = new HashMap<Integer, WIdPickerProteinGroup>(protGroupIds.size()*2);
        
        // map of peptide groupID and peptide group
        Map<Integer, WIdPickerPeptideGroup> peptideGroups = new HashMap<Integer, WIdPickerPeptideGroup>();
        
        // get a list of protein groups
        for(int protGrpId: protGroupIds) {
            List<WIdPickerProtein> grpProteins = getGroupProteins(pinferId, protGrpId, peptideDef);
            WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
            proteinGroups.put(protGrpId, grp);
            
            List<Integer> peptideGroupIds =  idpPeptBaseDao.getMatchingPeptGroupIds(pinferId, protGrpId);
            
            for(int peptGrpId: peptideGroupIds) {
                WIdPickerPeptideGroup peptGrp = peptideGroups.get(peptGrpId);
                if(peptGrp == null) {
                    List<IdPickerPeptideBase> groupPeptides = idpPeptBaseDao.loadIdPickerGroupPeptides(pinferId, peptGrpId);
                    peptGrp = new WIdPickerPeptideGroup(groupPeptides);
                    peptideGroups.put(peptGrpId, peptGrp);
                }
                peptGrp.addMatchingProteinGroupId(protGrpId);
            }
        }
        
        for(WIdPickerPeptideGroup peptGrp: peptideGroups.values()) {
            List<Integer> protGrpIds = peptGrp.getMatchingProteinGroupIds();
            if(protGrpIds.size() == 1) {
                proteinGroups.get(protGrpIds.get(0)).addUniqPeptideGrpId(peptGrp.getGroupId());
            }
            else {
                for(int protGrpId: protGrpIds)
                    proteinGroups.get(protGrpId).addNonUniqPeptideGrpId(peptGrp.getGroupId());
            }
        }
        
        WIdPickerCluster wCluster = new WIdPickerCluster(pinferId, clusterId);
        wCluster.setProteinGroups(new ArrayList<WIdPickerProteinGroup>(proteinGroups.values()));
        wCluster.setPeptideGroups(new ArrayList<WIdPickerPeptideGroup>(peptideGroups.values()));
        
        return wCluster;
    }

    
    
    
    

    


    


    
//    private static void satisfyPeptideDefinition(WIdPickerProteinGroup wGroup, 
//            IdPickerProteinGroup group, PeptideDefinition peptideDef) {
//        // if we are using sequence only to define unique peptides don't do anything
//        if(!peptideDef.isUseCharge() && !peptideDef.isUseMods())
//            return;
//        
//        // update the peptide count and unique peptide counts based on the peptide definition
//        int numPeptides = 0;
//        int numUniqPeptides = 0;
//        for(IdPickerPeptideGroup peptideGrp: group.getMatchingPeptideGroups()) {
//            for(IdPickerPeptide peptide: peptideGrp.getPeptides()) {
//                List<IdPickerPeptideIon> ions = getIonsForPeptide(peptide, peptideDef);
//                numPeptides += ions.size();
//                if(peptideGrp.isUniqueToProteinGroup())
//                    numUniqPeptides += ions.size();
//            }
//        }
//        wGroup.setMatchingPeptideCount(numPeptides);
//        wGroup.setUniqMatchingPeptideCount(numUniqPeptides);
//    }
    
   
    
//    public static List<WIdPickerSpectrumMatch<SequestSearchResult>> getSequestSpectrumMmatchesForRunSearch(int pinferId, int runSearchId) {
//        
//        List<Integer> psmIdList = specDao.getSpectrumMatchIdsForPinferRunAndRunSearch(pinferId, runSearchId);
//        List<WIdPickerSpectrumMatch<SequestSearchResult>> wIdpPsmList = new ArrayList<WIdPickerSpectrumMatch<SequestSearchResult>>(psmIdList.size());
//        for(Integer psmId: psmIdList) {
//            IdPickerSpectrumMatch idpPsm = specDao.getSpectrumMatch(psmId);
//            SequestSearchResult seqPsm = seqResDao.load(idpPsm.getMsRunSearchResultId());
//            MsScan scan = scanDao.load(seqPsm.getScanId());
//            WIdPickerSpectrumMatch<SequestSearchResult> widpPsm = new WIdPickerSpectrumMatch<SequestSearchResult>();
//            widpPsm.setIdPickerSpectrumMatch(idpPsm);
//            widpPsm.setScanNumber(scan.getStartScanNum());
//            widpPsm.setSpectrumMatch(seqPsm);
//            wIdpPsmList.add(widpPsm);
//        }
//        return wIdpPsmList;
//    }
    
//    public static List<WIdPickerSpectrumMatch<ProlucidSearchResult>> getProlucidSpectrumMmatchesForRunSearch(int pinferId, int runSearchId) {
//        
//        List<Integer> psmIdList = specDao.getSpectrumMatchIdsForPinferRunAndRunSearch(pinferId, runSearchId);
//        List<WIdPickerSpectrumMatch<ProlucidSearchResult>> wIdpPsmList = new ArrayList<WIdPickerSpectrumMatch<ProlucidSearchResult>>(psmIdList.size());
//        
//        for(Integer psmId: psmIdList) {
//            IdPickerSpectrumMatch idpPsm = specDao.getSpectrumMatch(psmId);
//            ProlucidSearchResult seqPsm = plcResDao.load(idpPsm.getMsRunSearchResultId());
//            MsScan scan = scanDao.load(seqPsm.getScanId());
//            WIdPickerSpectrumMatch<ProlucidSearchResult> widpPsm = new WIdPickerSpectrumMatch<ProlucidSearchResult>();
//            widpPsm.setIdPickerSpectrumMatch(idpPsm);
//            widpPsm.setScanNumber(scan.getStartScanNum());
//            widpPsm.setSpectrumMatch(seqPsm);
//            wIdpPsmList.add(widpPsm);
//        }
//        return wIdpPsmList;
//    }
//    
}
