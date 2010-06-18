/**
 * 
 */
package org.yeastrc.www.go;

import org.yeastrc.bio.go.GONode;

/**
 * GONodeAnnotation.java
 * @author Vagisha Sharma
 * Jun 17, 2010
 * 
 */
public class GONodeAnnotation {

	private boolean isExact = false;
	private GONode node;
	
	public GONode getNode() {
		return node;
	}

	public void setNode(GONode node) {
		this.node = node;
	}

	public boolean isExact() {
		return isExact;
	}

	public void setExact(boolean isExact) {
		this.isExact = isExact;
	}
}
