package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;

/**
 * Represents a 'S' line in the SQT file
 */
public class ScanResult implements SQTSearchScan {

    private int startScan;
    private int endScan;
    private int charge;
    private int processingTime; // seconds
    private String server; // server handling this scan
    private BigDecimal observedMass; // Observed M+H+ value (calculated from m/z) 
    private BigDecimal totalIntensity; 
    private BigDecimal lowestSp; // Lowest Sp value for top 500 spectra
    private int numMatching; // Number of sequences matching this precursor ion
    
    private List<SQTSearchResult> resultList;
    
    public ScanResult() {
        resultList = new ArrayList<SQTSearchResult>();
    }

    public boolean isValid() {
       return resultList.size() > 0; 
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

    public int getScanNumber() {
        return startScan;
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
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }

    /**
     * @return the processingTime
     */
    public int getProcessingTime() {
        return processingTime;
    }

    /**
     * @param processingTime the processingTime to set
     */
    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the observedMass
     */
    public BigDecimal getObservedMass() {
        return observedMass;
    }

    /**
     * @param observedMass the observedMass to set
     */
    public void setObservedMass(BigDecimal observedMass) {
        this.observedMass = observedMass;
    }

    /**
     * @return the totalIntensity
     */
    public BigDecimal getTotalIntensity() {
        return totalIntensity;
    }

    /**
     * @param totalIntensity the totalIntensity to set
     */
    public void setTotalIntensity(BigDecimal totalIntensity) {
        this.totalIntensity = totalIntensity;
    }

    /**
     * @return the lowestSp
     */
    public BigDecimal getLowestSp() {
        return lowestSp;
    }

    /**
     * @param lowestSp the lowestSp to set
     */
    public void setLowestSp(BigDecimal lowestSp) {
        this.lowestSp = lowestSp;
    }

    /**
     * @return the numMatching
     */
    public int getNumMatching() {
        return numMatching;
    }

    /**
     * @param numMatching the numMatching to set
     */
    public void setNumMatching(int numMatching) {
        this.numMatching = numMatching;
    }

    public void addPeptideResult(PeptideResult result) {
        resultList.add(result);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("S\t");
        buf.append(startScan);
        buf.append("\t");
        buf.append(endScan);
        buf.append("\t");
        buf.append(charge);
        buf.append("\t");
        buf.append(processingTime);
        buf.append("\t");
        buf.append(server);
        buf.append("\t");
        buf.append(observedMass);
        buf.append("\t");
        buf.append(totalIntensity);
        buf.append("\t");
        buf.append(lowestSp);
        buf.append("\t");
        buf.append(numMatching);
        
        buf.append("\n");
        
        for (SQTSearchResult res: resultList) {
            buf.append(res.toString());
            buf.append("\n");
        }
        
        buf.deleteCharAt(buf.length() -1);
        return buf.toString();        
        
    }

    public int getProcessTime() {
        return getProcessingTime();
    }

    public String getServerName() {
        return getServer();
    }

    public List<SQTSearchResult> getScanResults() {
        return this.resultList;
    }
}
