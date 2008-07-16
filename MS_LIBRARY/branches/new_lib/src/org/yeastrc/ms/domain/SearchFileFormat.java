/**
 * SearchFileFormat.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

public enum SearchFileFormat {

    SQT, PEPXML, UNKNOWN;

    public static SearchFileFormat instance(String extString) {
        if (extString.equalsIgnoreCase(SearchFileFormat.SQT.name()))
            return SearchFileFormat.SQT;
        else if (extString.equals(SearchFileFormat.PEPXML.name()))
            return SearchFileFormat.PEPXML;
        else return SearchFileFormat.UNKNOWN;
    }
}