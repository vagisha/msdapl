/**
 * MS2ChargeDependentAnalysisDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;


/**
 * 
 */
public interface MS2ChargeDependentAnalysisDb extends MS2Field {

    /**
     * @return database if of the scan + charge this belongs to.
     */
    public abstract int getScanChargeId();
    
    /**
     * @return database id of this charge dependent analysis item.
     */
    public abstract int getId();
}
