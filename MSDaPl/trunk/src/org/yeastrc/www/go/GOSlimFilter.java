/**
 * 
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * GOSlimFilter.java
 * @author Vagisha Sharma
 * Jul 12, 2010
 * 
 */
public class GOSlimFilter {

	private int slimTermId;
	private int goAspect;
	private boolean exactAnnotation;
	private List<Integer> excludeEvidenceCodes;
	
	public int getSlimTermId() {
		return slimTermId;
	}

	public void setSlimTermId(int slimTermId) {
		this.slimTermId = slimTermId;
	}
	
	public int getGoAspect() {
		return goAspect;
	}
	public void setGoAspect(int goAspect) {
		this.goAspect = goAspect;
	}
	public boolean isExactAnnotation() {
		return exactAnnotation;
	}
	public void setExactAnnotation(boolean exactAnnotation) {
		this.exactAnnotation = exactAnnotation;
	}
	public List<Integer> getExcludeEvidenceCodes() {
		return excludeEvidenceCodes;
	}
	public void setExcludeEvidenceCodes(List<Integer> excludeEvidenceCodes) {
		this.excludeEvidenceCodes = excludeEvidenceCodes;
	}
}
