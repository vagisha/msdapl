/**
 * MS2FileRun.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsRun;

/**
 * 
 */
public class MS2FileRun extends MsRun {

    private List<MS2FileHeader> headers;
    
    public MS2FileRun() {
        headers = new ArrayList<MS2FileHeader>();
    }
    
    public MS2FileRun(MsRun run) {
        this();
        setMsExperimentId(run.getMsExperimentId());
        setFileName(run.getFileName());
        setFileFormat(run.getFileFormat());
        setSha1Sum(run.getSha1Sum());
        setCreationDate(run.getCreationDate());
        setConversionSW(run.getConversionSW());
        setConversionSWOptions(run.getConversionSWOptions());
        setConversionSWVersion(run.getConversionSWVersion());
        setInstrumentModel(run.getInstrumentModel());
        setInstrumentSN(run.getInstrumentSN());
        setInstrumentVendor(run.getInstrumentVendor());
        setDataType(run.getDataType());
        setAcquisitionMethod(run.getAcquisitionMethod());
        setComment(run.getComment());
        setEnzymeList(run.getEnzymeList());
    }
    
    public void setHeaderList(List<MS2FileHeader> headers) {
        this.headers = headers;
    }
    
    public List<MS2FileHeader> getHeaderList() {
        return headers;
    }
    
    public void addMS2Header(MS2FileHeader header) {
        headers.add(header);
    }
   
}
