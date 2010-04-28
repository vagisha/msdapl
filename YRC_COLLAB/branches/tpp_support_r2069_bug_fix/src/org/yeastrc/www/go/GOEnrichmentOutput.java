/**
 * GOEnrichmentOutput.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * 
 */
public class GOEnrichmentOutput {

    private final int speciesId; // NRSEQ species id
    private final List<Integer> proteinIds; // NRSEQ protein ids
    private int numSpeciesProteins;
    private final double pValCutoff;
    
    private final boolean useCellularComponent;
    private final boolean useMolecularFunction;
    private final boolean useBiologicalProcess;
    
    private List<EnrichedGOTerm> cellularComponentEnriched;  // enriched terms for Cellular Component
    private int totalAnnotatedCellularComponent;
    private List<EnrichedGOTerm> biologicalProcessEnriched;  // enriched terms for Biological Process
    private int totalAnnotatedBiologicalProcess;
    private List<EnrichedGOTerm> molecularFunctionEnriched;  // enriched terms for Molecular Function
    private int totalAnnotatedMolecularFunction;
    
    public GOEnrichmentOutput(GOEnrichmentInput input) {
        this.speciesId = input.getSpeciesId();
        this.proteinIds = input.getProteinIds();
        this.pValCutoff = input.getPValCutoff();
        this.useBiologicalProcess = input.useBiologicalProcess();
        this.useMolecularFunction = input.useMolecularFunction();
        this.useCellularComponent = input.useCellularComponent();
    }
    
    public int getSpeciesId() {
        return speciesId;
    }
    
    public List<Integer> getProteinIds() {
        return proteinIds;
    }
    
    public double getpValCutoff() {
        return pValCutoff;
    }

    public boolean useCellularComponent() {
        return useCellularComponent;
    }

    public boolean useMolecularFunction() {
        return useMolecularFunction;
    }

    public boolean useBiologicalProcess() {
        return useBiologicalProcess;
    }

    public List<EnrichedGOTerm> getCellularComponentEnriched() {
        return cellularComponentEnriched;
    }

    public void setCellularComponentEnriched(
            List<EnrichedGOTerm> cellularComponentEnriched) {
        this.cellularComponentEnriched = cellularComponentEnriched;
    }

    public List<EnrichedGOTerm> getBiologicalProcessEnriched() {
        return biologicalProcessEnriched;
    }

    public void setBiologicalProcessEnriched(
            List<EnrichedGOTerm> biologicalProcessEnriched) {
        this.biologicalProcessEnriched = biologicalProcessEnriched;
    }

    public List<EnrichedGOTerm> getMolecularFunctionEnriched() {
        return molecularFunctionEnriched;
    }

    public void setMolecularFunctionEnriched(
            List<EnrichedGOTerm> molecularFunctionEnriched) {
        this.molecularFunctionEnriched = molecularFunctionEnriched;
    }

    public int getTotalAnnotatedCellularComponent() {
        return totalAnnotatedCellularComponent;
    }

    public void setTotalAnnotatedCellularComponent(
            int totalAnnotatedCellularComponent) {
        this.totalAnnotatedCellularComponent = totalAnnotatedCellularComponent;
    }

    public int getTotalAnnotatedBiologicalProcess() {
        return totalAnnotatedBiologicalProcess;
    }

    public void setTotalAnnotatedBiologicalProcess(
            int totalAnnotatedBiologicalProcess) {
        this.totalAnnotatedBiologicalProcess = totalAnnotatedBiologicalProcess;
    }

    public int getTotalAnnotatedMolecularFunction() {
        return totalAnnotatedMolecularFunction;
    }

    public void setTotalAnnotatedMolecularFunction(
            int totalAnnotatedMolecularFunction) {
        this.totalAnnotatedMolecularFunction = totalAnnotatedMolecularFunction;
    }

    public int getNumInputProteins() {
        return proteinIds.size();
    }
    
    public int getNumSpeciesProteins() {
        return numSpeciesProteins;
    }

    public void setNumSpeciesProteins(int numSpeciesProteins) {
        this.numSpeciesProteins = numSpeciesProteins;
    }
}
