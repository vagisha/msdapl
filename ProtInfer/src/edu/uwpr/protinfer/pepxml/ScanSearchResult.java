package edu.uwpr.protinfer.pepxml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;

public class ScanSearchResult {

    private String spectrumString;
    private int startScan;
    private int endScan;
    private BigDecimal precursorNeutralMass;
    private int assumedCharge;
    private float retentionTime;
    
    private List<SearchHit> searchHits;
    
    public ScanSearchResult() {
        searchHits = new ArrayList<SearchHit>();
    }

    public List<SearchHit> getSearchHits() {
        return searchHits;
    }
    
    public void addSearchHit(SearchHit hit) {
        searchHits.add(hit);
    }
    
    /**
     * @return the spectrumString
     */
    public String getSpectrumString() {
        return spectrumString;
    }

    /**
     * @param spectrumString the spectrumString to set
     */
    public void setSpectrumString(String spectrumString) {
        this.spectrumString = spectrumString;
    }

    /**
     * @return the startScan
     */
    public int getStartScan() {
        return startScan;
    }

    /**
     * @param startScan the startScan to set
     */
    public void setStartScan(int startScan) {
        this.startScan = startScan;
    }

    /**
     * @return the endScan
     */
    public int getEndScan() {
        return endScan;
    }

    /**
     * @param endScan the endScan to set
     */
    public void setEndScan(int endScan) {
        this.endScan = endScan;
    }

    /**
     * @return the precursorNeutralMass
     */
    public BigDecimal getPrecursorNeutralMass() {
        return precursorNeutralMass;
    }

    /**
     * @param precursorNeutralMass the precursorNeutralMass to set
     */
    public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
        this.precursorNeutralMass = precursorNeutralMass;
    }

    /**
     * @return the assumedCharge
     */
    public int getAssumedCharge() {
        return assumedCharge;
    }

    /**
     * @param assumedCharge the assumedCharge to set
     */
    public void setAssumedCharge(int assumedCharge) {
        this.assumedCharge = assumedCharge;
    }

    /**
     * @return the retentionTime
     */
    public float getRetentionTime() {
        return retentionTime;
    }

    /**
     * @param retentionTime the retentionTime to set
     */
    public void setRetentionTime(float retentionTime) {
        this.retentionTime = retentionTime;
    }
}
