package org.yeastrc.ms.domain.db;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.IMsRun;


public class MsRun implements IMsRun {

    private int id; // unique id (database) for this run

    private int msExperimentId; // id (database) of the experiment this run belongs to

    // File for this run
    private String fileName; 
    private RunFileFormat fileFormat = RunFileFormat.UNKNOWN;
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

    public RunFileFormat getRunFileFormat() {
        return fileFormat;
    }

    public String getFileFormat() {
        return fileFormat.toString();
    }

    public void setRunFileFormat(String fileFormatStr) {
        this.fileFormat = RunFileFormat.getFileFormatForString(fileFormatStr);
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
