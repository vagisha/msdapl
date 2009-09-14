/**
 * InteractPepXmlDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;

/**
 * 
 */
public interface InteractPepXmlDataProvider <T extends PepXmlSearchScanIn> extends PepxmlDataProvider<T>{

    public abstract boolean hasNextRunSearch() throws DataProviderException;
   
}
