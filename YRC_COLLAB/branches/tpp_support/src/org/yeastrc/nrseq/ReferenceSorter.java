/**
 * ReferenceSorter.java
 * @author Vagisha Sharma
 * Mar 12, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ReferenceSorter {

	private static final Logger log = Logger.getLogger(ReferenceSorter.class.getName());
	
	private ReferenceSorter () {}
	
	public static List<ProteinReference> sort(List<ProteinReference> references, StandardDatabase[] dbOrder) {
	
		List<RankedReference> rRefs = new ArrayList<RankedReference>(references.size());
		
		
		for(ProteinReference ref: references) {
			
			boolean foundMatch = false;
			String dbName = null;
			try {
				dbName = ref.getDatabaseName();
			} catch (SQLException e) {
				log.error("Error getting database name for id: "+ref.getDatabaseId(), e);
			}
			
			if(dbName == null)
				continue;
			
			for(int i = 0; i < dbOrder.length; i++) {
				if(dbName.equals(dbOrder[i].getDatabaseName())) {
					rRefs.add(new RankedReference(ref, i));
					foundMatch = true;
					break;
				}
			}
			if(!foundMatch)
				rRefs.add(new RankedReference(ref, dbOrder.length));
		}
		
		Collections.sort(rRefs, new Comparator<RankedReference>(){
			public int compare(RankedReference o1, RankedReference o2) {
				if(o1.rank < o2.rank)	return -1;
				if(o1.rank > o2.rank)	return 1;
				return 0;
			}});
		
		List<ProteinReference> sortedRefs = new ArrayList<ProteinReference>(references.size());
		for(RankedReference rr: rRefs)
			sortedRefs.add(rr.ref);
		
		return sortedRefs;
	}
	
	private static class RankedReference {
		
		private ProteinReference ref;
		private int rank = Integer.MAX_VALUE;
		public RankedReference(ProteinReference ref, int rank) {
			this.ref = ref;
			this.rank = rank;
		}
	}
}
