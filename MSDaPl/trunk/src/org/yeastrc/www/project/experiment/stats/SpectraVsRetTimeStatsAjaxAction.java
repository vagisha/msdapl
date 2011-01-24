package org.yeastrc.www.project.experiment.stats;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.stats.PercolatorQCStatsGetter;

public class SpectraVsRetTimeStatsAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(SpectraVsRetTimeStatsAjaxAction.class);

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
        statsGetter.setGetSpectraRtStats(true);
        statsGetter.getStats(analysisId, qvalue);
        
        // -----------------------------------------------------------------------------
        // Spectra-RT plot
        // -----------------------------------------------------------------------------
        request.setAttribute("spectraRTDistributionChart", statsGetter.getSpectraDistrUrl());
        request.setAttribute("spectraRtFileStats", statsGetter.getSpectraFileStats());
        request.setAttribute("spectraAnalysisStats", statsGetter.getSpectraAnalysisStats());
        
        return mapping.findForward("Success");
    }
}
