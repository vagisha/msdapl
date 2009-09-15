/**
 * ProtxmlDataUploadService.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.protxml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
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
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRocDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetSubsumedProteinDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetParam;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.protxml.InteractProtXmlParser;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.ModifiedSequenceBuilder;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.service.ProtinferUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetResultUploadDAO;

/**
 * 
 */
public class ProtxmlDataUploadService implements ProtinferUploadService {

    private final DAOFactory daoFactory;
    private final MsSearchResultDAO resDao;
    private final PeptideProphetResultUploadDAO ppResDao;
    
    
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
//    private int analysisId;
    private String protxmlDirectory;
    private int nrseqDatabaseId;
    
//    private int uploadedPinferId;
    private int indistinguishableProteinGroupId = 1;
    
//    private int numProteinGroups;
    
    private StringBuilder uploadMsg;
    private StringBuilder preUploadCheckMsg;
    private boolean preUploadCheckDone = false;
    
    private static final Pattern fileNamePattern = Pattern.compile("interact*.prot.xml");
    private List<String> protXmlFiles = new ArrayList<String>();
    
    private static final Logger log = Logger.getLogger(ProtxmlDataUploadService.class.getName());
    
    public ProtxmlDataUploadService() {
        
        peptideMap = new HashMap<String, Integer>();
        ionMap = new HashMap<String, Integer>();
        modifiedStateMap = new HashMap<String, Integer>();
        peptModStateCountMap = new HashMap<Integer, Integer>();
        
        
        piDaoFactory = ProteinferDAOFactory.instance();
        daoFactory = DAOFactory.instance();
        
        resDao = daoFactory.getMsSearchResultDAO();
        ppResDao = UploadDAOFactory.getInstance().getPeptideProphetResultDAO();
        
        runDao = piDaoFactory.getProteinferRunDao();
        peptDao = piDaoFactory.getProteinferPeptideDao();
        protDao = piDaoFactory.getProteinferProteinDao();
        ionDao = piDaoFactory.getProteinferIonDao();
        psmDao = piDaoFactory.getProteinferSpectrumMatchDao();
        grpDao = piDaoFactory.getProteinProphetProteinGroupDao();
        ppProtDao = piDaoFactory.getProteinProphetProteinDao();
        ppProteinIonDao = piDaoFactory.getProteinProphetProteinIonDao();
        ppSusumedDao = piDaoFactory.getProteinProphetSubsumedProteinDao();
        
    }
    
    
    public void upload() throws UploadException {
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!PROTEIN INFERENCE WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        uploadMsg = new StringBuilder();
        
        // Make sure we have a nrseq database ID
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        List<MsSearchDatabase> dbList = search.getSearchDatabases();
        if(dbList.size() != 1) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("No NRSEQ fasta database ID found for searchID: "+searchId);
            ex.appendErrorMessage("\n\t!!!PROTEIN INFERENCE WILL NOT BE UPLOADED\n");
            throw ex;
        }
        else {
            this.nrseqDatabaseId = dbList.get(0).getSequenceDatabaseId();
        }
        
