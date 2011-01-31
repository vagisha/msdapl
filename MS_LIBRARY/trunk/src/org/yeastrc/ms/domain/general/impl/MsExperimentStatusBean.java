/**
 * MsExperimentStatusBean.java
 * @author Vagisha Sharma
 * Jan 30, 2011
 */
package org.yeastrc.ms.domain.general.impl;

import org.yeastrc.ms.domain.general.MsExperimentStatus;
import org.yeastrc.ms.domain.general.Status;

/**
 * 
 */
public class MsExperimentStatusBean implements MsExperimentStatus {

	private int experimentId;
	private Status status;
	private String log;
	
	/* (non-Javadoc)
	 * @see org.yeastrc.ms.domain.general.MsExperimentStatus#getExperimentId()
	 */
	@Override
	public int getExperimentId() {
		return experimentId;
	}

	/* (non-Javadoc)
	 * @see org.yeastrc.ms.domain.general.MsExperimentStatus#getLog()
	 */
	@Override
	public String getLog() {
		return log;
	}

	/* (non-Javadoc)
	 * @see org.yeastrc.ms.domain.general.MsExperimentStatus#getStatus()
	 */
	@Override
	public Status getStatus() {
		return this.status;
	}

	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
