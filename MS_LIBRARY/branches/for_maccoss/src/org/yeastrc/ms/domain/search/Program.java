package org.yeastrc.ms.domain.search;

public enum Program {

    SEQUEST("SEQUEST"),
    //EE_NORM_SEQUEST("EE-normalized SEQUEST"),
    PERCOLATOR("Percolator"),
    PROLUCID("ProLuCID"),
    PEPPROBE("PEP_PROBE"),
    BIBLIOSPEC("BiblioSpec"),
    UNKNOWN("Unknown");
 
    private String displayName;
    
    private Program(String displayName) {
        this.displayName = displayName;
    }
    
    public String displayName() {
        return displayName;
    }
    
    public static Program programForFileFormat(SearchFileFormat format) {
        if (SearchFileFormat.SQT_SEQ == format)
            return Program.SEQUEST;
//        else if (SearchFileFormat.SQT_NSEQ == format)
//            return Program.EE_NORM_SEQUEST;
        else if (SearchFileFormat.SQT_PLUCID == format)
            return Program.PROLUCID;
        else if (SearchFileFormat.SQT_PERC == format)
            return Program.PERCOLATOR;
        else if (SearchFileFormat.SQT_PPROBE == format)
            return Program.PEPPROBE;
        else if (SearchFileFormat.SQT_BIBLIO == format)
            return Program.BIBLIOSPEC;
        else
            return Program.UNKNOWN;
    }
    
    public static Program instance(String prog) {
        if (Program.SEQUEST.name().equalsIgnoreCase(prog))
            return Program.SEQUEST;
//        else if (Program.EE_NORM_SEQUEST.name().equalsIgnoreCase(prog))
//            return Program.EE_NORM_SEQUEST;
        else if (Program.PROLUCID.name().equalsIgnoreCase(prog))
            return Program.PROLUCID;
        else if (Program.PEPPROBE.name().equalsIgnoreCase(prog))
            return Program.PEPPROBE;
        else if (Program.PERCOLATOR.name().equalsIgnoreCase(prog))
            return Program.PERCOLATOR;
        else if (Program.BIBLIOSPEC.name().equalsIgnoreCase(prog))
            return Program.BIBLIOSPEC;
        else return Program.UNKNOWN;
    }
    
    public static boolean isSearchProgram(Program program) {
        if(program == SEQUEST || // program == EE_NORM_SEQUEST ||
           program == PROLUCID || program == PEPPROBE||
           program == BIBLIOSPEC)
            return true;
        return false;
    }
    
    public static boolean isAnalysisProgram(Program program) {
        if(program == PERCOLATOR)
            return true;
        return false;
    }
}
