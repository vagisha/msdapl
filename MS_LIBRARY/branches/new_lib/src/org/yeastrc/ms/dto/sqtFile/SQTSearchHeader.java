package org.yeastrc.ms.dto.sqtFile;

import org.yeastrc.ms.dto.ms2File.BaseHeader;

public class SQTSearchHeader extends BaseHeader {

    private int id;
    private int searchId;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }
    /**
     * @param searchId the searchId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
}
