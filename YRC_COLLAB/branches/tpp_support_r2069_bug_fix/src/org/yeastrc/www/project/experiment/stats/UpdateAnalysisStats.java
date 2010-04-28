/**
 * UpdateAnalysisStats.java
 * @author Vagisha Sharma
 * Jan 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.project.experiment.stats;

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
import org.yeastrc.experiment.stats.RetTimePSMDistributionCalculator;
import org.yeastrc.experiment.stats.RetTimeSpectraDistributionCalculator;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class UpdateAnalysisStats extends Action {

    private static final Logger log = Logger.getLogger(ViewAnalysisStats.class.getName());

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

        QcPlotsFilterForm myForm = (QcPlotsFilterForm) form;
        
        int analysisId = myForm.getAnalysisId();
        // If we don't have a valid id, return an error
        if(analysisId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int experimentId = myForm.getExperimentId();
        // If we don't have a valid id, return an error
        if(experimentId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        DistributionPlotter plotter = new DistributionPlotter();
        PlotUrlCache cache = PlotUrlCache.getInstance();
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        String psmDistrUrl = cache.getPsmRtPlotUrl(analysisId, myForm.getQvalue());
        List<FileStats> psmFileStats = cache.getPsmRtFileStats(analysisId, myForm.getQvalue());
        int totalCount = 0; int goodCount = 0; double goodPerc = 0;
        if(psmDistrUrl == null) {
            RetTimePSMDistributionCalculator dg = new RetTimePSMDistributionCalculator(analysisId, myForm.getQvalue());
            dg.calculate();
            psmFileStats = dg.getFileStats();
            psmDistrUrl = plotter.plotGoogleChartForPSM_RTDistribution(dg);
            cache.addPsmRtPlotUrl(analysisId, myForm.getQvalue(), psmDistrUrl, psmFileStats);
        }
        
        log.info("#PSM-RT Plot URL: "+psmDistrUrl);
        
        for(FileStats stat: psmFileStats) { totalCount += stat.getTotalCount(); goodCount += stat.getGoodCount();}
        goodPerc = Math.round(((double)goodCount/(double)totalCount) * 100.0);
        request.setAttribute("psmRTDistributionChart", psmDistrUrl);
        request.setAttribute("psmRtFileStats", psmFileStats);
        request.setAttribute("totalPsmCount", totalCount);
        request.setAttribute("goodPsmCount", goodCount);
        request.setAttribute("goodPsmPerc", goodPerc);
        
        
        // -----------------------------------------------------------------------------
        // Spectra-RT plot
        // -----------------------------------------------------------------------------
        String spectraDistrUrl = cache.getSpectraRtPlotUrl(analysisId, myForm.getQvalue());
        List<FileStats> spectraFileStats = cache.getSpectraRtFileStats(analysisId, myForm.getQvalue());
        int totalSpectraCount = 0; int goodSpectraCount = 0; double goodSpectraPerc = 0;
        if(spectraDistrUrl == null) {
            RetTimeSpectraDistributionCalculator dg = new RetTimeSpectraDistributionCalculator(analysisId, myForm.getQvalue());
            dg.calculate();
            spectraFileStats = dg.getFileStats();
            spectraDistrUrl = plotter.plotGoogleChartForScan_RTDistribution(dg);
            PlotUrlCache.getInstance().addSpectraRtPlotUrl(analysisId, myForm.getQvalue(), spectraDistrUrl, spectraFileStats);
        }
        
        log.info("#Spectra-RT Plot URL: "+spectraDistrUrl);
        for(FileStats stat: spectraFileStats) { totalSpectraCount += stat.getTotalCount(); goodSpectraCount += stat.getGoodCount();}
        goodSpectraPerc = Math.round(((double)goodSpectraCount/(double)totalSpectraCount) * 100.0);
        request.setAttribute("spectraRTDistributionChart", spectraDistrUrl);
        request.setAttribute("spectraRtFileStats", spectraFileStats);
        request.setAttribute("totalSpectraCount", totalSpectraCount);
        request.setAttribute("goodSpectraCount", goodSpectraCount);
        request.setAttribute("goodSpectraPerc", goodSpectraPerc);
        
        
        return mapping.findForward("Success");
    }
}
