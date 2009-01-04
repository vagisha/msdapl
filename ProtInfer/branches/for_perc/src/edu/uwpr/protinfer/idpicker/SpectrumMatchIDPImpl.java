package edu.uwpr.protinfer.idpicker;


public class SpectrumMatchIDPImpl implements SpectrumMatchIDP {

    private int charge;
    private int hitId;
    private int runSearchId;
    private int scanId;
    private double fdr = 1.0;
    private boolean accepted;
    private int rank;
    
    public void setCharge(int charge) {
        this.charge = charge;
    }
    
    public int getCharge() {
        return charge;
    }

    public void setHitId(int hitId) {
        this.hitId = hitId;
    }
    
    public int getHitId() {
        return this.hitId;
    }

    
    public int getSourceId() {
        return runSearchId;
    }
    
    public void setSourceId(int sourceId) {
        this.runSearchId = sourceId;
    }
    
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    
    @Override
    public int getScanId() {
        return this.scanId;
    }

    @Override
    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
    }

    @Override
    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    @Override
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }

}
