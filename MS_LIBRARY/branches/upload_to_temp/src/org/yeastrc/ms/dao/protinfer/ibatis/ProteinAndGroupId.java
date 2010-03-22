/**
 * ProteinAndGroupId.java
 * @author Vagisha Sharma
 * Mar 21, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.ibatis;

/**
 * 
 */
public final class ProteinAndGroupId {

	private int proteinId;
    private int groupId;
    
    public void setProteinId(int proteinId) {
        this.proteinId = proteinId;
    }
    
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

	public int getProteinId() {
		return proteinId;
	}

	public int getGroupId() {
		return groupId;
	}
}
