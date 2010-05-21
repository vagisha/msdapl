package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;

public class IdPickerResultSaver {

    private static final Logger log = Logger.getLogger(IdPickerResultSaver.class);
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
    private static final IdPickerRunDAO runDao = factory.getIdPickerRunDao();
    private static final IdPickerInputDAO inputDao = factory.getIdPickerInputDao();
    private static final IdPickerProteinDAO protDao = factory.getIdPickerProteinDao();
    private static final IdPickerPeptideDAO peptDao = factory.getIdPickerPeptideDao();
    
    private static final IdPickerResultSaver instance = new IdPickerResultSaver();
    
    private IdPickerResultSaver() {}
    
    public static final IdPickerResultSaver instance() {
        return instance;
    }
    
    public void saveIdPickerResults(IdPickerRun idpRun, List<InferredProtein<SpectrumMatchIDP>> proteins) {
        
        long s = System.currentTimeMillis();
        
        // update the run summary
        runDao.saveIdPickerRunSummary(idpRun); // this will save entries in the IDPickerRunSummary table only
        
        // save the summary for each input
        inputDao.saveIdPickerInputList(idpRun.getInputSummaryList());
        
        // save the inferred proteins, associated peptides and spectrum matches
        saveInferredProteins(idpRun.getId(), proteins);
        
        
        long e = System.currentTimeMillis();
        log.info("SAVED IDPickerResults in: "+timeElapsed(s,e));
    }
    
    private void saveInferredProteins(int pinferId, List<InferredProtein<SpectrumMatchIDP>> proteins) {
        
        // map of peptide sequence and pinferPeptideIDs
        Map<String, Integer> idpPeptideIds = new HashMap<String, Integer>();
        
        for(InferredProtein<SpectrumMatchIDP> protein: proteins) {
            
            // save the protein
            IdPickerProtein idpProt = new IdPickerProtein();
            idpProt.setProteinferId(pinferId);
            idpProt.setNrseqProteinId(protein.getProteinId());
            idpProt.setClusterId(protein.getProteinClusterId());
            idpProt.setGroupId(protein.getProteinGroupId());
            idpProt.setIsParsimonious(protein.getIsAccepted());
            idpProt.setCoverage(protein.getPercentCoverage());
            int pinferProteinId = protDao.saveIdPickerProtein(idpProt);
            
            
            // save the peptides and the associated spectrum matches
            for(PeptideEvidence<SpectrumMatchIDP> pev: protein.getPeptides()) {
                Peptide peptide = pev.getPeptide();
                Integer pinferPeptideId = idpPeptideIds.get(peptide.getSequence());
                if(pinferPeptideId == null) {
                    IdPickerPeptide idpPept = new IdPickerPeptide();
                    idpPept.setGroupId(peptide.getPeptideGroupId());
                    idpPept.setSequence(peptide.getSequence());
                    
                    List<IdPickerSpectrumMatch> idpPsmList = new ArrayList<IdPickerSpectrumMatch>(pev.getSpectrumMatchCount());
                    for(SpectrumMatchIDP psm: pev.getSpectrumMatchList()) {
                        IdPickerSpectrumMatch idpPsm = new IdPickerSpectrumMatch();
                        idpPsm.setMsRunSearchResultId(psm.getHitId());
                        idpPsm.setFdr(psm.getFdr());
                        idpPsmList.add(idpPsm);
                    }
                    idpPept.setSpectrumMatchList(idpPsmList);
                    pinferPeptideId = peptDao.saveIdPickerPeptide(idpPept); // this will save the spectrum matches also
                    // add to our map
                    idpPeptideIds.put(peptide.getSequence(), pinferPeptideId);
                }
                // link the protein and peptide
                protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
                // make the group association if it does not already exist
                protDao.saveProteinPeptideGroupAssociation(pinferId, idpProt.getGroupId(), peptide.getPeptideGroupId());
            }
        }
    }

    private static float timeElapsed(long start, long end) {
        return (end - start)/(1000.0f);
    }
}
