package edu.uwpr.protinfer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwpr.protinfer.database.dao.GenericProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.GenericProteinferRun;
import edu.uwpr.protinfer.database.dao.GenericProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerFilterDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;
import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferInput;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.ProteinferStatus;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;
import edu.uwpr.protinfer.idpicker.IdPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerSummary;
import edu.uwpr.protinfer.idpicker.IdPickerSummary.RunSearchSummary;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class ProteinferSaver {

    private ProteinferSaver(){}
    
    public static <T extends SpectrumMatch> void saveProteinInferenceResults(IdPickerSummary searchSummary, IdPickerParams params, 
            List<InferredProtein<T>> proteins) {
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        GenericProteinferRun runDao = fact.getProteinferRunDao();
        IdPickerFilterDAO filterDao = fact.getProteinferFilterDao();
        ProteinferInputDAO inputDao = fact.getProteinferInputDao();
        
        int pinferId = runDao.saveNewProteinferRun();
        runDao.setProteinferStatus(pinferId, ProteinferStatus.COMPLETE);
        runDao.setProteinferUnfilteredProteinCount(pinferId, searchSummary.getNumTotalProteins());
        runDao.setProteinferCompletionDate(pinferId, new Date(System.currentTimeMillis()));
        
        // save the filters
        IdPickerFilter filter = new IdPickerFilter("decoyRatio", String.valueOf(params.getDecoyRatio()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new IdPickerFilter("decoyPrefix", params.getDecoyPrefix(), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new IdPickerFilter("parsimonyAnalysis", String.valueOf(params.getDoParsimonyAnalysis()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new IdPickerFilter("maxAbsFDR", String.valueOf(params.getMaxAbsoluteFdr()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new IdPickerFilter("maxRelativeFDR", String.valueOf(params.getMaxRelativeFdr()), pinferId);
        filterDao.saveProteinferFilter(filter);
        
        // save the input runSearchIDs
        List<ProteinferInput> inputList = new ArrayList<ProteinferInput>();
        for(RunSearchSummary rs: searchSummary.getRunSearchList()) {
            ProteinferInput input = new ProteinferInput();
            input.setProteinferId(pinferId);
            input.setRunSearchId(rs.getRunSearchId());
            input.setNumTargetHits(rs.getTotalTargetHits());
            input.setNumDecoyHits(rs.getTotalDecoyHits());
            input.setNumFilteredTargetHits(rs.getFilteredTargetHits());
            inputList.add(input);
        }
        inputDao.saveIdPickerInputList(inputList);
        
        // save the results
        GenericProteinferSpectrumMatchDAO specDao = fact.getProteinferSpectrumMatchDao();
        GenericProteinferPeptideDAO peptDao = fact.getProteinferPeptideDao();
        GenericProteinferProteinDAO protDao = fact.getProteinferProteinDao();
        Map<String, Integer> dbPeptideIds = new HashMap<String, Integer>();
        
        for(InferredProtein<T> prot: proteins) {
            
            // save the protein
            BaseProteinferProtein<T> protein = new ProteinferProtein();
            protein.setClusterId(prot.getProteinClusterId());
            protein.setGroupId(prot.getProteinGroupId());
            protein.setNrseqProteinId(prot.getProteinId());
            protein.setAccession(prot.getAccession());
            protein.setCoverage(prot.getPercentCoverage());
            protein.setProteinferId(pinferId);
            protein.setIsParsimonious(prot.getIsAccepted());
            protDao.saveProteinferProtein(protein);
            
            
            // save all the peptides for the protein
            for(PeptideEvidence<T> pev: prot.getPeptides()) {
                
                // save the peptide and get the Id
                Integer pinferPeptideId = dbPeptideIds.get(pev.getModifiedPeptideSeq());
                if(pinferPeptideId == null) {
                    BaseProteinferPeptide<T> peptide = new ProteinferPeptide(pinferId, pev.getPeptide().getPeptideGroupId(), pev.getModifiedPeptideSeq());
                    pinferPeptideId = peptDao.saveProteinferPeptide(prot.getProteinId(), peptide);
                    
                    dbPeptideIds.put(pev.getModifiedPeptideSeq(), pinferPeptideId);
                    
                    // save all the spectrum matches for the peptide
                    for(SpectrumMatch psm: pev.getSpectrumMatchList()) {
                        ProteinferSpectrumMatch match = new ProteinferSpectrumMatch(pinferPeptideId, psm.getHitId(), psm.getFdr());
                        specDao.saveSpectrumMatch(match);
                    }
                }
                // link the protein and peptide
                peptDao.saveProteinferProteinPeptideMatch(prot.getProteinId(), pinferPeptideId);
            }
        }
    }
}
