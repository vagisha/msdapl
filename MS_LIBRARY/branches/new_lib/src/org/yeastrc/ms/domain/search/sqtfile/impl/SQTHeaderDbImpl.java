package org.yeastrc.ms.domain.search.sqtfile.impl;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

public class SQTHeaderDbImpl implements SQTHeaderDb {

    private int id;
    private int runSearchId;
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
    public int getRunSearchId() {
        return runSearchId;
    }
    /**
     * @param runSearchId the runSearchId to set
     */
    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
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
