/**
 * SequestParamWrap.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import org.yeastrc.ms.domain.search.sequest.SequestParam;

/**
 * 
 */
public class SequestParamWrap implements SequestParam {

    
    private int searchId;
    private SequestParam param;
    
    public SequestParamWrap(SequestParam param, int searchId) {
        this.param = param;
        this.searchId = searchId;
    }
   
    @Override
    public String getParamName() {
        return param.getParamName();
    }
    
    @Override
    public String getParamValue() {
        return param.getParamValue();
    }
    
    public int getSearchId() {
        return searchId;
    }
}
