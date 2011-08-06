/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.parser.unimod.jaxb.ModT;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymeType;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymesType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ModificationParamsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchModificationType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpecificityRulesType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ToleranceType;

/**
 * AnalysisProtocolCollectionMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public class AnalysisProtocolCollectionMaker {

	private final UnimodRepository unimodRepository;
	
	public static final String SEQUEST_PROTOCOL_ID = "sequest_protocol";
	
	public AnalysisProtocolCollectionMaker(UnimodRepository unimodRepository) {
		
		this.unimodRepository = unimodRepository;
	}
	
	public AnalysisProtocolCollectionType getSequestAnalysisProtocol (SequestParamsParser parser) 
			throws MzIdentMlWriterException {
		
		AnalysisProtocolCollectionType protocolColl = new AnalysisProtocolCollectionType();
		
		SpectrumIdentificationProtocolType specIdProtocol = new SpectrumIdentificationProtocolType();
		protocolColl.getSpectrumIdentificationProtocol().add(specIdProtocol);
		
		
		specIdProtocol.setId(SEQUEST_PROTOCOL_ID);
		
		specIdProtocol.setAnalysisSoftwareRef(AnalysisSoftwareMaker.SEQUEST_SW_ID);
		
		ParamType searchType = new ParamType();
		searchType.setCvParam(CvParamMaker.getInstance().make("MS:1001083", "ms-ms search", CvConstants.PSI_CV));
		specIdProtocol.setSearchType(searchType);
		
		
		// modifications
		ModificationParamsType modParams = new ModificationParamsType();
		
		// dynamic modifications
		addDynamicResidueModifications(parser, modParams);
		// static modifications
		addStaticResidueModifications(parser, modParams);
		
		if(modParams.getSearchModification().size() > 0) {
			specIdProtocol.setModificationParams(modParams);
		}
		
		
		// Enzyme
		EnzymesType enzymesType = new EnzymesType();
		specIdProtocol.setEnzymes(enzymesType);
		EnzymeType enzymeType = EnzymeTypeMaker.makeEnzymeType(parser.getSearchEnzyme());
		enzymesType.getEnzyme().add(enzymeType);
		
		// Mass table
		
		// Fragment tolerance
		specIdProtocol.setFragmentTolerance(makeFragmentTolerance(parser));
		
		// Parent tolerance
		specIdProtocol.setParentTolerance(makeParentTolerance(parser));
		
		// threshold
		ParamListType paramList = new ParamListType();
		paramList.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001494", "no threshold", CvConstants.PSI_CV));
		specIdProtocol.setThreshold(paramList);
		
		
		return protocolColl;
		
	}

	private ToleranceType makeParentTolerance(SequestParamsParser parser) throws MzIdentMlWriterException {
		
		double tolerance = -1.0;
		boolean unitIsDaltons = true;
		
		for(Param param: parser.getParamList()) {
			
			if(param.getParamName().equalsIgnoreCase("peptide_mass_tolerance")) {
				tolerance = Double.parseDouble(param.getParamValue());
			}
			
			if(param.getParamName().equalsIgnoreCase("peptide_mass_units")) {
				
				// peptide_mass_units = 0                  ; 0=amu, 1=mmu, 2=ppm
				String val = param.getParamValue();
				if("0".equals(val)) {
					unitIsDaltons = true;
				}
				else if("1".equals(val) || "2".equals(val)) {
					unitIsDaltons = false;
				}
			}
		}
		
		if(tolerance == -1.0) {
			throw new MzIdentMlWriterException("Did not find peptide_mass_tolerance param in sequest.params file");
		}
		
		return makeTolerance(tolerance, unitIsDaltons);
	}

	private ToleranceType makeFragmentTolerance(SequestParamsParser parser) throws MzIdentMlWriterException {
		
		double tolerance = -1.0;
		
		for(Param param: parser.getParamList()) {
			
			if(param.getParamName().equalsIgnoreCase("fragment_ion_tolerance")) {
				tolerance = Double.parseDouble(param.getParamValue());
				break;
			}
		}
		
		if(tolerance == -1.0) {
			throw new MzIdentMlWriterException("Did not find fragment_ion_tolerance param in sequest.params file");
		}
		
		return makeTolerance(tolerance, true);
	}
	
	private ToleranceType makeTolerance(double tolerance, boolean unitIsDalton) {
		
		ToleranceType tolType = new ToleranceType();
		
		// Example: 
		// <cvParam accession="MS:1001412" name="search tolerance plus value" cvRef="PSI-MS" value="1.5" unitAccession="UO:0000221" unitName="dalton" unitCvRef="UO"/>
	    // <cvParam accession="MS:1001413" name="search tolerance minus value" cvRef="PSI-MS" value="1.5" unitAccession="UO:0000221" unitName="dalton" unitCvRef="UO"/>

		CVParamType param = CvParamMaker.getInstance().make("MS:1001412", "search tolerance plus value", String.valueOf(tolerance), CvConstants.PSI_CV);
		param.setUnitCvRef(CvConstants.UNIT_ONTOLOGY_CV.getId());
		tolType.getCvParam().add(param);
		
		if(unitIsDalton) {
			param.setUnitAccession("UO:0000221");
			param.setUnitName("dalton");
		}
		else {
			param.setUnitAccession("UO:0000166");
			param.setUnitName("parts per notation unit");
		}
		return tolType;
	}
	
	private void addDynamicResidueModifications(SequestParamsParser parser,
			ModificationParamsType modParams) throws MzIdentMlWriterException {
		
		List<MsResidueModificationIn> mods = parser.getDynamicResidueMods();
		addResidueModifications(modParams, mods);
	}
	
	private void addStaticResidueModifications(SequestParamsParser parser,
			ModificationParamsType modParams) throws MzIdentMlWriterException {
		
		List<MsResidueModificationIn> mods = parser.getStaticResidueMods();
		addResidueModifications(modParams, mods);
	}

	private void addResidueModifications(ModificationParamsType modParams,
			List<MsResidueModificationIn> mods) throws MzIdentMlWriterException {
		
		
		for(MsResidueModificationIn mod: mods) { 
			
			SearchModificationType modType = new SearchModificationType();
			modParams.getSearchModification().add(modType);
			
			modType.setFixedMod(false);
			modType.setMassDelta(mod.getModificationMass().floatValue());
			modType.getResidues().add(String.valueOf(mod.getModifiedResidue()));
			
			SpecificityRulesType specRuleType = new SpecificityRulesType();
			modType.setSpecificityRules(specRuleType);
			
			// get the Unimod entry for this modification
			try {
				ModT unimod_mod = unimodRepository.getModification(mod.getModifiedResidue(), 
						mod.getModificationMass().doubleValue(), 
						UnimodRepository.DELTA_MASS_TOLERANCE_001, true);

				if(unimod_mod != null) {

					CvParamMaker paramMaker = CvParamMaker.getInstance();
					CVParamType param = paramMaker.make("UNIMOD:" + unimod_mod.getRecordId(), unimod_mod.getTitle(), CvConstants.UNIMOD_CV);

					specRuleType.getCvParam().add(param);
				}
			}
			catch(UnimodRepositoryException e) {
				
				throw new MzIdentMlWriterException("Unimod repository lookup failed for modification  "+
						mod.getModifiedResidue()+" with delta mass: "+mod.getModificationMass(), e);
			}
		}
	}
}
