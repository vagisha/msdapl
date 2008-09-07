/**
 * MsResultDynamicTerminalModDbImpl.java
 * @author Vagisha Sharma
 * Aug 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsResultTerminalMod;

/**
 * 
 */
public class ResultTerminalModBean extends TerminalModification implements
        MsResultTerminalMod {

    private int modId;
    private int resultId;
    
    @Override
    public int getModificationId() {
        return modId;
    }

    public void setModificationId(int modId) {
        this.modId = modId;
    }
    
    @Override
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
}
