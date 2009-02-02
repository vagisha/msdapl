/**
 * IdPickerExecutorNoFDR.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.infer.InferredProtein;
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
        
        // get all the search hits for the given inputIds
        List<PeptideSpectrumMatchNoFDR> allPsms = IdPickerInputGetter.instance().getInputNoFdr(idpRun, params);
        System.out.println("Got all spectrum matches");
        Thread.sleep(5*1000);
        System.out.println("After Sleeping");

        // assign ids to peptides and proteins(nrseq ids)
        IDPickerExecutor.assignIdsToPeptidesAndProteins(allPsms, program);
        System.out.println("Assigned IDs");
        Thread.sleep(5*1000);
        System.out.println("After Sleeping");
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatch>> proteins = IDPickerExecutor.inferProteins(allPsms, params);
        System.out.println("Got inferred proteins");
        Thread.sleep(5*1000);
        System.out.println("After Sleeping");
        
        // FINALLY save the results
        IdPickerResultSaver.instance().saveResults(idpRun, proteins);
    }
   
}
