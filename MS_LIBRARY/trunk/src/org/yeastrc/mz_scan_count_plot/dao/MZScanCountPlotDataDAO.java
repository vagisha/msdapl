package org.yeastrc.mz_scan_count_plot.dao;

import org.yeastrc.mz_scan_count_plot.dto.MZScanCountPlotDataDTO;

/**
 * 
 * for table mz_scan_count_plot_data
 */
public interface MZScanCountPlotDataDAO {

    public int save(MZScanCountPlotDataDTO mzScanCountPlotDataDTO);
    
    public MZScanCountPlotDataDTO load(int experimentId);
    
    public MZScanCountPlotDataDTO loadFromExperimentIdAndDataVersion(int experimentId, int dataVersion);
}
