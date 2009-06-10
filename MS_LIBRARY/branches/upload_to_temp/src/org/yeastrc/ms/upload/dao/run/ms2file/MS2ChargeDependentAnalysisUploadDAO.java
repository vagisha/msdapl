/**
 * Ms2FileChargeDependentAnalysisDAO.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

/**
 * 
 */
public interface MS2ChargeDependentAnalysisUploadDAO {

    public abstract void save(MS2NameValuePair analysis, int scanChargeId);
    
    public abstract void saveAll(List<MS2ChargeDependentAnalysisWId> analysisList);

}
