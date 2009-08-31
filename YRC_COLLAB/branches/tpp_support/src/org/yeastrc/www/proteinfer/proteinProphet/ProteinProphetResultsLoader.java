/**
 * ProteinProphetResultsLoader.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferInputDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetPeptideDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinGroupDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.www.compare.CommonNameLookupUtil;
import org.yeastrc.www.compare.FastaProteinLookupUtil;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.compare.ProteinListing;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinAccessionSorter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;

/**
 * 
 */
public class ProteinProphetResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = msDataDaoFactory.getMsRunSearchAnalysisDAO();
    
    private static final SequestSearchResultDAO seqResDao = msDataDaoFactory.getSequestResultDAO();
    private static final PeptideProphetResultDAO peptPResDao = msDataDaoFactory.getPeptideProphetResultDAO();
    
    private static final ProteinferSpectrumMatchDAO psmDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final ProteinferPeptideDAO peptDao = pinferDaoFactory.getProteinferPeptideDao();
    private static final ProteinProphetProteinDAO ppProtDao = pinferDaoFactory.getProteinProphetProteinDao();
    private static final ProteinProphetProteinGroupDAO ppProtGrpDao = pinferDaoFactory.getProteinProphetProteinGroupDao();
    private static final ProteinProphetPeptideDAO ppPeptDao = pinferDaoFactory.getProteinProphetPeptideDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final ProteinferInputDAO inputDao = pinferDaoFactory.getProteinferInputDao();
    private static final ProteinferRunDAO ppRunDao = pinferDaoFactory.getProteinferRunDao();
    
    private static final Logger log = Logger.getLogger(ProteinProphetResultsLoader.class);
    
    private ProteinProphetResultsLoader(){}
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given filtering criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getProteinIds(int pinferId, ProteinProphetFilterCriteria filterCriteria) {
        long s = System.currentTimeMillis();
        List<Integer> proteinIds = ppProtDao.getFilteredSortedProteinIds(pinferId, filterCriteria);
        log.info("Returned "+proteinIds.size()+" protein IDs for protein inference ID: "+pinferId);
        
        // filter by accession, if required
        if(filterCriteria.getAccessionLike() != null) {
            log.info("Filtering by accession: "+filterCriteria.getAccessionLike());
            proteinIds = ProteinAccessionFilter.filterByProteinAccession(pinferId, proteinIds, filterCriteria.getAccessionLike());
        }
        
        // filter by description, if required
        if(filterCriteria.getDescriptionLike() != null) {
            log.info("Filtering by description: "+filterCriteria.getDescriptionLike());
            proteinIds = ProteinDescriptionFilter.filterByProteinDescription(pinferId, proteinIds, filterCriteria.getDescriptionLike());
        }
        
        long e = System.currentTimeMillis();
        log.info("Time: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteinIds;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all the proteins in a indistinguishable protein group
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getGroupProteins(int pinferId, int groupId, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        
        List<ProteinProphetProtein> groupProteins = ppProtDao.loadProteinProphetIndistinguishableGroupProteins(pinferId, groupId);
        
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(groupProteins.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        for(ProteinProphetProtein prot: groupProteins) {
            prot.setPeptideDefinition(peptideDef);
            proteins.add(getWProteinProphetProtein(prot, fastaDatabaseIds));
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get proteins in a group: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein groups
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProteinGroup> getProteinProphetGroups(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        return getProteinProphetGroups(pinferId, proteinIds, true, peptideDef);
    }
    
    public static List<WProteinProphetProteinGroup> getProteinProphetGroups(int pinferId, List<Integer> proteinIds, boolean append, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        List<WProteinProphetProtein> proteins = getProteins(pinferId, proteinIds, peptideDef);
        
        if(proteins.size() == 0) {
            return new ArrayList<WProteinProphetProteinGroup>(0);
        }
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        if(append) {
            // protein Ids should be sorted by protein prophet groupID. If the proteins at the top of the list
            // does not have all members of the group in the list, add them
            int groupId_top = proteins.get(0).getProtein().getGroupId();
            List<ProteinProphetProtein> groupProteins = ppProtDao.loadProteinProphetGroupProteins(pinferId, groupId_top);
            for(ProteinProphetProtein prot: groupProteins) {
                if(!proteinIds.contains(prot.getId())) {
                    prot.setPeptideDefinition(peptideDef);
                    proteins.add(0, getWProteinProphetProtein(prot, fastaDatabaseIds));
                }
            }

            // protein Ids should be sorted by protein prophet groupID. If the proteins at the bottom of the list
            // does not have all members of the group in the list, add them
            int groupId_last = proteins.get(proteins.size() - 1).getProtein().getGroupId();
            if(groupId_last != groupId_top) {
                groupProteins = ppProtDao.loadProteinProphetGroupProteins(pinferId, groupId_last);
                for(ProteinProphetProtein prot: groupProteins) {
                    if(!proteinIds.contains(prot.getId())) {
                        prot.setPeptideDefinition(peptideDef);
                        proteins.add(getWProteinProphetProtein(prot, fastaDatabaseIds));
                    }
                }
            }
        }
       
        if(proteins.size() == 0)
            return new ArrayList<WProteinProphetProteinGroup>(0);
        
        int currGrpId = -1;
        List<WProteinProphetProtein> prophetGrpProteins = null;
        List<WProteinProphetProteinGroup> prophetGrps = new ArrayList<WProteinProphetProteinGroup>();
        
        for(WProteinProphetProtein prot: proteins) {
            if(prot.getProtein().getProteinProphetGroupId() != currGrpId) {
                if(prophetGrpProteins != null && prophetGrpProteins.size() > 0) {
                    ProteinProphetGroup prophetGroup = ppProtGrpDao.load(currGrpId);
                    
                    List<WProteinProphetIndistProteinGroup> indistGrps = makeIndistinguishableGroups(prophetGrpProteins);
                    WProteinProphetProteinGroup grp = new WProteinProphetProteinGroup(prophetGroup, indistGrps);
                    prophetGrps.add(grp);
                }
                currGrpId = prot.getProtein().getProteinProphetGroupId();
                prophetGrpProteins = new ArrayList<WProteinProphetProtein>();
            }
            prophetGrpProteins.add(prot);
        }
        if(prophetGrpProteins != null && prophetGrpProteins.size() > 0) {
            ProteinProphetGroup prophetGroup = ppProtGrpDao.load(currGrpId);
            
            List<WProteinProphetIndistProteinGroup> indistGrps = makeIndistinguishableGroups(prophetGrpProteins);
            WProteinProphetProteinGroup grp = new WProteinProphetProteinGroup(prophetGroup, indistGrps);
            prophetGrps.add(grp);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get WProteinProphetProteinsGroups: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return prophetGrps;
    }
    
    private static List<WProteinProphetIndistProteinGroup> makeIndistinguishableGroups(
            List<WProteinProphetProtein> prophetGrpProteins) {
        
        // sort by indistinguishable protein group ID
        Collections.sort(prophetGrpProteins, new Comparator<WProteinProphetProtein>() {
            @Override
            public int compare(WProteinProphetProtein o1, WProteinProphetProtein o2) {
                return Integer.valueOf(o1.getProtein().getGroupId()).compareTo(o2.getProtein().getGroupId());
            }});
        
        List<WProteinProphetIndistProteinGroup> indistGrps = new ArrayList<WProteinProphetIndistProteinGroup>();
        int currGrpId = -1;
        List<WProteinProphetProtein> iGrpProteins = null;
        for(WProteinProphetProtein prot: prophetGrpProteins) {
            if(prot.getProtein().getGroupId() != currGrpId) {
                if(iGrpProteins != null && iGrpProteins.size() > 0) {
                    
                    WProteinProphetIndistProteinGroup grp = new WProteinProphetIndistProteinGroup(iGrpProteins);
                    indistGrps.add(grp);
                }
                currGrpId = prot.getProtein().getGroupId();
                iGrpProteins = new ArrayList<WProteinProphetProtein>();
            }
            iGrpProteins.add(prot);
        }
        if(iGrpProteins != null && iGrpProteins.size() > 0) {
            
            WProteinProphetIndistProteinGroup grp = new WProteinProphetIndistProteinGroup(iGrpProteins);
            indistGrps.add(grp);
        }
        return indistGrps;
    }

    //---------------------------------------------------------------------------------------------------
    // Get a list of proteins
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getProteins(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(proteinIds.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        for(int id: proteinIds) 
            proteins.add(getWProteinProphetProtein(id, peptideDef, fastaDatabaseIds));
        long e = System.currentTimeMillis();
        log.info("Time to get WProteinProphetProteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteins;
    }
    
    public static WProteinProphetProtein getWProteinProphetProtein(int pinferProteinId, 
            PeptideDefinition peptideDef, List<Integer> databaseIds) {
        ProteinProphetProtein protein = ppProtDao.loadProtein(pinferProteinId);
        protein.setPeptideDefinition(peptideDef);
        return getWProteinProphetProtein(protein, databaseIds);
    }
    
    public static WProteinProphetProtein getWProteinProphetProtein(int pinferId, int pinferProteinId, 
            PeptideDefinition peptideDef) {
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
       return getWProteinProphetProtein(pinferProteinId, peptideDef, fastaDatabaseIds);
    }
    
    private static WProteinProphetProtein getWProteinProphetProtein(ProteinProphetProtein protein, List<Integer> databaseIds) {
        WProteinProphetProtein wProt = new WProteinProphetProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinAccessionDescription(wProt, databaseIds);
        return wProt;
    }
    
    //---------------------------------------------------------------------------------------------------
    // NR_SEQ lookup 
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinAccessionDescription(WProteinProphetProtein wProt, List<Integer> databaseIds) {
        
        String[] acc_descr = getProteinAccessionDescription(wProt.getProtein().getNrseqProteinId(), databaseIds);
        wProt.setAccession(acc_descr[0]);
        wProt.setDescription(acc_descr[1]);
        wProt.setCommonName(acc_descr[2]);
    }
    
    private static String[] getProteinAccessionDescription(int nrseqProteinId, List<Integer> databaseIds) {
        return getProteinAccessionDescription(nrseqProteinId, databaseIds, true);
    }

    private static String[] getProteinAccessionDescription(int nrseqProteinId, List<Integer> databaseIds,
            boolean getCommonName) {
        
        ProteinListing listing = FastaProteinLookupUtil.getInstance().getProteinListing(nrseqProteinId, databaseIds);
        String accession = listing.getAllNames();
        String description = listing.getAllDescriptions();
        
        String commonName = "";
        if(getCommonName) {

            try {
                commonName = CommonNameLookupUtil.getInstance().getProteinListing(nrseqProteinId).getName();
            }
            catch (Exception e) {
                log.error("Exception getting common name for protein Id: "+nrseqProteinId, e);
            }
        }
        return new String[] {accession, description, commonName};
        
    }
    
    //---------------------------------------------------------------------------------------------------
    // Protein Prophet result summary
    //---------------------------------------------------------------------------------------------------
    public static WProteinProphetResultSummary getProteinProphetResultSummary(int pinferId, List<Integer> proteinIds) {
        
        long s = System.currentTimeMillis();
        
        WProteinProphetResultSummary summary = new WProteinProphetResultSummary();
        summary.setFilteredProteinCount(proteinIds.size());
        // parsimonious protein IDs
        List<Integer> parsimProteinIds = ppProtDao.getProteinferProteinIds(pinferId, true);
        Map<Integer, Integer> protGroupMap = ppProtDao.getProteinGroupIds(pinferId, false);
        
        
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
        log.info("Time to get WProteinProphetResultSummary: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return summary;
    }
    
    //---------------------------------------------------------------------------------------------------
    // PEPTIDE COUNT
    //---------------------------------------------------------------------------------------------------
    public static int getUniquePeptideCount(int pinferId) {
        return peptDao.getUniquePeptideSequenceCountForRun(pinferId);
    }
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a indistinguishable protein group 
    // (sorted by sequence, modification state and charge)
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetIon> getPeptideIonsForProteinGroup(int pinferId, int pinferProteinGroupId) {
        
        long s = System.currentTimeMillis();
        
        List<WProteinProphetIon> ionList = new ArrayList<WProteinProphetIon>();
        
        // get the id of one of the proteins in the group. All proteins in a group match the same peptides
        int proteinId = ppProtDao.getProteinProphetGroupProteinIds(pinferId, pinferProteinGroupId).get(0);
        
        ProteinferRun run = ppRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        
        if(pinferProgram == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<ProteinProphetProteinPeptide> peptides = ppPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(ProteinProphetProteinPeptide peptide: peptides) {
                List<ProteinProphetProteinPeptideIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinProphetProteinPeptideIon ion: ions) {
                    WProteinProphetIon wIon = makeWProteinProphetIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Invalid program for the action: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions for pinferID: "+pinferId+
                ", proteinGroupID: "+pinferProteinGroupId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }
    
    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
        WProteinProphetIon makeWProteinProphetIon(I ion, Program inputGenerator) {
        
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = getOriginalResult(psm.getMsRunSearchResultId(), inputGenerator);
        return new WProteinProphetIon(ion, origResult);
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
        if(inputGenerator == Program.PEPTIDE_PROPHET) {//|| inputGenerator == Program.EE_NORM_SEQUEST) {
            return peptPResDao.load(msRunSearchResultId);
        }
        else {
            log.warn("Unrecognized input generator for protein inference: "+inputGenerator);
            return null;
        }
    }
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a protein (sorted by sequence, modification state and charge
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetIon> getPeptideIonsForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WProteinProphetIon> ionList = new ArrayList<WProteinProphetIon>();
        
        ProteinferRun run = ppRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        ProteinferProtein protein = protDao.loadProtein(proteinId);
        String proteinSeq = getProteinSequence(protein);
        
        if (pinferProgram == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<ProteinProphetProteinPeptide> peptides = ppPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(ProteinProphetProteinPeptide peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<ProteinProphetProteinPeptideIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinProphetProteinPeptideIon ion: ions) {
                    WProteinProphetIon wIon = makeWProteinProphetIonForProtein(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of protein inference program: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions (with ALL spectra) for pinferID: "+pinferId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }
    
    private static List<Character>[] getTerminalresidues(String proteinSeq,
            String sequence) {
        List<Character> nterm = new ArrayList<Character>(2);
        List<Character> cterm = new ArrayList<Character>(2);
        int idx = proteinSeq.indexOf(sequence);
        while(idx != -1) {
            if(idx == 0)    nterm.add('-');
            else            nterm.add(proteinSeq.charAt(idx-1));
            if(idx+sequence.length() >= proteinSeq.length())
                cterm.add('-');
            else            cterm.add(proteinSeq.charAt(idx+sequence.length()));
            
            idx = proteinSeq.indexOf(sequence, idx+sequence.length());
        }
        return new List[]{nterm, cterm};
    }

    private static String getProteinSequence(ProteinferProtein protein) {
        NRProtein nrprot = null;
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        try {
            nrprot = (NRProtein)(nrpf.getProtein(protein.getNrseqProteinId()));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }        

        return nrprot.getPeptide().getSequenceString();
    }
    
    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
        WProteinProphetIon makeWProteinProphetIonForProtein(I ion, Program inputGenerator) {
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = getOriginalResult(psm.getMsRunSearchResultId(), inputGenerator);
        return new WProteinProphetIon(ion, origResult);
    }

  //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given sorting criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getSortedProteinIds(int pinferId, PeptideDefinition peptideDef, 
            List<Integer> proteinIds, SORT_BY sortBy, boolean groupProteins) {
        
        long s = System.currentTimeMillis();
        List<Integer> allIds = null;
        if(sortBy == SORT_BY.ACCESSION) {
            List<Integer> sortedIds = ProteinAccessionSorter.sortIdsByAccession(proteinIds, pinferId);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        else if (sortBy == SORT_BY.PROTEIN_PROPHET_GROUP) {
            allIds = ppProtDao.sortProteinIdsByProteinProphetGroup(pinferId);
        }
        else if (sortBy == SORT_BY.COVERAGE) {
            allIds = ppProtDao.sortProteinIdsByCoverage(pinferId, groupProteins);
        }
        else if(sortBy == SORT_BY.VALIDATION_STATUS) {
            allIds = ppProtDao.sortProteinIdsByValidationStatus(pinferId);
        }
        else if (sortBy == SORT_BY.NUM_PEPT) {
            allIds = ppProtDao.sortProteinIdsByPeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_UNIQ_PEPT) {
            allIds = ppProtDao.sortProteinIdsByUniquePeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_SPECTRA) {
            allIds = ppProtDao.sortProteinIdsBySpectrumCount(pinferId, groupProteins);
        }
        else if (sortBy == SORT_BY.PROBABILITY) {
            allIds = ppProtDao.sortProteinIdsByProbability(pinferId, groupProteins);
        }
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
}
