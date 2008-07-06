package org.yeastrc.ms.dto;

public class MsDigestionEnzyme {

    private int id; // id (database) from the enzyme
    private String name; // Name of the enzyme
    private short sense = -1;   // a value of 0 means the enzyme cleaves on the C-terminal end; 
                                // 1 means it cleaves on the N-terminal;
                                // -1 indicates that we don't know where the enzyme cleaves
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
    public short getSense() {
        return sense;
    }
    /**
     * @param sense the sense to set
     */
    public void setSense(short sense) {
        this.sense = sense;
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
