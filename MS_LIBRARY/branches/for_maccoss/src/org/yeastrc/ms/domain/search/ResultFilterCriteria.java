/**
 * ResultsFilterCriteria.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

/**
 * 
 */
public class ResultFilterCriteria {

    private Integer minScan;
    private Integer maxScan;
    
    private Integer minCharge;
    private Integer maxCharge;
    
    private BigDecimal minObservedMass;
    private BigDecimal maxObservedMass;
    
    private BigDecimal minRetentionTime;
    private BigDecimal maxRetentionTime;
    
    private String peptide;
    
    private boolean showOnlyModified;
    private boolean showOnlyUnmodified;
    
    
    public boolean hasFilters() {
        return (hasScanFilter() ||
                hasChargeFilter() ||
                hasMassFilter() ||
                hasRTFilter() ||
                hasPeptideFilter() ||
                hasMofificationFilter());
    }
    
    protected boolean hasScanFilters() {
        return (hasScanFilter() || hasRTFilter());
    }
    
    protected String makeFilterSql(String columnName, Number min, Number max) {
        if(columnName == null || columnName.length() == 0)
            return "";
        
        if(min == null && max == null)
            return "";
        
        if(minScan != null && maxScan != null)
            return " ("+columnName+" BETWEEN "+minScan+" AND "+maxScan+") ";
        if(minScan != null && maxScan == null) 
            return " ("+columnName+" >= "+minScan+") ";
        if(minScan == null && maxScan != null)
            return " ("+columnName+" <= "+maxScan+") ";
        
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
    public BigDecimal getMinObservedMass() {
        return minObservedMass;
    }
    public void setMinObservedMass(BigDecimal minObservedMass) {
        this.minObservedMass = minObservedMass;
    }
    public BigDecimal getMaxObservedMass() {
        return maxObservedMass;
    }
    public void setMaxObservedMass(BigDecimal maxObservedMass) {
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
    public BigDecimal getMinRetentionTime() {
        return minRetentionTime;
    }
    public void setMinRetentionTime(BigDecimal minRetentionTime) {
        this.minRetentionTime = minRetentionTime;
    }
    
    public BigDecimal getMaxRetentionTime() {
        return maxRetentionTime;
    }
    public void setMaxRetentionTime(BigDecimal maxRetentionTime) {
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
    public String makePeptideSql() {
        if(hasPeptideFilter())
            return " (peptide LIKE '%"+peptide+"%') ";
        return "";
    }
    
    
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
                return " (modID = 0) ";
        }
        return "";
    }
    
}
