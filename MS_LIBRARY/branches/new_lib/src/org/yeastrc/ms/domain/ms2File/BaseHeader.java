package org.yeastrc.ms.domain.ms2File;

public class BaseHeader implements IHeader {

    private String name;
    private String value;

    public BaseHeader() {
        super();
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.ms2File.IHeader#getName()
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

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.ms2File.IHeader#getValue()
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