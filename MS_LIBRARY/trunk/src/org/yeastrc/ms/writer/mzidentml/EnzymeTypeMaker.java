/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymeType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamListType;

/**
 * EnzymeTypeMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public class EnzymeTypeMaker {

	/*
	 * From sequest.params:  https://proteomicsresource.washington.edu/sequest_release/release_201101.php
	 * 
	 [SEQUEST_ENZYME_INFO]
	 0.  No_Enzyme              0      -           -
	 1.  Trypsin                1      KR          P
	 2.  Chymotrypsin           1      FWY         P
	 3.  Clostripain            1      R           -
	 4.  Cyanogen_Bromide       1      M           -
	 5.  IodosoBenzoate         1      W           -
	 6.  Proline_Endopept       1      P           -
	 7.  Staph_Protease         1      E           -
	 8.  Trypsin_K              1      K           P
	 9.  Trypsin_R              1      R           P
	 10. AspN                   0      D           -
	 11. Cymotryp/Modified      1      FWYL        P
	 12. Elastase               1      ALIV        P
	 13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P
	 14. Trypsin                1      KRD         -

	 * 
	 */
	
	/*
	 
	  08/04/11
	  These are the enzymes currently in MSDaPl's database:
	  +----+-------------------+-------+------+-------+-------------+
	  | id | name              | sense | cut  | nocut | description |
      +----+-------------------+-------+------+-------+-------------+
      |  1 | Trypsin           |     1 | KR   | P     | NULL        |
      |  2 | LysC              |     1 | K    | P     | NULL        |
      |  3 | Elastase          |     1 | ALIV | P     | NULL        |
      |  4 | Cymotryp/Modified |     1 | FWYL | P     | NULL        |
      |  5 | LysC              |     1 | K    | -     | NULL        |
      +----+-------------------+-------+------+-------+-------------+

	 
	 */
	
	/**
	 * Only some enzymes are supported.  
	 */
	public static EnzymeType makeEnzymeType(MsEnzymeIn enzyme) {
		
		
		// !----------------------------------------------------------
		// TODO This method should be tested and other enzymes should be supported.
		// !----------------------------------------------------------
		
		if (enzyme == null) {
			return makeNoEnzyme();
		}
		
		// found entries for these in the controlled vocabulary:
		if(enzyme.getName().equalsIgnoreCase("Trypsin")) {
			return makeTrypsin();
		}
		else if(enzyme.getName().equalsIgnoreCase("Cyanogen_Bromide")) {
			return makeCyanogenBromide();
		}
		else if(enzyme.getName().equalsIgnoreCase("Trypsin_K") ||
				enzyme.getName().equalsIgnoreCase("LysC")) {
			
			if(enzyme.getNocut().equalsIgnoreCase("P"))
				return makeLysC_OR_TrypsinK_nocutP();
			else
				return makeLysC_OR_TrypsinK();
		}
		else if(enzyme.getName().equalsIgnoreCase("Trypsin_R")) {
			return makeArgC_OR_TrypsinR();
		}
		else if(enzyme.getName().equalsIgnoreCase("Cymotryp/Modified")) {
			return makeModifiedChymotrypsin();
		}
		
		
		// could not find and entry for these:
		else if(enzyme.getName().equalsIgnoreCase("Chymotrypsin")) {
			return null;  // NOTE: can't find a term for this in PSI-MS vocabulary
			              // There is an entry for Chymotrypsin but the given regex does not match
			              // the "cut" residues in sequest.params
		}
		else if(enzyme.getName().equalsIgnoreCase("Clostripain")) {
			return null;  // NOTE: can't find a term for this in PSI-MS vocabulary
		}
		
		else if(enzyme.getName().equalsIgnoreCase("IodosoBenzoate")) {
			return null;  // NOTE: can't find a term for this in PSI-MS vocabulary
		}
		else if(enzyme.getName().equalsIgnoreCase("Staph_Protease")) {
			return null;  // NOTE: can't find a term for this in PSI-MS vocabulary
		}
		
		
		return null;
	}
	
	public static EnzymeType makeNoEnzyme() {
		
		EnzymeType enzymeType = new EnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001091", "NoEnzyme", CvConstants.PSI_CV));
		return enzymeType;
	}
	
	public static EnzymeType makeTrypsin() {
		
		// 1.  Trypsin                1      KR          P
		
		/*
		 	[Term]
			id: MS:1001251
			name: Trypsin
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001176 ! (?<=[KR])(?!P)
		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001251", "Trypsin", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeModifiedChymotrypsin() {
		
		/*
		 	[Term]
			id: MS:1001306
			name: Chymotrypsin
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001332 ! (?<=[FYWL])(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001306", "Chymotrypsin", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeCyanogenBromide() {
		
		// 4.  Cyanogen_Bromide       1      M           -
		/*
		 	[Term]
			id: MS:1001307
			name: CNBr
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001333 ! (?<=M)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001307", "CNBr", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeLysC_OR_TrypsinK_nocutP() {
		
		// 8.  Trypsin_K              1      K           P
		/*
		 	[Term]
			id: MS:1001309
			name: Lys-C
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001335 ! (?<=K)(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001309", "Lys-C", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeLysC_OR_TrypsinK() {
		
		/*
		 	[Term]
			id: MS:1001310
			name: Lys-C/P
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001336 ! (?<=K)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001310", "Lys-C/P", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeArgC_OR_TrypsinR() {
		
		// 9.  Trypsin_R              1      R           P
		/*
		 	[Term]
			id: MS:1001303
			name: Arg-C
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001272 ! (?<=R)(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001303", "Arg-C", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	private static EnzymeType makeEnzymeType() {
		
		EnzymeType enzymeType = new EnzymeType();
		enzymeType.setCTermGain("OH");
		enzymeType.setNTermGain("H");
		enzymeType.setMinDistance(1);
		return enzymeType;
	}
}
