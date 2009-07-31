/**
 * ProtxmlDataUploadService.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.protxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferInputDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferIonDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetParamDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinGroupDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinIonDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetSubsumedProteinDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetParam;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon.Modification;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.protxml.InteractProtXmlParser;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.ModifiedSequenceBuilder;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public class ProtxmlDataUploadService {

    private final DAOFactory daoFactory;
    private final MsSearchResultDAO resDao;
    private final PeptideProphetResultDAO ppResDao;
    
    private final int nrseqDatabaseId;
    
    private final ProteinferDAOFactory piDaoFactory;
    private ProteinferRunDAO runDao;
    private ProteinferPeptideDAO peptDao;
    private ProteinferProteinDAO protDao;
    private ProteinferIonDAO ionDao;
    private ProteinferSpectrumMatchDAO psmDao;
    private ProteinProphetProteinGroupDAO grpDao;
    private ProteinProphetProteinDAO ppProtDao;
    private ProteinProphetProteinIonDAO ppProteinIonDao; 
    private ProteinProphetSubsumedProteinDAO ppSusumedDao;
    
    private Map<String, Integer> peptideMap; // peptide sequence and its ID in the database
    private Map<String, Integer> ionMap;     // ion sequence (chg_modseq) and its ID in the database
    private Map<String, Integer> modifiedStateMap;  // map of modified sequence and modificationStateID
    private Map<Integer, Integer> peptModStateCountMap; // map to keep track of # of mod. states for a peptide
    
    private DynamicModLookupUtil modLookup;
    private int searchId;
    
    public ProtxmlDataUploadService(int nrseqDatabaseId) {
        
        peptideMap = new HashMap<String, Integer>();
        ionMap = new HashMap<String, Integer>();
        peptModStateCountMap = new HashMap<Integer, Integer>();
        
        
        piDaoFactory = ProteinferDAOFactory.instance();
        daoFactory = DAOFactory.instance();
        
        resDao = daoFactory.getMsSearchResultDAO();
        ppResDao = daoFactory.getPeptideProphetResultDAO();
        
        runDao = piDaoFactory.getProteinferRunDao();
        peptDao = piDaoFactory.getProteinferPeptideDao();
        protDao = piDaoFactory.getProteinferProteinDao();
        ionDao = piDaoFactory.getProteinferIonDao();
        psmDao = piDaoFactory.getProteinferSpectrumMatchDao();
        grpDao = piDaoFactory.getProteinProphetProteinGroupDao();
        ppProtDao = piDaoFactory.getProteinProphetProteinDao();
        ppProteinIonDao = piDaoFactory.getProteinProphetProteinIonDao();
        ppSusumedDao = piDaoFactory.getProteinProphetSubsumedProteinDao();
        
        this.nrseqDatabaseId = nrseqDatabaseId;
    }
    
    public int upload(String filepath, int searchId) throws UploadException {
        
        InteractProtXmlParser parser = new InteractProtXmlParser();
        try {
            parser.open(filepath);
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PROTXML_ERROR, e);
            ex.appendErrorMessage(e.getMessage());
            throw ex;
        }
        
        this.searchId = searchId;
        modLookup = new DynamicModLookupUtil(searchId);
        
        // create a new entry for this protein inference run
        int pinferId = 0;
        try {pinferId = addProteinInferenceRun(parser, searchId);}
        catch(UploadException ex) {
            ex.appendErrorMessage("DELETING PROTEIN INFERENCE...");
            runDao.delete(pinferId);
            throw ex;
        }
        
        // save the protein and protein groups
        try {
            while(parser.hasNextProteinGroup()) {
                saveProteinProphetGroup(parser.getNextGroup(), pinferId);
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PROTXML_ERROR, e);
            ex.appendErrorMessage(e.getErrorMessage());
            ex.appendErrorMessage("DELETING PROTEIN INFERENCE...");
            runDao.delete(pinferId);
            throw ex;
        }
        catch(UploadException e) {
            e.appendErrorMessage("DELETING PROTEIN INFERENCE...");
            runDao.delete(pinferId);
            throw e;
        }
        
        parser.close();
        
        return pinferId;
    }


    private void saveProteinProphetGroup(ProteinProphetGroup proteinGroup,
            int pinferId) throws UploadException {
        
        int ppGrpId = grpDao.saveGroup(pinferId, proteinGroup.getProbability(), proteinGroup.getGroupNumber());
        
        Map<Integer, Set<String>> subsumedMap = new HashMap<Integer, Set<String>>();
        Map<String, Integer> proteinIdMap = new HashMap<String, Integer>();
        
        for(ProteinProphetProtein protein: proteinGroup.getProteinList()) {
            
            protein.setProteinferId(pinferId);
            protein.setProteinProphetGroupId(ppGrpId);
            int piProteinId = saveProtein(protein, subsumedMap);
            proteinIdMap.put(protein.getProteinName(), piProteinId);
            
            // Are there indistinguishable proteins?
            for(String name: protein.getIndistinguishableProteins()) {
                ProteinProphetProtein iProt = protein.getIndistinguishableProtein(name);
                piProteinId = saveProtein(iProt, subsumedMap);
                proteinIdMap.put(name, piProteinId);
                // TODO what about protein coverage
            }
        }
        
        saveSubsumedProteins(pinferId, subsumedMap, proteinIdMap);
    }
    
    private void saveSubsumedProteins(int pinferId, Map<Integer, Set<String>> subsumedMap, Map<String, Integer> proteinIdMap) {
        
        for(int subsumedId: subsumedMap.keySet()) {
            Set<String> subsuming = subsumedMap.get(subsumedId);
            for(String name: subsuming) {
                int subsumingId = proteinIdMap.get(name);
                ppSusumedDao.saveSubsumedProtein(pinferId, subsumedId, subsumingId);
            }
        }
    }

    public int saveProtein(ProteinProphetProtein protein, Map<Integer, Set<String>> subsumedMap) throws UploadException {
        
        int nrseqId = getNrseqProteinId(protein.getProteinName(), nrseqDatabaseId);
        protein.setNrseqProteinId(nrseqId);
        int piProteinId = ppProtDao.save(protein);
        // save peptides
        savePeptides(protein);
        
        // Is this a subsumed protein
        // NOTE: assuming all subsuming proteins for a protein will be in the same
        // protein group as the protein
        if(protein.isSubsumed()) {
            subsumedMap.put(piProteinId, protein.getSusumingProteins());
        }
        return piProteinId;
    }
    
    private void savePeptides(ProteinProphetProtein protein) throws UploadException {
        
        for(ProteinProphetProteinPeptide peptide: protein.getPeptides()) {
            // is this peptide saved already? 
            Integer pinferPeptideId = peptideMap.get(peptide.getSequence());
            if(pinferPeptideId == null) {
                // save the peptide
                pinferPeptideId = peptDao.save(peptide);
                peptideMap.put(peptide.getSequence(), pinferPeptideId);
            }
            
            // link this peptide and protein
            protDao.saveProteinferProteinPeptideMatch(protein.getId(), pinferPeptideId);
            
            
            // look at each ion for the peptide
            for(ProteinProphetProteinPeptideIon ion: peptide.getIonList()) {
                
                Integer pinferIonId = savePeptideIon(peptide, ion);
                
                // create an entry in the ProteinProphetProteinIon table
                ion.setId(pinferIonId);
                ion.setPinferProteinId(protein.getId());
                ppProteinIonDao.save(ion);
            }
        }
    }

    private Integer savePeptideIon(ProteinProphetProteinPeptide peptide,
            ProteinProphetProteinPeptideIon ion) throws UploadException {
        
        Integer pinferIonId = ionMap.get(ion.getCharge()+"_"+ion.getModifiedSequence());
        
        if(pinferIonId == null) {
            
            // Update the modified sequence for each ion based on the modifications we have
            // in the database for this search
            List<MsResultResidueMod> modList = null;
            if(ion.getModifications().size() > 0) {
                String strippedSeq = peptide.getSequence();
                
                modList = getMatchingModifications(ion, strippedSeq);
                updateModifiedSequence(ion, strippedSeq, modList);
            }
            
            Integer modStateId = modifiedStateMap.get(ion.getModifiedSequence());
            if(modStateId == null) {
                Integer modStateCnt = peptModStateCountMap.get(peptide.getId());
                if(modStateCnt == null) {
                    modStateCnt = 1;
                    peptModStateCountMap.put(peptide.getId(), modStateCnt);
                }
                else
                    peptModStateCountMap.put(peptide.getId(), ++modStateCnt);
                modStateId = modStateCnt;
                modifiedStateMap.put(ion.getModifiedSequence(), modStateId);
                
            }
            ion.setModificationStateId(modStateId);
            pinferIonId = ionDao.save(ion);
            ion.setId(pinferIonId);
            
            // save spectra for the ion
            saveIonSpectra(ion, searchId, modList);
        }
        return pinferIonId;
    }

    private void updateModifiedSequence(ProteinProphetProteinPeptideIon ion,
            String strippedSeq, List<MsResultResidueMod> modList)
            throws UploadException {
        
        try {
            String modifiedSequence = ModifiedSequenceBuilder.build(strippedSeq, modList);
            ion.setModifiedSequence(modifiedSequence);
        }
        catch (ModifiedSequenceBuilderException e) {
            UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED, e);
            ex.appendErrorMessage(e.getMessage());
            throw ex;
        }
    }

    private List<MsResultResidueMod> getMatchingModifications(
            ProteinProphetProteinPeptideIon ion, String strippedSeq)
            throws UploadException {
        
        List<MsResultResidueMod> modList = new ArrayList<MsResultResidueMod>(ion.getModifications().size());
        
        for(Modification mod: ion.getModifications()) {
            
            // if this is not a dynamic modification ignore it
            // Dynamic modifications will be of the form: CPETLFQPSFIGMESAGIHETTYNSIM[147]K
            // where 147 is the mass of the amino acid (M) plus the mass of the modification
            // The mass [147] will not be appended to the sequence for a static modification.
            // If the character just after the modified amino acid is a valid amino acid character
            // this the modification is NOT a dynamic modification.
            // NOTE: ProtXml modifications are 1-based.  
            if(AminoAcidUtils.isAminoAcid(ion.getModifiedSequence().charAt(mod.getPosition())))
                continue;
            
            MsResidueModification dbMod = modLookup.getDynamicResidueModification(
                        strippedSeq.charAt(mod.getPosition() - 1), // ProtXml modifications are 1-based
                        mod.getMass(),
                        true); // mass = mass of amino acid + modification mass
            
            if(dbMod == null) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.appendErrorMessage("searchId: "+searchId+
                        "; char: "+strippedSeq.charAt(mod.getPosition() - 1)+
                        "; mass: "+mod.getMass());
                throw ex;
            }
            
            ResultResidueModBean resModBean = new ResultResidueModBean();
            resModBean.setModificationMass(dbMod.getModificationMass());
            resModBean.setModificationSymbol(dbMod.getModificationSymbol());
            resModBean.setModifiedPosition(mod.getPosition() - 1); // ProtXml modifications are 1-based
            resModBean.setModifiedResidue(dbMod.getModifiedResidue());
            
            modList.add(resModBean);
        }
        return modList;
    }
    
    private void saveIonSpectra(ProteinProphetProteinPeptideIon ion, int searchId, List<MsResultResidueMod> modList) 
        throws UploadException {
        
        
        // get all spectra for the given searchID that have the given sequence and modifications
        List<Integer> resultIds = resDao.loadResultIdsForSearchChargePeptide(searchId, ion.getCharge(), ion.getUnmodifiedSequence());
        List<PeptideProphetResult> matchingResults = new ArrayList<PeptideProphetResult>();
        for(int resultId: resultIds) {
            
            PeptideProphetResult result = ppResDao.load(resultId);
            
            // ignore all spectra with PeptideProphet probability < 0.05
            if(result.getProbability() < 0.05)
                continue;
            
            matchingResults.add(result);
        }
        
        // sort the results by probability
        Collections.sort(matchingResults, new Comparator<PeptideProphetResult>() {
            @Override
            public int compare(PeptideProphetResult o1, PeptideProphetResult o2) {
                return Double.valueOf(o1.getProbability()).compareTo(o2.getProbability());
            }});
        
        // store the ones that have the charge and modification state as this ion
        int rank = 1;
        int numFound = 0;
        for(PeptideProphetResult result: matchingResults) {
        
            List<MsResultResidueMod> resMods = result.getResultPeptide().getResultDynamicResidueModifications();
            String modifiedSeq;
            try {
                modifiedSeq = ModifiedSequenceBuilder.build(ion.getUnmodifiedSequence(), resMods);
            }
            catch (ModifiedSequenceBuilderException e) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.appendErrorMessage("Error building modified sequence for result: "+result.getId()+
                        "; sequence: "+result.getResultPeptide().getPeptideSequence());
                throw ex;
            }
            if(ion.getModifiedSequence().equals(modifiedSeq)) {
                numFound++;
                ProteinferSpectrumMatch psm = new ProteinferSpectrumMatch();
                psm.setMsRunSearchResultId(result.getId());
                psm.setProteinferIonId(ion.getId());
                psm.setRank(rank); 
                psmDao.saveSpectrumMatch(psm);
                rank++;
            }
        }
        
        // make sure the number of results returned above match the spectrum count for this ion in the 
        // ProtXml file.
        if(numFound != ion.getSpectrumCount()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.appendErrorMessage("Spectrum count for ion ("+ion.getModifiedSequence()+
                        ") does not match the number of results returned: "+numFound);
            throw ex;
        }
    }

    private int getNrseqProteinId(String accession, int nrseqDatabaseId) {
        NrDbProtein protein = NrSeqLookupUtil.getDbProtein(nrseqDatabaseId, accession);
        return protein.getProteinId();
    }

    private int addProteinInferenceRun(InteractProtXmlParser parser, int searchId) throws UploadException {
        
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        ProteinProphetRun run = new ProteinProphetRun();
        run.setInputGenerator(search.getSearchProgram());
        run.setProgram(ProteinInferenceProgram.PROTEIN_PROPHET);
        run.setProgramVersion(parser.getProgramVersion());
        run.setDate(new java.sql.Date(parser.getDate().getTime()));
        int pinferId = runDao.save(run);
        
        MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
        
        ProteinferInputDAO inputDao = ProteinferDAOFactory.instance().getProteinferInputDao();
        try {
            for(int runSearchId: runSearchIds) {
                ProteinferInput input = new ProteinferInput();
                input.setInputId(runSearchId);
                input.setInputType(InputType.SEARCH);
                input.setProteinferId(pinferId);
                inputDao.saveProteinferInput(input);
            }
        }
        catch(RuntimeException ex) {
            UploadException e = new UploadException(ERROR_CODE.GENERAL, ex);
            e.appendErrorMessage("Error saving ProteinProphet input.");
            throw e;
        }
        
        
        // save the parameters
        List<ProteinProphetParam> params = parser.getParams();
        ProteinProphetParamDAO paramDao = ProteinferDAOFactory.instance().getProteinProphetParamDao();
        try {
            for(ProteinProphetParam param: params) {
                paramDao.saveProteinProphetParam(param);
            }
        }
        catch(RuntimeException ex) {
            UploadException e = new UploadException(ERROR_CODE.GENERAL, ex);
            e.appendErrorMessage("Error saving ProteinProphet params.");
            throw e;
        }
        
        return pinferId;
    }
    
}
