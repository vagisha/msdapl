package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideIon;

public class WIdPickerPeptideIonWSpectra <T extends MsSearchResult> extends WIdPickerPeptideIon {

    private List<T> psmList;
    
    public WIdPickerPeptideIonWSpectra(IdPickerPeptideIon ion) {
        super(ion);
        psmList = new ArrayList<T>();
    }
    
    public void addMsSearchResult(T result) {
        psmList.add(result);
    }
    
    public List<T> getPsmList() {
        return psmList;
    }
}
