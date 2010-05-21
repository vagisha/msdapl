/**
 * NrDatabase.java
 * @author Vagisha Sharma
 * Sep 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.nrseq;

/**
 * 
 */
public class NrDatabase {

    private int id;
    private String name;
    private String description;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
