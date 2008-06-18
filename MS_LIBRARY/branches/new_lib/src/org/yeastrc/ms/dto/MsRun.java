package org.yeastrc.ms.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MsRun {

    public static enum RunFileFormat {MS2, MZXML, MZDATA, MZML};
    
    private int id; // unique id (database) for this run
    
    private int msExperimentId; // id (database) of the experiment this run belongs to
    
    // File for this run
    private String fileName; 
    private RunFileFormat fileFormat;
    private String creationDate;
    
    // conversion software
    private String conversionSW; // software used to convert the RAW file
    private String conversionSWVersion; // version of the conversion software used
    private String conversionSWOptions; // options used for conversion
    
    // acquisition instrument
    private String instrumentVendor;
    private String instrumentModel;
    private String instrumentSN; // serial number of the instrument
    private String fragmentationType; 
    
    private String comment;
    
    private List<MsScan> scanList; // list of scans in this run
    
    public MsRun() {
        scanList = new ArrayList<MsScan>();
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
        else return null;
    }

    /**
     * @return the fileFormat
     */
    public RunFileFormat getRunFileFormat() {
        return fileFormat;
    }

    public String getFileFormat() {
        return fileFormat.name();
    }
    
    /**
     * @param fileFormat the fileFormat to set
     */
    public void setFileFormat(String fileFormatStr) {
        this.fileFormat = getFileFormatForString(fileFormatStr);
    }

    /**
     * @return the msExperimentId
     */
    public int getMsExperimentId() {
        return msExperimentId;
    }

    /**
     * @param msExperimentId the msExperimentId to set
     */
    public void setMsExperimentId(int msExperimentId) {
        this.msExperimentId = msExperimentId;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the conversionSW
     */
    public String getConversionSW() {
        return conversionSW;
    }

    /**
     * @param conversionSW the conversionSW to set
     */
    public void setConversionSW(String conversionSW) {
        this.conversionSW = conversionSW;
    }


    /**
     * @return the conversionSWVersion
     */
    public String getConversionSWVersion() {
        return conversionSWVersion;
    }


    /**
     * @param conversionSWVersion the conversionSWVersion to set
     */
    public void setConversionSWVersion(String conversionSWVersion) {
        this.conversionSWVersion = conversionSWVersion;
    }

    /**
     * @return the conversionSWOptions
     */
    public String getConversionSWOptions() {
        return conversionSWOptions;
    }

    /**
     * @param conversionSWOptions the conversionSWOptions to set
     */
    public void setConversionSWOptions(String conversionSWOptions) {
        this.conversionSWOptions = conversionSWOptions;
    }

    /**
     * @return the instrumentVendor
     */
    public String getInstrumentVendor() {
        return instrumentVendor;
    }

    /**
     * @param instrumentVendor the instrumentVendor to set
     */
    public void setInstrumentVendor(String instrumentVendor) {
        this.instrumentVendor = instrumentVendor;
    }

    /**
     * @return the instrumentModel
     */
    public String getInstrumentModel() {
        return instrumentModel;
    }

    /**
     * @param instrumentModel the instrumentModel to set
     */
    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }

    /**
     * @return the instrumentSN
     */
    public String getInstrumentSN() {
        return instrumentSN;
    }

    /**
     * @param instrumentSN the instrumentSN to set
     */
    public void setInstrumentSN(String instrumentSN) {
        this.instrumentSN = instrumentSN;
    }

    /**
     * @return the fragmentationType
     */
    public String getFragmentationType() {
        return fragmentationType;
    }

    /**
     * @param fragmentationType the fragmentationType to set
     */
    public void setFragmentationType(String fragmentationType) {
        this.fragmentationType = fragmentationType;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the scanList
     */
    public List<MsScan> getScanList() {
        return scanList;
    }

    /**
     * @param scanList the scanList to set
     */
    public void setScanList(List<MsScan> scanList) {
        this.scanList = scanList;
    }
    
    /**
     * @return the number of scans in this run
     */
    public int getScanCount() {
        return scanList.size();
    }
    
    /**
     * @return {@link Iterator} for the scan list in this run
     */
    public Iterator<MsScan> getScanIterator() {
        return scanList.iterator();
    }
}
