package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

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
