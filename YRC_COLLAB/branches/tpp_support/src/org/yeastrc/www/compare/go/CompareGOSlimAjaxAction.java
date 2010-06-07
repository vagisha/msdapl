/**
 * 
 */
package org.yeastrc.www.compare.go;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.go.GOSlimAnalysis;
import org.yeastrc.www.go.GOSlimChartUrlCreator;
import org.yeastrc.www.go.GOSlimStatsCalculator;

/**
 * CompareGOSlimAjaxAction.java
 * @author Vagisha Sharma
 * Jun 3, 2010
 * 
 */
public class CompareGOSlimAjaxAction extends Action{

	private static final Logger log = Logger.getLogger(CompareGOSlimAjaxAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        long s = System.currentTimeMillis();
        
        ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
        if(comparison == null) {
            request.setAttribute("errorMessage", "Comparison dataset not found in request");
            return mapping.findForward("FailureMessage");
        }
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
        	request.setAttribute("errorMessage", "Comparison form not found in request");
            return mapping.findForward("FailureMessage");
        }
        
        
        int goAspect = myForm.getGoAspect();
        int goSlimTermId = myForm.getGoSlimTermId();
        
        List<Integer> nrseqIds = new ArrayList<Integer>(comparison.getProteins().size());
        log.info(nrseqIds.size()+" proteins for GO enrichment analysis");
        for(ComparisonProtein protein: comparison.getProteins()) {
            nrseqIds.add(protein.getNrseqId());
        }
        
        GOSlimStatsCalculator calculator = new GOSlimStatsCalculator();
        calculator.setGoAspect(goAspect);
        calculator.setGoSlimTermId(goSlimTermId);
        calculator.setNrseqProteinIds(nrseqIds);
        calculator.calculate();
		
        GOSlimAnalysis goAnalysis = calculator.getAnalysis();
        if(goAnalysis.getNumAnnotated() > 0) {
        	String pieChartUrl = GOSlimChartUrlCreator.getPieChartUrl(goAnalysis, 15);
            request.setAttribute("pieChartUrl", pieChartUrl);
            
            String barChartUrl = GOSlimChartUrlCreator.getBarChartUrl(goAnalysis, 15);
            request.setAttribute("barChartUrl", barChartUrl);
        }
        
        request.setAttribute("goAnalysis",goAnalysis);
        
		long e = System.currentTimeMillis();
		log.info("CompareGOSlimAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
    }
}
