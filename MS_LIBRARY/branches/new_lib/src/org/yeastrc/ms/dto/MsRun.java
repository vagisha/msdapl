package org.yeastrc.ms.dto;

import java.util.ArrayList;
import java.util.List;


public class MsRun implements IMsRun {

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

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getRunFileFormat()
     */
    public RunFileFormat getRunFileFormat() {
        return fileFormat;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getFileFormat()
     */
    public String getFileFormat() {
        if (fileFormat == null)
            return null;
        return fileFormat.name();
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setFileFormat(java.lang.String)
     */
    public void setFileFormat(String fileFormatStr) {
        this.fileFormat = getFileFormatForString(fileFormatStr);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getMsExperimentId()
     */
    public int getMsExperimentId() {
        return msExperimentId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setMsExperimentId(int)
     */
    public void setMsExperimentId(int msExperimentId) {
        this.msExperimentId = msExperimentId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getFileName()
     */
    public String getFileName() {
        return fileName;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setFileName(java.lang.String)
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getCreationDate()
     */
    public String getCreationDate() {
        return creationDate;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setCreationDate(java.lang.String)
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getConversionSW()
     */
    public String getConversionSW() {
        return conversionSW;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setConversionSW(java.lang.String)
     */
    public void setConversionSW(String conversionSW) {
        this.conversionSW = conversionSW;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getConversionSWVersion()
     */
    public String getConversionSWVersion() {
        return conversionSWVersion;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setConversionSWVersion(java.lang.String)
     */
    public void setConversionSWVersion(String conversionSWVersion) {
        this.conversionSWVersion = conversionSWVersion;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getConversionSWOptions()
     */
    public String getConversionSWOptions() {
        return conversionSWOptions;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setConversionSWOptions(java.lang.String)
     */
    public void setConversionSWOptions(String conversionSWOptions) {
        this.conversionSWOptions = conversionSWOptions;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getInstrumentVendor()
     */
    public String getInstrumentVendor() {
        return instrumentVendor;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setInstrumentVendor(java.lang.String)
     */
    public void setInstrumentVendor(String instrumentVendor) {
        this.instrumentVendor = instrumentVendor;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getInstrumentModel()
     */
    public String getInstrumentModel() {
        return instrumentModel;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setInstrumentModel(java.lang.String)
     */
    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getInstrumentSN()
     */
    public String getInstrumentSN() {
        return instrumentSN;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setInstrumentSN(java.lang.String)
     */
    public void setInstrumentSN(String instrumentSN) {
        this.instrumentSN = instrumentSN;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getComment()
     */
    public String getComment() {
        return comment;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setComment(java.lang.String)
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getId()
     */
    public int getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setId(int)
     */
    public void setId(int id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getSha1Sum()
     */
    public String getSha1Sum() {
        return sha1Sum;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setSha1Sum(java.lang.String)
     */
    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getDataType()
     */
    public String getDataType() {
        return dataType;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setDataType(java.lang.String)
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getAcquisitionMethod()
     */
    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }


    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setAcquisitionMethod(java.lang.String)
     */
    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#getEnzymeList()
     */
    public List<MsDigestionEnzyme> getEnzymeList() {
        return enzymeList;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#setEnzymeList(java.util.List)
     */
    public void setEnzymeList(List<MsDigestionEnzyme> enzymeList) {
        this.enzymeList = enzymeList;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsRun#addEnzyme(org.yeastrc.ms.dto.MsDigestionEnzyme)
     */
    public void addEnzyme(MsDigestionEnzyme enzyme) {
        enzymeList.add(enzyme);
    }

}
