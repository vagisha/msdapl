/**
 * ProlucidParam.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.util.List;

/**
 * 
 */
public interface ProlucidParam {

    public abstract String getParamElementName();
    
    public abstract String getParamElementValue();
    
    public abstract List<ProlucidParam> getChildParamElements();
}
