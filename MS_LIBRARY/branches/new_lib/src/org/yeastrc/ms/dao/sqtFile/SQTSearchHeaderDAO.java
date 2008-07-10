package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.sqtFile.SQTSearchHeader;

public interface SQTSearchHeaderDAO {

    public abstract List<SQTSearchHeader> loadSQTHeadersForSearch(int searchId);

    public abstract void saveSQTHeader(SQTSearchHeader header);

    public abstract void deleteSQTHeadersForSearch(int searchId);

}