        for(String protxmlFile: this.protXmlFiles) {
            uploadProtxmlFile(protxmlFile);
        }
    }


    private void uploadProtxmlFile(String protxmlFile) throws UploadException {
        
        log.info("Uploading protein inference results in file: "+protxmlFile);
        
        indistinguishableProteinGroupId = 1;
        int numProteinGroups = 0;
        int uploadedPinferId = 0;
        
        InteractProtXmlParser parser = new InteractProtXmlParser();
        try {
            parser.open(this.protxmlDirectory+File.separator+protxmlFile);
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PROTXML_ERROR, e);
            ex.appendErrorMessage(e.getMessage());
            throw ex;
        }
        
        modLookup = new DynamicModLookupUtil(searchId);
        
        // create a new entry for this protein inference run
        try {uploadedPinferId = addProteinInferenceRun(parser);}
        catch(UploadException ex) {
            parser.close();
            ex.appendErrorMessage("DELETING PROTEIN INFERENCE..."+uploadedPinferId);
            runDao.delete(uploadedPinferId);
            throw ex;
        }
        
        // save the protein and protein groups
        try {
            while(parser.hasNextProteinGroup()) {
                saveProteinProphetGroup(parser.getNextGroup(), uploadedPinferId);
                numProteinGroups++;
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PROTXML_ERROR, e);
            ex.appendErrorMessage(e.getErrorMessage());
            ex.appendErrorMessage("DELETING PROTEIN INFERENCE..."+uploadedPinferId);
            runDao.delete(uploadedPinferId);
            throw ex;
        }
        catch(UploadException e) {
            e.appendErrorMessage("DELETING PROTEIN INFERENCE..."+uploadedPinferId);
            runDao.delete(uploadedPinferId);
            throw e;
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.appendErrorMessage("DELETING PROTEIN INFERENCE..."+uploadedPinferId);
            runDao.delete(uploadedPinferId);
            throw ex;
        }
        finally {
            parser.close();
        }
        log.info("Uploaded file: "+protxmlFile+"; ID: "+uploadedPinferId+"; #protein groups: "+numProteinGroups);
        
        uploadMsg.append("\n\tProtein inferenceID: "+uploadedPinferId);
        uploadMsg.append("; #Protein groups in file: "+protxmlFile+": "+numProteinGroups);
    }


    private void saveProteinProphetGroup(ProteinProphetGroup proteinGroup,
            int pinferId) throws UploadException {
        
        proteinGroup.setProteinferId(pinferId);
        
        int ppGrpId = grpDao.saveGroup(proteinGroup);
        
        Map<Integer, Set<String>> subsumedMap = new HashMap<Integer, Set<String>>();
        Map<String, Integer> proteinIdMap = new HashMap<String, Integer>();
        
        for(ProteinProphetProtein protein: proteinGroup.getProteinList()) {
            
            protein.setProteinferId(pinferId);
            protein.setProteinProphetGroupId(ppGrpId);
            protein.setGroupId(this.indistinguishableProteinGroupId);
            int piProteinId = saveProtein(protein, subsumedMap);
            proteinIdMap.put(protein.getProteinName(), piProteinId);
            
            // Are there indistinguishable proteins?
            for(String name: protein.getIndistinguishableProteins()) {
                if(name.equals(protein.getProteinName()))
                    continue;
                ProteinProphetProtein iProt = protein.getIndistinguishableProtein(name);
                piProteinId = saveProtein(iProt, subsumedMap);
                proteinIdMap.put(name, piProteinId);
                // TODO what about protein coverage
            }
            
            this.indistinguishableProteinGroupId++;
        }
        
        saveSubsumedProteins(subsumedMap, proteinIdMap);
    }
    
    private void saveSubsumedProteins(Map<Integer, Set<String>> subsumedMap, Map<String, Integer> proteinIdMap) {
        
        for(int subsumedId: subsumedMap.keySet()) {
            Set<String> subsuming = subsumedMap.get(subsumedId);
            for(String name: subsuming) {
                int subsumingId = proteinIdMap.get(name);
                ppSusumedDao.saveSubsumedProtein(subsumedId, subsumingId);
            }
        }
    }

    public int saveProtein(ProteinProphetProtein protein, Map<Integer, Set<String>> subsumedMap) throws UploadException {
        
        int nrseqId = getNrseqProteinId(protein.getProteinName(), nrseqDatabaseId);
        if(nrseqId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.PROTEIN_NOT_FOUND);
            ex.appendErrorMessage("No NRSEQ id foud for protein: "+protein.getProteinName()+"; databaseId: "+nrseqDatabaseId);
            throw ex;
        }
        
        protein.setNrseqProteinId(nrseqId);
        int piProteinId = ppProtDao.saveProteinProphetProtein(protein);
        // save peptides
        savePeptides(protein);
        
        // Is this a subsumed protein
        // NOTE: assuming all subsuming proteins for a protein will be in the same
        // protein group as the protein
        if(protein.getSubsumed()) {
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
                peptide.setProteinferId(protein.getProteinferId());
                pinferPeptideId = peptDao.save(peptide);
                peptideMap.put(peptide.getSequence(), pinferPeptideId);
            }
            peptide.setId(pinferPeptideId);
            
            // link this peptide and protein
            protDao.saveProteinferProteinPeptideMatch(protein.getId(), peptide.getId());
            
            
            // look at each ion for the peptide
            for(ProteinProphetProteinPeptideIon ion: peptide.getIonList()) {
                
                ion.setPiProteinId(protein.getId());
                ion.setProteinferPeptideId(pinferPeptideId);
                
                Integer pinferIonId = savePeptideIon(peptide, ion);
                
                // create an entry in the ProteinProphetProteinIon table
                ion.setId(pinferIonId);
                ppProteinIonDao.save(ion);
            }
        }
    }

    private Integer savePeptideIon(ProteinProphetProteinPeptide peptide,
            ProteinProphetProteinPeptideIon ion) throws UploadException {
        
        // Update the modified sequence for the ion based on the modifications we have
        // in the database for this search
        List<MsResultResidueMod> modList = null;
        if(ion.getModifications().size() > 0) {
            String strippedSeq = peptide.getSequence();
            
            modList = getMatchingModifications(ion, strippedSeq);
            updateModifiedSequence(ion, strippedSeq, modList);
        }
        
        Integer pinferIonId = ionMap.get(ion.getCharge()+"_"+ion.getModifiedSequence());
        
        if(pinferIonId == null) {
            
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
            
            ionMap.put(ion.getCharge()+"_"+ion.getModifiedSequence(), pinferIonId);
            
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
            // NOTE: ProtXml modifications are 1-based.  
            if(!modLookup.hasDynamicModification(strippedSeq.charAt(mod.getPosition() - 1)))
                continue;
            
            MsResidueModification dbMod = modLookup.getDynamicResidueModification(
                        strippedSeq.charAt(mod.getPosition() - 1), // ProtXml modifications are 1-based
                        mod.getMass(),
                        true); // mass = mass of amino acid + modification mass
            
            if(dbMod == null) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.appendErrorMessage("searchId: "+searchId+
                        "; peptide: "+strippedSeq+
                        "; char: "+strippedSeq.charAt(mod.getPosition() - 1)+
                        "; pos: "+mod.getPosition()+
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
        
        
        // get all spectra for the given searchID that have the given unmodified sequence
        List<Integer> resultIds = resDao.loadResultIdsForSearchPeptide(searchId, ion.getUnmodifiedSequence());
        List<PeptideProphetResult> matchingResults = new ArrayList<PeptideProphetResult>();
        for(int resultId: resultIds) {
            
            PeptideProphetResult result = ppResDao.load(resultId);
            if(result == null)
                continue;
            
            // ignore all spectra with PeptideProphet probability < 0.05
            if(result.getProbability() < 0.05)
                continue;
            
            matchingResults.add(result);
        }
        
//        if(ion.getUnmodifiedSequence().equals("HQGVMVGMGQK")) {
//            System.out.println("Found");
//        }
        
        // sort the results by probability
        Collections.sort(matchingResults, new Comparator<PeptideProphetResult>() {
            @Override
            public int compare(PeptideProphetResult o1, PeptideProphetResult o2) {
                return Double.valueOf(o2.getProbability()).compareTo(o1.getProbability());
            }});
        
        // store the ones that have the charge and modification state as this ion
        int rank = 0;
        int numFound = 0;
        for(PeptideProphetResult result: matchingResults) {
        
            rank++;
            // make sure they are the same charge
            if(result.getCharge() != ion.getCharge())
                continue;
            
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
            }
        }
        
        // make sure the number of results returned above match the spectrum count for this ion in the 
        // ProtXml file.
        if(numFound != ion.getSpectrumCount()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.appendErrorMessage("Spectrum count ("+ion.getSpectrumCount()+") for ion ("+ion.getModifiedSequence()+
                        ") does not match the number of results returned: "+numFound);
            throw ex;
        }
    }

    private int getNrseqProteinId(String accession, int nrseqDatabaseId) {
        NrDbProtein protein = NrSeqLookupUtil.getDbProtein(nrseqDatabaseId, accession);
        if(protein != null)
            return protein.getProteinId();
        else
            return 0;
    }

    private int addProteinInferenceRun(InteractProtXmlParser parser) throws UploadException {
        
        int uploadedPinferId = 0;
        
        List<String> inputFiles = parser.getInputFiles();
        
        if(inputFiles.size() == 0) {
            UploadException e = new UploadException(ERROR_CODE.GENERAL);
            e.appendErrorMessage("No input(pepXML) files found for ProteinProphet run");
            throw e; 
        }
        
        boolean first = true;
        for(String inputPepXml: inputFiles) {
            PeptideProphetAnalysisUploadDAO pprophAnalysisDao = UploadDAOFactory.getInstance().getPeptideProphetAnalysisDAO();
            String fileName = new File(inputPepXml).getName();
            PeptideProphetAnalysis analysis = pprophAnalysisDao.loadAnalysisForFileName(fileName, this.searchId);
            if(analysis == null) {
                UploadException e = new UploadException(ERROR_CODE.GENERAL);
                e.appendErrorMessage("No matching PeptideProphet analysis found for input file: "+fileName+" and searchID: "+searchId);
                throw e;
            }
            
            if(first) {
                ProteinProphetRun run = new ProteinProphetRun();
                run.setInputGenerator(analysis.getAnalysisProgram());
                run.setProgram(ProteinInferenceProgram.PROTEIN_PROPHET);
                run.setProgramVersion(parser.getProgramVersion());
                run.setDate(new java.sql.Date(parser.getDate().getTime()));
                uploadedPinferId = runDao.save(run);
                first = false;
            }
            
            MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
            List<Integer> rsaIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysis.getId());
            
            ProteinferInputDAO inputDao = ProteinferDAOFactory.instance().getProteinferInputDao();
            try {
                for(int rsaId: rsaIds) {
                    ProteinferInput input = new ProteinferInput();
                    input.setInputId(rsaId);
                    input.setInputType(InputType.ANALYSIS);
                    input.setProteinferId(uploadedPinferId);
                    inputDao.saveProteinferInput(input);
                }
            }
            catch(RuntimeException ex) {
                UploadException e = new UploadException(ERROR_CODE.GENERAL, ex);
                e.appendErrorMessage("Error saving ProteinProphet input.");
                throw e;
            }
        }
        
        // save the parameters
        List<ProteinProphetParam> params = parser.getParams();
        ProteinProphetParamDAO paramDao = ProteinferDAOFactory.instance().getProteinProphetParamDao();
        try {
            for(ProteinProphetParam param: params) {
                param.setProteinferId(uploadedPinferId);
                paramDao.saveProteinProphetParam(param);
            }
        }
        catch(RuntimeException ex) {
            UploadException e = new UploadException(ERROR_CODE.GENERAL, ex);
            e.appendErrorMessage("Error saving ProteinProphet params.");
            throw e;
        }
        
        
        // save the ROC points
        ProteinProphetRocDAO rocDao = piDaoFactory.getProteinProphetRocDao();
        try {
            ProteinProphetROC roc = parser.getProteinProphetRoc();
            roc.setProteinferId(uploadedPinferId);
            rocDao.saveRoc(roc);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.appendErrorMessage("Error saving ProteinProphet ROC points.");
            throw ex;
        }
        return uploadedPinferId;
    }


    @Override
    public void setAnalysisId(int analysisId) {
        throw new UnsupportedOperationException("ProxmlDataUploadService determines the analysis ID based "+ 
                "on source_files attribute in pep.mxl files");
//        this.analysisId = analysisId;
    }

    @Override
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        return "\tProtein inference file format: "+SearchFileFormat.PROTXML+
        "\n\t"+uploadMsg.toString();
    }

    @Override
    public boolean preUploadCheckPassed() {
        
        preUploadCheckMsg = new StringBuilder();
        
        // 1. valid data directory
        File dir = new File(protxmlDirectory);
        if(!dir.exists()) {
            appendToMsg("Data directory does not exist: "+protxmlDirectory);
            return false;
        }
        if(!dir.isDirectory()) {
            appendToMsg(protxmlDirectory+" is not a directory");
            return false;
        }
        
        // 2. Look for interact*.prot.xml file
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_lc = name.toLowerCase();
                return name_lc.endsWith(".prot.xml");
            }});
        
        boolean found = false;
        for (int i = 0; i < files.length; i++) {
            if (fileNamePattern.matcher(files[i].getName().toLowerCase()).matches()) {
                protXmlFiles.add(files[i].getName());
                found = true;
            }
        }
        if(!found) {
            appendToMsg("Could not find interact*.prot.xml file(s) in directory: "+protxmlDirectory);
            return false;
        }
        
        
        preUploadCheckDone = true;
        
        return true;
    }
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

    @Override
    public void setDirectory(String directory) {
        this.protxmlDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        throw new UnsupportedOperationException();
    }
    
    public static void main(String[] args) throws UploadException {
        ProtxmlDataUploadService p = new ProtxmlDataUploadService();
        p.setDirectory("/Users/silmaril/Desktop/18mix_new");
//        p.setAnalysisId(30);
        p.setSearchId(35);
        p.upload();
        System.out.println(p.getUploadSummary());
    }
}
