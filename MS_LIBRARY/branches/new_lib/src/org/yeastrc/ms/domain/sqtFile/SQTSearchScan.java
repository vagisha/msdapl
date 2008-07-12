package org.yeastrc.ms.domain.sqtFile;

import java.math.BigDecimal;

public interface SQTSearchScan {

    /**
     * @return the charge
     */
    public abstract int getCharge();

    /**
     * @return the processTime
     */
    public abstract int getProcessTime();

    /**
     * @return the serverName
     */
    public abstract String getServerName();

    /**
     * @return the totalIntensity
     */
    public abstract BigDecimal getTotalIntensity();

    /**
     * @return the lowestSp
     */
    public abstract BigDecimal getLowestSp();

}