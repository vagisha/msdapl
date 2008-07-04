package org.yeastrc.ms.dto.ms2File;

public class BaseHeader {

    private String name;
    private String value;

    public BaseHeader() {
        super();
    }

    /**
     * @return the name of the header
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
     * @return the value of the header
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