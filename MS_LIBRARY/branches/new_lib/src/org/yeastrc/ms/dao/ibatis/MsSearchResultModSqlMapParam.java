/**
 * MsSearchResultModSqlMapParam.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.domain.MsSearchResultModification;

/**
 * 
 */
public class MsSearchResultModSqlMapParam {

    private int resultId;
    private int modId;
    private int modPosition;

    public MsSearchResultModSqlMapParam(int resultId, int modId, MsSearchResultModification mod) {
        this.resultId = resultId;
        this.modId = modId;
        this.modPosition = mod.getModifiedPosition();
    }
    
    public int getResultId() {
        return resultId;
    }

    public int getModificationId() {
        return modId;
    }
    
    public int getModifiedPosition() {
        return modPosition;
    }
}
