/**
 * MsRunDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

/**
 * 
 */
public interface MsRunDb extends MsRunBase {

    /**
     * @return database id of the experiment this run belongs to.
     */
    public abstract int getExperimentId();
    
    /**
     * @return database id of the run
     */
    public abstract int getId();
    
    /**
     * @ return the list of enzymes for this run.
     */
    public abstract List<MsEnzymeDb> getEnzymeList();
}
