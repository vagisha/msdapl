/**
 * GOSupportChecker.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;

/**
 * 
 */
public class GOSupportChecker {

	private GOSupportChecker() {}
	
	public static boolean isSupported(int pinferId) {
		
		List<Integer> speciesIds = ProteinInferToSpeciesMapper.map(pinferId);
		for(int speciesId: speciesIds) {
			if(speciesId == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE)
				return true;
		}
		return false;
	}
}
