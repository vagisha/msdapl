package org.yeastrc.www.proteinfer.idpicker;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInputSummary;

public class WIdPickerInputSummary {

    private IdPickerInputSummary idpInput;
    private String fileName;
    
    public WIdPickerInputSummary(IdPickerInputSummary idpInput) {
        this.idpInput = idpInput;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public IdPickerInputSummary getInput() {
        return idpInput;
    }
}
