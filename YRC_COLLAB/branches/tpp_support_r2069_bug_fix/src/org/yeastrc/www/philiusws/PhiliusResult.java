/**
 * PhiliusResult.java
 * @author Vagisha Sharma
 * Feb 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.List;

/**
 * 
 */
public class PhiliusResult {

	private PhiliusSequenceAnnotationWS annotation;
	private List<String> coveredSequences;
	
	public PhiliusSequenceAnnotationWS getAnnotation() {
		return annotation;
	}

	public void setAnnotation(PhiliusSequenceAnnotationWS annotation) {
		this.annotation = annotation;
	}

	public List<String> getCoveredSequences() {
		return coveredSequences;
	}

	public void setCoveredSequences(List<String> coveredSequence) {
		this.coveredSequences = coveredSequence;
	}
	 
}
