/**
 * Enzyme.java
 * @author Vagisha Sharma
 * Aug 25, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public class Enzyme implements MsEnzyme {

    private String name;
    private Sense sense;
    private String cut;
    private String nocut;
    private String description;


    public String getCut() {
        return cut;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public String getNocut() {
        return nocut;
    }
    public Sense getSense() {
        return sense;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSense(Sense sense) {
        this.sense = sense;
    }
    public void setCut(String cut) {
        this.cut = cut;
    }
    public void setNocut(String nocut) {
        this.nocut = nocut;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
