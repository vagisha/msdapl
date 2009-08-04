/**
 * Modification.java
 * @author Vagisha Sharma
 * Aug 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.math.BigDecimal;

public class Modification {
    private int position;
    private BigDecimal mass;
    
    public Modification(int pos, BigDecimal mass) {
        this.position = pos;
        this.mass = mass;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public BigDecimal getMass() {
        return mass;
    }
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }
}