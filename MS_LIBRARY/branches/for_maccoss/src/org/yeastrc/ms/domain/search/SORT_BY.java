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
    PEPTIDE("Peptide", "peptide"),
    FILE_SEARCH("File", "runSearchID"),
    
    // Sequest specific
    XCORR("XCorr", "XCorr"),
    SP("Sp", "sp"),
    DELTACN("DeltaCN", "deltaCN"),
    EVAL("e-value", "evalue"),
    CALC_MASS_SEQ("Calc. Mass", "calculatedMass"),
    
    // Percolator specific
    FILE_PERC("File", "runSearchAnalysisID"),
    QVAL("q-value", "qvalue"),
    PEP("PEP", "pep"),
    P_RT("Predict. RT", "predictedRetentionTime");

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
        else if (name.equalsIgnoreCase(ID.name())) return ID;
        else if (name.equalsIgnoreCase(SCAN.name())) return SCAN;
        else if (name.equalsIgnoreCase(CHARGE.name())) return CHARGE;
        else if (name.equalsIgnoreCase(MASS.name())) return MASS;
        else if (name.equalsIgnoreCase(RT.name())) return RT;
        else if (name.equalsIgnoreCase(PEPTIDE.name())) return PEPTIDE;
        else if (name.equalsIgnoreCase(XCORR.name())) return XCORR;
        else if (name.equalsIgnoreCase(SP.name())) return SP;
        else if (name.equalsIgnoreCase(DELTACN.name())) return DELTACN;
        else if (name.equalsIgnoreCase(EVAL.name())) return EVAL;
        else if (name.equalsIgnoreCase(CALC_MASS_SEQ.name())) return CALC_MASS_SEQ;
        else if (name.equalsIgnoreCase(QVAL.name())) return QVAL;
        else if (name.equalsIgnoreCase(PEP.name())) return PEP;
        else if (name.equalsIgnoreCase(P_RT.name())) return P_RT;
        else    return null;
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
    
}
