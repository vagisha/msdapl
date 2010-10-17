/**
 * PsmVsRetTimeStatsAjaxAction.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.www.project.experiment.stats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.stats.DistributionPlotter;
import org.yeastrc.experiment.stats.FileStats;
import org.yeastrc.experiment.stats.PercolatorPsmRetTimeDistribution;
import org.yeastrc.experiment.stats.PercolatorPsmRetTimeDistributionGetter;

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
        
        
        DistributionPlotter plotter = new DistributionPlotter();
        PlotUrlCache cache = PlotUrlCache.getInstance();
        
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        String psmDistrUrl = cache.getPsmRtPlotUrl(analysisId, qvalue);
        List<FileStats> psmFileStats = cache.getPsmRtFileStats(analysisId, qvalue);
        int totalCount = 0; int goodCount = 0;
        if(psmDistrUrl == null) {
        	PercolatorPsmRetTimeDistributionGetter distrGetter = new PercolatorPsmRetTimeDistributionGetter(analysisId, qvalue);
        	PercolatorPsmRetTimeDistribution result = distrGetter.getDistribution();
            psmFileStats = result.getFileStatsList();
            Collections.sort(psmFileStats, new Comparator<FileStats>() {
				@Override
				public int compare(FileStats o1, FileStats o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
            psmDistrUrl = plotter.plotGoogleChartForPSM_RTDistribution(result);
            cache.addPsmRtPlotUrl(analysisId, qvalue, psmDistrUrl, psmFileStats);
        }
        
        log.info("#PSM-RT Plot URL: "+psmDistrUrl);
        
        for(FileStats stat: psmFileStats) { totalCount += stat.getTotalCount(); goodCount += stat.getGoodCount();}
        request.setAttribute("psmRTDistributionChart", psmDistrUrl);
        request.setAttribute("psmRtFileStats", psmFileStats);
        
        FileStats psmAnalysisStats = new FileStats(analysisId, "none");
    	psmAnalysisStats.setTotalCount(totalCount);
    	psmAnalysisStats.setGoodCount(goodCount);
    	
        if(psmFileStats.get(0).getHasPopulationStats()) {
        	FileStats st = psmFileStats.get(0);
        	psmAnalysisStats.setPopulationMin(st.getPopulationMin());
        	psmAnalysisStats.setPopulationMax(st.getPopulationMax());
        	psmAnalysisStats.setPopulationMean(st.getPopulationMean());
        	psmAnalysisStats.setPopulationStandardDeviation(st.getPopulationStandardDeviation());
        }
        
        request.setAttribute("psmAnalysisStats", psmAnalysisStats);
        
        
        return mapping.findForward("Success");
    }
}
