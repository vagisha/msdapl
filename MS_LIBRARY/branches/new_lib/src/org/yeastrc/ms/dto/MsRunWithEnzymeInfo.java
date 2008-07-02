package org.yeastrc.ms.dto;

import java.util.ArrayList;
import java.util.List;

public class MsRunWithEnzymeInfo extends MsRun {

    private List <MsDigestionEnzyme> enzymeList;
    
    public MsRunWithEnzymeInfo() {
        enzymeList = new ArrayList<MsDigestionEnzyme>();
    }
    
    /**
     * @return the enzymeList
     */
    public List<MsDigestionEnzyme> getEnzymeList() {
        return enzymeList;
    }

    /**
     * @param enzymeList the enzymeList to set
     */
    public void setEnzymeList(List<MsDigestionEnzyme> enzymeList) {
        this.enzymeList = enzymeList;
    }
    
    public void addEnzyme(MsDigestionEnzyme enzyme) {
        enzymeList.add(enzyme);
    }
    
}
