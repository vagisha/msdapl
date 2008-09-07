/**
 * SQTSearchDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearchDb;

/**
 * 
 */
public interface SQTRunSearchDb extends MsRunSearchDb {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTHeaderItem> getHeaders();
}
