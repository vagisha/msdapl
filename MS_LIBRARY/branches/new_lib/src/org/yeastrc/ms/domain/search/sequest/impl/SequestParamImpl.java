/**
 * SequestParamImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import org.yeastrc.ms.domain.search.sequest.SequestParam;

/**
 * 
 */
public class SequestParamImpl implements SequestParam {

    private String name;
    private String value;
    
    @Override
    public String getParamName() {
        return name;
    }
    
    public void setParamName(String name) {
        this.name = name;
    }

    @Override
    public String getParamValue() {
        return value;
    }
    
    public void setParamValue(String value) {
        this.value = value;
    }

}
