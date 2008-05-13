/*
 * ViewSpectraAction.java
 * Created on Oct 20, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesCycle;
import org.yeastrc.yates.YatesCycleFactory;
import org.yeastrc.yates.YatesPeptide;
import org.yeastrc.yates.YatesResult;
import org.yeastrc.yates.YatesRun;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 20, 2004
 */

public class ViewSpectraAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// The run we're viewing
		int pepID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		YatesPeptide yp = null;
		YatesRun run = null;
		YatesResult result = null;
		try {
			String strID = request.getParameter("id");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			pepID = Integer.parseInt(strID);

			// Load our screen
			yp = new YatesPeptide();
			yp.load(pepID);

			result = new YatesResult();
			result.load(yp.getResultID());
			run = (YatesRun)(result.getRun());

			Project project = run.getProject();
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (InvalidIDException iie) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		request.setAttribute("run", run);
		request.setAttribute("result", result);
		request.setAttribute("peptide", yp);		

		try {
			String Dr = run.getDirectoryName();
			String Sq = yp.getSequence();
			String filename = yp.getFilename();
	
			String[] fields = filename.split("\\.");
			String Z = fields[fields.length - 1];
	
			// Scan Number
			String Sc = fields[fields.length - 3];
			
			// Dat file
			String Da = fields[0];
	
			// clean
			fields = null;
	
			// Get our cycle
			YatesCycle cycle = YatesCycleFactory.getInstance().getCycle(Da, run);
			if (cycle == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("spectra", new ActionMessage("error.yates.spectra.cyclenotfound"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			
	
			String Sd = ".";
			String directory_filebase = Da + "/" + Da;
			String fullpath = Dr + "/" + directory_filebase + "." + Sc + ".*." + Z;
			directory_filebase = null;
			
			String SqMod = "";
			
			// Parse the DTA information
			YatesSpectraMS2Parser ms2parser = null;
			try {
				ms2parser = new YatesSpectraMS2Parser();
				ms2parser.parseMS2Spectra(cycle, Sc, Z);
			} catch (Exception e) {
				ActionErrors errors = new ActionErrors();
				errors.add("spectra", new ActionMessage("error.yates.spectra.ms2error", e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			
			// Parse the SQT information
			YatesSpectraSQTParser sqtparser = null;
			try {
				sqtparser = new YatesSpectraSQTParser();
				sqtparser.parseSQTData(cycle, Sc, Z);
			} catch (Exception e) {
				ActionErrors errors = new ActionErrors();
				errors.add("spectra", new ActionMessage("error.yates.spectra.sqterror", e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
				
			
			// Set up some values to send to the View Page
			LinkedList masslist = ms2parser.getMasslist();
			LinkedList intlist = ms2parser.getIntlist();
			List params = new LinkedList();
			
			request.setAttribute("firstMass", masslist.getFirst());
			request.setAttribute("firstCharge", intlist.getFirst());
			request.setAttribute("datfile", Da);
			request.setAttribute("scanNumber", Sc);
			request.setAttribute("database", sqtparser.getDbase());
			
	
			// SET UP THE PARAM LIST TO PASS TO THE APPLET ON THE VIEW PAGE
			params.add("<PARAM NAME=\"PreMPlusH\" VALUE=\"" + masslist.removeFirst() + "\">");
			params.add("<PARAM NAME=\"PreZ\" VALUE=\"" + intlist.removeFirst() + "\">");
	
			// Handle masses and charges
			Iterator iter = masslist.iterator();
			int i = 0;
			Object[] masses = masslist.toArray();
			Object[] ints = intlist.toArray();
			while (iter.hasNext()) {
				i++;
		        params.add("<PARAM NAME=\"MZ" + i + "\" VALUE=\"" + (String)(masses[i-1]) + "\">");
		        params.add("<PARAM NAME=\"Int" +i + "\" VALUE=\"" + (String)(ints[i-1]) + "\">");
		        iter.next();
			}
			masses = null;
			ints = null;
			
			// Handle static mod
			LinkedList staticmod = sqtparser.getStaticmod();
			iter = staticmod.iterator();
			i = 1;
			while (iter.hasNext()) {
				String smod = (String)(iter.next());
				String[] tmp = smod.split("=");
				if (tmp.length < 2) continue;
				
				String smr = tmp[0];
				String smm = tmp[1];
				
		        params.add("<PARAM NAME=\"SMM" + i + "\" VALUE=\"" + smm + "\">");
		        params.add("<PARAM NAME=\"SMR" + i + "\" VALUE=\"" + smr + "\">");
		        
		        i++;
			}
			staticmod = null;
	
			
			/*
			// Handle diff mods
			LinkedList diffmod = sqtparser.getDiffmod();
			iter = diffmod.iterator();
			i = 1;
			while (iter.hasNext()) {
				String dmod = (String)(iter.next());
				String[] tmp = dmod.split("=");
				String dmm = null;
				String dms = null;
	
				if (tmp.length == 2) {
					
					// New SEQUEST format
					dmm = tmp[0];
					dms = tmp[1];
				} else {
					
					// Old SEQUEST format
					Pattern p = Pattern.compile("[A-Z0-9]+?(\\S)\\s([\\+\\-]\\d+?\\.\\d+)");
					Matcher m = p.matcher(dmod);
					if (m.matches()) {
						dmm = m.group(1);
						dms = m.group(2);
					}
				}
				
				if (dmm != null && dms != null) {
					params.add("<PARAM NAME=\"DMM" + i + "\" VALUE=\"" + dmm + "\">");
			        params.add("<PARAM NAME=\"DMS" + i + "\" VALUE=\"" + dms + "\">");
				}			
			}
			*/
			
			params.add("<PARAM NAME=\"CPepMod\" VALUE=\"0.0\">\n");
			params.add("<PARAM NAME=\"NPepMod\" VALUE=\"0.0\">\n");
			params.add("<PARAM NAME=\"CProtMod\" VALUE=\"0.0\">\n");
			params.add("<PARAM NAME=\"NProtMod\" VALUE=\"0.0\">\n");
			params.add("<PARAM NAME=\"AvgForFrag\" VALUE=\"" + sqtparser.getAvgForFrag() + "\">");
			params.add("<PARAM NAME=\"AvgForParent\" VALUE=\"" + sqtparser.getAvgForParent() + "\">");		
			
			/*
			List seqMatches = sqtparser.getSequestLines();
			iter = seqMatches.iterator();
			int check;
			i = 0;
			while (iter.hasNext()) {
				String thePeptide = ((YatesSpectraSQTParser.SequestLine)iter.next()).getPeptide();
				String nakedPeptide = thePeptide;
				int whichPeptide = 0;
				Pattern p = Pattern.compile("\\.(\\S+?)\\.");
				Matcher m = p.matcher(nakedPeptide);
				if (m.matches())
					nakedPeptide = m.group(1);
				
				String nakedSq = Sq;
				nakedSq = nakedSq.replaceAll("\\W", "");
				nakedPeptide = nakedPeptide.replaceAll("\\W", "");
				
				if (nakedPeptide.equals("nakedSq")) {
					check = i + 1;
					params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + thePeptide + "\">");
					break;
				}
				i++;
			}
			*/
			
			params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + Sq + "\">");
			request.setAttribute("params", params);
			
			// Clean up
			run = null;
			result = null;
			yp = null;
			masslist = null;
			intlist = null;
			sqtparser = null;
			ms2parser = null;
			// seqMatches = null;
			iter = null;

		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("spectra", new ActionMessage("error.yates.spectra.general", e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Do a garbage collection now, to clean up.
		System.gc();
		
		return mapping.findForward("Success");
	}

}