/**
 * WProteinProphetResultSummary.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

/**
 * 
 */
public class WProteinProphetResultSummary {

    private int filteredProteinCount;
    private int filteredParsimoniousProteinCount;
    
    private int filteredProteinGroupCount;
    private int filteredParsimoniousProteinGroupCount;
    
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
