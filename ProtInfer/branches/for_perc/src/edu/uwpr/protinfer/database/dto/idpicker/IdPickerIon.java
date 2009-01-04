package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;

public class IdPickerIon extends GenericProteinferIon<IdPickerSpectrumMatch> {

    public double getBestFdr() {
        double best = this.getBestSpectrumMatch().getFdr();
        return (Math.round(best*100.0) / 100.0);
    }
}
