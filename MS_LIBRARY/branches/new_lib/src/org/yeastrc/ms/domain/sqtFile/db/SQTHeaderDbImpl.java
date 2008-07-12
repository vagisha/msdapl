package org.yeastrc.ms.domain.sqtFile.db;

import org.yeastrc.ms.domain.sqtFile.SQTHeaderDb;

public class SQTHeaderDbImpl implements SQTHeaderDb {

    private int id;
    private int searchId;
    private String name;
    private String value;
    
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
    
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
