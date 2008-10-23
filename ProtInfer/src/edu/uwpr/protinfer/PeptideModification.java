package edu.uwpr.protinfer;

import java.math.BigDecimal;

public class PeptideModification {

    private int position;
    private BigDecimal massShift;
    
    public PeptideModification(int position, BigDecimal massShift) {
        this.position = position;
        this.massShift = massShift;
    }

    public int getPosition() {
        return position;
    }

    public BigDecimal getMassShift() {
        return massShift;
    }
}
