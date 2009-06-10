package org.yeastrc.ms.upload.dao.search.sqtfile;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

public interface SQTHeaderUploadDAO {

    public abstract void saveSQTHeader(SQTHeaderItem headerItem);

}