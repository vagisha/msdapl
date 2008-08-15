/**
 * MS2ChargeIndependentAnalysisDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;


/**
 * 
 */
public interface MS2ChargeIndependentAnalysisDb extends MS2Field {

    /**
     * @return database id of the scan this belongs to.
     */
    public abstract int getScanId();
    
    /**
     * @return database id of this charge independent analysis item.
     */
    public abstract int getId();
}
