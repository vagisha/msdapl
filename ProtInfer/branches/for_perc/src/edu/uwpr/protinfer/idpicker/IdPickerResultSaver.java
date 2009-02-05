package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferIonDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideBase;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.util.TimeUtils;

public class IdPickerResultSaver {

    private static final Logger log = Logger.getLogger(IdPickerResultSaver.class);
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
//    private static final IdPickerRunDAO runDao = factory.getIdPickerRunDao();
    private static final IdPickerInputDAO inputDao = factory.getIdPickerInputDao();
    private static final IdPickerProteinBaseDAO protDao = factory.getIdPickerProteinBaseDao();
    private static final IdPickerPeptideBaseDAO peptDao = factory.getIdPickerPeptideBaseDao();
    private static final ProteinferIonDAO ionDao = factory.getProteinferIonDao();
    private static final ProteinferSpectrumMatchDAO psmDao = factory.getProteinferSpectrumMatchDao();
    private static final IdPickerSpectrumMatchDAO idpPsmDao = factory.getIdPickerSpectrumMatchDao();
    
    
    private static final IdPickerResultSaver instance = new IdPickerResultSaver();
    
    private IdPickerResultSaver() {}
    
    public static final IdPickerResultSaver instance() {
        return instance;
    }
    
    public <T extends SpectrumMatch> void saveResults(IdPickerRun idpRun, List<InferredProtein<T>> proteins) {
        
        long s = System.currentTimeMillis();
        
        // update the run summary
        // runDao.saveIdPickerRunSummary(idpRun); // this will save entries in the IDPickerRunSummary table only
        
        // save the summary for each input
        inputDao.saveIdPickerInputList(idpRun.getInputList());
        
        // save the inferred proteins, associated peptides and spectrum matches
        saveInferredProteins(idpRun.getId(), proteins);
        
        
        long e = System.currentTimeMillis();
        log.info("SAVED IDPickerResults in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes.");
    }
    
    private <T extends SpectrumMatch> void saveInferredProteins(int pinferId, List<InferredProtein<T>> proteins) {
        
        // map of peptide id and pinferPeptideIDs
        Map<Integer, Integer> idpPeptideIds = new HashMap<Integer, Integer>();
        
        for(InferredProtein<T> protein: proteins) {
            
            // save the protein
            IdPickerProteinBase idpProt = new IdPickerProteinBase();
            idpProt.setProteinferId(pinferId);
            idpProt.setNrseqProteinId(protein.getProteinId());
            idpProt.setClusterId(protein.getProteinClusterId());
            idpProt.setGroupId(protein.getProteinGroupId());
            idpProt.setIsParsimonious(protein.getIsAccepted());
            idpProt.setCoverage(protein.getPercentCoverage());
            idpProt.setNsaf(protein.getNSAF());
            int pinferProteinId = protDao.saveIdPickerProtein(idpProt);
            
            
            // If the user ran protein inference with a peptide evidence being more
            // than just the peptide sequence, we will group all peptide evidence 
            // that reprensent the same peptide sequence (w/o mods). This is because
            // data in the tables should be stored uniformly regardless of peptide
            // definition used for running protein inference
            List<PeptideEvidence<T>> pevList = groupPeptideEvidenceByPeptideSeq(protein.getPeptides());
            
            // save the peptides, ions and the associated spectrum matches
            for(PeptideEvidence<T> pev: pevList) {
                Peptide peptide = pev.getPeptide();
                Integer pinferPeptideId = idpPeptideIds.get(peptide.getId());
                if(pinferPeptideId == null) {
                    pinferPeptideId = savePeptideEvidence(pev, pinferId);
                    // add to our map
                    idpPeptideIds.put(peptide.getId(), pinferPeptideId);
                }
                // link the protein and peptide
                protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
                // make the group association if it does not already exist
                protDao.saveProteinPeptideGroupAssociation(pinferId, idpProt.getGroupId(), peptide.getPeptideGroupId());
            }
        }
    }

