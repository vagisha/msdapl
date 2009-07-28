/**
 * PeptideProphetResultBean.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;

/**
 * 
 */
public class PeptideProphetResultBean extends SearchResultBean implements PeptideProphetResult {

    private static final Pattern allNttPattern = Pattern.compile("^\\((\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*\\)$");
    private PeptideProphetResultDataBean data = new PeptideProphetResultDataBean();
    
    @Override
    public String getAllNttProb() {
        return data.getAllNttProb();
    }
    
    public void setAllNttProb(String allNttProb) {
        this.data.setAllNttProb(allNttProb);
    }

    @Override
    public double getMassDifference() {
        return data.getMassDifference();
    }
    
    public void setMassDifference(double massDiff) {
        data.setMassDifference(massDiff);
    }

    @Override
    public double getMassDifferenceRounded() {
        return Math.round(data.getMassDifference() * 1000.0) / 1000.0;
    }

    @Override
    public int getNumMissedCleavages() {
        return data.getNumMissedCleavages();
    }
    
    public void setNumMissedCleavages(int nmc) {
        this.data.setNumMissedCleavages(nmc);
    }

    @Override
    public int getNumTrypticTermini() {
        return data.getNumTrypticTermini();
    }
    
    public void setNumTrypticTermini(int ntt) {
        this.data.setNumEnzymaticTermini(ntt);
    }

    @Override
    public double getProbability() {
        return data.getProbability();
    }
    
    public void setProbability(double probability) {
        this.data.setProbability(probability);
    }

    @Override
    public double getProbabilityRounded() {
        return Math.round(data.getProbability() * 1000.0) / 1000.0;
    }

    @Override
    public int getRunSearchAnalysisId() {
        return data.getRunSearchAnalysisId();
    }
    
    public void setRunSearchAnalysisId(int runSearchAnalysisId) {
        this.data.setRunSearchAnalysisId(runSearchAnalysisId);
    }
    
    @Override
    public double getfVal() {
        return data.getfVal();
    }
    
    public void setfVal(double fVal) {
        this.data.setfVal(fVal);
    }

    @Override
    public double getfValRounded() {
        return Math.round(data.getfVal() * 1000.0) / 1000.0;
    }

    @Override
    public double getProbabilityNtt_0() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(1));
        }
        return -1.0;
    }

    @Override
    public double getProbabilityNtt_1() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(2));
        }
        return -1.0;
    }

    @Override
    public double getProbabilityNtt_2() {
        if(this.getAllNttProb() == null)
            return -1.0;
        Matcher m = allNttPattern.matcher(getAllNttProb());
        if(m.matches()) {
            return Double.parseDouble(m.group(3));
        }
        return -1.0;
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
