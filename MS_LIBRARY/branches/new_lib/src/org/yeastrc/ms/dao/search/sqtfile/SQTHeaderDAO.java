package org.yeastrc.ms.dao.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

public interface SQTHeaderDAO {

    public abstract List<SQTHeaderDb> loadSQTHeadersForRunSearch(int runSearchId);

    public abstract void saveSQTHeader(SQTField header, int runSearchId);

    public abstract void deleteSQTHeadersForRunSearch(int runSearchId);

}