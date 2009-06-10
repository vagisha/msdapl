package org.yeastrc.ms.upload.dao.run.ms2file;

import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

public interface MS2HeaderUploadDAO {

    public abstract void save(MS2NameValuePair header, int runId);

//    public abstract List<MS2NameValuePair> loadHeadersForRun(int runId);
//    
//    public abstract void deleteHeadersForRunId(int runId);
}