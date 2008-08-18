/**
 * SequestSearchDb.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchDb;

/**
 * 
 */
public interface SequestSearchDb extends MsSearchDb {

    public abstract List<SequestParam> getSequestParams();
}
