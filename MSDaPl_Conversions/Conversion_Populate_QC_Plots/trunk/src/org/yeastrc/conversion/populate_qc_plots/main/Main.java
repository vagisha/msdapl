package org.yeastrc.conversion.populate_qc_plots.main;

import java.io.IOException;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.qc_plots.plot_intensity_per_experiment.service.QC_Plot_IntensityPerExperiment_Plotter;
import org.yeastrc.qc_plots.plot_peaks_per_scan_per_experiment.service.QC_Plot_PeaksPerScanPerExperiment_Plotter;
import org.yeastrc.qc_plots.premz_scan_count_plot.service.PreMZScanCountPlotter;

public class Main {

	public static void main(String[] args) throws IOException {
	
		
		
		DAOFactory daoFactory = DAOFactory.instance();
		
		MsExperimentDAO msExperimentDAO = daoFactory.getMsExperimentDAO();
		
		List<Integer> allExperimentsList = msExperimentDAO.getAllExperimentIds();
		
		for ( int experimentId : allExperimentsList ) {
			
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