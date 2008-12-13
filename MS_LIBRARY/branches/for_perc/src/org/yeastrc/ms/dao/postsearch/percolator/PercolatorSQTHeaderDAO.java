package org.yeastrc.ms.dao.postsearch.percolator;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

public interface PercolatorSQTHeaderDAO {

    public abstract List<SQTHeaderItem> loadSQTHeadersForPercolatorOutput(int percOutputId);

    public abstract void saveSQTHeader(SQTHeaderItem headerItem, int percOutputId);

    public abstract void deleteSQTHeadersForPercolatorOutput(int percOutputId);
    
}
