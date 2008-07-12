/**
 * MS2Field.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

public interface MS2Field {

    /**
     * @return the name of the field
     */
    public abstract String getName();

    /**
     * @return the value of the field
     */
    public abstract String getValue();

}