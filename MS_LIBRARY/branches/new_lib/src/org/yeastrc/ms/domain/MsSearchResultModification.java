/**
 * MsSearchResultModification.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public interface MsSearchResultModification extends MsSearchModification {

    /**
     * @return index (0 based) in the protein sequence at which this modification is present.
     */
    public abstract int getModifiedPosition();
}
