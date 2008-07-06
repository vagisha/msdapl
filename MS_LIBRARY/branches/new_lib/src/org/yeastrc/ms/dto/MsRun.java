package org.yeastrc.ms.dto;

import java.util.ArrayList;
import java.util.List;


public class MsRun {

    public static enum RunFileFormat {MS2, MZXML, MZDATA, MZML, UNKNOWN};
    
    private int id; // unique id (database) for this run
    
    private int msExperimentId; // id (database) of the experiment this run belongs to
    
    // File for this run
    private String fileName; 
    private RunFileFormat fileFormat;
    private String sha1Sum;
    private String creationDate;
    
    // conversion software
    private String conversionSW; // software used to convert the RAW file
    private String conversionSWVersion; // version of the conversion software used
    private String conversionSWOptions; // options used for conversion
    private String dataType; // centroid / profile etc. 
    
    // acquisition instrument
    private String instrumentVendor;
    private String instrumentModel;
    private String instrumentSN; // serial number of the instrument
    private String acquisitionMethod;
    
    
    private String comment;
    
  private List <MsDigestionEnzyme> enzymeList;
    
    public MsRun() {
        enzymeList = new ArrayList<MsDigestionEnzyme>();
    }
    
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

    public RunFileFormat getRunFileFormat() {
        return fileFormat;
    }

    public String getFileFormat() {
        if (fileFormat == null)
            return null;
        return fileFormat.name();
    }
    
    public void setFileFormat(String fileFormatStr) {
        this.fileFormat = getFileFormatForString(fileFormatStr);
    }

    public int getMsExperimentId() {
        return msExperimentId;
    }

    public void setMsExperimentId(int msExperimentId) {
        this.msExperimentId = msExperimentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getConversionSW() {
        return conversionSW;
    }

    public void setConversionSW(String conversionSW) {
        this.conversionSW = conversionSW;
    }

    public String getConversionSWVersion() {
        return conversionSWVersion;
    }

    public void setConversionSWVersion(String conversionSWVersion) {
        this.conversionSWVersion = conversionSWVersion;
    }

    public String getConversionSWOptions() {
        return conversionSWOptions;
    }

    public void setConversionSWOptions(String conversionSWOptions) {
        this.conversionSWOptions = conversionSWOptions;
    }

    public String getInstrumentVendor() {
        return instrumentVendor;
    }

    public void setInstrumentVendor(String instrumentVendor) {
        this.instrumentVendor = instrumentVendor;
    }

    public String getInstrumentModel() {
        return instrumentModel;
    }

    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }

    public String getInstrumentSN() {
        return instrumentSN;
    }

    public void setInstrumentSN(String instrumentSN) {
        this.instrumentSN = instrumentSN;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSha1Sum() {
        return sha1Sum;
    }

    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }

    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }
    
    public List<MsDigestionEnzyme> getEnzymeList() {
        return enzymeList;
    }

    public void setEnzymeList(List<MsDigestionEnzyme> enzymeList) {
        this.enzymeList = enzymeList;
    }
    
    public void addEnzyme(MsDigestionEnzyme enzyme) {
        enzymeList.add(enzyme);
    }

}
