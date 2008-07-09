package org.yeastrc.ms.parser.sqtFile;

import org.yeastrc.ms.dto.ms2File.IHeader;

public class HeaderItem implements IHeader {
    
    private String name;
    private String value;
    
    public HeaderItem(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("H\t");
        buf.append(name);
        if (value != null) {
            buf.append("\t");
            buf.append(value);
        }
        return buf.toString();
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
}
