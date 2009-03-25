/**
 * RunFileFormat.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

public enum RunFileFormat {
    
    MS2, CMS2, MZXML, MZDATA, MZML, UNKNOWN;
    
    public static RunFileFormat instance(String fmtString) {
        if(fmtString.startsWith("."))
            fmtString = fmtString.substring(1);
        if (fmtString.equalsIgnoreCase(RunFileFormat.MS2.name()))
            return RunFileFormat.MS2;
        if (fmtString.equalsIgnoreCase(RunFileFormat.CMS2.name()))
            return RunFileFormat.CMS2;
        else if (fmtString.equals(RunFileFormat.MZXML.name()))
            return RunFileFormat.MZXML;
        else if (fmtString.equalsIgnoreCase(RunFileFormat.MZDATA.name()))
            return RunFileFormat.MZDATA;
        else if (fmtString.equalsIgnoreCase(RunFileFormat.MZML.name()))
            return RunFileFormat.MZML;
        else return RunFileFormat.UNKNOWN;
    }
    
    public static RunFileFormat forFileExtension(String extString) {
        return instance(extString);
    }
    
    public static boolean isSupportedFormat(String extString) {
        if(RunFileFormat.instance(extString) == RunFileFormat.UNKNOWN)
            return false;
        else
            return true;
    }
}