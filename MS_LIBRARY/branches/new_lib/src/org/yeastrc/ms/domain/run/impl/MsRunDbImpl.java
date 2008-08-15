package org.yeastrc.ms.domain.run.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.RunFileFormat;


public class MsRunDbImpl implements MsRunDb {

    private int id; // unique id (database) for this run

    // File for this run
    private String fileName; 
    private RunFileFormat fileFormat = RunFileFormat.UNKNOWN;
    private String sha1Sum;
    private String creationDate;

    // conversion software
    private String conversionSW; // software used to convert the RAW file
    private String conversionSWVersion; // version of the conversion software used
    private String conversionSWOptions; // options used for conversion
    
    // acquisition instrument
    private String instrumentVendor;
    private String instrumentModel;
    private String instrumentSN; // serial number of the instrument
    private String acquisitionMethod;


    private String comment;

    private List <MsEnzymeDb> enzymeList;

    public MsRunDbImpl() {
        enzymeList = new ArrayList<MsEnzymeDb>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    //---------------------------------------------------------------------------------------
    // File format
    //---------------------------------------------------------------------------------------
    public RunFileFormat getRunFileFormat() {
        return fileFormat;
    }

    public void setRunFileFormat(RunFileFormat format) {
        this.fileFormat = format;
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

    public String getSha1Sum() {
        return sha1Sum;
    }

    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }

    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }

    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }

    public List<MsEnzymeDb> getEnzymeList() {
        return enzymeList; 
    }

    public void addEnzyme(MsEnzymeDb enzyme) {
        enzymeList.add(enzyme);
    }
    
    public void  setEnzymeList(List<? extends MsEnzymeDb> enzymeList) {
        this.enzymeList = (List<MsEnzymeDb>) enzymeList;
    }
}
