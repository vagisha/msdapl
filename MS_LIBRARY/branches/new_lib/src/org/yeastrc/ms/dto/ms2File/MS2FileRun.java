/**
 * MS2FileRun.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dto.IMsRun;
import org.yeastrc.ms.dto.MsDigestionEnzyme;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;

/**
 * 
 */
public class MS2FileRun implements IMsRun {

    private List<MS2FileHeader> headers;
    private IMsRun run;
    
    public MS2FileRun(IMsRun run) {
        this.run = run;
        headers = new ArrayList<MS2FileHeader>();
    }
    
    public void setMS2Headers(List<MS2FileHeader> headers) {
        this.headers = headers;
    }
    
    public List<MS2FileHeader> getMS2Headers() {
        return headers;
    }
    
    public void addMS2Header(MS2FileHeader header) {
        headers.add(header);
    }

    @Override
    public void addEnzyme(MsDigestionEnzyme enzyme) {
        run.addEnzyme(enzyme);
    }

    @Override
    public String getAcquisitionMethod() {
        return run.getAcquisitionMethod();
    }

    @Override
    public String getComment() {
        return run.getComment();
    }

    @Override
    public String getConversionSW() {
        return run.getConversionSW();
    }

    @Override
    public String getConversionSWOptions() {
        return run.getConversionSWOptions();
    }

    @Override
    public String getConversionSWVersion() {
       return run.getConversionSWVersion();
    }

    @Override
    public String getCreationDate() {
       return run.getCreationDate();
    }

    @Override
    public String getDataType() {
        return run.getDataType();
    }

    @Override
    public List<MsDigestionEnzyme> getEnzymeList() {
       return run.getEnzymeList();
    }

    @Override
    public String getFileFormat() {
       return run.getFileFormat();
    }

    @Override
    public String getFileName() {
       return run.getFileName();
    }

    @Override
    public int getId() {
       return run.getId();
    }

    @Override
    public String getInstrumentModel() {
        return run.getInstrumentModel();
    }

    @Override
    public String getInstrumentSN() {
       return run.getInstrumentSN();
    }

    @Override
    public String getInstrumentVendor() {
        return run.getInstrumentVendor();
    }

    @Override
    public int getMsExperimentId() {
       return run.getMsExperimentId();
    }

    @Override
    public RunFileFormat getRunFileFormat() {
       return run.getRunFileFormat();
    }

    @Override
    public String getSha1Sum() {
       return run.getSha1Sum();
    }

    @Override
    public void setAcquisitionMethod(String acquisitionMethod) {
       run.setAcquisitionMethod(acquisitionMethod);
    }

    @Override
    public void setComment(String comment) {
       run.setComment(comment);
    }

    @Override
    public void setConversionSW(String conversionSW) {
       run.setConversionSW(conversionSW);
    }

    @Override
    public void setConversionSWOptions(String conversionSWOptions) {
       run.setConversionSWOptions(conversionSWOptions);
    }

    @Override
    public void setConversionSWVersion(String conversionSWVersion) {
       run.setConversionSWVersion(conversionSWVersion);
    }

    @Override
    public void setCreationDate(String creationDate) {
       run.setCreationDate(creationDate);
    }

    @Override
    public void setDataType(String dataType) {
       run.setDataType(dataType);
    }

    @Override
    public void setEnzymeList(List<MsDigestionEnzyme> enzymeList) {
       run.setEnzymeList(enzymeList);
    }

    @Override
    public void setFileFormat(String fileFormatStr) {
       run.setFileFormat(fileFormatStr);
    }

    @Override
    public void setFileName(String fileName) {
        run.setFileName(fileName);
    }

    @Override
    public void setId(int id) {
        run.setId(id);
    }

    @Override
    public void setInstrumentModel(String instrumentModel) {
        run.setInstrumentModel(instrumentModel);
    }

    @Override
    public void setInstrumentSN(String instrumentSN) {
      run.setInstrumentSN(instrumentSN);
    }

    @Override
    public void setInstrumentVendor(String instrumentVendor) {
       run.setInstrumentVendor(instrumentVendor);
    }

    @Override
    public void setMsExperimentId(int msExperimentId) {
       run.setMsExperimentId(msExperimentId);
    }

    @Override
    public void setSha1Sum(String sha1Sum) {
       run.setSha1Sum(sha1Sum);
    }
}
