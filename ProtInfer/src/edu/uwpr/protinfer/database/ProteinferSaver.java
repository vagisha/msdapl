package edu.uwpr.protinfer.database;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferFilterDAO;
import edu.uwpr.protinfer.database.dao.ProteinferInputDAO;
import edu.uwpr.protinfer.database.dao.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferFilter;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.SearchSummary;
import edu.uwpr.protinfer.idpicker.SearchSummary.RunSearch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class ProteinferSaver {

    private ProteinferSaver(){}
    
    public static <T extends SpectrumMatch> void saveProteinInferenceResults(SearchSummary searchSummary, IDPickerParams params, 
            List<InferredProtein<T>> proteins) {
        
        DAOFactory fact = DAOFactory.instance();
        ProteinferRunDAO runDao = fact.getProteinferRunDao();
        ProteinferFilterDAO filterDao = fact.getProteinferFilterDao();
        ProteinferInputDAO inputDao = fact.getProteinferInputDao();
        
        int pinferId = runDao.saveNewProteinferRun();
        
        // save the filters
        ProteinferFilter filter = new ProteinferFilter("decoyRatio", String.valueOf(params.getDecoyRatio()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new ProteinferFilter("decoyPrefix", params.getDecoyPrefix(), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new ProteinferFilter("parsimonyAnalysis", String.valueOf(params.getDoParsimonyAnalysis()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new ProteinferFilter("maxAbsFDR", String.valueOf(params.getMaxAbsoluteFdr()), pinferId);
        filterDao.saveProteinferFilter(filter);
        filter = new ProteinferFilter("maxRelativeFDR", String.valueOf(params.getMaxRelativeFdr()), pinferId);
        filterDao.saveProteinferFilter(filter);
        
        // save the input runSearchIDs
        List<Integer> runSearchIdList = new ArrayList<Integer>();
        for(RunSearch rs: searchSummary.getRunSearchList())
            runSearchIdList.add(rs.getRunSearchId());
        inputDao.saveProteinferInput(pinferId, runSearchIdList);
        
        // save the results
        ProteinferSpectrumMatchDAO specDao = fact.getProteinferSpectrumMatchDao();
        ProteinferPeptideDAO peptDao = fact.getProteinferPeptideDao();
        ProteinferProteinDAO protDao = fact.getProteinferProteinDao();
        
        for(InferredProtein<T> prot: proteins) {
            
            // save the protein
            ProteinferProtein protein = new ProteinferProtein();
            protein.setAccession(prot.getAccession());
            protein.setClusterId(prot.getProteinClusterId());
            protein.setGroupId(prot.getProteinGroupId());
            protein.setNrseqProteinId(prot.getProteinId());
            protein.setCoverage(98.0);
            protein.setProteinferId(pinferId);
            protein.setIsParsimonious(prot.getIsAccepted());
            int pinferProteinId = protDao.saveProteinferProtein(protein);
            
            
            // save all the peptides for the protein
            for(PeptideEvidence<T> pev: prot.getPeptides()) {
                
                // save the peptide and get the Id
                int pinferPeptideId = peptDao.getMatchingProteinferPeptideId(pinferId, pev.getModifiedPeptideSeq());
                if(pinferPeptideId == 0) {
                    ProteinferPeptide peptide = new ProteinferPeptide(pinferId, pev.getPeptide().getPeptideGroupId(), pev.getModifiedPeptideSeq());
                    pinferPeptideId = peptDao.saveProteinferPeptide(pinferProteinId, peptide);
                    
                    // save all the spectrum matches for the peptide
                    for(SpectrumMatch psm: pev.getSpectrumMatchList()) {
                        ProteinferSpectrumMatch match = new ProteinferSpectrumMatch(pinferPeptideId, psm.getHitId(), psm.getFdr());
                        specDao.saveSpectrumMatch(match);
                    }
                }
                // link the protein and peptide
                peptDao.saveProteinferPeptideProteinMatch(pinferProteinId, pinferPeptideId);
            }
        }
    }
}
