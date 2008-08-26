/**
 * ProlucidResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataDb;

/**
 * 
 */
public class ProlucidResultDataDbImpl extends ProlucidResultDataImpl implements
        ProlucidResultDataDb {

    private int resultId;
    
    @Override
    public int getResultId() {
        return resultId;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
}
