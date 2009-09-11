/**
 * DTASelectRunFormBean.java
 * @author Vagisha Sharma
 * Apr 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.yates.YatesRun;

/**
 * 
 */
public class DTASelectRunFormBean extends ProteinferRunFormBean {

    public DTASelectRunFormBean() {} 
    
    public DTASelectRunFormBean(YatesRun dtasRun) {
        super(dtasRun.getProjectID(), 
              dtasRun.getId(), 
              dtasRun.getRunDate(), 
              dtasRun.getComments(), 
              ProteinInferenceProgram.DTA_SELECT);
    }
}
