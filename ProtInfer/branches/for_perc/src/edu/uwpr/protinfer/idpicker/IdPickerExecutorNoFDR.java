/**
 * IdPickerExecutorNoFDR.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SpectrumMatch;

/**
 * 
 */
public class IdPickerExecutorNoFDR {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    public void execute(IdPickerRun idpRun, IDPickerParams params) throws Exception {
        
        // get the program used for the generating the input data
        // NOTE: WE ASSUME ALL THE GIVEN inputIds WERE SEARCHED/ANALYSED WITH THE SAME PROGRAM
        Program program = idpRun.getInputGenerator();
        
        // get all the search hits for the given runSearchIds
        List<PeptideSpectrumMatchNoFDR> allPsms = getAllSearchHits(idpRun, params, program);
        
        // assign ids to peptides and proteins(nrseq ids)
        IDPickerExecutor.assignIdsToPeptidesAndProteins(allPsms, program);
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatch>> proteins = IDPickerExecutor.inferProteins(allPsms, params);
        
        // calculate the protein coverage
        IDPickerExecutor.calculateProteinSequenceCoverage(proteins);
        
        
        // FINALLY save the results
        IdPickerResultSaver.instance().saveResults(idpRun, proteins);
    }
    
    // This method also updates the summary with the total number of proteins found for all the 
    // search hits.
    private List<PeptideSpectrumMatchNoFDR> getAllSearchHits(IdPickerRun idpRun, IDPickerParams params, Program inputGenerator) {
        
        Set<String> allProteins = new HashSet<String>();
        Set<String> allPeptides = new HashSet<String>();
        
        List<IdPickerInput> idpInputList = idpRun.getInputList();
        List<PeptideSpectrumMatchNoFDR> allPsms = new ArrayList<PeptideSpectrumMatchNoFDR>();
        IdPickerInputGetter resGetter = IdPickerInputGetter.instance();
        
        for(IdPickerInput input: idpInputList) {
            
            int inputId = input.getInputId();
            
            List<PeptideSpectrumMatchNoFDR> psms = resGetter.getInputNoFdr(inputId, params, inputGenerator);
            allPsms.addAll(psms);
            
            for(PeptideSpectrumMatchNoFDR psm: psms) {
                PeptideHit pept = psm.getPeptideHit();
                allPeptides.add(pept.getSequence());
                for(ProteinHit prot: pept.getProteinList()) {
                    allProteins.add(prot.getAccession());
                }
            }
            input.setNumFilteredTargetHits(psms.size());
            input.setNumTargetHits(resGetter.getUnfilteredInputCount(inputId, inputGenerator));
        }
        idpRun.setNumUnfilteredProteins(allProteins.size());
        idpRun.setNumUnfilteredPeptides(allPeptides.size());
        
        return allPsms;
    }
}
