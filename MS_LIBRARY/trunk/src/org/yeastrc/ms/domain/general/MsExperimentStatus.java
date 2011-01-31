/**
 * MsExperimentStatus.java
 * @author Vagisha Sharma
 * Jan 30, 2011
 */
package org.yeastrc.ms.domain.general;

/**
 * 
 */
public interface MsExperimentStatus {

	public abstract int getExperimentId();
	
	public abstract Status getStatus();
	
	public abstract String getLog();
	
}
