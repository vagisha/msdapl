/**
 * ResultsFilterCriteria.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public class ResultFilterCriteria {

    
    private Integer minScan;
    private Integer maxScan;
    
    private Integer minCharge;
    private Integer maxCharge;
    
    private Double minObservedMass;
    private Double maxObservedMass;
    
    private Double minRetentionTime;
    private Double maxRetentionTime;
    
    private String peptide;
    private boolean exactMatch = true;
    
    private String[] filterFileNames;
    
    private boolean showOnlyModified;
    private boolean showOnlyUnmodified;
    
    
    public boolean hasFilters() {
        return (hasScanFilter() ||
                hasChargeFilter() ||
                hasMassFilter() ||
                hasRTFilter() ||
                hasPeptideFilter() ||
                hasMofificationFilter() ||
                hasFileNamesFilter());
    }
    
    protected boolean hasScanFilters() {
        return (hasScanFilter() || hasRTFilter());
    }
    
    protected String makeFilterSql(String columnName, Number min, Number max) {
        if(columnName == null || columnName.length() == 0)
            return "";
        
        if(min == null && max == null)
            return "";
        
        if(min != null && max != null)
            return " ("+columnName+" BETWEEN "+min+" AND "+max+") ";
        if(min != null && max == null) 
            return " ("+columnName+" >= "+min+") ";
        if(min == null && max != null)
            return " ("+columnName+" <= "+max+") ";
        
        return "";
        
    }
    
    //-------------------------------------------------------------
    // SCAN FILTER
    //-------------------------------------------------------------
    public Integer getMinScan() {
        return minScan;
    }
    public void setMinScan(Integer minScan) {
        this.minScan = minScan;
    }
    public Integer getMaxScan() {
        return maxScan;
    }
    public void setMaxScan(Integer maxScan) {
        this.maxScan = maxScan;
    }
    public boolean hasScanFilter() {
        return (minScan != null && maxScan != null);
    }
    public String makeScanFilterSql() {
        return makeFilterSql("startScanNumber", minScan, maxScan);
    }
    
    
    //-------------------------------------------------------------
    // CHARGE FILTER
    //-------------------------------------------------------------
    public Integer getMinCharge() {
        return minCharge;
    }
    public void setMinCharge(Integer minCharge) {
        this.minCharge = minCharge;
    }
    public Integer getMaxCharge() {
        return maxCharge;
    }
    public void setMaxCharge(Integer maxCharge) {
        this.maxCharge = maxCharge;
    }
    public boolean hasChargeFilter() {
        return (minCharge != null || maxCharge != null);
    }
    public String makeChargeFilterSql() {
        return makeFilterSql("charge", minCharge, maxCharge);
    }
    
    //-------------------------------------------------------------
    // OBSERVED MASS FILTER
    //-------------------------------------------------------------
    public Double getMinObservedMass() {
        return minObservedMass;
    }
    public void setMinObservedMass(Double minObservedMass) {
        this.minObservedMass = minObservedMass;
    }
    public Double getMaxObservedMass() {
        return maxObservedMass;
    }
    public void setMaxObservedMass(Double maxObservedMass) {
        this.maxObservedMass = maxObservedMass;
    }
    public boolean hasMassFilter() {
        return (minObservedMass != null || maxObservedMass != null);
    }
    public String makeMassFilterSql() {
        return makeFilterSql("observedMass", minObservedMass, maxObservedMass);
    }
    
    
    //-------------------------------------------------------------
    // RETENTION TIME FILTER
    //-------------------------------------------------------------
    public Double getMinRetentionTime() {
        return minRetentionTime;
    }
    public void setMinRetentionTime(Double minRetentionTime) {
        this.minRetentionTime = minRetentionTime;
    }
    
    public Double getMaxRetentionTime() {
        return maxRetentionTime;
    }
    public void setMaxRetentionTime(Double maxRetentionTime) {
        this.maxRetentionTime = maxRetentionTime;
    }
    public boolean hasRTFilter() {
        return (minRetentionTime != null || maxRetentionTime != null);
    }
    public String makeRTFilterSql() {
        return makeFilterSql("retentionTime", minRetentionTime, maxRetentionTime);
    }
    
    //-------------------------------------------------------------
    // PEPTIDE FILTER
    //-------------------------------------------------------------
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }
    public boolean hasPeptideFilter() {
        return peptide != null && peptide.length() > 0;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactMatch = exact;
    }
    
    public String makePeptideSql() {
        if(hasPeptideFilter()) {
            if(exactMatch)
                return " (peptide = '"+peptide+"') ";
            else
                return " (peptide LIKE '%"+peptide+"%') ";
        }
        return "";
    }
    
    //-------------------------------------------------------------
    // FILE NAME FILTER
    //-------------------------------------------------------------
    public String[] getFileNames() {
        return filterFileNames;
    }
    public void setFileNames(String[] fileNames) {
        this.filterFileNames = fileNames;
    }
    public boolean hasFileNamesFilter() {
        return filterFileNames != null && filterFileNames.length > 0;
    }
    
//    public abstract String makeFileNameFilterSql();
    
    
    //-------------------------------------------------------------
    // PEPTIDE MODIFICATIONS FILTER
    //-------------------------------------------------------------
    public boolean isShowOnlyModified() {
        return showOnlyModified;
    }
    public void setShowOnlyModified(boolean showOnlyModified) {
        this.showOnlyModified = showOnlyModified;
    }
    
    public boolean isShowOnlyUnmodified() {
        return showOnlyUnmodified;
    }
    public void setShowOnlyUnmodified(boolean showOnlyUnmodified) {
        this.showOnlyUnmodified = showOnlyUnmodified;
    }
    public boolean hasMofificationFilter() {
        return showOnlyModified != showOnlyUnmodified;
    }
    public String makeModificationFilter() {
        if(hasMofificationFilter()) {
            if(showOnlyModified)
                return " (modID != 0) ";
            if(showOnlyUnmodified)
                return " (modID IS NULL) ";
        }
        return "";
    }
    
}
