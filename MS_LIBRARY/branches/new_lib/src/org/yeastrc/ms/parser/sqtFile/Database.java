package org.yeastrc.ms.parser.sqtFile;

import org.yeastrc.ms.domain.search.MsSearchDatabase;

public class Database implements MsSearchDatabase {
    
    private String serverPath;
    private String serverAddress;
    private long sequenceLength = 0;
    private int proteinCount = 0;
    

    public int getProteinCount() {
        return proteinCount;
    }

    public long getSequenceLength() {
        return sequenceLength;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getServerPath() {
       return serverPath;
    }

    public void setServerPath(String filePath) {
        this.serverPath = filePath;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setSequenceLength(long sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public void setProteinCount(int locusCount) {
        this.proteinCount = locusCount;
    }
}

