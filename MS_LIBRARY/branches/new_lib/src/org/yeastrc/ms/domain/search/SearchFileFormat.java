/**
 * SearchFileFormat.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

public enum SearchFileFormat {

    SQT_SEQ, 
    SQT_NSEQ, 
    SQT_PLUCID, 
    SQT_PPROBE, 
    SQT_PERC, 
    PEPXML, 
    UNKNOWN;

    public static SearchFileFormat instance(String extString) {
        if (extString.equalsIgnoreCase(SearchFileFormat.SQT_SEQ.name()))
            return SearchFileFormat.SQT_SEQ;
        else if (extString.equalsIgnoreCase(SearchFileFormat.SQT_NSEQ.name()))
            return SearchFileFormat.SQT_NSEQ;
        else if (extString.equalsIgnoreCase(SearchFileFormat.SQT_PLUCID.name()))
            return SearchFileFormat.SQT_PLUCID;
        else if (extString.equalsIgnoreCase(SearchFileFormat.SQT_PERC.name()))
            return SearchFileFormat.SQT_PERC;
        else if (extString.equalsIgnoreCase(SearchFileFormat.SQT_PPROBE.name()))
            return SearchFileFormat.SQT_PPROBE;
        else if (extString.equals(SearchFileFormat.PEPXML.name()))
            return SearchFileFormat.PEPXML;
        else return SearchFileFormat.UNKNOWN;
    }
}