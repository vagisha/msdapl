/**
 * PeptideProphetRocDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;

/**
 * 
 */
public interface PeptideProphetRocUploadDAO {

    public abstract PeptideProphetROC loadRoc(int analysisId);

    public abstract void saveRoc(PeptideProphetROC roc);
}
