package org.yeastrc.ms.domain.protinfer;

import org.yeastrc.ms.domain.protinfer.ProgramParam.DoubleValidator;
import org.yeastrc.ms.domain.protinfer.ProgramParam.ParamMaker;
import org.yeastrc.ms.domain.protinfer.ProgramParam.SCORE;
import org.yeastrc.ms.domain.protinfer.ProgramParam.TYPE;


public class ProteinInferenceProgram {

    private final String name;
    private final String displayName;
    private String version;
    private String description;
    private ProgramParam[] params;
    
    public static final String protInferVersion = "0.1.1";
    
    public static final ProteinInferenceProgram PROTINFER_SEQ = new PISequestProgram();
    public static final ProteinInferenceProgram PROTINFER_PLCID = new PIProlucidProgram();
    public static final ProteinInferenceProgram PROTINFER_PERC = new PIPercolatorProgram();
    public static final ProteinInferenceProgram PROTINFER_PERC_OLD = new PIPercolatorProgramOld();
    public static final ProteinInferenceProgram PROTEIN_PROPHET = new ProteinProphetProgram();
    
    
    private ProteinInferenceProgram(String name, String displayName, String version) {
        this.name = name;
        this.displayName = displayName;
        this.version = version;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String getVersion() {
        return version;
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
                program == PROTINFER_PERC_OLD ||
                program == PROTINFER_SEQ || 
                program == PROTINFER_PLCID);
    }
    
    public static boolean isIdPicker(ProteinInferenceProgram program) {
        return isSupported(program); // for now all are IDPicker.
    }
    
    public static ProteinInferenceProgram getProgramForName(String name) {
        if("PROTINFER_SEQ".equalsIgnoreCase(name))
            return PROTINFER_SEQ;
        if("PROTINFER_PLCID".equalsIgnoreCase(name))
            return PROTINFER_PLCID;
        else if ("PROTINFER_PERC".equalsIgnoreCase(name))
            return PROTINFER_PERC;
        else if ("PROTINFER_PERC_OLD".equalsIgnoreCase(name))
            return PROTINFER_PERC_OLD;
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
   
    public ProgramParam getParamForName(String name) {
        for(ProgramParam param: this.params) {
            if(name.equalsIgnoreCase(param.getName()))
                return param;
        }
        return null;
    }
    
    static class PISequestProgram extends ProteinInferenceProgram {
        private PISequestProgram() {
            super("PROTINFER_SEQ", "ProtInfer", protInferVersion);
            this.setDescription("This protein inference program is based on the IDPicker program developed in David Tabb's lab.");
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
    
    static class PIProlucidProgram extends ProteinInferenceProgram {
        private PIProlucidProgram() {
            super("PROTINFER_PLCID", "ProtInfer", protInferVersion);
            this.setDescription("This protein inference program is based on the IDPicker program developed in David Tabb's lab.");
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
    
    static class PIPercolatorProgram extends ProteinInferenceProgram {
        private PIPercolatorProgram() {
            super("PROTINFER_PERC", "ProtInfer", protInferVersion);
            this.setDescription("This protein inference program is based on the IDPicker program developed in David Tabb's lab.");
            
            DoubleValidator validator = new DoubleValidator();
            validator.setMinVal(0.0);
            validator.setMaxVal(1.0);
            
            ProgramParam qvalParam = new ProgramParam(TYPE.DOUBLE, 
                    "qval_percolator", "Max. q-value", 
                    "0.05", null,
                    "Qvalue threshold for filtering search hits");
            qvalParam.setValidator(validator);
            
            ProgramParam pepParam = new ProgramParam(TYPE.DOUBLE, 
                    "pep_percolator", "Max. PEP", 
                    "0.05", null,
                    "Posterior Error Probability threshold for filtering search hits");
                pepParam.setValidator(validator);
            
            this.setProgramParams(new ProgramParam[]{
                    qvalParam,
                    pepParam,
                    ParamMaker.makeMinPeptParam(),
                    ParamMaker.makeMinUniqPeptParam(),
                    ParamMaker.makePeptideDefParam(),
                    ParamMaker.makeMinCoverageParam(),
                    ParamMaker.makeMinPeptLengthParam(),
                    ParamMaker.makeRemoveAmbigSpectraParam()
            });
        }
    }
    
    static class PIPercolatorProgramOld extends ProteinInferenceProgram {
        private PIPercolatorProgramOld() {
            super("PROTINFER_PERC_OLD", "ProtInfer", protInferVersion);
            this.setDescription("This protein inference program is based on the IDPicker program developed in David Tabb's lab.");
            
            DoubleValidator qValValidator = new DoubleValidator();
            qValValidator.setMinVal(0.0);
            qValValidator.setMaxVal(1.0);            
            
            ProgramParam qvalParam = new ProgramParam(TYPE.DOUBLE, 
                    "qval_percolator", "Max. q-value", 
                    "0.05", null,
                    "Qvalue threshold for filtering search hits");
            qvalParam.setValidator(qValValidator);
            
            ProgramParam dsParam = new ProgramParam(TYPE.DOUBLE, 
                        "discriminantScore_percolator", "Min. Discriminant Score", 
                        null, null,
                        "Discriminant (SVM) score threshold for filtering search hits");
            DoubleValidator dsValidator = new DoubleValidator();
            dsValidator.setMinVal(-100);
            dsValidator.setMaxVal(100); 
            dsParam.setValidator(dsValidator);
            
            this.setProgramParams(new ProgramParam[]{
                    qvalParam,
                    dsParam,
                    ParamMaker.makeMinPeptParam(),
                    ParamMaker.makeMinUniqPeptParam(),
                    ParamMaker.makePeptideDefParam(),
                    ParamMaker.makeMinCoverageParam(),
                    ParamMaker.makeMinPeptLengthParam(),
                    ParamMaker.makeRemoveAmbigSpectraParam()
            });
        }
    }
    
    static class ProteinProphetProgram extends ProteinInferenceProgram {
        
        private ProteinProphetProgram() {
            super("ProteinProphet", "ProteinProphet", "unknown");
            this.setDescription("ProteinProphet");
        }
    }
        
}
