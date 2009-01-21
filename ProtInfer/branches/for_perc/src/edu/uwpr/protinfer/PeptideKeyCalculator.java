/**
 * PeptideKeyCalculator.java
 * @author Vagisha Sharma
 * Jan 20, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer;

import org.yeastrc.ms.domain.search.MsSearchResult;

/**
 * 
 */
public class PeptideKeyCalculator {

    private PeptideKeyCalculator() {}
    
    public static String getKey(MsSearchResult result, PeptideDefinition peptideDef) {
        String key = null;
        if(peptideDef.isUseMods()) {
            key = result.getResultPeptide().getModifiedPeptide();
        }
        else {
            key = result.getResultPeptide().getPeptideSequence();
        }
        if(peptideDef.isUseCharge())
            key += "_"+result.getCharge();
        return key;
    }
}
