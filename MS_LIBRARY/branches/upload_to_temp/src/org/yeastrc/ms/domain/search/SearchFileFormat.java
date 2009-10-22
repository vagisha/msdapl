/**
 * SearchFileFormat.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;



public enum SearchFileFormat {

    SQT("SQT: GENERIC"),
    SQT_SEQ("SQT: "+Program.SEQUEST.displayName()), 
    //SQT_NSEQ("SQT: "+Program.EE_NORM_SEQUEST.displayName()), 
    SQT_PLUCID("SQT: "+Program.PROLUCID.displayName()), 
    SQT_PPROBE("SQT: "+Program.PEPPROBE), 
    SQT_PERC("SQT: "+Program.PERCOLATOR.displayName()), 
    SQT_BIBLIO("SQT: "+Program.BIBLIOSPEC.displayName()),
    PEPXML("pepxml: GENERIC"), 
    PEPXML_SEQ("pepxml: "+Program.SEQUEST.displayName()),
    PEPXML_MASCOT("pepxml: "+Program.MASCOT.displayName()),
    PEPXML_XTANDEM("pepxml: "+Program.XTANDEM.displayName()),
    PEPXML_PEPT_PROPHET("pepxml: "+Program.PEPTIDE_PROPHET.displayName()),
    PROTXML("protxml"),
    UNKNOWN("Unknown");

    private String typeName;
    private SearchFileFormat(String typeName) {
        this.typeName = typeName;
    }
    public String getFormatType() {
        return typeName;
    }
    
    public static SearchFileFormat instance(String fmtString) {
        if (SearchFileFormat.SQT_SEQ.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.SQT_SEQ;
//        else if (SearchFileFormat.SQT_NSEQ.name().equalsIgnoreCase(fmtString))
//            return SearchFileFormat.SQT_NSEQ;
        else if (SearchFileFormat.SQT_PLUCID.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.SQT_PLUCID;
        else if (SearchFileFormat.SQT_PERC.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.SQT_PERC;
        else if (SearchFileFormat.SQT_PPROBE.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.SQT_PPROBE;
        else if (SearchFileFormat.PEPXML.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.PEPXML;
        else if (SearchFileFormat.PEPXML_SEQ.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.PEPXML_SEQ;
        else if (SearchFileFormat.PEPXML_MASCOT.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.PEPXML_MASCOT;
        else if (SearchFileFormat.PEPXML_XTANDEM.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.PEPXML_XTANDEM;
        else if (SearchFileFormat.PROTXML.name().equalsIgnoreCase(fmtString))
            return SearchFileFormat.PROTXML;
        else return SearchFileFormat.UNKNOWN;
    }
    
    public static SearchFileFormat forFileExtension(String extString) {
        if(extString.startsWith("."))
            extString = extString.substring(1);
        if (extString.equalsIgnoreCase(SearchFileFormat.SQT.name()))
            return SearchFileFormat.SQT;
        else if (extString.equalsIgnoreCase(SearchFileFormat.PEPXML.name()) ||
                extString.equalsIgnoreCase("pep.xml"))
            return SearchFileFormat.PEPXML;
        else if (extString.equalsIgnoreCase(SearchFileFormat.PROTXML.name()) ||
                extString.equalsIgnoreCase("prot.xml"))
            return SearchFileFormat.PROTXML;
        else return SearchFileFormat.UNKNOWN;
    }
}