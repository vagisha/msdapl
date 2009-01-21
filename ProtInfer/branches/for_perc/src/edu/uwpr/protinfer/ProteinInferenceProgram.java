package edu.uwpr.protinfer;

import edu.uwpr.protinfer.ProteinInferenceProgram.ProgramParam.TYPE;

public enum ProteinInferenceProgram {

    // we have only one right now!
    IDPICKER("IDPicker", 
             "This is a Java implementation of the IDPicker program developed in David Tabb's lab. [Reference]",
             new ProgramParam[] {new ProgramParam(TYPE.DOUBLE, 
                                                 "maxAbsFDR", "Max. Absolute FDR", 
                                                 "0.05", null,
                                                 "This parameter sets the maximum FDR that the \"xcorr\"-type score of each peptide identification must meet"),
                                 new ProgramParam(TYPE.DOUBLE, 
                                                 "maxRelFDR", "Max. Relative FDR", 
                                                 "0.05", null,
                                                 "This parameter sets the maximum confidence that the \"deltcn\"-type score of each peptide identification must meet "),
                                new ProgramParam(TYPE.CHOICE,
                                                 "FDRFormula", "FDR Formula",
                                                 "2R/(F+R)", new String[]{"2R/(F+R)", "R/F"},
                                                 "Formula used for calculating FDR. R = # decoy hits; F = # target hits"),
                                new ProgramParam(TYPE.DOUBLE, 
                                                 "decoyRatio", "Decoy Ratio", 
                                                 "1.0", null, 
                                                 "Ratio of target proteins to decoy proteins in the database"),
                                 new ProgramParam(TYPE.STRING, 
                                                 "decoyPrefix", "Decoy Prefix", 
                                                 "Reverse_", null,
                                                 "Prefix used to identify decoy protein accessions"),
                                 new ProgramParam(TYPE.CHOICE,
                                                 "PeptDef", "Peptide Definition",
                                                 "Sequence", new String[]{"Sequence", 
                                                                          "Sequence + Modifications", 
                                                                          "Sequence + Charge",
                                                                          "Sequence + Modifications + Charge"},
                                                 "These options determine what uniquely defines a peptide"),
                                 new ProgramParam(TYPE.INTEGER, "minPept", "Min. Peptides", "2", null, "Minimum number of peptides required for a protein(group) to be included in the analysis."),
                                 new ProgramParam(TYPE.INTEGER, "minUniqePept", "Min. Unique Peptides", "0", null, "Minimum number of unique peptides required for a protein(group) to be included in the analysis."),
                                 new ProgramParam(TYPE.DOUBLE, "coverage", "Coverage(%)", "0", null, "Minimum sequence coverage required for a protein to be included in the analysis."),
                                 new ProgramParam(TYPE.INTEGER, "minPeptLen", "Min. Peptide Length", "5", null, "Minimum length required for a peptide to be included in the analysis."),
//                                 new ProgramParam(TYPE.INTEGER, "minPeptSpectra", "Min. Spectra / peptide", "1", null, "Minimum number of spectrum matches required for a peptide to be included in the analysis."),
                                 new ProgramParam(TYPE.BOOLEAN, 
                                         "removeAmbigSpectra", "Remove Ambiguous Spectra", 
                                         "true", null,
                                         "If checked, spectra with > 1 peptide matches, that pass through the filter criteria, will be removed from analysis.")
//                                 new ProgramParam(TYPE.BOOLEAN, 
//                                                 "parsimonyAnalysis", "Parsimony Analysis", 
//                                                 "true", null,
//                                                 "This parameter controls whether the final protein list is filtered by the minimum covering set, i.e. the minimum set of proteins that is necessary to describe all identified peptides")
                             }
            ),
    
   
                                                 
     IDPICKER_PERC("IDPicker (for Percolator)",
                   "This version uses the greedy set cover algorithm to infer a parsimonious list of proteins",
                   new ProgramParam[]{
                     new ProgramParam(TYPE.DOUBLE, 
                           "qval_percolator", "Qvalue Threshold", 
                           "0.05", null,
                           "Qvalue threshold for filtering search hits"),
                    new ProgramParam(TYPE.DOUBLE, 
                           "pep_percolator", "PEP Threshold", 
                           "0.05", null,
                           "Posterior Error Probability threshold for filtering search hits"),
                   new ProgramParam(TYPE.CHOICE,
                           "PeptDef", "Peptide Definition",
                           "Sequence", new String[]{"Sequence", 
                                                   "Sequence + Modifications", 
                                                   "Sequence + Charge",
                                                   "Sequence + Modifications + Charge"},
                           "These options determine what uniquely defines a peptide."),
                   new ProgramParam(TYPE.INTEGER, "minPept", "Min. Peptides", "2", null, "Minimum number of peptides required for a protein(group) to be included in the analysis."),
                   new ProgramParam(TYPE.INTEGER, "minUniqePept", "Min. Unique Peptides", "0", null, "Minimum number of unique peptides required for a protein(group) to be included in the analysis."),
                   new ProgramParam(TYPE.DOUBLE, "coverage", "Coverage(%)", "0", null, "Minimum sequence coverage required for a protein to be included in the analysis."),
                   new ProgramParam(TYPE.INTEGER, "minPeptLen", "Min. Peptide Length", "5", null, "Minimum length required for a peptide to be included in the analysis."),
//                   new ProgramParam(TYPE.INTEGER, "minPeptSpectra", "Min. Spectra / peptide", "1", null, "Minimum number of spectrum matches required for a peptide to be included in the analysis."),
                   new ProgramParam(TYPE.BOOLEAN, 
                           "removeAmbigSpectra", "Remove Ambiguous Spectra", 
                           "true", null,
                           "If checked, spectra with > 1 peptide matches, that pass through the filter criteria, will be removed from analysis."),
                   }
         ),
                   
     NONE("NONE", null, null);
    
    private String displayName;
    private String description;
    private ProgramParam[] params;
    
    
    private ProteinInferenceProgram(String displayName, String description, ProgramParam[] params) {
        this.displayName = displayName;
        this.description = description;
        this.params = params;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ProgramParam[] getProgramParams() {
        if(params == null)
            return new ProgramParam[0];
        else
            return params;
    }
    
    
    public static ProteinInferenceProgram getProgramForName(String name) {
        if("IDPICKER".equalsIgnoreCase(name))
            return IDPICKER;
        else if ("IDPICKER_PERC".equalsIgnoreCase(name))
            return IDPICKER_PERC;
        else 
            return NONE;
    }
    
    
    public static final class ProgramParam {
        
        public static enum TYPE {STRING, DOUBLE, INTEGER, BOOLEAN, CHOICE};
        private String name;
        private String displayName;
        private String description;
        private String defaultValue;
        private TYPE type = TYPE.DOUBLE;
        private String[] values;
        
        
        private ProgramParam(TYPE type, String name, String displayName, 
                String defaultVal, String[] values,
                String description) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.type = type;
            this.defaultValue = defaultVal;
            if(values != null)
                this.values = values;
            else
                values = new String[0];
        }
        
        public String getName() {
            return name;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getDescription() {
            return description;
        }
        
        public TYPE getType() {
            return this.type;
        }
        public String getDefaultValue() {
            return defaultValue;
        }
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public String[] getValues() {
            return this.values;
        }
    }
}
