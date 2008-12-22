package edu.uwpr.protinfer;

public class PeptideDefinition {

    private boolean useMods;
    private boolean useCharge;
    
    public PeptideDefinition() {}
    
    public PeptideDefinition(boolean useMods, boolean useCharge) {
        this.useMods = useMods;
        this.useCharge = useCharge;
    }
    
    public boolean isUseMods() {
        return useMods;
    }
    public void setUseMods(boolean useMods) {
        this.useMods = useMods;
    }
    public boolean isUseCharge() {
        return useCharge;
    }
    public void setUseCharge(boolean useCharge) {
        this.useCharge = useCharge;
    }
    
    
}
