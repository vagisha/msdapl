/**
 * GOEnrichmentInput.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * 
 */
public class GOEnrichmentInput {

    private final int speciesId; // NRSEQ species id
    private List<Integer> proteinIds; // NRSEQ protein ids
    private double pValCutoff = 0.01;
    
    private boolean useCellularComponent = true;
    private boolean useMolecularFunction = true;
    private boolean useBiologicalProcess = true;
    
    public GOEnrichmentInput(int speciesId) {
        this.speciesId = speciesId;
    }
    
    public int getSpeciesId() {
        return speciesId;
    }
    
    public List<Integer> getProteinIds() {
        return proteinIds;
    }
    public void setProteinIds(List<Integer> proteinIds) {
        this.proteinIds = proteinIds;
    }
    
    public double getPValCutoff() {
        return pValCutoff;
    }
    public void setPValCutoff(double valCutoff) {
        pValCutoff = valCutoff;
    }

    public boolean useCellularComponent() {
        return useCellularComponent;
    }
    public void setUseCellularComponent(boolean useCellularComponent) {
        this.useCellularComponent = useCellularComponent;
    }

    public boolean useMolecularFunction() {
        return useMolecularFunction;
    }
    public void setUseMolecularFunction(boolean useMolecularFunction) {
        this.useMolecularFunction = useMolecularFunction;
    }

    public boolean useBiologicalProcess() {
        return useBiologicalProcess;
    }

    public void setUseBiologicalProcess(boolean useBiologicalProcess) {
        this.useBiologicalProcess = useBiologicalProcess;
    }
    
}
