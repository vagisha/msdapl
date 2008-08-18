/**
 * SequestResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;

/**
 * 
 */
public class SequestResultDataDbImpl extends SequestResultDataImpl implements
        SequestResultDataDb {

    private int resultId;
    
    @Override
    public int getResultId() {
        return resultId;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

}
