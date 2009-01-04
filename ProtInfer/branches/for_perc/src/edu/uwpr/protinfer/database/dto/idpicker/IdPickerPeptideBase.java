package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class IdPickerPeptideBase extends GenericIdPickerPeptide<ProteinferSpectrumMatch, ProteinferIon> {

    @Override
    protected GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> newPeptide() {
        return new IdPickerPeptideBase();
    }

}
