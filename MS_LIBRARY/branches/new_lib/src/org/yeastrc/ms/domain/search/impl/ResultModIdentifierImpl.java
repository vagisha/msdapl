/**
 * ResultModIdentifierImpl.java
 * @author Vagisha Sharma
 * Sep 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.ResultModIdentifier;

/**
 * 
 */
public class ResultModIdentifierImpl implements ResultModIdentifier {

    private final int modId;
    private final int resultId;
    
    public ResultModIdentifierImpl(int resultId, int modId) {
        this.resultId = resultId;
        this.modId = modId;
    }
    
    @Override
    public int getModificationId() {
        return modId;
    }

    @Override
    public int getResultId() {
        return resultId;
    }

}
