package org.yeastrc.nrseq;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;

public enum StandardDatabase {

	SGD ("SGD", 1, TaxonomyUtils.SACCHAROMYCES_CEREVISIAE),
	S_POMBE("Sanger Pombe", 1, TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE),
	WORMBASE("WormBase", 1, TaxonomyUtils.CAENORHABDITIS_ELEGANS),
	FLYBASE("FlyBase", 1, TaxonomyUtils.DROSOPHILA_MELANOGASTER),
	HGNC ("HGNC (HUGO)", 1, TaxonomyUtils.HOMO_SAPIENS),
	SWISSPROT("Swiss-Prot", 2, 0),
	NCBI_NR("NCBI NR", 3, 0);
	
	
	private String name;
	private int taxonomyId;
	private int tier;
	
	private StandardDatabase(String name, int tier, int taxonomyId) {
		this.name = name;
		this.tier = tier;
		this.taxonomyId = taxonomyId;
	}
	
	public String getDatabaseName() {
		return name;
	}
	
	public int getTaxonomyId() {
		return taxonomyId;
	}
	
	public boolean isTierOne() {
		return tier == 1;
	}
	
	public boolean isTierTwo() {
		return tier == 2;
	}
	
	public boolean isTierThree() {
		return tier == 3;
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
	
	public static List<StandardDatabase> getTierTwoDatabases() {
		List<StandardDatabase> dbs = new ArrayList<StandardDatabase>();
		for(StandardDatabase db: StandardDatabase.values()) {
			if(db.isTierTwo()) 
				dbs.add(db);
		}
		return dbs;
	}
	
	public static List<StandardDatabase> getTierThreeDatabases() {
		List<StandardDatabase> dbs = new ArrayList<StandardDatabase>();
		for(StandardDatabase db: StandardDatabase.values()) {
			if(db.isTierThree()) 
				dbs.add(db);
		}
		return dbs;
	}
}
