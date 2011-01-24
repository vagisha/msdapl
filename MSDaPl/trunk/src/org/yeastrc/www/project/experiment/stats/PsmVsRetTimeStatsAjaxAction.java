/**
 * PsmVsRetTimeStatsAjaxAction.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.www.project.experiment.stats;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.stats.PercolatorQCStatsGetter;

/**
 * 
 */
public class PsmVsRetTimeStatsAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(PsmVsRetTimeStatsAjaxAction.class);

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        int analysisId = 0;
        String strVal = null;
        try {
            strVal = request.getParameter("analysisId");
            if(strVal != null)
                analysisId = Integer.parseInt(strVal);


        } catch (NumberFormatException nfe) {
           analysisId = 0;
        }
        
        if(analysisId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid analysis ID: "+strVal+"</b>");
            return null;
        }

        Double qvalue = null;
        try {
        	strVal = request.getParameter("qvalue");
        	if(strVal != null) {
        		qvalue = Double.parseDouble(strVal);
        	}
        }
        catch(Exception e) {qvalue = null;}
        if(qvalue == null) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid qvalue: "+strVal+"</b>");
            return null;
        }
        request.setAttribute("qvalue", qvalue);
        
        
        PercolatorQCStatsGetter statsGetter = new PercolatorQCStatsGetter();
        statsGetter.setGetPsmRtStats(true);
        statsGetter.getStats(analysisId, qvalue);
        
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        request.setAttribute("psmRTDistributionChart", statsGetter.getPsmDistrUrl());
        request.setAttribute("psmRtFileStats", statsGetter.getPsmFileStats());
        request.setAttribute("psmAnalysisStats", statsGetter.getPsmAnalysisStats());
        
        
        return mapping.findForward("Success");
    }
}
