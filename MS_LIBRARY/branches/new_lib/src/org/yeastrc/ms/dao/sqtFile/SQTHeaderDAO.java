package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.sqtFile.SQTField;
import org.yeastrc.ms.domain.sqtFile.SQTHeaderDb;

public interface SQTHeaderDAO {

    public abstract List<SQTHeaderDb> loadSQTHeadersForSearch(int searchId);

    public abstract void saveSQTHeader(SQTField header, int searchId);

    public abstract void deleteSQTHeadersForSearch(int searchId);

}