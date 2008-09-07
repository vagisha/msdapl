package org.yeastrc.ms.domain.general.impl;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;

public class MsEnzymeImpl implements MsEnzyme {

    private MsEnzymeIn enzyme;
    
    public MsEnzymeImpl(MsEnzymeIn enzyme) {
        this.enzyme = enzyme;
    }
    
    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCut() {
        return enzyme.getCut();
    }

    @Override
    public String getDescription() {
        return enzyme.getDescription();
    }

    @Override
    public String getName() {
        return enzyme.getName();
    }

    @Override
    public String getNocut() {
        return enzyme.getNocut();
    }

    @Override
    public Sense getSense() {
        return enzyme.getSense();
    }
}
