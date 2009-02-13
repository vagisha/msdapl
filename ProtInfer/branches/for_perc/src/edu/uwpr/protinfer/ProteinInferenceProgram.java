package edu.uwpr.protinfer;

import edu.uwpr.protinfer.ProgramParam.ParamMaker;
import edu.uwpr.protinfer.ProgramParam.SCORE;
import edu.uwpr.protinfer.ProgramParam.TYPE;


public class ProteinInferenceProgram {

    private final String name;
    private final String displayName;
    private String description;
    private ProgramParam[] params;
    
    
    public static final ProteinInferenceProgram PROTINFER_SEQ = new PISequestProgram();
    public static final ProteinInferenceProgram PROTINFER_PLCID = new PIPickerProlucidProgram();
    public static final ProteinInferenceProgram PROTINFER_PERC = new PIPickerPercolatorProgram();
    
    private ProteinInferenceProgram(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String name() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    void setDescription(String description) {
        this.description = description;
    }
    
    public ProgramParam[] getProgramParams() {
        if(params == null)
            return new ProgramParam[0];
        else
            return params;
    }
    void setProgramParams(ProgramParam[] params) {
        this.params = params;
    }
    
    public static boolean isSupported(ProteinInferenceProgram program) {
        return (program == PROTINFER_PERC || 
                program == PROTINFER_SEQ || 
                program == PROTINFER_PLCID);
    }
    
    public static ProteinInferenceProgram getProgramForName(String name) {
        if("PROTINFER_SEQ".equalsIgnoreCase(name))
            return PROTINFER_SEQ;
        if("PROTINFER_PLCID".equalsIgnoreCase(name))
            return PROTINFER_PLCID;
        else if ("PROTINFER_PERC".equalsIgnoreCase(name))
            return PROTINFER_PERC;
        else 
            return null;
    }
    
    public String getDisplayNameForParam(String name) {
        for(ProgramParam param: params) {
            if(param.getName().equals(name)) 
                return param.getDisplayName();
        }
        return null;
    }
   
    
    static class PISequestProgram extends ProteinInferenceProgram {
        private PISequestProgram() {
            super("PROTINFER_SEQ", "ProtInfer");
            this.setDescription("This protein inference program is base on the IDPicker program developed in David Tabb's lab.");
            this.setProgramParams(new ProgramParam[]{
                        ParamMaker.makeMaxFDRParam(),
                        ParamMaker.makeFDRScoreParam(SCORE.XCorr, new SCORE[]{SCORE.XCorr}),
                        ParamMaker.makeFDRFormulaParam(),
                        ParamMaker.makeDecoyPrefixParam(),
                        ParamMaker.makePeptideDefParam(),
                        ParamMaker.makeMinPeptParam(),
                        ParamMaker.makeMinUniqPeptParam(),
                        ParamMaker.makeMinCoverageParam(),
                        ParamMaker.makeMinPeptLengthParam(),
                        ParamMaker.makeRemoveAmbigSpectraParam()
            });
        }
    }
    
    static class PIPickerProlucidProgram extends ProteinInferenceProgram {
        private PIPickerProlucidProgram() {
            super("PROTINFER_PLCID", "ProtInfer");
            this.setDescription("This protein inference program is base on the IDPicker program developed in David Tabb's lab.");
            this.setProgramParams(new ProgramParam[]{
                        ParamMaker.makeMaxFDRParam(),
                        ParamMaker.makeFDRScoreParam(SCORE.PrimaryScore, 
                                new SCORE[]{SCORE.PrimaryScore}),
                        ParamMaker.makeFDRFormulaParam(),
                        ParamMaker.makeDecoyPrefixParam(),
                        ParamMaker.makePeptideDefParam(),
                        ParamMaker.makeMinPeptParam(),
                        ParamMaker.makeMinUniqPeptParam(),
                        ParamMaker.makeMinCoverageParam(),
                        ParamMaker.makeMinPeptLengthParam(),
                        ParamMaker.makeRemoveAmbigSpectraParam()
            });
        }
    }
    
    static class PIPickerPercolatorProgram extends ProteinInferenceProgram {
        private PIPickerPercolatorProgram() {
            super("PROTINFER_PERC", "ProtInfer");
            this.setDescription("This protein inference program is base on the IDPicker program developed in David Tabb's lab.");
            this.setProgramParams(new ProgramParam[]{
                    new ProgramParam(TYPE.DOUBLE, 
                          "qval_percolator", "Qvalue Threshold", 
                          "0.05", null,
                          "Qvalue threshold for filtering search hits"),
                    new ProgramParam(TYPE.DOUBLE, 
                          "pep_percolator", "PEP Threshold", 
                          "0.05", null,
                          "Posterior Error Probability threshold for filtering search hits"),
                        ParamMaker.makePeptideDefParam(),
                        ParamMaker.makeMinPeptParam(),
                        ParamMaker.makeMinUniqPeptParam(),
                        ParamMaker.makeMinCoverageParam(),
                        ParamMaker.makeMinPeptLengthParam(),
                        ParamMaker.makeRemoveAmbigSpectraParam()
            });
        }
    }
        
}
