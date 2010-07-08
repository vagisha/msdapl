/**
 * DescriptionDisplayOrder.java
 * @author Vagisha Sharma
 * Mar 12, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;

/**
 * 
 */
public class DescriptionOrder {

	private DescriptionOrder () {}
	
	public static StandardDatabase[] getOrder(int speciesId) {
		switch (speciesId) {
		case TaxonomyUtils.SACCHAROMYCES_CEREVISIAE:
			return new StandardDatabase[] {StandardDatabase.SGD, StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		case TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE:
			return new StandardDatabase[] {StandardDatabase.S_POMBE, StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		case TaxonomyUtils.CAENORHABDITIS_ELEGANS:
			return new StandardDatabase[] {StandardDatabase.WORMBASE, StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		case TaxonomyUtils.DROSOPHILA_MELANOGASTER:
			return new StandardDatabase[] {StandardDatabase.FLYBASE, StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		case TaxonomyUtils.HOMO_SAPIENS:
			return new StandardDatabase[] {StandardDatabase.HGNC, StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		default:
			return new StandardDatabase[] {StandardDatabase.SWISSPROT, StandardDatabase.NCBI_NR};
		}
	}
}
