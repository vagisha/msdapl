/* ProgressReportGenerator.java
 * Created on May 19, 2004
 */
package org.yeastrc.progress;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.*;

import org.w3c.dom.*;
//JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*; 

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.yeastrc.project.Project;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.project.Training;
//import org.yeastrc.www.user.Groups;

/**
 * This class holds methods pertaining to the annual NCRR progress report.
 * This class will produce the annual report in XML form from the database.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.1, June 2006
 *
 */
public class ProgressReportGenerator {

	/**
	 * Constructor
	 */
	public ProgressReportGenerator() {
		this.startDate = null;
		this.endDate = null;
		this.researchers = null;
		this.projects = null;
		this.document = null;
		this.XML = null;
		this.hostOrganization = "University of Washington";
	}

	/**
	 * Runs the report.
	 * @throws Exception
	 */
	public void runReport() throws Exception {
		this.loadProperties();
		this.generateDOM();
		this.generateXML();
	}

	/**
	 * Generates a DOM for an eventual XML document based on the date parameters
	 * supplied and the Projects in the database, based on the scheme for a
	 * P41 Annual Progress Report for NCRR
	 * 
	 * @throws Exception
	 */
	private void generateDOM() throws Exception {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
		DocumentBuilder db = dbf.newDocumentBuilder ();
		Document doc = db.newDocument ();

		// First non comment line to look like:
		// <P41_Progress_Report xmlns="http://sis.ncrr.nih.gov" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://sis.ncrr.nih.gov http://aprsis.ncrr.nih.gov/XML/p41_progress_report.xsd">
		
		Element root = doc.createElement ("P41_Progress_Report");
		root.setAttribute( "xmlns", "http://sis.ncrr.nih.gov" );
		root.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
		root.setAttribute( "xsi:schemaLocation", "http://sis.ncrr.nih.gov http://aprsis.ncrr.nih.gov/XML/p41_progress_report.xsd" );
		
		doc.appendChild (root);
		
		root.appendChild(doc.createComment("Built by Java libraries written by Michael Riffle (mriffle@u.washington.edu)"));
		root.appendChild(doc.createComment("Run on: " + new Date()));
		root.appendChild(doc.createComment("Start date: " + this.startDate));
		root.appendChild(doc.createComment("End date: " + this.endDate));

		// Generate and add the Grant_Info element
		this.generateGrantInfo(doc, root);

		// Generate and add the Progress_Summary element
		//this.generateProgressSummary(doc, root);

		// Generate the Highlight elements
		//this.generateHighlights(doc, root);
		
		// Generate and add the Person elements
		this.generatePersons(doc, root);
		
		// Generate and add the Subproject elements
		this.generateSubprojects(doc, root);
		
		// we don't do this any more... there is a project in the database that summarizes these now - 2007-06-18
		//this.generatePlasmidSubprojects(doc, root);
		//this.generateQuestionSubprojects(doc, root);
		
		// Generate the Publication elements
		this.generatePublications(doc, root);

		// Generate the Research_Risk element and sub elements
		this.generateResearchRisk(doc, root);
		
		this.document = doc;
	}
	
	/**
	 * Generate the Research_Risk element and sub elements
	 * @param doc
	 * @param root
	 */
	private void generateResearchRisk(Document doc, Element root) {
		Element researchRisk = doc.createElement("Research_Risk");
		Element elem;
		
		elem = doc.createElement("Human_Subjects_Involved");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);

		elem = doc.createElement("Human_Subjects_IRB_Approval");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		elem = doc.createElement("Human_Subjects_Training");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		elem = doc.createElement("DS_Monitoring_Plan");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		elem = doc.createElement("Gender_Minority_Req");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		elem = doc.createElement("Vertebrate_Animals_Used");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		elem = doc.createElement("IACUC_Approval");
		elem.appendChild(doc.createTextNode("N"));
		researchRisk.appendChild(elem);
		
