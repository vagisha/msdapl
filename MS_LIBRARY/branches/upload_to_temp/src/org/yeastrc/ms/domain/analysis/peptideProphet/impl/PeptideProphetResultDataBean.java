/**
 * PeptideProphetResultDataBean.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;

/**
 * 
 */
public class PeptideProphetResultDataBean implements PeptideProphetResultDataWId {

    private int resultId;
    private int runSearchAnalysisId;
    private double probability = -1.0;
    private double fVal = -1.0; // TODO is this always positive?
    private double massDiff = 0.0;
    private int ntt = -1;
    private int nmc = -1;
    private String allNttProb;
    
    private static final Pattern allNttPattern = Pattern.compile("^\\((\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*\\)$");
    
    @Override
    public String getAllNttProb() {
        return allNttProb;
    }
    
    public void setAllNttProb(String allNttProb) {
        this.allNttProb = allNttProb;
    }

    @Override
    public double getProbabilityNet_0() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(1));
        }
        return -1.0;
    }

    @Override
    public double getProbabilityNet_1() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(2));
        }
        return -1.0;
    }

    @Override
    public double getProbabilityNet_2() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(3));
        }
        return -1.0;
    }
    @Override
    public double getMassDifference() {
        return massDiff;
    }
    
    public void setMassDifference(double massDiff) {
        this.massDiff = massDiff;
    }

    @Override
    public int getNumMissedCleavages() {
        return nmc;
    }
    
    public void setNumMissedCleavages(int nmc) {
        this.nmc = nmc;
    }

    @Override
    public int getNumTrypticTermini() {
        return ntt;
    }
    
    public void setNumEnzymaticTermini(int ntt) {
        this.ntt = ntt;
    }

    @Override
    public double getProbability() {
        return probability;
    }
    
    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    @Override
    public int getRunSearchAnalysisId() {
        return runSearchAnalysisId;
    }
    
    public void setRunSearchAnalysisId(int analysisId) {
        this.runSearchAnalysisId = analysisId;
    }

    @Override
    public double getfVal() {
        return fVal;
    }
    
    public void setfVal(double fVal) {
        this.fVal = fVal;
    }

    public static void main(String[] args) {
        String allnttProb = "(0.0000,0.0029,0.3436)";
        Matcher m = allNttPattern.matcher(allnttProb);
        if(m.matches()) {
            System.out.println("NTT_0: "+m.group(1));
            System.out.println("NTT_1: "+m.group(2));
            System.out.println("NTT_2: "+m.group(3));
        }
    }
}
