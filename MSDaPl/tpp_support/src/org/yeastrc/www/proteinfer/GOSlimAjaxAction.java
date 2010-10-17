/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOSlimAnalysis;
import org.yeastrc.www.go.GOSlimChartUrlCreator;
import org.yeastrc.www.go.GOSlimStatsCalculator;

/**
 * GOSlimAjaxAction.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class GOSlimAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		
		long s = System.currentTimeMillis();
		
        int goAspect = (Integer)request.getAttribute("goAspect");
        int goSlimTermId = (Integer) request.getAttribute("goSlimTermId");
        List<Integer> nrseqIds = (List<Integer>) request.getAttribute("nrseqProteinIds");
        
        
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
}
