/**
 * SORT_BY.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


public enum SORT_BY {
    
    ID("ID", "id"),
    SCAN("Scan", "startScanNumber"), 
    CHARGE("Charge", "charge"), 
    MASS("Obs. Mass", "observedMass"), 
    RT("RT", "retentionTime"), 
    AREA("Area", "area"),
    PEPTIDE("Peptide", "peptide"),
    PROTEIN("Protein", "accession"),
    FILE_SEARCH("File", "runSearchID"),
    FILE_ANALYSIS("File", "runSearchAnalysisID"),
    
    // Sequest specific
    XCORR("XCorr", "XCorr"),
    XCORR_RANK("XCr", "XCorrRank"),
    SP("Sp", "sp"),
    DELTACN("dCN", "deltaCN"),
    EVAL("e-value", "evalue"),
    CALC_MASS_SEQ("Calc. Mass", "calculatedMass"),
    
    // Mascot specific
    ION_SCORE("IonScore", "ionScore"),
    IDENTITY_SCORE("IdScore", "identityScore"),
    HOMOLOGY_SCORE("HomolScore", "homologyScore"),
    MASCOT_EXPECT("Expect", "expect"),
    STAR("Star", "star"),
    MASCOT_RANK("Rank", "rank"),
    
    // Xtandem specific
    HYPER_SCORE("HyperScore", "hyperscore"),
    NEXT_SCORE("NextScore", "nextscore"),
    B_SCORE("B-Score", "bscore"),
    Y_SCORE("Y-Score", "yscore"),
    XTANDEM_EXPECT("Expect", "expect"),
    XTANDEM_RANK("Rank", "rank"),
    
    // Percolator specific
    QVAL("q-value", "qvalue"),
    PEP("PEP", "pep"),
    DS("DS", "discriminantScore"),
    P_RT("Predict. RT", "predictedRetentionTime"),

    // PeptideProphet specific
    PEPTP_PROB("Probability", "probability");
    
    private String displayName;
    private String columnName;

    private SORT_BY(String displayName, String columnName) {
        this.displayName = displayName;
        this.columnName = columnName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public static SORT_BY getSortByForName(String name) {
        if(name == null)
            return null;
        SORT_BY[] arr = SORT_BY.values();
        for(SORT_BY sortBy: arr) {
            if(sortBy.name().equalsIgnoreCase(name)) 
                return sortBy;
        }
        return null;
    }

    public static SORT_BY defaultSortBy() {
        return ID;
    }
    
    public static boolean isScanRelated(SORT_BY sortBy) {
        return sortBy == SCAN || sortBy == RT;
    }
    
    public static boolean isSearchRelated(SORT_BY sortBy) {
        return sortBy == CHARGE || sortBy == MASS || sortBy == PEPTIDE;
    }
    
    public static boolean isPercolatorRelated(SORT_BY sortBy) {
        return sortBy == QVAL || sortBy == PEP || sortBy == P_RT;
    }
    
    public static boolean isSequestRelated(SORT_BY sortBy) {
        return sortBy == XCORR || sortBy == DELTACN || sortBy == SP || sortBy == EVAL || sortBy == CALC_MASS_SEQ;
    }
    
    public static boolean isMascotRelated(SORT_BY sortBy) {
        return sortBy == CALC_MASS_SEQ || sortBy == ION_SCORE || sortBy == IDENTITY_SCORE || sortBy == HOMOLOGY_SCORE || sortBy == MASCOT_EXPECT || sortBy == STAR;
    }
    
    public static boolean isXtandemRelated(SORT_BY sortBy) {
        return sortBy == HYPER_SCORE || sortBy == NEXT_SCORE || sortBy == B_SCORE || sortBy == Y_SCORE || sortBy == MASCOT_EXPECT;
    }
    
    public static boolean isPeptideProphetRelated(SORT_BY sortBy) {
        return sortBy == PEPTP_PROB;
    }
}
