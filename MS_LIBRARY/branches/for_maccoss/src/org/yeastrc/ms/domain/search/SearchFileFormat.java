/**
 * SearchFileFormat.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


public enum SearchFileFormat {

    SQT_SEQ("SQT: "+Program.SEQUEST.displayName()), 
    SQT_NSEQ("SQT: "+Program.EE_NORM_SEQUEST.displayName()), 
    SQT_PLUCID("SQT: "+Program.PROLUCID.displayName()), 
    SQT_PPROBE("SQT: "+Program.PEPPROBE), 
    SQT_PERC("SQT: "+Program.PERCOLATOR.displayName()), 
    SQT_BIBLIO("SQT: "+Program.BIBLIOSPEC.displayName()),
    PEPXML("pepxml"), 
    UNKNOWN("Unknown");

    private String typeName;
    private SearchFileFormat(String typeName) {
        this.typeName = typeName;
    }
    public String getFormatType() {
        return typeName;
    }
    
    public static SearchFileFormat instance(String extString) {
        if (SearchFileFormat.SQT_SEQ.name().equalsIgnoreCase(extString))
            return SearchFileFormat.SQT_SEQ;
        else if (SearchFileFormat.SQT_NSEQ.name().equalsIgnoreCase(extString))
            return SearchFileFormat.SQT_NSEQ;
        else if (SearchFileFormat.SQT_PLUCID.name().equalsIgnoreCase(extString))
            return SearchFileFormat.SQT_PLUCID;
        else if (SearchFileFormat.SQT_PERC.name().equalsIgnoreCase(extString))
            return SearchFileFormat.SQT_PERC;
        else if (SearchFileFormat.SQT_PPROBE.name().equalsIgnoreCase(extString))
            return SearchFileFormat.SQT_PPROBE;
        else if (SearchFileFormat.PEPXML.name().equalsIgnoreCase(extString))
            return SearchFileFormat.PEPXML;
        else return SearchFileFormat.UNKNOWN;
    }
}