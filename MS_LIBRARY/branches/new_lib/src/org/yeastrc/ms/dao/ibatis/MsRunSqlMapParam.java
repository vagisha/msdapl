package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsRun;

public class MsRunSqlMapParam implements MsRun {

    private int experimentId;
    private MsRun run;
    
    public MsRunSqlMapParam(int experimentId, MsRun run) {
        this.experimentId = experimentId;
        this.run = run;
    }
    
    /**
     * @return the experimentId
     */
    public int getExperimentId() {
        return experimentId;
    }

    public List<? extends MsEnzyme> getEnzymeList() {
        return run.getEnzymeList();
    }

    public String getAcquisitionMethod() {
        return run.getAcquisitionMethod();
    }

    public String getComment() {
        return run.getComment();
    }

    public String getConversionSW() {
        return run.getConversionSW();
    }

    public String getConversionSWOptions() {
        return run.getConversionSWOptions();
    }

    public String getConversionSWVersion() {
        return run.getConversionSWVersion();
    }

    public String getCreationDate() {
        return run.getCreationDate();
    }

    public String getDataType() {
        return run.getDataType();
    }

    public String getFileName() {
        return run.getFileName();
    }

    public String getInstrumentModel() {
        return run.getInstrumentModel();
    }

    public String getInstrumentSN() {
        return run.getInstrumentSN();
    }

    public String getInstrumentVendor() {
        return run.getInstrumentVendor();
    }

    public RunFileFormat getRunFileFormat() {
        return run.getRunFileFormat();
    }

    public String getSha1Sum() {
        return run.getSha1Sum();
    }
}
