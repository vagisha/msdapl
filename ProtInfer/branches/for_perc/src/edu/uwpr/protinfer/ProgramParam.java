/**
 * ProgramParam.java
 * @author Vagisha Sharma
 * Feb 7, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer;

/**
 * 
 */
public final class ProgramParam {

    public static enum TYPE {STRING, DOUBLE, INTEGER, BOOLEAN, CHOICE};
    public static enum SCORE {XCorr, DeltaCN, PrimaryScore};
    
    private String name;
    private String displayName;
    private String description;
    private String defaultValue;
    private TYPE type = TYPE.DOUBLE;
    private String[] values;


    ProgramParam(TYPE type, String name, String displayName, 
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
    
    public static class ParamMaker {
        
        public static ProgramParam makeMaxFDRParam() {
            return new ProgramParam(TYPE.DOUBLE, 
                  "maxFDR", "Max. FDR", 
                  "0.05", null,
                  "This parameter sets the maximum FDR that the score of each peptide identification must meet");
        }
        
        public static ProgramParam makeFDRScoreParam(SCORE defaultScore, SCORE[] options) {
            String[] opts = new String[options.length];
            for(int i = 0; i < options.length; i++)
                opts[i] = options[i].name();
            
            return new ProgramParam(TYPE.CHOICE, 
                  "useScore", "Use Score", 
                  defaultScore.name(), opts,
                  "This parameter sets the score that will be used for calculating FDR");
        }
        
        public static ProgramParam makeFDRFormulaParam() {
            return new ProgramParam(TYPE.CHOICE,
                  "FDRFormula", "FDR Formula",
                  "2R/(F+R)", new String[]{"2R/(F+R)", "R/F"},
                  "Formula used for calculating FDR. R = # decoy hits; F = # target hits");
        }
        
        public static ProgramParam makeDecoyPrefixParam() {
            return new ProgramParam(TYPE.STRING, 
                  "decoyPrefix", "Decoy Prefix", 
                  "Reverse_", null,
                  "Prefix used to identify decoy protein accessions");
        }
        
        public static ProgramParam makePeptideDefParam() {
            return new ProgramParam(TYPE.CHOICE,
                  "PeptDef", "Peptide Definition",
                  "Sequence", new String[]{"Sequence", 
                                           "Sequence + Modifications", 
                                           "Sequence + Charge",
                                           "Sequence + Modifications + Charge"},
                  "These options determine what uniquely defines a peptide");
        }
        
        public static ProgramParam makeMinPeptParam() {
            return new ProgramParam(TYPE.INTEGER, "minPept", "Min. Peptides", "2", null, 
                    "Minimum number of peptides required for a protein(group) to be included in the analysis.");
        }
        
        public static ProgramParam makeMinUniqPeptParam() {
            return new ProgramParam(TYPE.INTEGER, "minUniqePept", "Min. Unique Peptides", "0", null, 
                    "Minimum number of unique peptides required for a protein(group) to be included in the analysis.");
        }

        public static ProgramParam makeMinCoverageParam() {
            return new ProgramParam(TYPE.DOUBLE, "coverage", "Coverage(%)", "0", null, 
                    "Minimum sequence coverage required for a protein to be included in the analysis.");
        }

        public static ProgramParam makeMinPeptLengthParam() {
            return new ProgramParam(TYPE.INTEGER, "minPeptLen", "Min. Peptide Length", "5", null, 
                    "Minimum length required for a peptide to be included in the analysis.");
        }
//      new ProgramParam(TYPE.INTEGER, "minPeptSpectra", "Min. Spectra / peptide", "1", null, "Minimum number of spectrum matches required for a peptide to be included in the analysis."),
        public static ProgramParam makeRemoveAmbigSpectraParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "removeAmbigSpectra", "Remove Ambiguous Spectra", 
              "true", null,
              "If checked, spectra with > 1 peptide matches, that pass through the filter criteria, will be removed from analysis.");
        }
//        new ProgramParam(TYPE.BOOLEAN, 
//            "parsimonyAnalysis", "Parsimony Analysis", 
//            "true", null,
//            "This parameter controls whether the final protein list is filtered by the minimum covering set, i.e. the minimum set of proteins that is necessary to describe all identified peptides")

    }
}


