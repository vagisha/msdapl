package org.yeastrc.ms.domain.sqtFile;

import java.math.BigDecimal;
import java.util.List;

public interface SQTSearchScan {

    
    public abstract int getScanNumber();
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
    
    
    public abstract List<? extends SQTSearchResult> getScanResults();

}