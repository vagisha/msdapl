/**
 * PeptideProphetResultDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;

/**
 * 
 */
public interface PeptideProphetResultUploadDAO {

    public abstract PeptideProphetResult load(int resultId);
    
    public abstract void saveAllPeptideProphetResultData(List<PeptideProphetResultDataWId> dataList);
    
}
