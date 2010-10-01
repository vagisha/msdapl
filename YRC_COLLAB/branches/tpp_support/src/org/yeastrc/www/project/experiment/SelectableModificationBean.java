/**
 * SelectableModificationBean.java
 * @author Vagisha Sharma
 * Sep 30, 2010
 */
package org.yeastrc.www.project.experiment;

import org.yeastrc.ms.domain.search.impl.ResidueModificationBean;

/**
 * 
 */
public class SelectableModificationBean extends ResidueModificationBean {

	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
