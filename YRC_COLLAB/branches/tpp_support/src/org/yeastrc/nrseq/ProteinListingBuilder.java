/**
 * ProteinListingBuilder.java
 * @author Vagisha Sharma
 * Mar 5, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.SQLException;
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
			
			return build(nrProtein, fastaDatabaseIds, StandardDatabase.SGD, 
													  StandardDatabase.SWISSPROT,
													  StandardDatabase.NCBI_NR);
		}
		else if(speciesId == TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.S_POMBE, 
					  StandardDatabase.SWISSPROT,
					  StandardDatabase.NCBI_NR);
		}
		else if(speciesId == TaxonomyUtils.CAENORHABDITIS_ELEGANS) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.WORMBASE, 
					  StandardDatabase.SWISSPROT,
					  StandardDatabase.NCBI_NR);
		}
		else if(speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.FLYBASE, 
					  StandardDatabase.SWISSPROT,
					  StandardDatabase.NCBI_NR);
		}
		else if(speciesId == TaxonomyUtils.HOMO_SAPIENS) {

			return build(nrProtein, fastaDatabaseIds, StandardDatabase.HGNC, 
					  StandardDatabase.SWISSPROT,
					  StandardDatabase.NCBI_NR);
		}
		else
			return build(nrProtein, fastaDatabaseIds, StandardDatabase.SWISSPROT,
					  StandardDatabase.NCBI_NR);
		
	}
	
	private ProteinListing build(NrProtein protein, List<Integer> fastaDatabaseIds, 
			StandardDatabase...sdbList) {
		
		int nrseqId = protein.getId();
		ProteinListing listing = new ProteinListing(nrseqId, protein.getSpeciesId());

		// first get the references for the given fasta databaseIDs
		List<NrDbProtein> matchingProteins = NrSeqLookupUtil.getDbProteins(nrseqId, fastaDatabaseIds);
		for(NrDbProtein prot: matchingProteins) {
			if(prot.isCurrent()) // add only current references
				listing.addReference(new ProteinReference(prot));
		}
		
		// now do a lookup for the given standard databases
		NrseqDatabaseDAO dbDao = NrseqDatabaseDAO.getInstance();
		for(StandardDatabase sdb: sdbList) {
			NrDatabase db = null;
			try {
				db = dbDao.getDatabase(sdb.getDatabaseName());
			} catch (SQLException e) {
				log.error("Lookup of standard database "+sdb.getDatabaseName()+" failed", e);
			}
			if(db != null) {
				matchingProteins = NrSeqLookupUtil.getDbProteins(nrseqId, db.getId());
				for(NrDbProtein prot: matchingProteins) {
					if(prot.isCurrent()) // add only current references
						listing.addReference(new ProteinReference(prot));
				}
			}
		}
		
		
		// Get the appropriate common name and description from species specific database
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
		StandardDatabase db = StandardDatabase.getStandardDatabaseForSpecies(protein.getSpeciesId());
		if(db != null) {
			
			
			List<ProteinCommonReference> commonRefs = null;
			// TODO This is temporary till I figure out how HGNC lookups should work
			if(db == StandardDatabase.HGNC) {
				List<ProteinReference> refs = null;
				try {
					refs = listing.getReferencesForDatabase(db.getDatabaseName());
				} catch (SQLException e) {
					log.error("Error getting references for database: "+db.getDatabaseName(), e);
				}
				if(refs != null) {
					for(ProteinReference ref: refs) {
						ProteinCommonReference cref = new ProteinCommonReference();
						cref.setDatabase(db);
						cref.setName(ref.getAccession());
						cref.setDescription(ref.getDescription());
						listing.addCommonReference(cref);
					}
				}
			}
			else {
				// Get the accession strings from table: YRC_NRSEQ.tblProteinDatabase for our species specific database name.
				List<String> accessions = null;
				try {
					accessions = listing.getAccessionsForDatabase(db.getDatabaseName());
				} catch (SQLException e) {
					log.error("Error getting accesions for database: "+db.getDatabaseName(), e);
				}
				commonRefs = CommonNameLookupUtil.getInstance().getCommonReferences(accessions, protein.getSpeciesId());
				// If we did not find a common name based on the accessions above look for a match with other accession we have for this protein.
				if(commonRefs.size() == 0) {
					try {
						accessions = listing.getAccessionsForNotStandardDatabases();
					} catch (SQLException e) {
						log.error("Error getting accesions for non standard databases: "+db.getDatabaseName(), e);
					}
					commonRefs = CommonNameLookupUtil.getInstance().getCommonReferences(accessions, protein.getSpeciesId());
				}
			}
			
			if(commonRefs != null) {
				for(ProteinCommonReference ref: commonRefs)
					listing.addCommonReference(ref);
			}
		}
		
		return listing;
	}
	
}