    private<T extends SpectrumMatch> List<PeptideEvidence<T>> groupPeptideEvidenceByPeptideSeq(List<PeptideEvidence<T>> pevList) {
        
        Map<String, PeptideEvidence<T>> peptListMap = new HashMap<String, PeptideEvidence<T>>(pevList.size()*2);
        for(PeptideEvidence<T> pev: pevList) {
            PeptideEvidence<T> pevO = peptListMap.get(pev.getPeptide().getPeptideSequence());
            if(pevO == null) {
                peptListMap.put(pev.getPeptide().getPeptideSequence(), pev);
            }
            else {
                PeptideEvidence<T> pevC = combinePeptideEvidence(pev, pevO);
                peptListMap.put(pev.getPeptide().getPeptideSequence(), pevC);
            }
        }
        
        return new ArrayList<PeptideEvidence<T>>(peptListMap.values());
    }
    
    private <T extends SpectrumMatch> PeptideEvidence<T> combinePeptideEvidence(PeptideEvidence<T> pev1, PeptideEvidence<T> pev2) {
       
        for(T psm: pev1.getSpectrumMatchList()) {
            pev2.addSpectrumMatch(psm);
        }
        return pev2;
    }
    
    private <T extends SpectrumMatch> int savePeptideEvidence(PeptideEvidence<T> pev, int pinferId) {
        
        Peptide peptide = pev.getPeptide();
        IdPickerPeptideBase idpPept = new IdPickerPeptideBase();
        idpPept.setGroupId(peptide.getPeptideGroupId());
        idpPept.setSequence(peptide.getPeptideSequence());
        idpPept.setUniqueToProtein(peptide.isUniqueToProtein());
        idpPept.setProteinferId(pinferId);
        
        int pinferPeptideId = peptDao.saveIdPickerPeptide(idpPept);
        
        
        // sort the psm's by sequence + mod_state + charge
        Map<String, List<T>> map = new HashMap<String, List<T>>(pev.getSpectrumMatchList().size());
        Map<String, Integer> modIds = new HashMap<String, Integer>();
        int modId = 1;
        for(T psm: pev.getSpectrumMatchList()) {
            String modseq = psm.getModifiedSequence(); // this is the modified sequence
            
            
            String key = modseq+"_"+psm.getCharge();
            List<T> list = map.get(key);
            if(list == null) {
                list = new ArrayList<T>();
                map.put(key, list);
                
                // get the modification id
                Integer mid = modIds.get(modseq);
                if(mid == null) {
                    mid = modId;
                    modId++;
                    modIds.put(modseq, mid);
                }
            }
            list.add(psm);
        }
        
        // save all the ions (sequence + mod_state + charge) for the peptide
        for(String key: map.keySet()) {
            ProteinferIon ion = new ProteinferIon();
            List<T> psmList = map.get(key);
            ion.setProteinferPeptideId(pinferPeptideId);
            ion.setCharge(psmList.get(0).getCharge());
            String modseq = key.substring(0, key.lastIndexOf("_"));
//            ion.setSequence(modseq);
            ion.setModificationStateId(modIds.get(modseq));
            int ionId = ionDao.save(ion);
            
            // save all the spectra for this ion
            for(T psm: psmList) {
                if(psm instanceof SpectrumMatchIDP) {
                    IdPickerSpectrumMatch idpPsm = new IdPickerSpectrumMatch();
                    idpPsm.setProteinferIonId(ionId);
                    idpPsm.setMsRunSearchResultId(psm.getHitId());
                    idpPsm.setFdr(((SpectrumMatchIDP)psm).getFdr());
                    idpPsm.setRank(psm.getRank());
                    idpPsmDao.saveSpectrumMatch(idpPsm);
                }
                else {
                    ProteinferSpectrumMatch idpPsm = new ProteinferSpectrumMatch();
                    idpPsm.setProteinferIonId(ionId);
                    idpPsm.setMsRunSearchResultId(psm.getHitId());
                    idpPsm.setRank(psm.getRank());
                    psmDao.saveSpectrumMatch(idpPsm);
                }
            }
        }
        return pinferPeptideId;
    }
   
}
