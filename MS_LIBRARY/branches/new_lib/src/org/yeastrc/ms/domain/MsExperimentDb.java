/**
 * MsExperimentDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

public interface MsExperimentDb extends MsExperiment {
    
    /**
     * @return the database id for the experiment
     */
    public abstract int getId();
}
