/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.parser;

/**
 * 
 */
public class Ms2FileHeader {

    private String label;
    private String value;
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("H\t");
        buf.append(label);
        buf.append("\t");
        buf.append(value);
        return buf.toString();
    }
}
