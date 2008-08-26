/**
 * ProlucidParamDbImpl.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import org.yeastrc.ms.domain.search.prolucid.ProlucidParamDb;

/**
 * 
 */
public class ProlucidParamDbImpl implements ProlucidParamDb {

    private String elName;
    private String elValue;
    private int parentElId;
    private int id;
    
    @Override
    public String getParamElementName() {
        return elName;
    }

    @Override
    public String getParamElementValue() {
        return elValue;
    }

    @Override
    public int getParentParamElementId() {
        return parentElId;
    }

    public void setElName(String elName) {
        this.elName = elName;
    }

    public void setElValue(String elValue) {
        this.elValue = elValue;
    }

    public void setParentElId(int parentElId) {
        this.parentElId = parentElId;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
