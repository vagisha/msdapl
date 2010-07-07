/**
 * GOTermSearchAction.java
 * @author Vagisha Sharma
 * Jul 5, 2010
 */
package org.yeastrc.www.go;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class GOTermSearchAction extends Action {

	private static final Logger log = Logger.getLogger(GOTermSearchAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		
		long s = System.currentTimeMillis();
		
        
		GOTermSearchForm myForm = (GOTermSearchForm)form;
		request.setAttribute( "goTermSearchForm", myForm);
		
		if (myForm.getTerms() == null || myForm.getTerms().trim().length() == 0 )
			return mapping.findForward( "Success" );
		
		String terms = myForm.getTerms();
		terms = terms.replaceAll(",", " "); // replace commas with spaces
		terms = terms.replaceAll("\\s+", " "); // replace multiple spaces with single space.
		myForm.setTerms(terms);
		
		List<GONode> nodes = GoTermSearcher.getMatchingTerms(myForm.getTerms().trim(),
											myForm.isBiologicalProcess(),
											myForm.isMolecularFunction(),
											myForm.isCellularComponent(),
											myForm.isMatchAllTerms(),
											myForm.isSearchTermSynonyms());
		
		
		request.setAttribute( "nodes", nodes );
		request.setAttribute( "numNodes", nodes.size() );
		
		
		long e = System.currentTimeMillis();
		log.info("GOTermSearchAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
	}
}
