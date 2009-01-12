/**
 * WIdPickerResultSummary.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

/**
 * 
 */
public class WIdPickerResultSummary {

    private int unfilteredProteinCount;
    private int filteredProteinCount;
    private int filteredParsimoniousProteinCount;
    
    private int filteredProteinGroupCount;
    private int filteredParsimoniousProteinGroupCount;
    
    public int getUnfilteredProteinCount() {
        return unfilteredProteinCount;
    }
    public void setUnfilteredProteinCount(int unfilteredProteinCount) {
        this.unfilteredProteinCount = unfilteredProteinCount;
    }
    public int getFilteredProteinCount() {
        return filteredProteinCount;
    }
    public void setFilteredProteinCount(int filteredProteinCount) {
        this.filteredProteinCount = filteredProteinCount;
    }
    public int getFilteredParsimoniousProteinCount() {
        return filteredParsimoniousProteinCount;
    }
    public void setFilteredParsimoniousProteinCount(
            int filteredParsimoniousProteinCount) {
        this.filteredParsimoniousProteinCount = filteredParsimoniousProteinCount;
    }
    public int getFilteredProteinGroupCount() {
        return filteredProteinGroupCount;
    }
    public void setFilteredProteinGroupCount(int filteredProteinGroupCount) {
        this.filteredProteinGroupCount = filteredProteinGroupCount;
    }
    public int getFilteredParsimoniousProteinGroupCount() {
        return filteredParsimoniousProteinGroupCount;
    }
    public void setFilteredParsimoniousProteinGroupCount(
            int filteredParsimoniousProteinGroupCount) {
        this.filteredParsimoniousProteinGroupCount = filteredParsimoniousProteinGroupCount;
    }
}
