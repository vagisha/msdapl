/**
 * SQTSearchDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

/**
 * 
 */
public interface SQTSearchDb extends MsSearchDb {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTHeaderDb> getHeaders();
}
