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
import org.yeastrc.experiment.stats.PercolatorSpectraRetTimeDistribution;
import org.yeastrc.experiment.stats.PercolatorSpectraRetTimeDistributionGetter;
import org.yeastrc.www.util.RoundingUtils;

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
        
        
        DistributionPlotter plotter = new DistributionPlotter();
        PlotUrlCache cache = PlotUrlCache.getInstance();
        
        
     // -----------------------------------------------------------------------------
        // Spectra-RT plot
        // -----------------------------------------------------------------------------
        String spectraDistrUrl = cache.getSpectraRtPlotUrl(analysisId, qvalue);
        List<FileStats> spectraFileStats = cache.getSpectraRtFileStats(analysisId, qvalue);
        int totalSpectraCount = 0; int goodSpectraCount = 0;
        if(spectraDistrUrl == null) {
        	PercolatorSpectraRetTimeDistributionGetter distrGetter = new PercolatorSpectraRetTimeDistributionGetter(analysisId, qvalue);
        	PercolatorSpectraRetTimeDistribution result = distrGetter.getDistribution();
            spectraFileStats = result.getFileStatsList();
            Collections.sort(spectraFileStats, new Comparator<FileStats>() {
				@Override
				public int compare(FileStats o1, FileStats o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
            spectraDistrUrl = plotter.plotGoogleChartForScan_RTDistribution(result);
            PlotUrlCache.getInstance().addSpectraRtPlotUrl(analysisId, qvalue, spectraDistrUrl, spectraFileStats);
        }
        
        log.info("#Spectra-RT Plot URL: "+spectraDistrUrl);
        for(FileStats stat: spectraFileStats) { totalSpectraCount += stat.getTotalCount(); goodSpectraCount += stat.getGoodCount();}
        request.setAttribute("spectraRTDistributionChart", spectraDistrUrl);
        request.setAttribute("spectraRtFileStats", spectraFileStats);
        
        FileStats spectraAnalysisStats = new FileStats(analysisId, "none");
        spectraAnalysisStats.setTotalCount(totalSpectraCount);
        spectraAnalysisStats.setGoodCount(goodSpectraCount);
    	
        if(spectraFileStats.get(0).getHasPopulationStats()) {
        	FileStats st = spectraFileStats.get(0);
        	spectraAnalysisStats.setPopulationMin(st.getPopulationMin());
        	spectraAnalysisStats.setPopulationMax(st.getPopulationMax());
        	spectraAnalysisStats.setPopulationMean(st.getPopulationMean());
        	spectraAnalysisStats.setPopulationStandardDeviation(st.getPopulationStandardDeviation());
        }
        
        request.setAttribute("spectraAnalysisStats", spectraAnalysisStats);
        
        
        
        return mapping.findForward("Success");
    }
}
