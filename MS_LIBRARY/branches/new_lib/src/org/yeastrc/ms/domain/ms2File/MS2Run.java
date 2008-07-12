/**
 * MS2Run.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.MsRun;

public interface MS2Run extends MsRun {

    /**
     * @return the list of headers for the MS2 run.
     */
    public abstract List<? extends MS2Field> getHeaderList();

}