package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public abstract class GenericIdPickerPeptide <S extends ProteinferSpectrumMatch, T extends GenericProteinferIon<S>> 
                            extends GenericProteinferPeptide<S, T> {

    private int groupId = -1;
    
    public GenericIdPickerPeptide () {}
    
    public GenericIdPickerPeptide (int peptideGroupId) {
        this.groupId = peptideGroupId;
    }
    
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