		// Append this node to the document
		root.appendChild(researchRisk);
		root.insertBefore(doc.createTextNode("\n"), researchRisk);
	}
	
	/**
	 * Generate the Grant_Info element of the progress report
	 * @param doc
	 * @param root
	 */
	private void generateGrantInfo(Document doc, Element root) {
		Element grantInfo = doc.createElement("Grant_Info");
		Element elem;
		
		elem = doc.createElement("Serial_Number");
		elem.appendChild(doc.createTextNode(this.grantSerialNumber));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Fiscal_Year");
		elem.appendChild(doc.createTextNode(this.grantSupportYear));
		grantInfo.appendChild(elem);		
		
		elem = doc.createElement("Reporting_To_Date");
		elem.appendChild(doc.createTextNode(this.grantToDate));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Reporting_From_Date");
		elem.appendChild(doc.createTextNode(this.grantFromDate));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Project_Title");
		elem.appendChild(doc.createTextNode(this.grantTitle));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Recipient_Institution_Name");
		elem.appendChild(doc.createTextNode(this.grantRecipientInstitution));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Activity_Code");
		elem.appendChild(doc.createTextNode("P41"));
		grantInfo.appendChild(elem);
		
		/*
		elem = doc.createElement("Director_Name");
		elem.appendChild(doc.createTextNode(this.grantDirectorName));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Director_Academic_Title");
		elem.appendChild(doc.createTextNode(this.grantDirectorTitle));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Director_Phone_Number");
		elem.appendChild(doc.createTextNode(this.grantDirectorPhone));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Director_Fax_Number");
		elem.appendChild(doc.createTextNode(this.grantDirectorFax));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Director_Email_Address");
		elem.appendChild(doc.createTextNode(this.grantDirectorEmail));
		grantInfo.appendChild(elem);
		*/
		
		// new element for 2008
		elem = doc.createElement("Director_Person_ID");
		elem.appendChild(doc.createTextNode( String.valueOf( this.directorPersonID) ) );
		grantInfo.appendChild(elem);

		elem = doc.createElement("Health_Professional_School_Name");
		elem.appendChild(doc.createTextNode(this.grantHealthSchoolName));
		grantInfo.appendChild(elem);

		elem = doc.createElement("Patent_Or_Copyright_Award");
		elem.appendChild(doc.createTextNode(this.grantPatent));
		grantInfo.appendChild(elem);

		// Append this node to the document
		root.appendChild(grantInfo);
		root.insertBefore(doc.createTextNode("\n"), grantInfo);
	}

	/**
	 * Generate the Progress_Summary element of the progress report
	 * @param doc
	 * @param root
	 */
	private void generateProgressSummary(Document doc, Element root) {
		Element progressSummary = doc.createElement("Progress_Summary");
		Element body = doc.createElement("Body");
		
		
		body.setAttribute("URL", this.progressURL);
		progressSummary.appendChild(body);
		
		// Append this node to the document
		root.appendChild(progressSummary);
		root.insertBefore(doc.createTextNode("\n"), progressSummary);
	}

	/**
	 * Generate the Highlight elements of the progress report
	 * @param doc
	 * @param root
	 */
	private void generateHighlights(Document doc, Element root) throws Exception {
		List highlights = HighlightUtils.getAllHighlights();
		int year = this.endDate.getYear() + 1900;
		
		Iterator iter = highlights.iterator();
		while (iter.hasNext()) {
			Highlight hl = (Highlight)(iter.next());
			if (hl.getYear() != year) continue;
			
			Element highlight = doc.createElement("Highlight");
			
			Element elem = doc.createElement("Title");
			String tmpStr = hl.getTitle();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 200) tmpStr = tmpStr.substring(0,199);
			elem.appendChild(doc.createTextNode(tmpStr));
			highlight.appendChild(elem);
			highlight.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("Subproject_ID");
			tmpStr = String.valueOf(hl.getProjectID());
			if (tmpStr == null) tmpStr = "";
			elem.appendChild(doc.createTextNode(tmpStr));
			highlight.appendChild(elem);
			highlight.insertBefore(doc.createTextNode("\n"), elem);
			
			elem = doc.createElement("Body");
			tmpStr = hl.getBody();
			if (tmpStr == null) tmpStr = "";
			elem.appendChild(doc.createTextNode(tmpStr));
			highlight.appendChild(elem);
			highlight.insertBefore(doc.createTextNode("\n"), elem);
			
			root.appendChild(highlight);
			root.insertBefore(doc.createTextNode("\n"), highlight);
		}
	}

	/**
	 * Generates the Publication elements of the progress report
	 * @param doc
	 * @param root
	 * @throws Exception
	 */
	private void generatePublications(Document doc, Element root) throws Exception {
		List publications = PublicationUtils.getAllPublications();
		//int year = this.endDate.getYear() + 1900;
		
		Iterator iter = publications.iterator();
		while (iter.hasNext()) {
			Publication p = (Publication)(iter.next());
			//if (p.getReportYear() != year) continue;
	
			Element publication = doc.createElement("Publication");
			publication.setAttribute("Publication_ID", String.valueOf(p.getId()));
			
			Element elem = doc.createElement("Publication_Type");
			String tmpStr = p.getType();
			if (tmpStr == null) tmpStr = "J";
			elem.appendChild(doc.createTextNode(tmpStr));
			publication.appendChild(elem);
			publication.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("In_Press");
			tmpStr = p.getInPress();
			if (tmpStr == null) tmpStr = "N";
			elem.appendChild(doc.createTextNode(tmpStr));
			publication.appendChild(elem);
			publication.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("Resource_Acknowledged");
			tmpStr = p.getYrcAcknowledged();
			if (tmpStr == null) tmpStr = "Y";
			elem.appendChild(doc.createTextNode(tmpStr));
			publication.appendChild(elem);
			publication.insertBefore(doc.createTextNode("\n"), elem);

			if (p.getPubMedID() != 0) {
				elem = doc.createElement("PM_UID");
				tmpStr = String.valueOf(p.getPubMedID());
				elem.appendChild(doc.createTextNode(tmpStr));
				publication.appendChild(elem);
				publication.insertBefore(doc.createTextNode("\n"), elem);
			} else {
				elem = doc.createElement("Body");
				tmpStr = p.getCitation();
				if (tmpStr == null) tmpStr = "";
				elem.appendChild(doc.createTextNode(tmpStr));
				publication.appendChild(elem);
				publication.insertBefore(doc.createTextNode("\n"), elem);
			}
			
			root.appendChild(publication);
			root.insertBefore(doc.createTextNode("\n"), publication);
		}
	}

	/**
	 * Generates the Person elements of the progress report
	 * @param doc
	 * @param root
	 */
	private void generatePersons(Document doc, Element root) throws Exception {
		if (this.researchers == null) {
			this.buildResearchers();
		}
		if (this.researchers == null) {
			return;
		}

		// Loop through all researchers and create an element for each one
		Iterator iter = researchers.iterator();
		while (iter.hasNext()) {
			Researcher researcher = (Researcher)(iter.next());
			String tmpStr;
			
			Element person = doc.createElement("Person");
			person.setAttribute("Person_ID", String.valueOf(researcher.getID()));
			
			Element elem = doc.createElement("Last_Name");
			tmpStr = researcher.getLastName();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 30) tmpStr = tmpStr.substring(0,29);
			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("First_Name");
			tmpStr = researcher.getFirstName();
			if (tmpStr == null) tmpStr = "";

			// Strip out a trailing period if it's present
			if (tmpStr.endsWith(".")) {
				tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
			}
			
			if (tmpStr.length() > 30) tmpStr = tmpStr.substring(0,29);
			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("Full_Name");
			
			tmpStr = researcher.getFirstName();
			if (tmpStr == null) tmpStr = "";

			// Strip out a trailing period if it's present
			if (tmpStr.endsWith(".")) {
				tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
			}
			
			tmpStr += " " + researcher.getLastName();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 100) tmpStr = tmpStr.substring(0,99);

			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);

			// Handle Degree(s)
			tmpStr = researcher.getDegree();
			if (tmpStr != null && !tmpStr.equals("none") && !tmpStr.equals("not_listed") && !tmpStr.equals("no_answer")) {
				tmpStr = tmpStr.replaceAll("\\.", "");
				String[] degrees = tmpStr.split(", ");
				for (int i = 0; i < degrees.length; i++) {
					elem = doc.createElement("Academic_Degree");
					if (degrees[i].equals("PhD") || degrees[i].equals("Phd"))
						degrees[i] = "PHD";
					elem.appendChild(doc.createTextNode(degrees[i]));
					person.appendChild(elem);
					person.insertBefore(doc.createTextNode("\n"), elem);
				}
			}

			elem = doc.createElement("Phone_Number");
			tmpStr = researcher.getPhone();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 30) tmpStr = tmpStr.substring(0,29);
			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);
			
			elem = doc.createElement("Email_Address");
			tmpStr = researcher.getEmail();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 100) tmpStr = tmpStr.substring(0,99);
			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);

			elem = doc.createElement("Department");
			tmpStr = researcher.getDepartment();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 100) tmpStr = tmpStr.substring(0,99);
			elem.appendChild(doc.createTextNode(tmpStr));
			person.appendChild(elem);
			person.insertBefore(doc.createTextNode("\n"), elem);
			
			// Only include these elements if they're not part of the host organization
			if (!this.hostOrganization.equals(researcher.getOrganization())) {
				elem = doc.createElement("Nonhost_Name");
				tmpStr = researcher.getOrganization();
				if (tmpStr == null) tmpStr = "";
				if (tmpStr.length() > 100) tmpStr = tmpStr.substring(0,99);
				elem.appendChild(doc.createTextNode(tmpStr));
				person.appendChild(elem);
				person.insertBefore(doc.createTextNode("\n"), elem);
				
				tmpStr = researcher.getState();
				if (tmpStr == null) tmpStr = "";
				else if (tmpStr.equals("NO")) tmpStr = "";
				else if (tmpStr.equals("No")) tmpStr = "";
				else if (tmpStr.equals("no")) tmpStr = "";				

				if (!tmpStr.equals("")) {
					elem = doc.createElement("Nonhost_State");
					elem.appendChild(doc.createTextNode(tmpStr));
					person.appendChild(elem);
					person.insertBefore(doc.createTextNode("\n"), elem);
				}
				
				elem = doc.createElement("Nonhost_Country");
				tmpStr = researcher.getCountry();
				
				if (tmpStr == null) tmpStr = "";
				else if (tmpStr.equals("us")) tmpStr = "usa";
				
				elem.appendChild(doc.createTextNode(tmpStr));
				person.appendChild(elem);
				person.insertBefore(doc.createTextNode("\n"), elem);
			}

			// handle funding for this Person
			// Changed to only execute for project PIs (Mike Riffle, July 2010)
			if( researcher.isPI() ) {
				Map<Funding, Set<Integer>> fundingMap = this.getFundingForResearcher( researcher );
				List<Funding> fkeys = new ArrayList<Funding>( fundingMap.keySet() );
				Collections.sort( fkeys );
				
				for ( Funding fundingObject : fkeys ) {
	
					// they no longer accept 'OTH' as funding agency (why?  i have NO idea)
					if ( fundingObject.isFederal() && fundingObject.getSourceName().equals( "OTH" ) )
						continue;
					
					/*
					// Create a Funding element
					Element funding = doc.createElement("Funding");
					*/
					
					Element funding = null;
					if (!fundingObject.isFederal() )
						funding = doc.createElement( "Non_Federal_Funding" );
					else if (!fundingObject.isPHS() )
						funding = doc.createElement( "Federal_Non_PHS_Funding" );
					else
						funding = doc.createElement( "Federal_PHS_Funding" );
					
					Element sElem = null;
					if ( !fundingObject.isFederal() ) {
						sElem = doc.createElement("Source_Type");
						sElem.appendChild(doc.createTextNode( fundingObject.getSourceType() ) );
						funding.appendChild(sElem);
					} else {
						
						String sn = fundingObject.getSourceName();
						if ( sn.equals( "DVA" ) ) sn = "VA";
						
						sElem = doc.createElement("Organization");
						sElem.appendChild(doc.createTextNode( sn ) );
						funding.appendChild(sElem);
					}
					
					// Get the projects associated with this
					Iterator fpIter = fundingMap.get( fundingObject ).iterator();
					while (fpIter.hasNext()) {
						String projectString = ((Integer)(fpIter.next())).toString();
						sElem = doc.createElement("Sub_ID");
						sElem.appendChild(doc.createTextNode( projectString ));
						funding.appendChild(sElem);
					}
					
					if ( !fundingObject.isFederal() ) {
						sElem = doc.createElement("Organization_Name");
						sElem.appendChild(doc.createTextNode( fundingObject.getSourceName() ) );
						funding.appendChild(sElem);
					}
					
					if ( fundingObject.getGrantNumber() != null ) {
						String gm = fundingObject.getGrantNumber();
						if ( gm.length() > 50 )
							gm = gm.substring(0, 49 );
						
						sElem = doc.createElement("Grant_Or_Contract_Number");
						sElem.appendChild(doc.createTextNode( gm ) );
						funding.appendChild(sElem);
					} else {
						sElem = doc.createElement("Grant_Or_Contract_Number");
						sElem.appendChild(doc.createTextNode( "n/a" ) );
						funding.appendChild(sElem);
					}
					
					/*
					sElem = doc.createElement("Source_Name");
					sElem.appendChild(doc.createTextNode( fundingObject.getSourceName() ) );
					funding.appendChild(sElem);
					*/
					
					if ( fundingObject.getGrantAmount() != null ) {
						String tsf = fundingObject.getGrantAmount();
						int amt = 0;
						try {
							tsf = tsf.replace( "$", "" );
							tsf = tsf.replaceAll( ",", "" );
							amt = Integer.valueOf( tsf );
						} catch ( Exception e ) { 
							System.out.println( "Throwing out: " + fundingObject.getGrantAmount() );
						}
						
						sElem = doc.createElement("Total_Support_Funds");
						sElem.appendChild(doc.createTextNode( String.valueOf( amt ) ) );
						funding.appendChild(sElem);
					} else {
						sElem = doc.createElement("Total_Support_Funds");
						sElem.appendChild(doc.createTextNode( "0" ) );
						funding.appendChild(sElem);
					}
					
					person.appendChild(funding);
					person.insertBefore(doc.createTextNode("\n"), funding);
	
				}
			}
			
			root.appendChild(person);
			root.insertBefore(doc.createTextNode("\n"), person);
			
			/*
			// Get the federal funding information for this researcher
			Map fundingMap = this.getFederalFundingForResearcher( researcher );
			Iterator fundingIter = fundingMap.keySet().iterator();
			while (fundingIter.hasNext()) {
				String fedFundingType = (String)(fundingIter.next());
				
				// Create a Funding element
				Element funding = doc.createElement("Funding");

				Element sElem = doc.createElement("Source_Type");
				sElem.appendChild(doc.createTextNode("FED"));
				funding.appendChild(sElem);

				// Get the projects associated with this
				Iterator fpIter = ((Set)(fundingMap.get( fedFundingType ))).iterator();
				while (fpIter.hasNext()) {
					String projectString = ((Integer)(fpIter.next())).toString();
					sElem = doc.createElement("Sub_ID");
					sElem.appendChild(doc.createTextNode( projectString ));
					funding.appendChild(sElem);
					
				}
				
				sElem = doc.createElement("Source_Name");
				sElem.appendChild(doc.createTextNode(fedFundingType));
				funding.appendChild(sElem);

				person.appendChild(funding);
				person.insertBefore(doc.createTextNode("\n"), funding);
			}
			

			// Get the NON-federal funding information for this researcher
			fundingMap = this.getNonFederalFundingForResearcher( researcher );
			fundingIter = fundingMap.keySet().iterator();
			while (fundingIter.hasNext()) {
				String fundingType = (String)(fundingIter.next());
				
				Map fundingNames = (Map)fundingMap.get( fundingType );
				Iterator nameIter = fundingNames.keySet().iterator();
				
				while (nameIter.hasNext()) {
					String fundingName = (String)nameIter.next();
					
					// Create a Funding element
					Element funding = doc.createElement("Funding");
	
					Element sElem = doc.createElement("Source_Type");
					sElem.appendChild(doc.createTextNode(fundingType));
					funding.appendChild(sElem);
					
					// Get the projects associated with this
					Iterator fpIter = ((Set)((Map)(fundingMap.get( fundingType ))).get( fundingName )).iterator();

					while (fpIter.hasNext()) {
						String projectString = ((Integer)(fpIter.next())).toString();
						sElem = doc.createElement("Sub_ID");
						sElem.appendChild(doc.createTextNode( projectString ));
						funding.appendChild(sElem);
						
					}
	
					sElem = doc.createElement("Source_Name");
					sElem.appendChild(doc.createTextNode(fundingName));
					funding.appendChild(sElem);
					
					person.appendChild(funding);
					person.insertBefore(doc.createTextNode("\n"), funding);

				
				}
			
			}

			// clean these up
			fundingIter = null;
			fundingMap = null;
			*/
		}
	}

	/**
	 * Generate the Subproject elements for the report
	 * @param doc
	 * @param root
	 */
	private void generateSubprojects(Document doc, Element root) throws Exception {
		
		Iterator iter = this.projects.iterator();
		while (iter.hasNext()) {
			Project project = (Project)(iter.next());
			
			/*
			// Skip Dissemination (plasmid) projects, they are handled separately
			if(project.getShortType().equals("D")) continue;
			*/
			
			/*
			// Skip training projects that are people just asking questions
			if ( project.getShortType().equals(Projects.TRAINING) ) {
				if ( java.util.Arrays.binarySearch( this.realTrainings, project.getID() ) < 0 )
					continue;
			}
			*/
			
			Element subproject = doc.createElement("Subproject");
			
			subproject.setAttribute("Sub_ID", String.valueOf(project.getID()));

			subproject.appendChild(doc.createComment("Submit date: " + project.getSubmitDate()));
			subproject.appendChild(doc.createComment("Last Change date: " + project.getLastChange()));
			subproject.appendChild(doc.createComment("Progress Change date: " + project.getProgressLastChange()));
			
			Element elem;
			String tmpStr;
			
			/*
			tmpStr = String.valueOf(project.getNCRRID());
			//if(tmpStr.equals("0")) tmpStr = "0000";
			if (!tmpStr.equals("0")) {
				elem = doc.createElement("Subproject_ID");
				elem.appendChild(doc.createTextNode(tmpStr));
				subproject.appendChild(elem);
				subproject.insertBefore(doc.createTextNode("\n"), elem);
			}
			*/
			
			elem = doc.createElement("Title");
			tmpStr = project.getTitle();
			if (tmpStr == null) tmpStr = "";
			if (tmpStr.length() > 80) tmpStr = tmpStr.substring(0,79);
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);

			
			// Add the AIDS_Flag element to the document
			elem = doc.createElement("AIDS_Flag");
			tmpStr = "N";
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
			
			
			elem = doc.createElement("Abstract");
			tmpStr = project.getPublicAbstract();
			if (tmpStr == null) tmpStr = "";
			
			if (project.getShortType().equals(Projects.TRAINING)) {
				if (!tmpStr.equals("")) tmpStr = tmpStr + "\n\n";
				tmpStr = tmpStr + ((Training)project).getDescription();
				if (tmpStr == null || tmpStr.equals("")) tmpStr = "Training provided by the YRC.";
			}

			
			//if (project.getProgress() != null) { tmpStr = tmpStr + "\n\n" + project.getProgress(); }

			tmpStr = cleanText(tmpStr);
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
			
			
			elem = doc.createElement("Type");
			tmpStr = project.getShortType();
			if (tmpStr.equals("T")) tmpStr = "D";
			else if (tmpStr.equals("D")) tmpStr = "D";
			else if (tmpStr.equals("C")) tmpStr = "C";
			else if (tmpStr.equals("Tech")) tmpStr = "T";
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
			
			if (project.getProgress() != null || DataForProjectGenerator.getInstance().generateDataForProject( project, this.startDate, this.endDate ) != null) {
				
				elem = doc.createElement("Progress");

				tmpStr = project.getProgress();
				if (tmpStr == null) tmpStr = "";

				// add data uploaded to this project if we have any
				if (DataForProjectGenerator.getInstance().generateDataForProject( project, this.startDate, this.endDate ) != null)
					tmpStr += DataForProjectGenerator.getInstance().generateDataForProject( project, this.startDate, this.endDate );
				
				elem.appendChild(doc.createTextNode(tmpStr));
				subproject.appendChild(elem);
				subproject.insertBefore(doc.createTextNode("\n"), elem);
				
			}
			
			// Set up the investigator elements
			Set projectResearchers = new HashSet();
			Researcher r = project.getPI();
			if (r != null) { addResearcherToSubproject(doc, subproject, r.getID(), "P", projectResearchers); }
			
			// add the rest of the researchers
			for( Researcher tr : project.getResearchers() ) {
				
				// don't add the PI
				if( project.getPI() != null && project.getPI().getID() == tr.getID() )
					continue;
				
				addResearcherToSubproject(doc, subproject, tr.getID(), "C", projectResearchers);
			}
			
			// Add YRC group members as researchers to this project
			String[] groups = project.getGroupsArray();
			for (int i = 0; i < groups.length; i++) {
				if (groups[i].equals(Projects.MICROSCOPY))
					addResearchersToSubproject(doc, subproject, this.microMembers, projectResearchers);
				else if (groups[i].equals(Projects.TWOHYBRID))
					addResearchersToSubproject(doc, subproject, this.y2hMembers, projectResearchers);
				else if (groups[i].equals(Projects.INFORMATICS))
					addResearchersToSubproject(doc, subproject, this.infoMembers, projectResearchers);
				
				// added 2007-06-18 to support "core" group
				else if (groups[i].equals( Projects.CORE ))
					addResearchersToSubproject( doc, subproject, this.coreMembers, projectResearchers );
				
				/*
				else if (groups[i].equals(Projects.AEBERSOLD))
					addResearchersToSubproject(doc, subproject, this.aebersoldMembers, projectResearchers);
				*/

				else if (groups[i].equals(Projects.YATES))
					addResearchersToSubproject(doc, subproject, this.yatesMembers, projectResearchers);
				else if (groups[i].equals(Projects.PSP))
					addResearchersToSubproject(doc, subproject, this.pspMembers, projectResearchers);
				else if (groups[i].equals(Projects.MACCOSS))
					addResearchersToSubproject(doc, subproject, this.maccossMembers, projectResearchers);
				else if (groups[i].equals(Projects.NOBLE))
					addResearchersToSubproject(doc, subproject, this.nobleMembers, projectResearchers);
			}

			// Generate the Publication_ID element(s)
			List projPubList = PublicationUtils.getPublicationsByProject(project.getID());
			if (projPubList != null) {
				//subproject.appendChild(doc.createComment("Got " + projPubList.size() + " Publication elements."));
				Iterator ppIter = projPubList.iterator();
				while (ppIter.hasNext()) {
					Publication pPub = (Publication)(ppIter.next());
					
					// Generate and add the actual element
					elem = doc.createElement("Publication_ID");
					tmpStr = String.valueOf(pPub.getId());
					elem.appendChild(doc.createTextNode(tmpStr));
					subproject.appendChild(elem);
					subproject.insertBefore(doc.createTextNode("\n"), elem);	
				}
			}
			
			elem = doc.createElement("Percent_Grant_Dollars");
			
			DecimalFormat df = new DecimalFormat( "0.000" );
		    tmpStr = df.format( project.getBTA() );

			if (tmpStr == null) tmpStr = "";
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);			
			
			elem = doc.createElement("Resource_ID");
			tmpStr = String.valueOf( project.getID() );
			elem.appendChild(doc.createTextNode(tmpStr));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
			
			/*
			// Handle the keywords
			tmpStr = project.getKeywords();
			if (tmpStr != null) {
				String[] keywords = tmpStr.split(",");
				if (keywords.length == 1) keywords = tmpStr.split("\n");
				
				for (int i = 0; i < keywords.length; i++) {
					keywords[i] = keywords[i].trim();
					if (keywords[i].length() > 40) keywords[i] = keywords[i].substring(0,39);
					elem = doc.createElement("Keyword");
					elem.appendChild(doc.createTextNode(keywords[i]));
					subproject.appendChild(elem);
					subproject.insertBefore(doc.createTextNode("\n"), elem);
				}
			}
			*/
			
			/*
			// Handle AXIS I codes
			tmpStr = project.getAxisI();
			if (tmpStr != null && tmpStr.length() > 0) {
				String[] codes = tmpStr.split(" ");
				
				for (int i = 0; i < codes.length; i++) {
					elem = doc.createElement("Axis_I_Code");
					elem.appendChild(doc.createTextNode(codes[i]));
					subproject.appendChild(elem);
					subproject.insertBefore(doc.createTextNode("\n"), elem);
				}
			} else {
				
				// Just create an Axis I element with the default value
				elem = doc.createElement("Axis_I_Code");
				elem.appendChild(doc.createTextNode("8"));
				subproject.appendChild(elem);
				subproject.insertBefore(doc.createTextNode("\n"), elem);
			}

			// Handle AXIS II codes
			tmpStr = project.getAxisII();
			if (tmpStr != null && tmpStr.length() > 0) {
				String[] codes = tmpStr.split(" ");
				
				for (int i = 0; i < codes.length; i++) {
					elem = doc.createElement("Axis_II_Code");
					elem.appendChild(doc.createTextNode(codes[i]));
					subproject.appendChild(elem);
					subproject.insertBefore(doc.createTextNode("\n"), elem);
				}
			} else {
				
				// Just create an Axis II element with the default value
				elem = doc.createElement("Axis_II_Code");
				elem.appendChild(doc.createTextNode("74h"));
				subproject.appendChild(elem);
				subproject.insertBefore(doc.createTextNode("\n"), elem);
			}
			*/

			root.appendChild(subproject);
			root.insertBefore(doc.createTextNode("\n"), subproject);
		}
	}

	/**
	 * Generate the Subproject elements relating to dissemination of plasmids
	 * @param doc
	 * @param root
	 */
	private void generatePlasmidSubprojects(Document doc, Element root) throws Exception {
		// Sets to store researcher IDs for each of these types
		Set mtSet = new HashSet();
		Set mSet = new HashSet();
		Set tSet = new HashSet();

		// Add YRC researchers to the sets
		for (int i = 0; i < this.y2hMembers.length; i++) {
			mtSet.add(new Integer(this.y2hMembers[i]));
			tSet.add(new Integer(this.y2hMembers[i]));
		}
		for (int i = 0; i < this.microMembers.length; i++) {
			mtSet.add(new Integer(this.microMembers[i]));
			mSet.add(new Integer(this.microMembers[i]));
		}


		/*
		// Build the researcher sets for the 3 types
		Iterator iter = this.projects.iterator();
		while (iter.hasNext()) {
			Project project = (Project)(iter.next());
			if (!project.getShortType().equals("D")) continue;
			
			// Figure out which set we're adding to
			String[] groups = project.getGroupsArray();
			Set theSet = null;
			if (groups.length == 2) theSet = mtSet;
			else if (groups[0].equals(Projects.MICROSCOPY)) theSet = mSet;
			else if (groups[0].equals(Projects.TWOHYBRID)) theSet = tSet;
			else { continue; }
			
			Researcher r = project.getPI();
			if (r != null) { theSet.add(new Integer(r.getID())); }
			
			r = project.getResearcherB();
			if (r != null) { theSet.add(new Integer(r.getID())); }

			r = project.getResearcherC();
			if (r != null) { theSet.add(new Integer(r.getID())); }

			r = project.getResearcherD();
			if (r != null) { theSet.add(new Integer(r.getID())); }						
		}//end while
		*/
		
		//Generate subproject elements for each of the 3 types of projects

		// Do the Microscopy plasmids
		int counter = 0;
		Iterator iter = mSet.iterator();
		int[] rIDs = new int[mSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generatePlasmidProject(doc, root, rIDs, "m");

		// Do the TwoHybrid plasmids
		counter = 0;
		iter = tSet.iterator();
		rIDs = new int[tSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generatePlasmidProject(doc, root, rIDs, "t");

		// Do the TwoHybrid + Microscopy plasmids
		/*
		counter = 0;
		iter = mtSet.iterator();
		rIDs = new int[mtSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generatePlasmidProject(doc, root, rIDs, "mt");
		*/
	}

	/**
	 * Generate one sub project element for the given plasmid dissemination type and array of researcher IDs
	 * @param doc
	 * @param root
	 * @param rIDs
	 * @param type
	 */
	private void generatePlasmidProject(Document doc, Element root, int[] rIDs, String type) throws Exception {
		String title = null;
		String abs = null;
		String bta = null;
		String[] keywords = null;
		//String[] axisi = this.plasmidAxisI;
		//String[] axisii = this.plasmidAxisII;
		
		// Initialize the vars we need for this plasmid dissemination type
		if (type.equals("m")) {
			title = this.microscopyTitle;
			
			abs = this.microscopyAbstract;
			abs = abs + "\n\nThe center fulfilled " + PlasmidProjectCounter.getInstance().countProjects( "M" ) +
			" requests submitted to the Microscopy group in the last reporting year.";
			
			//keywords = this.microscopyKeywords;
			bta = this.microscopyBTA;
		} else if (type.equals("t")) {
			title = this.twoHybridTitle;

			abs = this.twoHybridAbstract;
			abs = abs + "\n\nThe center fulfilled " + PlasmidProjectCounter.getInstance().countProjects( "T" ) +
			" requests submitted to the Two Hybrid group in the last reporting year.";

			//keywords = this.twoHybridKeywords;
			bta = this.twoHybridBTA;
		} else if (type.equals("mt")) {
			//title = this.twoHybridMicroscopyTitle;
			
			abs = this.microscopyAbstract + "\n\n" + this.twoHybridAbstract;
			abs = abs + "\n\nThe center fulfilled " + PlasmidProjectCounter.getInstance().countProjects( "MT" ) +
			" requests submitted to both the Microscopy and Two Hybrid groups in the last reporting year.";

			//keywords = this.twoHybridMicroscopyKeywords;
			//bta = this.twoHybridMicroscopyBTA;
		} else {
			return;
		}

		if (title == null) throw new Exception ("null for title");
		if (abs == null) throw new Exception ("null for abs");
		if (bta == null) throw new Exception ("null for bta");
		

		Element subproject = doc.createElement("Subproject");
		Element elem;
			
		subproject.setAttribute("Sub_ID", "plamid_" + type);
			
		elem = doc.createElement("Title");
		elem.appendChild(doc.createTextNode(title));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);

		// Add the AIDS_Flag element to the document
		elem = doc.createElement("AIDS_Flag");
		String tmpStr = "N";
		elem.appendChild(doc.createTextNode(tmpStr));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		
		elem = doc.createElement("Abstract");
		elem.appendChild(doc.createTextNode(abs));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);

		elem = doc.createElement("Type");
		elem.appendChild(doc.createTextNode("D"));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		
		// Set up the investigator elements
		addResearchersToSubproject(doc, subproject, rIDs, new HashSet());
		
		elem = doc.createElement("Percent_Grant_Dollars");
		elem.appendChild(doc.createTextNode(bta));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);			
		
		/*
		// Handle the keywords
				
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywords[i].trim();
			elem = doc.createElement("Keyword");
			elem.appendChild(doc.createTextNode(keywords[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}
		*/

		/*
		// Handle AXIS I codes				
		for (int i = 0; i < axisi.length; i++) {
			elem = doc.createElement("Axis_I_Code");
			elem.appendChild(doc.createTextNode(axisi[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}

		// Handle AXIS II codes
		for (int i = 0; i < axisii.length; i++) {
			elem = doc.createElement("Axis_II_Code");
			elem.appendChild(doc.createTextNode(axisii[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}
		*/

		/*
		elem = doc.createElement("Subproject_ID");
		elem.appendChild(doc.createTextNode("0000"));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		*/

		root.appendChild(subproject);
		root.insertBefore(doc.createTextNode("\n"), subproject);
	}

	
	/**
	 * Generate the Subproject elements encompassing all questions asked of groups in the YRC as 1 project per group
	 * @param doc
	 * @param root
	 */
	private void generateQuestionSubprojects(Document doc, Element root) throws Exception {
		// Sets to store researcher IDs for each of these types
		Set mSet = new HashSet();
		Set tSet = new HashSet();
		Set msSet = new HashSet();

		// Add YRC researchers to the sets
		for (int i = 0; i < this.y2hMembers.length; i++) {
			tSet.add(new Integer(this.y2hMembers[i]));
		}
		for (int i = 0; i < this.microMembers.length; i++) {
			mSet.add(new Integer(this.microMembers[i]));
		}

		// add yrc researchers for questions to the ms groups
		for (int i = 0; i < this.yatesMembers.length; i++) {
			msSet.add(new Integer(this.yatesMembers[i]));
		}
		for (int i = 0; i < this.maccossMembers.length; i++) {
			msSet.add(new Integer(this.maccossMembers[i]));
		}
		
		/*
		// Build the researcher sets for the 3 types
		Iterator iter = this.projects.iterator();
		while (iter.hasNext()) {
			Project project = (Project)(iter.next());

			if (!project.getShortType().equals("T")) continue;
			if ( Arrays.binarySearch( this.realTrainings, project.getID()) >= 0 )
				continue;
			
			// Figure out which set we're adding to
			String[] groups = project.getGroupsArray();
			Set theSet = null;
			if (groups[0].equals(Projects.MICROSCOPY)) theSet = mSet;
			else if (groups[0].equals(Projects.TWOHYBRID)) theSet = tSet;
			else { continue; }
			
			Researcher r = project.getPI();
			if (r != null) { theSet.add(new Integer(r.getID())); }
			
			r = project.getResearcherB();
			if (r != null) { theSet.add(new Integer(r.getID())); }

			r = project.getResearcherC();
			if (r != null) { theSet.add(new Integer(r.getID())); }

			r = project.getResearcherD();
			if (r != null) { theSet.add(new Integer(r.getID())); }						
		}//end while
		*/
		
		//Generate subproject elements for each of the 3 types of projects

		// Do the Microscopy plasmids
		int counter = 0;
		Iterator iter = mSet.iterator();
		int[] rIDs = new int[mSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generateQuestionProject(doc, root, rIDs, "m");

		// Do the TwoHybrid plasmids
		counter = 0;
		iter = tSet.iterator();
		rIDs = new int[tSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generateQuestionProject(doc, root, rIDs, "t");

		// Do the MS questions
		counter = 0;
		iter = msSet.iterator();
		rIDs = new int[msSet.size()];
		while (iter.hasNext()) {
			Integer rID = (Integer)(iter.next());
			rIDs[counter] = rID.intValue();
			counter++;
		}
		generateQuestionProject(doc, root, rIDs, "ms");
	
	}
	
	
	/**
	 * Generate one sub project element for the given plasmid dissemination type and array of researcher IDs
	 * @param doc
	 * @param root
	 * @param rIDs
	 * @param type
	 */
	private void generateQuestionProject(Document doc, Element root, int[] rIDs, String type) throws Exception {
		String title = null;
		String abs = null;
		String bta = null;
		//String[] keywords = null;
		//String[] axisi = this.questionAxisI;
		//String[] axisii = this.questionAxisII;
		
		// Initialize the vars we need for this plasmid dissemination type
		if (type.equals("m")) {
			title = this.microscopyQuestionTitle;
			abs = this.microscopyQuestionAbstract;
			//keywords = this.microscopyQuestionKeywords;
			bta = this.microscopyQuestionBTA;
		} else if (type.equals("t")) {
			title = this.twoHybridQuestionTitle;
			abs = this.twoHybridQuestionAbstract;
			//keywords = this.twoHybridQuestionKeywords;
			bta = this.twoHybridQuestionBTA;
		} else if (type.equals("ms")) {
			title = this.msQuestionTitle;
			abs = this.msQuestionAbstract;
			//keywords = this.twoHybridQuestionKeywords;
			bta = this.msQuestionBTA;
		} else {
			return;
		}

		if (title == null) throw new Exception ("null for title");
		if (abs == null) throw new Exception ("null for abs");
		if (bta == null) throw new Exception ("null for bta");
		

		Element subproject = doc.createElement("Subproject");
		Element elem;
			
		subproject.setAttribute("Sub_ID", "question_" + type);
			
		elem = doc.createElement("Title");
		elem.appendChild(doc.createTextNode(title));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);

		// Add the AIDS_Flag element to the document
		elem = doc.createElement("AIDS_Flag");
		String tmpStr = "N";
		elem.appendChild(doc.createTextNode(tmpStr));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		
		elem = doc.createElement("Abstract");
		elem.appendChild(doc.createTextNode(abs));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);

		elem = doc.createElement("Type");
		elem.appendChild(doc.createTextNode("D"));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		
		// Set up the investigator elements
		addResearchersToSubproject(doc, subproject, rIDs, new HashSet());
		
		elem = doc.createElement("Percent_Grant_Dollars");
		elem.appendChild(doc.createTextNode(bta));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);			
			
		
		/*
		// Handle the keywords
				
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywords[i].trim();
			elem = doc.createElement("Keyword");
			elem.appendChild(doc.createTextNode(keywords[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}
		*/

		/*
		// Handle AXIS I codes				
		for (int i = 0; i < axisi.length; i++) {
			elem = doc.createElement("Axis_I_Code");
			elem.appendChild(doc.createTextNode(axisi[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}

		// Handle AXIS II codes
		for (int i = 0; i < axisii.length; i++) {
			elem = doc.createElement("Axis_II_Code");
			elem.appendChild(doc.createTextNode(axisii[i]));
			subproject.appendChild(elem);
			subproject.insertBefore(doc.createTextNode("\n"), elem);
		}
		*/
		
		/*
		elem = doc.createElement("SPID");
		elem.appendChild(doc.createTextNode("0000"));
		subproject.appendChild(elem);
		subproject.insertBefore(doc.createTextNode("\n"), elem);
		*/
		
		root.appendChild(subproject);
		root.insertBefore(doc.createTextNode("\n"), subproject);
	}
	

	/**
	 * Add the suplied researcher of the supplied type ("P" or "C") to the supplied subproject element of the supplied document
	 * @param doc
	 * @param subproject
	 * @param rID
	 * @param type
	 * @param projectResearchers
	 */
	private void addResearcherToSubproject(Document doc, Element subproject, int rID, String type, Set projectResearchers) {

		// If this researcher has already been added to this sub project (that is, if they're in projectResearchers Set)
		// then do nothing
		Integer ri = new Integer(rID);
		if (projectResearchers.contains(ri)) return;

		// Add this ID to projectResearchers so it doesn't get added to this project again
		projectResearchers.add(ri);

		Element investigator = doc.createElement("Investigator");
		
		Element tElem = doc.createElement("Person_ID");
		tElem.appendChild(doc.createTextNode(String.valueOf(rID)));
		investigator.appendChild(tElem);

		tElem = doc.createElement("Investigator_Type");
		tElem.appendChild(doc.createTextNode(type));
		investigator.appendChild(tElem);

		subproject.appendChild(investigator);
		subproject.insertBefore(doc.createTextNode("\n"), investigator);
	}

	/**
	 * Add the suplied array of researchers to the supplied subproject element of the supplied document
	 * All researchers will be added as type "C"
	 * @param doc
	 * @param subproject
	 * @param rIDs
	 * @param projectResearchers
	 */
	private void addResearchersToSubproject(Document doc, Element subproject, int[] rIDs, Set projectResearchers) {
		for (int i = 0; i < rIDs.length; i++) {
			if(rIDs[i] == 0) continue;
			this.addResearcherToSubproject(doc, subproject, rIDs[i], "C", projectResearchers);
		}
	}


	/**
	 * Get the funding for a researcher
	 * @param r The researcher we're using for the search
	 * @return
	 */
	private Map<Funding, Set<Integer>> getFundingForResearcher(Researcher r) {
		Map<Funding, Set<Integer>> fundingMap = new HashMap<Funding, Set<Integer>>();
		
		Iterator pIter = projects.iterator();
		while (pIter.hasNext()) {
			Project p = (Project)(pIter.next());
			
			// Researcher is not part of this project, go to next project
			if( !p.getResearchers().contains( r ) )
				continue;
			
			// Loop through the funding types for this project
			String[] fundingTypes = p.getFundingTypesArray();
			if (fundingTypes == null) continue;

			for (int i = 0; i < fundingTypes.length; i++) {
				
				// Skip this funding type if it's not FEDERAL
				if (fundingTypes[i].equals("FEDERAL")) {

					String[] fedTypes = p.getFederalFundingTypesArray();
					if (fedTypes == null) continue;
				
					// Loop through the federal funding types for this project
					for (int k = 0; k < fedTypes.length; k++) {
						
						Funding funding = new Funding();
						funding.setSourceType( "FED" );
						
						if (fedTypes[k].equals("OTHER")) funding.setSourceName( "OTH" );
						else funding.setSourceName( fedTypes[k] );
					
						if (p.getGrantNumber() != null && !p.getGrantNumber().equals( "" ))
							funding.setGrantNumber( p.getGrantNumber() );
						
						if (p.getGrantAmount() != null && !p.getGrantAmount().equals( "" ))
							funding.setGrantAmount( p.getGrantAmount() );						
						
						if (!fundingMap.containsKey( funding ))
							fundingMap.put( funding, new HashSet<Integer>() );
						
						// Add this project to the set of projects for this researcher with this federal funding type
						fundingMap.get( funding ).add( p.getID() );

						continue;
					}
				} else { // funding type not federal
					
					String fundingType = fundingTypes[i];
					if (fundingType.equals("OTHER")) fundingType = "OTH";
					else if (fundingType.equals("PROFASSOC")) fundingType = "PVAS";
					else if (fundingType.equals("FOUNDATION")) fundingType = "FDN";
					else if (fundingType.equals("LOCGOV")) fundingType = "SCCF";
					else if (fundingType.equals("INDUSTRY")) fundingType = "PVAS";
					
					
					String name = p.getFoundationName();
					if (name == null || name.equals(""))
						name = "unknown";
					
					Funding funding = new Funding();
					funding.setSourceType( fundingType );
					funding.setSourceName( name );
					
					if (!fundingMap.containsKey( funding ))
						fundingMap.put( funding, new HashSet<Integer>() );
					
					// Add this project to the set of projects for this researcher with this federal funding type
					fundingMap.get( funding ).add( p.getID() );
					
				} // end non federal funding clause		
				
			}//end looping through funding types for this project

		}//end looping through projects
		
		return fundingMap;
	}
	
	
	
	/**
	 * Returns a Map of the following structure:
	 * 		{ "FED_TYPE" => "FEDERAL FUNDING TYPE NAME", (only here for type "FEDERAL")
	 * 		  "PROJECTS" => Set_OF_PROJECT IDS AS TYPE Integers
	 * 		}
	 * @param r The researcher we're using for the search
	 * @return
	 */
	private Map getFederalFundingForResearcher(Researcher r) {
		Map fundingMap = new HashMap();
		
		Iterator pIter = projects.iterator();
		while (pIter.hasNext()) {
			Project p = (Project)(pIter.next());
			
			// Skip dissemination and training projects
			if (p.getShortType().equals("D") || p.getShortType().equals("T"))
				continue;
			
			// Researcher is not part of this project, go to next project
			if( !p.getResearchers().contains( r ) )
				continue;
			
			// Loop through the funding types for this project
			String[] fundingTypes = p.getFundingTypesArray();
			if (fundingTypes == null) continue;

			for (int i = 0; i < fundingTypes.length; i++) {
				
				// Skip this funding type if it's not FEDERAL
				if (!fundingTypes[i].equals("FEDERAL"))
					continue;
				
				String[] fedTypes = p.getFederalFundingTypesArray();
				if (fedTypes == null) continue;
				
				// Loop through the federal funding types for this project
				for (int k = 0; k < fedTypes.length; k++) {
					if (fedTypes[k].equals("OTHER")) fedTypes[k] = "OTH";
					
					if (!fundingMap.containsKey( fedTypes[k] )) {
						fundingMap.put( fedTypes[k], new HashSet() );
					}
					
					// Add this project to the set of projects for this researcher with this federal funding type
					((Set)(fundingMap.get( fedTypes[k] ))).add( new Integer( p.getID() ) );
				}
			}
		}
		
		
		
		return fundingMap;
	}

	
	/**
	 * Returns a Map of the following structure:
	 * 		( "FDN" =>
	 * 				( "HHMI" => SET_OF_PROJECT_IDS,
	 * 				  "FOOO" => SET_OF_PROJECT_IDS
	 * 				),
	 * 		  "SCCF" =>
	 * 				( "BAAR" => SET_OF_PROJECT_IDS )
	 * 		)
	 * @param r The researcher we're using for the search
	 * @return
	 */
	private Map getNonFederalFundingForResearcher(Researcher r) {
		Map fundingMap = new HashMap();
		
		Iterator pIter = projects.iterator();
		while (pIter.hasNext()) {
			Project p = (Project)(pIter.next());
			
			// Skip dissemination and training projects
			if (p.getShortType().equals("D") || p.getShortType().equals("T"))
				continue;
			
			// Researcher is not part of this project, go to next project
			if( !p.getResearchers().contains( r ) )
				continue;
			
			// Loop through the funding types for this project
			String[] fundingTypes = p.getFundingTypesArray();
			if (fundingTypes == null) continue;

			for (int i = 0; i < fundingTypes.length; i++) {
				
				// Skip this funding type if it's FEDERAL
				if (fundingTypes[i].equals("FEDERAL"))
					continue;
				
				String fundingType = fundingTypes[i];
				if (fundingType.equals("OTHER")) fundingType = "OTH";
				else if (fundingType.equals("PROFASSOC")) fundingType = "PVAS";
				else if (fundingType.equals("FOUNDATION")) fundingType = "FDN";
				else if (fundingType.equals("LOCGOV")) fundingType = "SCCF";
				else if (fundingType.equals("INDUSTRY")) fundingType = "PVAS";
				
				Map subMap = null;
				if (!fundingMap.containsKey( fundingType )) {
					subMap = new HashMap();
					fundingMap.put( fundingType, subMap);
				} else
					subMap = (Map)fundingMap.get( fundingType );
				
				Set nameSet = null;
				String name = p.getFoundationName();
				if (name == null || name.equals(""))
					name = "unknown";
				
				if (!subMap.containsKey( name )) {
					nameSet = new HashSet();
					subMap.put( name, nameSet );
				} else {
					nameSet = (Set)subMap.get( name );
				}
				
				nameSet.add( new Integer( p.getID() ) );
				
				// Add this project to the set of projects for this researcher with this funding type
				//((Set)(fundingMap.get( fundingType ))).add( new Integer( p.getID() ) );

			}
		}
		
		
		
		return fundingMap;
	}

	/**
	 * Takes the DOM built from the database, and converts it to XML
	 * @throws Exception
	 */
	private void generateXML() throws Exception {

		// Prepare the DOM document for writing
		Source source = new DOMSource(this.document);
    
		// Prepare the output file
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
    
		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		//xformer.setOutputProperty( OutputKeys.ENCODING, "ISO-8859-1" );
		xformer.setOutputProperty(OutputKeys.INDENT,"yes");
		
		
		xformer.transform(source, result);
		
		this.XML = sw.toString();
	}

	/**
	 * Build the Researchers set
	 */
	private void buildResearchers() throws Exception {
		if (this.projects == null) {
			this.buildProjects();
		}
		if (this.projects == null) return;

		// Loop through all the projects, and build a set of researchers
		this.researchers = new HashSet();

		// Make sure we've included all researchers from within the YRC for all of the groups
		this.addYRCMembers();

		// Loop through all projects, and add all researchers in all projects to the set
		Iterator iter = projects.iterator();
		while (iter.hasNext()) {
			Project project = (Project)(iter.next());
			this.researchers.addAll( project.getResearchers() );
		}
	}

	/**
	 * Build the projects list, filtered by date
	 * @throws Exception
	 */
	private void buildProjects() throws Exception {
		this.projects = ReportProjectsSearcher.search(2010);
	}

	/**
	 * Populate the global researchers set with the YRC group members
	 */
	private void addYRCMembers() {
		
		// Microscopy Set
		for (int i = 0; i < this.microMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.microMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// Yeast Two-Hybrid Set
		for (int i = 0; i < this.y2hMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.y2hMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// Protein Structure Prediction Set
		for (int i = 0; i < this.pspMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.pspMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// Informatics Set
		for (int i = 0; i < this.infoMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.infoMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// Core Set
		for (int i = 0; i < this.coreMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.coreMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}
		
		
		// Yates Set
		for (int i = 0; i < this.yatesMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.yatesMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// Noble Set
		for (int i = 0; i < this.nobleMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.nobleMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}

		// MacCoss Set
		for (int i = 0; i < this.maccossMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.maccossMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}
		
		/*
		 * Commented out by Michael Riffle 2005-06-08
		 * Aebersold group no longer part of the YRC
		 * 
		// Aebersold Set
		for (int i = 0; i < this.aebersoldMembers.length; i++) {
			Researcher r = new Researcher();
			try {
				r.load(this.aebersoldMembers[i]);
				this.researchers.add(r);
			} catch (Exception e) { ; }
		}
		*/
	}
	
	/**
	 * Load attributes we are using for this report from its properties file
	 * @throws Exception if there is a problem accomplishing this
	 */
	private void loadProperties() throws Exception {
		InputStream is = getClass().getResourceAsStream(this.PROPS_FILE);
		Properties props = new Properties();
		props.load(is);
		
		this.directorPersonID = Integer.parseInt( props.getProperty( "director.id" ) );
		
		this.hostOrganization = props.getProperty("institution.host");
		
		this.twoHybridTitle = props.getProperty("title.plasmid.twohybrid");
		this.microscopyTitle = props.getProperty("title.plasmid.microscopy");
		//this.twoHybridMicroscopyTitle = props.getProperty("title.plasmid.twohybridmicroscopy");

		this.twoHybridQuestionTitle = props.getProperty("title.question.twohybrid");
		this.microscopyQuestionTitle = props.getProperty("title.question.microscopy");
		this.msQuestionTitle = props.getProperty("title.question.ms");
		
		this.twoHybridAbstract = props.getProperty("abstract.plasmid.twohybrid");
		this.microscopyAbstract = props.getProperty("abstract.plasmid.microscopy");

		this.twoHybridQuestionAbstract = props.getProperty("abstract.question.twohybrid");
		this.microscopyQuestionAbstract = props.getProperty("abstract.question.microscopy");
		this.msQuestionAbstract = props.getProperty("abstract.question.ms");
		
		/*
		this.twoHybridKeywords = props.getProperty("keywords.plasmid.twohybrid").split(", ");
		this.microscopyKeywords = props.getProperty("keywords.plasmid.microscopy").split(", ");
		this.twoHybridMicroscopyKeywords = props.getProperty("keywords.plasmid.twohybridmicroscopy").split(", ");

		this.twoHybridQuestionKeywords = props.getProperty("keywords.question.twohybrid").split(", ");
		this.microscopyQuestionKeywords = props.getProperty("keywords.question.microscopy").split(", ");
		
		this.plasmidAxisI = props.getProperty("axisi.plasmid").split(" ");
		this.plasmidAxisII = props.getProperty("axisii.plasmid").split(" ");

		this.questionAxisI = props.getProperty("axisi.question").split(" ");
		this.questionAxisII = props.getProperty("axisii.question").split(" ");
		*/
		
		this.microscopyBTA = props.getProperty("bta.plasmid.microscopy");
		this.twoHybridBTA = props.getProperty("bta.plasmid.twohybrid");
		//this.twoHybridMicroscopyBTA = props.getProperty("bta.plasmid.twohybridmicroscopy");

		this.microscopyQuestionBTA = props.getProperty("bta.question.microscopy");
		this.twoHybridQuestionBTA = props.getProperty("bta.question.twohybrid");
		this.msQuestionBTA = props.getProperty("bta.question.ms");
		
		DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
		ParsePosition pp = new ParsePosition(0);
		this.setStartDate(df.parse(props.getProperty("date.start"), pp));

		pp = new ParsePosition(0);
		this.setEndDate(df.parse(props.getProperty("date.end"), pp));
		
		this.progressURL = props.getProperty("progress.summary.url");

		// Grant Info values:
		this.grantSerialNumber = props.getProperty("grantinfo.serialnumber");
		this.grantSupportYear = props.getProperty("grantinfo.supportyear");
		this.grantToDate = props.getProperty("grantinfo.reporttodate");
		this.grantFromDate = props.getProperty("grantinfo.reportfromdate");
		this.grantTitle = props.getProperty("grantinfo.projecttitle");
		this.grantDirectorName = props.getProperty("grantinfo.director.name");
		this.grantDirectorTitle = props.getProperty("grantinfo.director.academictitle");
		this.grantDirectorPhone = props.getProperty("grantinfo.director.phonenumber");
		this.grantDirectorFax = props.getProperty("grantinfo.director.faxnumber");
		this.grantDirectorEmail = props.getProperty("grantinfo.director.emailaddress");
		this.grantHostState = props.getProperty("grantinfo.hoststate");
		this.grantRecipientInstitution = props.getProperty("grantinfo.recipientinstitutionname");
		this.grantHealthSchoolName = props.getProperty("grantinfo.healthschoolname");
		this.grantPatent = props.getProperty("grantinfo.patent");

	}

	/**
	 * Clean up some of the messy formatting of the text in the database from the old sign up form
	 * @param arg The text to clean
	 * @return The cleaned text
	 */
	private String cleanText(String arg) {
		if (arg == null) return arg;
		
		arg = arg.replaceAll("\\\\r\\\\n", "\n");
		arg = arg.replaceAll("\\\\n", "\n");
		arg = arg.replaceAll("\\\\r", "\n");
		
		return arg;
	}

	// Instance vars
	private Document document;
	private String XML;
	private List projects;
	private Set researchers;
	
	// Define the members of these groups... this shouldn't really be here, but is here for the interest of time
	// Will later be moved out of the source
	private final int[] microMembers = { 137, 194, 1212 };
	private final int[] y2hMembers = { 272, 1650, 1886 };
	private final int[] pspMembers = { 268 };
	private final int[] infoMembers = { 254, 1880, 137 };
	private final int[] yatesMembers = { 262 };
	private final int[] nobleMembers = { 1122 };
	private final int[] maccossMembers = { 1049 };
	private final int[] coreMembers = { 137 };
	//private final int[] aebersoldMembers = { 283, 143, 817, 282 };
	
	/*
	 * Define the projectIDs for training projects that are NOT really people asking questions
	 * This includes all dissemination and training projects, such as the plasmid dissemination project(s), seminars, etc
	 * Again, this should be in the props file, but I'm crunched for time
	 * THIS ARRAY MUST BE ORDERED
	 */
	//private final int[] realTrainings = { 1212, 1376, 1377, 1379, 1397, 1421, 1426, 1428, 1429, 1431, 1432, 1433 };
	//private final int[] realTrainings = { 1433, 1429, 1426, 1431, 1432, 1421, 1428, 1212, 1379, 1397, 1376, 1377 };
	
	private final String PROPS_FILE = "/progress.properties";

	// Instance vars loaded in from properties file
	private Date startDate;
	private Date endDate;
	private String progressURL;
	private String hostOrganization;

	private String twoHybridTitle;
	private String microscopyTitle;

	private String twoHybridQuestionTitle;
	private String microscopyQuestionTitle;
	private String msQuestionTitle;

	private String twoHybridAbstract;
	private String microscopyAbstract;	

	private String twoHybridQuestionAbstract;
	private String microscopyQuestionAbstract;	
	private String msQuestionAbstract;
	
	private String microscopyBTA;
	private String twoHybridBTA;
	
	private String microscopyQuestionBTA;
	private String twoHybridQuestionBTA;
	private String msQuestionBTA;
	
	/*
	private String[] twoHybridKeywords;
	private String[] microscopyKeywords;
	private String[] twoHybridMicroscopyKeywords;
	private String[] twoHybridQuestionKeywords;
	private String[] microscopyQuestionKeywords;
	
	private String[] plasmidAxisI;
	private String[] plasmidAxisII;
	private String[] questionAxisI;
	private String[] questionAxisII;
	*/
	
	private String grantSerialNumber;
	private String grantSupportYear;
	private String grantToDate;
	private String grantFromDate;
	private String grantTitle;
	private String grantDirectorName;
	private String grantDirectorTitle;
	private String grantDirectorPhone;
	private String grantDirectorFax;
	private String grantDirectorEmail;
	private String grantHostState;
	private String grantRecipientInstitution;
	private String grantHealthSchoolName;
	private String grantPatent;
	
	private int directorPersonID;
	
	/**
	 * @return The date, after which, projects submitted to the YRC won't be included.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return The date, before which, projects last changed won't be included.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set the date after which projects submitted to the YRC won't be included.
	 * Defaults to the end of time as we know it.
	 * @param date
	 */
	public void setEndDate(Date date) {
		endDate = date;
	}

	/**
	 * Project last changed before the date provided won't be included in the report.
	 * Defaults to The Big Bang.
	 * @param date
	 */
	public void setStartDate(Date date) {
		startDate = date;
	}

	/**
	 * @return The XML generated.
	 */
	public String getXML() {
		return XML;
	}

}
