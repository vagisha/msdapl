/**
 * IdPickerParamsMaker.java
 * @author Vagisha Sharma
 * Jan 21, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam;

/**
 * 
 */
public class IdPickerParamsMaker {

    private IdPickerParamsMaker() {}
    
    public static IDPickerParams makeIdPickerParams(List<IdPickerParam> paramList) {
        IDPickerParams idpParams = new IDPickerParams();
        idpParams.setDoFdrCalculation(false); // set this to false initially
                                           // if we find FDR calculation filters we will set this to true;
        List<IdPickerParam> moreFilters = new ArrayList<IdPickerParam>();
        
        for(IdPickerParam param: paramList) {
            // Max. Absolute FDR
            if(param.getName().equals("maxAbsFDR")) {
                idpParams.setMaxAbsoluteFdr(Float.parseFloat(param.getValue()));
                idpParams.setDoFdrCalculation(true);
            }
            // Max. Relative FDR
            else if(param.getName().equals("maxRelativeFDR")) {
                idpParams.setMaxRelativeFdr(Float.parseFloat(param.getValue()));
                idpParams.setDoFdrCalculation(true);
            }
            // Decoy Ratio
            else if (param.getName().equals("decoyRatio"))
                idpParams.setDecoyRatio(Float.parseFloat(param.getValue()));
            // Decoy Prefix
            else if (param.getName().equals("decoyPrefix"))
                idpParams.setDecoyPrefix(param.getValue());
//            else if (filter.getName().equalsIgnoreCase("parsimonyAnalysis"))
//                params.setDoParsimonyAnalysis(Boolean.valueOf(filter.getValue()));
            
            // Formula that will be used to calculate FDR
            else if(param.getName().equals("FDRFormula")) {
                String val = param.getValue();
                if(val.equals("2R/(F+R)"))
                    idpParams.setUseIdPickerFDRFormula(true);
                else
                    idpParams.setUseIdPickerFDRFormula(false);
            }
            // Peptide Definition
            else if(param.getName().equals("PeptDef")) {
                PeptideDefinition peptDef = new PeptideDefinition();
                String val = param.getValue();
                if(val.equals("Sequence + Modifications")) {
                    peptDef.setUseMods(true);
                }
                else if(val.equals("Sequence + Charge")) {
                    peptDef.setUseCharge(true);
                }
                else if(val.equals("Sequence + Modifications + Charge")) {
                    peptDef.setUseCharge(true);
                    peptDef.setUseMods(true);
                }
                idpParams.setPeptideDefinition(peptDef);
            }
            // Min. Peptides
            else if(param.getName().equals("minPept")) {
                idpParams.setMinPeptides(Integer.parseInt(param.getValue()));
            }
            // Min Unique Peptides
            else if(param.getName().equals("minUniqePept")) {
                idpParams.setMinUniquePeptides(Integer.parseInt(param.getValue()));
            }
            // Min. Peptide Length
            else if(param.getName().equals("minPeptLen")) {
                idpParams.setMinPeptideLength(Integer.parseInt(param.getValue()));
            }
            // Min. spectra per peptide
//            else if(filter.getName().equals("minPeptSpectra")) {
//                params.setMinPeptideSpectra(Integer.parseInt(filter.getValue()));
//            }
            //Min Coverage for a protein
            else if(param.getName().equals("coverage")) {
                idpParams.setMinCoverage(Float.parseFloat(param.getValue()));
            }
            // Remove Ambiguous Spectra
            else if(param.getName().equals("removeAmbigSpectra")) {
                idpParams.setRemoveAmbiguousSpectra(Boolean.parseBoolean(param.getValue()));
            }
            else {
                moreFilters.add(param);
            }
        }
        if(moreFilters.size() > 0)
            idpParams.addMoreFilters(moreFilters);
        return idpParams;
    }
}
