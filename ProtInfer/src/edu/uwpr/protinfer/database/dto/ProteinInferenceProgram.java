package edu.uwpr.protinfer.database.dto;

public enum ProteinInferenceProgram {

    // we have only one right now!
    IDPICKER("IDPicker", 
             "This is a Java implementation of the IDPicker program developed in David Tabb's lab. [Reference]",
             new ProgramParam[] {new ProgramParam("maxAbsFDR", "Max. Absolute FDR", "This parameter sets the maximum FDR that the \"xcorr\"-type score of each peptide identification must meet"),
                                 new ProgramParam("maxRelFDR", "Max. Relative FDR", "This parameter sets the maximum confidence that the \"deltcn\"-type score of each peptide identification must meet "),
                                 new ProgramParam("decoyRatio", "Decoy Ratio", "Ratio of target proteins to decoy proteins in the database"),
                                 new ProgramParam("decoyPrefix", "Decoy Prefix", "Prefix used to identify decoy protein accessions"),
                                 new ProgramParam("minDistinctPept", "Min. Distinct Peptides", null),
                                 new ProgramParam("parsimonyAnalysis", "Parsimony Analysis", "This parameter controls whether the final protein list is filtered by the minimum covering set, i.e. the minimum set of proteins that is necessary to describe all identified peptides")}),
    
    NONE("NONE", null, null);
    
    private String name;
    private String description;
    private ProgramParam[] params;
    
    
    private ProteinInferenceProgram(String name, String description, ProgramParam[] params) {
        this.name = name;
        this.description = description;
        this.params = params;
    }
    
    public String getName(){
        return name;
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
        if(name.equalsIgnoreCase("IDPicker"))
            return IDPICKER;
        else 
            return NONE;
    }
    
    
    public static final class ProgramParam {
        private String name;
        private String displayName;
        private String description;
        
        private ProgramParam(String name, String displayName, String description) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
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
    }
}
