package org.yeastrc.ms.domain.impl;

import org.yeastrc.ms.domain.MsEnzymeDb;

public class MsEnzymeDbImpl implements MsEnzymeDb {

    private int id; // id (database) from the enzyme
    private String name; // Name of the enzyme
    private Sense sense = Sense.UNKNOWN; // terminal at which the enzyme cleaves.
    private String cut; // amino acid residue(s) where the enzyme cleaves
    private String nocut; // amino acid(s), which when present next to the cleavage site result in no cleavage. 
    private String description; 
    
    
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
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the sense
     */
    public Sense getSense() {
        return sense;
    }
    
    public byte  getSenseByteVal() {
        return sense.getByteVal();
    }
    
    /**
     * @param sense the sense to set
     */
    public void setSenseByteVal(byte sense) {
        this.sense = Sense.getSenseForByteVal(sense);
    }
    /**
     * @return the cut
     */
    public String getCut() {
        return cut;
    }
    /**
     * @param cut the cut to set
     */
    public void setCut(String cut) {
        this.cut = cut;
    }
    /**
     * @return the nocut
     */
    public String getNocut() {
        return nocut;
    }
    /**
     * @param nocut the nocut to set
     */
    public void setNocut(String nocut) {
        this.nocut = nocut;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
