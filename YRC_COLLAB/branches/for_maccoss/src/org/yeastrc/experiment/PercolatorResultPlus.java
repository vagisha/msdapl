/**
 * PercolatorResultWScan.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.ValidationStatus;

/**
 * 
 */
public class PercolatorResultPlus implements PercolatorResult {

    private final PercolatorResult result;
    private final int scanNumber;
    private final BigDecimal retentionTime;
    
    public PercolatorResultPlus(PercolatorResult result, MsScan scan) {
        this.result = result;
        this.scanNumber = scan.getStartScanNum();
        this.retentionTime = scan.getRetentionTime();
    }
    
    public int getScanNumber() {
        return scanNumber;
    }
    
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    
    @Override
    public Double getDiscriminantScore() {
        return result.getDiscriminantScore();
    }

    @Override
    public Double getDiscriminantScoreRounded() {
        return result.getDiscriminantScoreRounded();
    }

    @Override
    public double getPosteriorErrorProbability() {
        return result.getPosteriorErrorProbability();
    }

    @Override
    public double getPosteriorErrorProbabilityRounded() {
        return result.getPosteriorErrorProbabilityRounded();
    }

    @Override
    public BigDecimal getPredictedRetentionTime() {
        return result.getPredictedRetentionTime();
    }

    @Override
    public double getQvalue() {
        return result.getQvalue();
    }

    @Override
    public double getQvalueRounded() {
        return result.getQvalueRounded();
    }

    @Override
    public int getRunSearchAnalysisId() {
        return result.getRunSearchAnalysisId();
    }

    @Override
    public int getId() {
        return result.getId();
    }

    @Override
    public List<MsSearchResultProtein> getProteinMatchList() {
        return result.getProteinMatchList();
    }

    @Override
    public int getRunSearchId() {
        return result.getRunSearchId();
    }

    @Override
    public int getScanId() {
        return result.getScanId();
    }

    @Override
    public int getCharge() {
        return result.getCharge();
    }

    @Override
    public BigDecimal getObservedMass() {
        return result.getObservedMass();
    }

    @Override
    public MsSearchResultPeptide getResultPeptide() {
        return result.getResultPeptide();
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return result.getValidationStatus();
    }

}
