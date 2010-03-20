/**
 * IdPickerFilterForm.java
 * @author Vagisha Sharma
 * Mar 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.www.proteinfer.ProteinInferFilterForm;

/**
 * 
 */
public class IdPickerFilterForm extends ProteinInferFilterForm {

	private boolean joinGroupProteins = true;
    private boolean showAllProteins = true;
    private boolean collapseGroups = false; // Used for downloads only
    private boolean printPeptides = false; // Used for downloads only
    private boolean printDescription = false; // used for downloads only
    
    
    public void reset() {
        joinGroupProteins = true;
        showAllProteins = true;
        super.reset();
    }
    
    public boolean isJoinGroupProteins() {
        return joinGroupProteins;
    }

    public void setJoinGroupProteins(boolean joinGroupProteins) {
        this.joinGroupProteins = joinGroupProteins;
    }
    
    public boolean isShowAllProteins() {
        return showAllProteins;
    }

    public void setShowAllProteins(boolean showAllProteins) {
        this.showAllProteins = showAllProteins;
    }
    
    public boolean isCollapseGroups() {
        return collapseGroups;
    }

    public void setCollapseGroups(boolean collapseGroups) {
        this.collapseGroups = collapseGroups;
    }

    public boolean isPrintPeptides() {
        return printPeptides;
    }

    public void setPrintPeptides(boolean printPeptides) {
        this.printPeptides = printPeptides;
    }
    
    public boolean isPrintDescriptions() {
        return printDescription;
    }

    public void setPrintDescriptions(boolean printDescription) {
        this.printDescription = printDescription;
    }
    
    public ProteinFilterCriteria getFilterCriteria(PeptideDefinition peptideDef) {
    	
    	ProteinFilterCriteria filterCriteria = super.getFilterCriteria(peptideDef);
    	
    	filterCriteria.setGroupProteins(isJoinGroupProteins());
    	 if(!isShowAllProteins())
             filterCriteria.setParsimoniousOnly();
    	 
        if(isCollapseGroups()) 
            filterCriteria.setSortBy(SORT_BY.GROUP_ID);
        else
            filterCriteria.setSortBy(ProteinFilterCriteria.defaultSortBy());
        
       
        
        return filterCriteria;
    }
}
