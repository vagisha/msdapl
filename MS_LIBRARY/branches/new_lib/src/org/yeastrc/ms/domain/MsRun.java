/**
 * MsRun.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

public interface MsRun {

    public static enum RunFileFormat {
        
        MS2, MZXML, MZDATA, MZML, UNKNOWN;
        
        public static RunFileFormat getFileFormatForString(String extString) {
            if (extString.equalsIgnoreCase(RunFileFormat.MS2.name()))
                return RunFileFormat.MS2;
            else if (extString.equals(RunFileFormat.MZXML.name()))
                return RunFileFormat.MZXML;
            else if (extString.equalsIgnoreCase(RunFileFormat.MZDATA.name()))
                return RunFileFormat.MZDATA;
            else if (extString.equalsIgnoreCase(RunFileFormat.MZML.name()))
                return RunFileFormat.MZML;
            else return RunFileFormat.UNKNOWN;
        }
    };
    
    public abstract RunFileFormat getRunFileFormat();

    public abstract String getFileName();

    public abstract String getCreationDate();

    public abstract String getConversionSW();

    public abstract String getConversionSWVersion();

    public abstract String getConversionSWOptions();

    public abstract String getInstrumentVendor();

    public abstract String getInstrumentModel();

    public abstract String getInstrumentSN();

    public abstract String getComment();

    public abstract String getSha1Sum();

    // TODO: rename method.  getDataConversion(Processing)Method()??
    public abstract String getDataType();

    public abstract String getAcquisitionMethod();

    public abstract List<? extends MsEnzyme> getEnzymeList();

}