/**
 * ProteinListingBuilder.java
 * @author Vagisha Sharma
 * Mar 5, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDatabase;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.nrseq.NrProtein;

/**
 * 
 */
public class ProteinListingBuilder {

	private static ProteinListingBuilder instance;
	
	private static final Logger log = Logger.getLogger(ProteinListingBuilder.class.getName());

	private ProteinListingBuilder() {}

	public static ProteinListingBuilder getInstance() {
		if(instance == null)
			instance = new ProteinListingBuilder();
		return instance;
	}

	public ProteinListing build(int nrseqId, List<Integer> fastaDatabaseIds) {
		
		// Get the protein and determine its species
		NrProtein nrProtein = NrSeqLookupUtil.getNrProtein(nrseqId);
		int speciesId = nrProtein.getSpeciesId();
		if(speciesId == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
			
			return build(nrProtein, fastaDatabaseIds, StandardDatabase.SGD);
		}
		else if(speciesId == TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.S_POMBE);
		}
		else if(speciesId == TaxonomyUtils.CAENORHABDITIS_ELEGANS) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.WORMBASE);
		}
		else if(speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.FLYBASE);
		}
		else if(speciesId == TaxonomyUtils.HOMO_SAPIENS) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.HGNC);
		}
		else
			return build(nrProtein, fastaDatabaseIds, null);
		
	}
	
	private ProteinListing build(NrProtein protein, List<Integer> fastaDatabaseIds,
			StandardDatabase sdb) {
		
		int nrseqId = protein.getId();
		ProteinListing listing = new ProteinListing(protein);

		// first get the references for the given fasta databaseIDs
		List<NrDbProtein> matchingProteins = NrSeqLookupUtil.getDbProteins(nrseqId, fastaDatabaseIds);
		for(NrDbProtein prot: matchingProteins) {
			if(prot.isCurrent()) // add only current references
				listing.addFastaReference(new ProteinReference(prot));
		}
		
		// get references to species specific database
		// This will also get any available common names from the tier-one databases
		List<ProteinReference> tierOneRefs = getReferences(protein, sdb);
		for(ProteinReference ref: tierOneRefs)
			listing.addTierOneReference(ref);
		
		// get references to Swiss-Prot
		// We will always try to get Swiss-Prot references for Drosophila proteins
		// since we want to display meaning ful descriptions.  Descriptions in FlyBase are 
		// not very useful.
		List<ProteinReference> tierTwoRefs = null;
		if(tierOneRefs.size() == 0 || protein.getSpeciesId() == TaxonomyUtils.DROSOPHILA_MELANOGASTER) {
			tierTwoRefs = getReferences(protein, StandardDatabase.SWISSPROT);
			for(ProteinReference ref: tierTwoRefs)
				listing.addTierTwoReference(ref);
		}
		
		// get references to NCBI-NR if no tier-one or tier-two references were found
		if(protein.getSpeciesId() != TaxonomyUtils.DROSOPHILA_MELANOGASTER) {
			if(tierOneRefs.size() == 0 && tierTwoRefs.size() == 0) {
				List<ProteinReference> tierThreeRefs = getReferences(protein, StandardDatabase.NCBI_NR);
				for(ProteinReference ref: tierThreeRefs)
					listing.addTierThreeReference(ref);
			}
		}
		else {
			// FlyBase descriptions are not very useful, so if we did not get a SwissProt reference
			// look for NCBI references.
			if(tierTwoRefs.size() == 0) { 
				List<ProteinReference> tierThreeRefs = getReferences(protein, StandardDatabase.NCBI_NR);
				for(ProteinReference ref: tierThreeRefs)
					listing.addTierThreeReference(ref);
			}
		}
		
		
		
		// YRC_NRSEQ incorporates species specific databases: eg. SGD, WORMBASE etc. 
		// In addition, we also have species specific databases external to YRC_NRSEQ. e.g. sgd_static_200709
		// Suppose we want the common name for a yeast protein, the common name lookup will work like this: 
		// 1. Get the accessionString for the protein from YRC_NRSEQ for the database "SGD". 
		// 2. Get the common name for this accession string (systematic name) from sgd_static_200709.
		// 3. If no common names were found it could mean one of two things:
		// 		a. No references were found for this protein in database "SGD" of YRC_NRSEQ
		//      b. The reference found was out-dated and is no longer part of sgd_static_200709 (Will this ever happen??)
		// 4. In case of 3. we will look at the accession strings for the user supplied database IDs (fastaDatabaseIds)
		//    These will typically be the database ID(s) of the fasta file(s) used for a search. 
		// TODO? If we do not find a common name even after 4 we could look at all accession strings associated with this 
		// protein and try to find a matching entry in sgd_static_200709
		
		// We should already have found common references when adding tier-one references
		// If we did not find a common name associated with those references
		// look for common names associated with the fasta file(s) used for the search
		// skip HGNC(HUGO). If there was a common name we would have found it already
		if(listing.getCommonReferences().size() == 0 && sdb != StandardDatabase.HGNC) {
			for(ProteinReference ref: listing.getFastaReferences()) {
				getCommonReference(sdb, ref);
			}
		}
		
		return listing;
	}
		
	
	private List<ProteinReference> getReferences(NrProtein protein, StandardDatabase sdb) {
		
		List<ProteinReference> refs = new ArrayList<ProteinReference>();
		
		if(sdb == null)
			return refs;
		
		NrDatabase db = StandardDatabaseCache.getNrDatabase(sdb);
		
		if(db != null) {
			List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteins(protein.getId(), db.getId());
			for(NrDbProtein prot: proteins) {
				if(prot.isCurrent())  {// add only current references
					ProteinReference ref = new ProteinReference(prot, sdb);

					// if this is a tier one database try to get a common name
					if(sdb.isTierOne()) {
						getCommonReference(sdb, ref); // get a common name/description if one can be found
					}

					refs.add(ref);
				}
			}
		}
		return refs;
	}
	
	private boolean getCommonReference(StandardDatabase db, ProteinReference reference) {
		
		ProteinCommonReference commonRef = null;
		// HGNC common names are in YRC_NRSQ -- accessionString == commonName
		if(db == StandardDatabase.HGNC) {
			
			commonRef = new ProteinCommonReference();
			commonRef.setDatabase(db);
			commonRef.setName(reference.getAccession());
			commonRef.setDescription(reference.getDescription());
			
		}
		else {
			commonRef = CommonNameLookupUtil.getInstance().getCommonReference(reference.getAccession(), 
					db);
		}
		
		if(commonRef != null) {
			reference.setCommonReference(commonRef);
			return true; // we found a common reference
		}
		return false;
	}
	
}
