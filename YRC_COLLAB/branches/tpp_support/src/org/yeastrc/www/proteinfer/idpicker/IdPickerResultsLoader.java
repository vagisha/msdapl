package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
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
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerInputDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerIon;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptide;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.nrseq.ProteinListingBuilder;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.protein.ProteinAbundanceDao;
import org.yeastrc.www.proteinfer.MsResultLoader;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;
import org.yeastrc.www.proteinfer.ProteinGoTermsFilter;
import org.yeastrc.www.proteinfer.ProteinInferToSpeciesMapper;
import org.yeastrc.www.proteinfer.ProteinProperties;
import org.yeastrc.www.proteinfer.ProteinPropertiesFilter;
import org.yeastrc.www.proteinfer.ProteinPropertiesSorter;
import org.yeastrc.www.proteinfer.ProteinPropertiesStore;

public class IdPickerResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    //private static final MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
    private static final MS2ScanDAO ms2ScanDao = msDataDaoFactory.getMS2FileScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = msDataDaoFactory.getMsRunSearchAnalysisDAO();
    
    private static final MsResultLoader resLoader = MsResultLoader.getInstance();
    
    private static final ProteinferSpectrumMatchDAO psmDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final IdPickerSpectrumMatchDAO idpPsmDao = pinferDaoFactory.getIdPickerSpectrumMatchDao();
    private static final IdPickerPeptideDAO idpPeptDao = pinferDaoFactory.getIdPickerPeptideDao();
    private static final IdPickerPeptideBaseDAO idpPeptBaseDao = pinferDaoFactory.getIdPickerPeptideBaseDao();
    private static final IdPickerProteinBaseDAO idpProtBaseDao = pinferDaoFactory.getIdPickerProteinBaseDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final IdPickerInputDAO inputDao = pinferDaoFactory.getIdPickerInputDao();
    private static final IdPickerRunDAO idpRunDao = pinferDaoFactory.getIdPickerRunDao();
    private static final ProteinferRunDAO piRunDao = pinferDaoFactory.getProteinferRunDao();
    
    private static final Logger log = Logger.getLogger(IdPickerResultsLoader.class);
    
    private IdPickerResultsLoader(){}
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given filtering criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
    	
        long start = System.currentTimeMillis();
        List<Integer> proteinIds = idpProtBaseDao.getFilteredSortedProteinIds(pinferId, filterCriteria);
        log.info("Returned "+proteinIds.size()+" protein IDs for protein inference ID: "+pinferId);
        
        // filter by accession, if required
        if(filterCriteria.getAccessionLike() != null) {
            log.info("Filtering by accession: "+filterCriteria.getAccessionLike());
            proteinIds = ProteinAccessionFilter.getInstance().filterForProtInferByProteinAccession(pinferId, proteinIds, filterCriteria.getAccessionLike());
        }
        
        // filter by description, if required
        if(filterCriteria.getDescriptionLike() != null) {
            log.info("Filtering by description (like): "+filterCriteria.getDescriptionLike());
            proteinIds = ProteinDescriptionFilter.getInstance().filterPiProteinsByDescriptionLike(pinferId, proteinIds, 
            		filterCriteria.getDescriptionLike(), filterCriteria.isSearchAllDescriptions());
        }
        
        if(filterCriteria.getDescriptionNotLike() != null) {
        	log.info("Filtering by description (NOT like): "+filterCriteria.getDescriptionLike());
            proteinIds = ProteinDescriptionFilter.getInstance().filterPiProteinsByDescriptionNotLike(pinferId, proteinIds, 
            		filterCriteria.getDescriptionNotLike(), filterCriteria.isSearchAllDescriptions());
        }
        
        // filter by molecular wt, if required
        if(filterCriteria.hasMolecularWtFilter()) {
        	log.info("Filtering by molecular wt.");
            proteinIds = ProteinPropertiesFilter.getInstance().filterForProtInferByMolecularWt(pinferId, proteinIds,
                    filterCriteria.getMinMolecularWt(), filterCriteria.getMaxMolecularWt());
        }
        
        // filter by pI, if required
        if(filterCriteria.hasPiFilter()) {
        	log.info("Filtering by pI");
            proteinIds = ProteinPropertiesFilter.getInstance().filterForProtInferByPi(pinferId, proteinIds,
                    filterCriteria.getMinPi(), filterCriteria.getMaxPi());
        }
        
        // filter by GO terms, if required
        if(filterCriteria.getGoFilterCriteria() != null) {
        	GOProteinFilterCriteria goFilters = filterCriteria.getGoFilterCriteria();
        	log.info("Filtering by GO terms: "+goFilters.toString());
        	try {
				proteinIds = ProteinGoTermsFilter.getInstance().filterPinferProteinsByGoAccession(proteinIds, goFilters);
			} catch (Exception e1) {
				log.error("Exception filtering proteins on GO terms", e1);
			}
        }
        
        long s = System.currentTimeMillis();
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.MOL_WT) {
            proteinIds = ProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
            
            long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by Mol. Wt.: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        	
        }
        
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.ACCESSION) {
        	proteinIds = ProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId,
        			filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
        	long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by accession: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.PI) {
            proteinIds = ProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs by pI: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Time: "+TimeUtils.timeElapsedSeconds(start, e)+" seconds");
        return proteinIds;
    }
    

    //---------------------------------------------------------------------------------------------------
    // Sort the list of given protein IDs according to the given sorting criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getSortedProteinIds(int pinferId, PeptideDefinition peptideDef, 
            List<Integer> proteinIds, SORT_BY sortBy, SORT_ORDER sortOrder, boolean groupProteins) {
        
        long s = System.currentTimeMillis();
        List<Integer> allIds = null;
        if(sortBy == SORT_BY.ACCESSION) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.MOL_WT) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.PI) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        
        if(sortBy == SORT_BY.CLUSTER_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByCluster(pinferId);
        }
        else if (sortBy == SORT_BY.GROUP_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByGroup(pinferId);
        }
        else if (sortBy == SORT_BY.COVERAGE) {
            allIds = idpProtBaseDao.sortProteinIdsByCoverage(pinferId, groupProteins, sortOrder);
        }
        else if(sortBy == SORT_BY.NSAF) {
            allIds = idpProtBaseDao.sortProteinsByNSAF(pinferId, groupProteins, sortOrder);
        }
        else if(sortBy == SORT_BY.VALIDATION_STATUS) {
            allIds = idpProtBaseDao.sortProteinIdsByValidationStatus(pinferId);
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
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of proteins
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getProteins(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(proteinIds.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        for(int id: proteinIds) 
            proteins.add(getIdPickerProtein(id, peptideDef, fastaDatabaseIds));
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerProteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteins;
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferProteinId, 
            PeptideDefinition peptideDef, List<Integer> databaseIds) {
        IdPickerProteinBase protein = idpProtBaseDao.loadProtein(pinferProteinId);
        protein.setPeptideDefinition(peptideDef);
        return getWIdPickerProtein(protein, databaseIds);
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferId, int pinferProteinId, 
            PeptideDefinition peptideDef) {
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
       return getIdPickerProtein(pinferProteinId, peptideDef, fastaDatabaseIds);
    }
    
    private static WIdPickerProtein getWIdPickerProtein(IdPickerProteinBase protein, List<Integer> databaseIds) {
        WIdPickerProtein wProt = new WIdPickerProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinListing(wProt, databaseIds);
        
        // get the molecular weight for the protein
        assignProteinProperties(wProt);
        
        return wProt;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all the proteins in a group
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getGroupProteins(int pinferId, int groupId, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        
        List<IdPickerProteinBase> groupProteins = idpProtBaseDao.loadIdPickerGroupProteins(pinferId, groupId);
        
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(groupProteins.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        for(IdPickerProteinBase prot: groupProteins) {
            prot.setPeptideDefinition(peptideDef);
            proteins.add(getWIdPickerProtein(prot, fastaDatabaseIds));
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get proteins in a group: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein groups
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getPageSublist(List<Integer> allProteinIds, int[] pageIndices, 
    		boolean completeGroups, boolean descending) {
    	
    	int firstIndex = descending ? pageIndices[1] : pageIndices[0];
    	int lastIndex = descending ? pageIndices[0] : pageIndices[1];
    	
    	if(completeGroups) {
    		firstIndex = getStartIndexToCompleteFirstGroup(allProteinIds, firstIndex);
    		lastIndex = getEndIndexToCompleteFirstGroup(allProteinIds, lastIndex);
    	}
    	
    	// sublist
        List<Integer> proteinIds = new ArrayList<Integer>();
        if(descending) {
        	for(int i = lastIndex; i >= firstIndex; i--)	
        		proteinIds.add(allProteinIds.get(i));
        }
        else {
        	for(int i = firstIndex; i <= lastIndex; i++)
        		proteinIds.add(allProteinIds.get(i));
        }
        return proteinIds;
    }
    
    private static int getStartIndexToCompleteFirstGroup(List<Integer> allProteinIds, int startIndex) {
    	
    	if(startIndex == 0)
    		return startIndex;
    	if(startIndex < 0) {
    		log.error("startIndex < 0 in getStartIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("startIndex < 0 in getStartIndexToCompleteFirstGroup");
    	}
    	if(startIndex >= allProteinIds.size()) {
    		log.error("startIndex >= list size in getStartIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("startIndex >= list size in getStartIndexToCompleteFirstGroup");
    	}
    	IdPickerProteinBase protein = idpProtBaseDao.loadProtein(allProteinIds.get(startIndex));
    	int groupId = protein.getGroupId();
    	int idx = startIndex - 1;
    	while(idx >= 0) {
    		protein = idpProtBaseDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getGroupId() != groupId) {
    			idx = idx+1;
    			break;
    		}
    		idx--;
    	}
    	return idx;
    }
    
    private static int getEndIndexToCompleteFirstGroup(List<Integer> allProteinIds, int endIndex) {
    	
    	if(endIndex == allProteinIds.size() - 1)
    		return endIndex;
    	if(endIndex >= allProteinIds.size()) {
    		log.error("endIndex >= list size in getEndIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("endIndex >= list size in getEndIndexToCompleteFirstGroup");
    	}
    	if(endIndex < 0) {
    		log.error("endInded < 0 in getEndIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("endInded < 0 in getEndIndexToCompleteFirstGroup");
    	}
    	IdPickerProteinBase protein = idpProtBaseDao.loadProtein(allProteinIds.get(endIndex));
    	int groupId = protein.getGroupId();
    	int idx = endIndex + 1;
    	while(idx < allProteinIds.size()) {
    		protein = idpProtBaseDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getGroupId() != groupId) {
    			idx = idx-1;
    			break;
    		}
    		idx++;
    	}
    	return idx;
    }
    
    
    public static List<WIdPickerProteinGroup> getProteinGroups(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = getProteins(pinferId, proteinIds, peptideDef);
        
        if(proteins.size() == 0) {
            return new ArrayList<WIdPickerProteinGroup>(0);
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
        
        // If this protein inference ID is associated with yeast species
        // get the yeast protein abundances.
        if(ProteinInferToSpeciesMapper.isSpeciesYeast(pinferId)) {
        	ProteinAbundanceDao aDao = ProteinAbundanceDao.getInstance();
        	for(WIdPickerProteinGroup grp: groups) {
        		for(WIdPickerProtein protein: grp.getProteins()) {
        			int nrseqId = protein.getProtein().getNrseqProteinId();
        			try {
						protein.setYeastProteinAbundance(aDao.getAbundance(nrseqId));
					} catch (SQLException e1) {
						log.error("Exception getting yeast protein abundance", e1);
					}
        		}
        	}
        }
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerProteinsGroups: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return groups;
    }
    
    //---------------------------------------------------------------------------------------------------
    // NR_SEQ lookup 
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinListing(WIdPickerProtein wProt, List<Integer> databaseIds) {
        
    	ProteinListing listing = ProteinListingBuilder.getInstance().build(wProt.getProtein().getNrseqProteinId(), databaseIds);
    	wProt.setProteinListing(listing);
    }
    
    //---------------------------------------------------------------------------------------------------
    // Protein properties
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinProperties(WIdPickerProtein wProt) {
        
        ProteinProperties props = ProteinPropertiesStore.getInstance().getProteinMolecularWtPi(wProt.getProtein().getProteinferId(), wProt.getProtein());
        if(props != null) {
            wProt.setMolecularWeight( (float) (Math.round(props.getMolecularWt()*100) / 100.0));
            wProt.setPi( (float) (Math.round(props.getPi()*100) / 100.0));
        }
    }
    
    //---------------------------------------------------------------------------------------------------
    // IDPicker input summary
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerInputSummary> getIDPickerInputSummary(int pinferId) {
        
        ProteinferRun run = piRunDao.loadProteinferRun(pinferId);
        List<IdPickerInput> inputSummary = inputDao.loadProteinferInputList(pinferId);
        List<WIdPickerInputSummary> wInputList = new ArrayList<WIdPickerInputSummary>(inputSummary.size());
        
        for(IdPickerInput input: inputSummary) {
            String filename = "";
            if(Program.isSearchProgram(run.getInputGenerator()))
                filename = rsDao.loadFilenameForRunSearch(input.getInputId());
            else if(Program.isAnalysisProgram(run.getInputGenerator()))
                filename = rsaDao.loadFilenameForRunSearchAnalysis(input.getInputId());
            else
                log.error("Unknown program type: "+run.getInputGenerator().name());
            
            WIdPickerInputSummary winput = new WIdPickerInputSummary(input);
            winput.setFileName(filename);
            wInputList.add(winput);
        }
        Collections.sort(wInputList, new Comparator<WIdPickerInputSummary>() {
            @Override
            public int compare(WIdPickerInputSummary o1,
                    WIdPickerInputSummary o2) {
                return o1.getFileName().compareTo(o2.getFileName());
            }});
        return wInputList;
    }
    
    public static int getUniquePeptideCount(int pinferId) {
        return idpPeptBaseDao.getUniquePeptideSequenceCountForRun(pinferId);
    }
    public static int getUniqueIonCount(int pinferId) {
    	return idpPeptBaseDao.getUniqueIonCountForRun(pinferId);
    }
    //---------------------------------------------------------------------------------------------------
    // IDPicker result summary
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
    // Peptide ions for a indistinguishable protein group 
    // (sorted by sequence, modification state and charge)
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
        
        
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
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
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
                pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
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
        MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
        // If this scan was processed with Bullseye it will have extra information in the scan headers.
        if(ms2ScanDao.isGeneratedByBullseye(origResult.getScanId())) {
            MS2Scan scan = ms2ScanDao.loadScanLite(origResult.getScanId());
            return new WIdPickerIon(ion, origResult, scan);
        }
        else {
            MsScan scan = scanDao.loadScanLite(origResult.getScanId());
            return new WIdPickerIon(ion, origResult, scan);
        }
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
    
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a protein (sorted by sequence, modification state and charge
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerIonForProtein> getPeptideIonsForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WIdPickerIonForProtein> ionList = new ArrayList<WIdPickerIonForProtein>();
        
        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        ProteinferProtein protein = protDao.loadProtein(proteinId);
        String proteinSeq = getProteinSequence(protein);
        
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
            List<IdPickerPeptide> peptides = idpPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptide peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<IdPickerIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(IdPickerIon ion: ions) {
                    WIdPickerIonForProtein wIon = makeWIdPickerIonForProtein(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
                 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            List<IdPickerPeptideBase> peptides = idpPeptBaseDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptideBase peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<ProteinferIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinferIon ion: ions) {
                    WIdPickerIonForProtein wIon = makeWIdPickerIonForProtein(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
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
            WIdPickerIonForProtein makeWIdPickerIonForProtein(I ion, Program inputGenerator) {
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
        MsScan scan = scanDao.loadScanLite(origResult.getScanId());
        return new WIdPickerIonForProtein(ion, origResult, scan);
    }

    public static List<WIdPickerSpectrumMatch> getHitsForIon(int pinferIonId, Program inputGenerator, ProteinInferenceProgram pinferProgram) {
        
        List<? extends ProteinferSpectrumMatch> psmList = null;
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
            psmList = idpPsmDao.loadSpectrumMatchesForIon(pinferIonId);
        }
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
                 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            psmList = psmDao.loadSpectrumMatchesForIon(pinferIonId);
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        List<WIdPickerSpectrumMatch> wPsmList = new ArrayList<WIdPickerSpectrumMatch>(psmList.size());
        for(ProteinferSpectrumMatch psm: psmList) {
            MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
            WIdPickerSpectrumMatch wPsm = null;
            int scanId = origResult.getScanId();
            if(ms2ScanDao.isGeneratedByBullseye(scanId)) {
                MS2Scan scan = ms2ScanDao.load(scanId);
                wPsm = new WIdPickerSpectrumMatch(psm, origResult, scan);
            }
            else {
                MsScan scan = scanDao.load(origResult.getScanId());
                wPsm = new WIdPickerSpectrumMatch(psm, origResult, scan);
            }
            wPsmList.add(wPsm);
        }
        
        return wPsmList;
    }
    

    
    //---------------------------------------------------------------------------------------------------
    // Protein and Peptide groups for a cluster
    //--------------------------------------------------------------------------------------------------- 
    public static WIdPickerCluster getIdPickerCluster(int pinferId, int clusterId, 
            PeptideDefinition peptideDef) {
       
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
 
}
