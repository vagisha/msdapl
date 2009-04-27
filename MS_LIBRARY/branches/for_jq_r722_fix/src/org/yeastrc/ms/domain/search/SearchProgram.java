package org.yeastrc.ms.domain.search;

public enum SearchProgram {

    SEQUEST("SEQUEST"),
    EE_NORM_SEQUEST("EE-normalized SEQUEST"),
    PERCOLATOR("Percolator"),
    PROLUCID("ProLuCID"),
    PEPPROBE("PEP_PROBE"),
    BIBLIOSPEC("BiblioSpec"),
    UNKNOWN("Unknown");
 
    private String displayName;
    
    private SearchProgram(String displayName) {
        this.displayName = displayName;
    }
    
    public String displayName() {
        return displayName;
    }
    
    public static SearchProgram programForFileFormat(SearchFileFormat format) {
        if (SearchFileFormat.SQT_SEQ == format)
            return SearchProgram.SEQUEST;
        else if (SearchFileFormat.SQT_NSEQ == format)
            return SearchProgram.EE_NORM_SEQUEST;
        else if (SearchFileFormat.SQT_PLUCID == format)
            return SearchProgram.PROLUCID;
        else if (SearchFileFormat.SQT_PERC == format)
            return SearchProgram.PERCOLATOR;
        else if (SearchFileFormat.SQT_PPROBE == format)
            return SearchProgram.PEPPROBE;
        else if (SearchFileFormat.SQT_BIBLIO == format)
            return SearchProgram.BIBLIOSPEC;
        else
            return SearchProgram.UNKNOWN;
    }
    
    public static SearchProgram instance(String prog) {
        if (SearchProgram.SEQUEST.name().equalsIgnoreCase(prog))
            return SearchProgram.SEQUEST;
        else if (SearchProgram.EE_NORM_SEQUEST.name().equalsIgnoreCase(prog))
            return SearchProgram.EE_NORM_SEQUEST;
        else if (SearchProgram.PROLUCID.name().equalsIgnoreCase(prog))
            return SearchProgram.PROLUCID;
        else if (SearchProgram.PEPPROBE.name().equalsIgnoreCase(prog))
            return SearchProgram.PEPPROBE;
        else if (SearchProgram.PERCOLATOR.name().equalsIgnoreCase(prog))
            return SearchProgram.PERCOLATOR;
        else if (SearchProgram.BIBLIOSPEC.name().equalsIgnoreCase(prog))
            return SearchProgram.BIBLIOSPEC;
        else return SearchProgram.UNKNOWN;
    }
}
