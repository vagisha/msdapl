package org.yeastrc.ms.domain.ms2File.db;

public interface IHeader {

    /**
     * @return the name of the header
     */
    public abstract String getName();

    /**
     * @return the value of the header
     */
    public abstract String getValue();

}