package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchHeader;

public interface SQTSearchHeaderDAO {

    public abstract List<SQTSearchHeader> loadSQTHeadersForSearch(int searchId);

    public abstract void saveSQTHeader(IHeader header, int searchId);

    public abstract void deleteSQTHeadersForSearch(int searchId);

}