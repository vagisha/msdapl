package org.yeastrc.www.sandbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesPeptide;
import org.yeastrc.yates.YatesResult;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunSearcher;

public class ProjectPhosphoStatsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		// the project we're getting summary phosphorylation statistics on
		Project project = null;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		try {
			project = ProjectFactory.getProject( Integer.parseInt( request.getParameter( "id" ) ) );
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}
		
		
		// get all MS runs associated with this project
		YatesRunSearcher yrs = new YatesRunSearcher();
		yrs.setProjectID( project.getID() );
		Collection<YatesRun> runs = yrs.search();
		
		// this is where we are going to store all that we find out!
		Map<NRProtein, Map<Integer, Set<String>>> proteinPhosSites = new HashMap<NRProtein, Map<Integer, Set<String>>>();
		Map<NRProtein, Map<Integer, Set<Integer>>> proteinPhosRuns = new HashMap<NRProtein, Map<Integer, Set<Integer>>>();
		
		// loop through all runs for this project
		//for ( YatesRun run : runs ) {
		Iterator<YatesRun> iter = runs.iterator();
		while ( iter.hasNext()) {
			
			YatesRun run = iter.next();
			//System.out.println( "Parsing run " + run.getId() );
			
			// loop through all protein identifications for this run
			Collection<YatesResult> results = run.getResults();
			for( YatesResult result : results ) {
				NRProtein hitProtein = (NRProtein)result.getHitProtein();
				
				//if (hitProtein.getId() != 530712) continue;
				
				String hitSequence = hitProtein.getPeptide().getSequenceString();
				
				//System.out.println( "\tParsing protein: " + hitProtein.getListing() );
				
				// loop through all peptides used to identify this protein in this run
				for( YatesPeptide peptide : result.getPeptides() ) {
				
					String pep = peptide.getSequence();
					if (!pep.contains( "*" ) )	continue;		// peptide contains no phosphorylation sites, skip it
					
					//System.out.println( "\t\tFound phos. seq.:" + peptide.getPeptide().getSequenceString() );
					
					// where is this peptide located in the parent sequence
					int startIndex = hitSequence.indexOf( peptide.getPeptide().getSequenceString() );
					
					if ( startIndex < 0 )
						throw new Exception( "Could not find peptide sequence in parent sequence?\nPeptide: " + peptide.getPeptide().getSequenceString() + "\nSequence: " + hitSequence );
					
					// loop through all phosphorylation sites identified in this peptide
					int pIndex = pep.indexOf( "*" );
					int counter = 0;
					while (pIndex > 0) {
						
						int pLocation = startIndex + pIndex - 2 - counter;
						
						//System.out.println( "\t\t\tFound phosphorylation: " + pIndex + " (" + pLocation + ")");
						
						if( !proteinPhosSites.containsKey( hitProtein ) )
							proteinPhosSites.put( hitProtein, new HashMap<Integer, Set<String>>() );

						if( !proteinPhosRuns.containsKey( hitProtein ) )
							proteinPhosRuns.put( hitProtein, new HashMap<Integer, Set<Integer>>() );
						
						if( !proteinPhosSites.get( hitProtein ).containsKey( pLocation ) )
							proteinPhosSites.get( hitProtein ).put( pLocation, new HashSet<String>() );

						if( !proteinPhosRuns.get( hitProtein ).containsKey( pLocation ) )
							proteinPhosRuns.get( hitProtein ).put( pLocation, new HashSet<Integer>() );
						
						// add this peptide sequence to the set of peptides used to identify this phosphorylation location in this protein
						proteinPhosSites.get( hitProtein ).get( pLocation ).add( peptide.getPeptide().getSequenceString() );
						proteinPhosRuns.get( hitProtein ).get( pLocation ).add( run.getId() );
						
						pIndex = pep.indexOf( "*", pIndex + 1 );
						counter++;
					}//end looping through phosphorylation sites in this peptide
				}// end looping through peptides
			}//end looping through identified proteins in this run
			
			// delete this run to clear some memory
			iter.remove();
			System.gc();
			
		}//end looping through runs

		//generate tab delimited text of results and dump to text viewing jsp page
		StringBuffer output = new StringBuffer();
		for ( NRProtein protein : proteinPhosSites.keySet() ) {
			List<Integer> sites = new ArrayList<Integer>( proteinPhosSites.get( protein ).keySet() );
			Collections.sort( sites );
			
			for( int site : sites ) {
				
				int count = proteinPhosSites.get( protein ).get( site ).size();

				output.append( protein.getListing() + "\t" + site + "\t" + count + "\t" );
				output.append( org.apache.commons.lang.StringUtils.join( proteinPhosRuns.get( protein ).get( site ).iterator(), "," ) );
				output.append( "\n" );
			}
		}
		
		request.setAttribute( "textToPrint", output.toString() );
		
		return mapping.findForward( "Success" );
	}
}
