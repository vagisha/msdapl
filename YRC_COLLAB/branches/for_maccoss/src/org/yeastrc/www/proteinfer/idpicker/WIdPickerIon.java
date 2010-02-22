package org.yeastrc.www.proteinfer.idpicker;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIon {

    private GenericProteinferIon<? extends ProteinferSpectrumMatch> ion;
    private MsSearchResult bestSpectrumMatch;
    private MsScan bestScan;
    private boolean uniqueToProteinGrp = false;
    private double precursorArea = -2.0; // -2.0 == we have not hit the database yet to get this info
                                         // -1.0 == we have hit the database and area was not found
    
    public WIdPickerIon(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, MsSearchResult psm, MsScan bestScan) {
        this.ion = ion;
        this.bestSpectrumMatch = psm;
        this.bestScan = bestScan;
    }

    public int getScanId() {
        return bestSpectrumMatch.getScanId();
    }

    public GenericProteinferIon<? extends ProteinferSpectrumMatch> getIon() {
        return ion;
    }
    
    public MsSearchResult getBestSpectrumMatch() {
        return bestSpectrumMatch;
    }

    public MsScan getBestScan() {
        return bestScan;
    }
    
    public BigDecimal getRetentionTime() {
        BigDecimal rt = bestScan.getRetentionTime();
        if(rt != null) {
            rt = new BigDecimal(String.valueOf(round(bestScan.getRetentionTime())));
        }
        return rt;
    }
    
    public double getPrecursorArea() {
        if(precursorArea == -2.0 && bestScan instanceof MS2Scan) {
            MS2Scan scan2 = (MS2Scan) bestScan;
            precursorArea = scan2.getBullsEyeArea();
        }
        else
            precursorArea  = -1.0; // area was not found
        return precursorArea;
    }
    
    public boolean hasPrecursorArea() {
        return (getPrecursorArea() != -1.0);
    }
    
    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean isUnique) {
        this.uniqueToProteinGrp = isUnique;
    }
    
    public String getIonSequence() {
        return removeTerminalResidues(bestSpectrumMatch.getResultPeptide().getModifiedPeptide());
    }
    
    public int getCharge() {
        return ion.getCharge();
    }
    
    public int getSpectrumCount() {
        return ion.getSpectrumCount();
    }
    
    protected static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
    
    private static double round(BigDecimal number) {
        return round(number.doubleValue());
    }
    
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }

}
