/**
 * DatasetSource.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

/**
 * 
 */
public enum DatasetSource {

    PROT_INFER, DTA_SELECT;
    
    public static DatasetSource instance(String name) {
        if(PROT_INFER.name().equals(name))      return PROT_INFER;
        else if(DTA_SELECT.name().equals(name)) return DTA_SELECT;
        return null;
    }
}
