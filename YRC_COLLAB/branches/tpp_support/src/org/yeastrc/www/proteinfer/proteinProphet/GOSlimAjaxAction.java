/**
 * 
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOSlimAnalysis;
import org.yeastrc.www.go.GOSlimChartUrlCreator;
import org.yeastrc.www.go.GOSlimStatsCalculator;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;

/**
 * GOSlimAjaxAction.java
 * @author Vagisha Sharma
 * May 21, 2010
 * 
 */
public class GOSlimAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		log.info("Got request for GO Slim analysis for protein inference");
		
		// form for filtering and display options
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;

		// get the protein inference id
		int pinferId = filterForm.getPinferId();

		long s = System.currentTimeMillis();
		
		// Get the peptide definition; We don't get peptide definition from ProteinProphet params so just
        // use a dummy one.
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        
        // filtering criteria from the request
        ProteinProphetFilterCriteria filterCriteria_request = filterForm.getFilterCriteria(peptideDef);
        
        // protein Ids
        List<Integer> proteinIds = null;
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        
        // Check if we already have information in the session
        ProteinProphetFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForProteinProphet(request, pinferId);
        proteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        
        // If we don't have a filtering criteria in the session return an error
        if(filterCriteria_session == null || proteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale ProteinProphet ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }
        else {
        	
        	log.info("Found information in session for: "+pinferId);
        	System.out.println("stored protein ids: "+proteinIds.size());
        	 
        	// we will use the sorting column and sorting order from the filter criteria in the session.
        	filterCriteria_request.setSortBy(filterCriteria_session.getSortBy());
        	filterCriteria_request.setSortOrder(filterCriteria_session.getSortOrder());
        	
        	boolean match = matchFilterCriteria(filterCriteria_session, filterCriteria_request);
        	
            
            // if the filtering criteria has changed we need to filter the results again
            if(!match)  {
                
            	log.info("Filtering criteria has changed");
            	
                proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria_request);
            }
        }
		
        int goAspect = filterForm.getGoAspect();
        int goSlimTermId = filterForm.getGoSlimTermId();
        
        // We have the protein inference protein IDs; Get the corresponding nrseq protein IDs
        List<Integer> nrseqIds = new ArrayList<Integer>(proteinIds.size());
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        for(int proteinId: proteinIds) {
            ProteinferProtein protein = protDao.loadProtein(proteinId);
            nrseqIds.add(protein.getNrseqProteinId());
        }
        
        GOSlimStatsCalculator calculator = new GOSlimStatsCalculator();
        calculator.setGoAspect(goAspect);
        calculator.setGoSlimTermId(goSlimTermId);
        calculator.setNrseqProteinIds(nrseqIds);
        calculator.calculate();
		
        GOSlimAnalysis goAnalysis = calculator.getAnalysis();
        request.setAttribute("goAnalysis",goAnalysis);
        
        if(goAnalysis.getNumAnnotated() > 0) {
        	String pieChartUrl = GOSlimChartUrlCreator.getPieChartUrl(goAnalysis, 15);
        	request.setAttribute("pieChartUrl", pieChartUrl);

        	String barChartUrl = GOSlimChartUrlCreator.getBarChartUrl(goAnalysis, 15);
        	request.setAttribute("barChartUrl", barChartUrl);
        }
        
		long e = System.currentTimeMillis();
		log.info("GOSlimAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
	}
	
	private boolean matchFilterCriteria(ProteinProphetFilterCriteria filterCritSession,  ProteinProphetFilterCriteria filterCriteria) {
        return filterCritSession.equals(filterCriteria);
    }

}
