/**
 * SQTSearchDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.MsRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

/**
 * 
 */
public interface SQTSearchDb extends MsRunSearchDb {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTHeaderDb> getHeaders();
}
