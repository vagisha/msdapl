package org.yeastrc.conversion.populate_qc_plots.main;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.qc_plots.plot_intensity_per_experiment.service.QC_Plot_IntensityPerExperiment_Plotter;
import org.yeastrc.qc_plots.plot_peaks_per_scan_per_experiment.service.QC_Plot_PeaksPerScanPerExperiment_Plotter;
import org.yeastrc.qc_plots.premz_scan_count_plot.service.PreMZScanCountPlotter;

public class Main {

	private static final Logger log = Logger.getLogger(QC_Plot_IntensityPerExperiment_Plotter.class);

	public static void main(String[] args) throws IOException {
	
		
		
		DAOFactory daoFactory = DAOFactory.instance();
		
		MsExperimentDAO msExperimentDAO = daoFactory.getMsExperimentDAO();
		
		List<Integer> allExperimentsList = msExperimentDAO.getAllExperimentIds();
		
		for ( int experimentId : allExperimentsList ) {
		

            // First check if this experiment is still getting uploaded
            // Add to list only if the upload is failed or complete.
            MSJob job = null;
            int status = 0;
            try {
//                job = MSJobFactory.getInstance().getMsJobForProjectExperiment( projectId, experimentId );
//                status = job.getStatus();
//                if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK)
//                    continue;
            	
            	
                job = MSJobFactory.getInstance().getMsJobForExperiment( experimentId );
                status = job.getStatus();
                
                if ( status != JobUtils.STATUS_COMPLETE ) {
                	
                	continue;
                }
                
            }
            catch(Exception e) {
            	log.error("No job found for experimentID: " + experimentId, e);
            	continue;
            } 

            //  Job is complete and successful so create the plots

            //  Compute data for MZ Scan Count Plot Chart, which will get saved into the database during the call
            PreMZScanCountPlotter.getMZScanCountPlot( experimentId );

            //  Compute data for Plot Chart, which will get saved into the database during the call
            QC_Plot_PeaksPerScanPerExperiment_Plotter.getPeaksPerScanPerExperimentPlot( experimentId );

            //  Compute data for Chart, which will get saved into the database during the call
            QC_Plot_IntensityPerExperiment_Plotter.getIntensityPerExperimentPlot( experimentId );

		}
		
		System.out.println("Run of populate_qc_plots Completed Successfully");
	}
}
;