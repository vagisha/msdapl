/**
 * 
 */
package org.yeastrc.www.compare;

/**
 * ComparisonAction.java
 * @author Vagisha Sharma
 * Jun 4, 2010
 * 
 */
public enum ComparisonCommand {

	FILTER(1, "Filter"),
	CLUSTER(2, "Cluster Spectrum Counts"),
	GO_SLIM(3, "GO Slim Analysis"),
	GO_ENRICH(4, "GO Enrichment Analysis");
	
	private ComparisonCommand(int id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
	private int id;
	private String displayName;
	
	public int getId() {
		return id;
	}
	public String getDisplayName() {
		return displayName;
	}
	
	public static ComparisonCommand forId(int id) {
		for(ComparisonCommand action: ComparisonCommand.values()) {
			if(action.getId() == id)
				return action;
		}
		return null;
	}
}
