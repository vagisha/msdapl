/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;

/**
 * CvMaker.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class CvMaker {

	// <cv id="PSI-MS" fullName="Proteomics Standards Initiative Mass Spectrometry Vocabularies" 
	//     uri="http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo" version="2.32.0"/>
	public CvType makePsiCv() {
		
		CvType cv = new CvType();
		cv.setUri("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");
		cv.setId("PSI-MS");
		cv.setVersion("2.32.0");
		cv.setFullName("PSI-MS");
		
		return cv;
	}
	
	// <cv id="UNIMOD" fullName="unimod modifications ontology" uri="http://www.unimod.org/obo/unimod.obo"/>
	public CvType makeUnimodCv() {
		
		CvType cv = new CvType();
		cv.setUri("http://www.unimod.org/obo/unimod.obo");
		cv.setId("UNIMOD");
		cv.setFullName("UNIMOD");
		
		return cv;
	}
	
	// <cv id="UO" fullName="Unit Ontology" uri="http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo"/>
	public CvType makeUnitOntologyCv() {
		
		CvType cv = new CvType();
		cv.setUri("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo");
		cv.setId("UO");
		cv.setFullName("Unit Ontology");
		
		return cv;
	}
}
