/**
 * MsRun.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

public interface MsRun extends MsRunBase {

    public abstract List<MsEnzyme> getEnzymeList();

}

interface MsRunBase {
    
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

    public abstract String getAcquisitionMethod();
}