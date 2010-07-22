package org.yeastrc.www.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.yeastrc.project.ResearcherAutocompleteSearcher;

public class ResearcherAutocompleteService extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		// Require the user to be logged in for this to work...
		/*
		User user = UserUtils.getUser(request);
		if (user == null) {
			return null;
		}
		*/
		
		String query = null;
		try {
			query = request.getParameter( "query" );
		} catch (Exception e ) { ; }
		
		Map<String,Integer> lhm = ResearcherAutocompleteSearcher.getInstance().search( query );
		
		//use JSON.simple library to wrap up the data into JSON (http://code.google.com/p/json-simple/)
		JSONArray researcherNames = new JSONArray();
		JSONArray researcherIDs = new JSONArray();
		
		for( String name : lhm.keySet() ) {
			researcherNames.add( name );
			researcherIDs.add( lhm.get( name ) );
		}
		
		JSONObject jobj = new JSONObject();
		jobj.put( "query", query );
		jobj.put( "suggestions", researcherNames );
		jobj.put( "data", researcherIDs);
		
		request.setAttribute( "textToPrint", jobj.toString() );
		
		return mapping.findForward( "Success" );
	}

}
