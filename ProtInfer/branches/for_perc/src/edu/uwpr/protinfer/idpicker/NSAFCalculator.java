/**
 * NSAFCalculator.java
 * @author Vagisha Sharma
 * Jan 28, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class NSAFCalculator {

    private static final Logger log = Logger.getLogger(NSAFCalculator.class);
    
    private static final NSAFCalculator instance = new NSAFCalculator();
    
    private NSAFCalculator () {}
    
    public static NSAFCalculator instance() {
        return instance;
    }
    
    public <S extends SpectrumMatch> void calculateNSAF(List<InferredProtein<S>> proteins) throws Exception {
        double totalSpC_L = 0;
        
        long s = System.currentTimeMillis();
        for(InferredProtein<S> protein: proteins) {
            String proteinSeq = null;
            try {
                proteinSeq = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(protein.getProteinId());
//                proteinSeq = NrSeqLookupUtil.getProteinSequence(protein.getProteinId());
            }
            catch (Exception e) {
                log.error("Exception getting nrseq protein for proteinId: "+protein.getProteinId(), e);
                throw e;
            }
            
            if(proteinSeq == null || proteinSeq.length() == 0) {
                log.error("Protein sequence for proteinId: "+protein.getProteinId()+" is null.");
                throw new Exception("Protein sequence for proteinId: "+protein.getProteinId()+" is null.");
            }
            
            double spc_L = (double)protein.getSpectralEvidenceCount() / (double)proteinSeq.length();
            totalSpC_L += spc_L;
            protein.setNSAF(spc_L);
        }
        
        for(InferredProtein<S> protein: proteins) {
            protein.setNSAF(protein.getNSAF() / totalSpC_L);
        }
        long e = System.currentTimeMillis();
        log.info("Time to calculate NSAF: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }
}
