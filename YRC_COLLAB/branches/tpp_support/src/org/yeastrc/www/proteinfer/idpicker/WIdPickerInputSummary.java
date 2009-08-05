package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;

public class WIdPickerInputSummary {

    private IdPickerInput idpInput;
    private String fileName;
    
    public WIdPickerInputSummary(IdPickerInput idpInput) {
        this.idpInput = idpInput;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public IdPickerInput getInput() {
        return idpInput;
    }
}
