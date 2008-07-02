package org.yeastrc.ms.dto;

import java.util.List;

import org.yeastrc.ms.dto.MsRun.RunFileFormat;

public interface IMsRun {

    /**
     * @return the fileFormat
     */
    public abstract RunFileFormat getRunFileFormat();

    public abstract String getFileFormat();

    /**
     * @param fileFormat the fileFormat to set
     */
    public abstract void setFileFormat(String fileFormatStr);

    /**
     * @return the msExperimentId
     */
    public abstract int getMsExperimentId();

    /**
     * @param msExperimentId the msExperimentId to set
     */
    public abstract void setMsExperimentId(int msExperimentId);

    /**
     * @return the fileName
     */
    public abstract String getFileName();

    /**
     * @param fileName the fileName to set
     */
    public abstract void setFileName(String fileName);

    /**
     * @return the creationDate
     */
    public abstract String getCreationDate();

    /**
     * @param creationDate the creationDate to set
     */
    public abstract void setCreationDate(String creationDate);

    /**
     * @return the conversionSW
     */
    public abstract String getConversionSW();

    /**
     * @param conversionSW the conversionSW to set
     */
    public abstract void setConversionSW(String conversionSW);

    /**
     * @return the conversionSWVersion
     */
    public abstract String getConversionSWVersion();

    /**
     * @param conversionSWVersion the conversionSWVersion to set
     */
    public abstract void setConversionSWVersion(String conversionSWVersion);

    /**
     * @return the conversionSWOptions
     */
    public abstract String getConversionSWOptions();

    /**
     * @param conversionSWOptions the conversionSWOptions to set
     */
    public abstract void setConversionSWOptions(String conversionSWOptions);

    /**
     * @return the instrumentVendor
     */
    public abstract String getInstrumentVendor();

    /**
     * @param instrumentVendor the instrumentVendor to set
     */
    public abstract void setInstrumentVendor(String instrumentVendor);

    /**
     * @return the instrumentModel
     */
    public abstract String getInstrumentModel();

    /**
     * @param instrumentModel the instrumentModel to set
     */
    public abstract void setInstrumentModel(String instrumentModel);

    /**
     * @return the instrumentSN
     */
    public abstract String getInstrumentSN();

    /**
     * @param instrumentSN the instrumentSN to set
     */
    public abstract void setInstrumentSN(String instrumentSN);

    /**
     * @return the comment
     */
    public abstract String getComment();

    /**
     * @param comment the comment to set
     */
    public abstract void setComment(String comment);

    /**
     * @return the id
     */
    public abstract int getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(int id);

    /**
     * @return the sha1Sum
     */
    public abstract String getSha1Sum();

    /**
     * @param sha1Sum the sha1Sum to set
     */
    public abstract void setSha1Sum(String sha1Sum);

    /**
     * @return the dataType
     */
    public abstract String getDataType();

    /**
     * @param dataType the dataType to set
     */
    public abstract void setDataType(String dataType);

    /**
     * @return the acquisitionMethod
     */
    public abstract String getAcquisitionMethod();

    /**
     * @param acquisitionMethod the acquisitionMethod to set
     */
    public abstract void setAcquisitionMethod(String acquisitionMethod);

    /**
     * @return the enzymeList
     */
    public abstract List<MsDigestionEnzyme> getEnzymeList();

    /**
     * @param enzymeList the enzymeList to set
     */
    public abstract void setEnzymeList(List<MsDigestionEnzyme> enzymeList);

    public abstract void addEnzyme(MsDigestionEnzyme enzyme);

}