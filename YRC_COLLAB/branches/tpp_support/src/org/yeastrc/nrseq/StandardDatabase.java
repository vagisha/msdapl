package org.yeastrc.nrseq;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;

public enum StandardDatabase {

	SGD ("SGD"),
	S_POMBE("Sanger Pombe"),
	WORMBASE("WormBase"),
	FLYBASE("FlyBase"),
	HGNC ("HGNC (HUGO)"),
	NCBI_NR("NCBI NR"),
	SWISSPROT("Swiss-Prot");
	
	
	private String name;
	private StandardDatabase(String name) {
		this.name = name;
	}
	
	public String getDatabaseName() {
		return name;
	}
	
	public static StandardDatabase getStandardDatabaseForSpecies(int speciesId) {
		
		switch (speciesId) {
			case TaxonomyUtils.SACCHAROMYCES_CEREVISIAE: 	return SGD;
			case TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE: 	return S_POMBE;
			case TaxonomyUtils.CAENORHABDITIS_ELEGANS: 		return WORMBASE;
			case TaxonomyUtils.DROSOPHILA_MELANOGASTER: 	return FLYBASE;
			case TaxonomyUtils.HOMO_SAPIENS:				return HGNC;
			default: 										return null;
		}
	}
	
	public static boolean isStandardDatabase(String name) {
		
		for(StandardDatabase db: StandardDatabase.values()) {
			if(db.getDatabaseName().equals(name))
				return true;
		}
		return false;
	}
}
