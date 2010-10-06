/**
 * ViewExperimentStats.java
 * @author Vagisha Sharma
 * Dec 8, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment.stats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.stats.DistributionPlotter;
import org.yeastrc.experiment.stats.FileStats;
import org.yeastrc.experiment.stats.PercolatorPsmRetTimeDistribution;
import org.yeastrc.experiment.stats.PercolatorPsmRetTimeDistributionGetter;
import org.yeastrc.experiment.stats.PercolatorSpectraRetTimeDistribution;
import org.yeastrc.experiment.stats.PercolatorSpectraRetTimeDistributionGetter;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAnalysisStats extends Action {

    private static final Logger log = Logger.getLogger(ViewAnalysisStats.class);

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        int analysisId = 0;
        try {
            String strID = request.getParameter("analysisId");
            if(strID != null)
                analysisId = Integer.parseInt(strID);


        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // If we still don't have a valid analysis id, return an error
        if(analysisId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        request.setAttribute("analysisId", analysisId);
        request.setAttribute("qvalue", 0.01);
        
        int experimentId = 0;
        try {
            String strID = request.getParameter("experimentId");
            if(strID != null)
                experimentId = Integer.parseInt(strID);


        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // If we still don't have a valid experiment id, return an error
        if(experimentId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // -----------------------------------------------------------------------------
        // Set up the form
        // -----------------------------------------------------------------------------
        QcPlotsFilterForm myForm = (QcPlotsFilterForm) form;
        myForm.setExperimentId(experimentId);
        myForm.setAnalysisId(analysisId);
        myForm.setQvalue(0.01);
        //request.setAttribute("filterForm", myForm);
        
        
        DistributionPlotter plotter = new DistributionPlotter();
        PlotUrlCache cache = PlotUrlCache.getInstance();
        
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        String psmDistrUrl = cache.getPsmRtPlotUrl(analysisId, myForm.getQvalue());
        List<FileStats> psmFileStats = cache.getPsmRtFileStats(analysisId, myForm.getQvalue());
        int totalCount = 0; int goodCount = 0;
        if(psmDistrUrl == null) {
        	PercolatorPsmRetTimeDistributionGetter distrGetter = new PercolatorPsmRetTimeDistributionGetter(analysisId, myForm.getQvalue());
        	PercolatorPsmRetTimeDistribution result = distrGetter.getDistribution();
            psmFileStats = result.getFileStatsList();
            Collections.sort(psmFileStats, new Comparator<FileStats>() {
				@Override
				public int compare(FileStats o1, FileStats o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
            psmDistrUrl = plotter.plotGoogleChartForPSM_RTDistribution(result);
            cache.addPsmRtPlotUrl(analysisId, myForm.getQvalue(), psmDistrUrl, psmFileStats);
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
        
        
        // -----------------------------------------------------------------------------
        // Spectra-RT plot
        // -----------------------------------------------------------------------------
        String spectraDistrUrl = cache.getSpectraRtPlotUrl(analysisId, myForm.getQvalue());
        List<FileStats> spectraFileStats = cache.getSpectraRtFileStats(analysisId, myForm.getQvalue());
        int totalSpectraCount = 0; int goodSpectraCount = 0;
        if(spectraDistrUrl == null) {
        	PercolatorSpectraRetTimeDistributionGetter distrGetter = new PercolatorSpectraRetTimeDistributionGetter(analysisId, myForm.getQvalue());
        	PercolatorSpectraRetTimeDistribution result = distrGetter.getDistribution();
            spectraFileStats = result.getFileStatsList();
            Collections.sort(spectraFileStats, new Comparator<FileStats>() {
				@Override
				public int compare(FileStats o1, FileStats o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
            spectraDistrUrl = plotter.plotGoogleChartForScan_RTDistribution(result);
            PlotUrlCache.getInstance().addSpectraRtPlotUrl(analysisId, myForm.getQvalue(), spectraDistrUrl, spectraFileStats);
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
        
//        request.setAttribute("spectraRTDistributionChart", "http://chart.apis.google.com/chart?cht=bvs&chbh=a&chs=500x325&chdl=MS/MS%20scans%20with%20good%20results%20(qvalue%3C=0.01)|All%20MS/MS%20Scans&chdlp=t&chg=10,10&chf=c,s,EFEFEF&chxt=x,y,x,y&chxl=2:|Retention%20Time|3:|Scans|&chxp=2,50|3,50&chxr=0,0,119.96055,11|1,0,1491,149&chds=0,1501,0,1501&chco=008000,800080&chd=t:87,26,27,47,24,28,90,232,381,398,407,360,475,503,577,532,537,533,546,447,504,513,477,497,477,422,501,440,426,406,468,443,496,502,437,470,449,448,427,461,446,443,429,444,463,421,373,327,156,7|669,427,672,699,487,402,451,774,1066,1045,1078,1115,1015,988,914,950,944,946,936,1024,965,939,979,959,979,1028,940,994,1009,1020,958,979,928,914,962,928,948,940,951,916,925,918,905,874,860,903,912,824,371,31");
        
        return mapping.findForward("Success");
    }
}
