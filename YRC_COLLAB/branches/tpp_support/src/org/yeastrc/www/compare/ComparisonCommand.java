/**
 * 
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

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
	GO_SLIM_TREE(5, "GO Slim Tree"),
	GO_ENRICH(4, "GO Enrichment Analysis"),
	GO_ENRICH_TREE(6, "Enriched Terms Tree");
	
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
	
	public static List<ComparisonCommand> getCommands() {
		List<ComparisonCommand> commands = new ArrayList<ComparisonCommand>();
		commands.add(FILTER);
		commands.add(CLUSTER);
		commands.add(GO_SLIM);
		commands.add(GO_ENRICH);
		return commands;
	}
	
	public static List<ComparisonCommand> getCommandsMinusGO() {
		List<ComparisonCommand> commands = new ArrayList<ComparisonCommand>();
		commands.add(FILTER);
		commands.add(CLUSTER);
		return commands;
	}
}
