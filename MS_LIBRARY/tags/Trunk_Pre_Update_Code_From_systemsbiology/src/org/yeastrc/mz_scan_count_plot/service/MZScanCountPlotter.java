package org.yeastrc.mz_scan_count_plot.service;

//import java.math.BigDecimal;
//import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.mz_scan_count_plot.dao.MZScanCountPlotDataDAO;
import org.yeastrc.mz_scan_count_plot.dao.jdbc.MZScanCountPlotDataDAOImpl;
import org.yeastrc.mz_scan_count_plot.dto.MZScanCountPlotDataDTO;


/**
 * Creates the JSON to define the Precursor MZ chart
 * 
 * The computed JSON is stored using MZScanCountPlotDataDAO
 * and retrieve uses that for the next request for the same
 * experiment id and if the data_version matches CURRENT_DATA_VERSION
 *
 */
public class MZScanCountPlotter {


	private static final Logger log = Logger.getLogger(MZScanCountPlotter.class);

    private static DAOFactory daoFactory = DAOFactory.instance();

	private static final int BIN_COUNT_PREMZ_VALUES = 50;  //  Number of bars on the chart


	 //  Increment this when change the JSON format so the existing data stored in the table will not be used
	private static final int CURRENT_DATA_VERSION = 1;
	


	
	/**
	 * @param experimentId
	 * @return
	 */
	public static String getMZScanCountPlot( int experimentId ) {

		String plotData = getStoredMZScanCountPlotFromDB( experimentId );
		
		if ( plotData != null ) {
			
			return plotData;
		}
		
		MZScanCountPlotDataDAO mzScanCountPlotDataDAO = new MZScanCountPlotDataDAOImpl();
		
		MZScanCountPlotDataDTO mzScanCountPlotDataDTO = generateMZScanCountPlotFromDB( experimentId );
		
		mzScanCountPlotDataDTO.setDataVersion( CURRENT_DATA_VERSION );

		try {
			
			mzScanCountPlotDataDAO.save( mzScanCountPlotDataDTO );
		
		} catch (Exception ex ) {
			
			log.error( "MZScanCountPlotter: getMZScanCountPlot(): Error saving stored plot data to DB", ex);
		}
		
		return mzScanCountPlotDataDTO.getPlotData();
	}
	

	/**
	 * @param experimentId
	 * @return
	 */
	public static String getStoredMZScanCountPlotFromDB( int experimentId ) {

		MZScanCountPlotDataDAO mzScanCountPlotDataDAO = new MZScanCountPlotDataDAOImpl();
		
		MZScanCountPlotDataDTO mzScanCountPlotDataDTO = null;
		
		try {
		
			mzScanCountPlotDataDTO = mzScanCountPlotDataDAO.loadFromExperimentIdAndDataVersion( experimentId, CURRENT_DATA_VERSION );

			if ( mzScanCountPlotDataDTO != null ) {

				return mzScanCountPlotDataDTO.getPlotData();
			}
		
		} catch (Exception ex ) {
			
			log.error( "MZScanCountPlotter: getMZScanCountPlot(): Error getting stored plot data from DB", ex);
		}
		
		return null;
	}
	
	
	/**
	 * @param experimentId
	 * @return
	 */
	public static MZScanCountPlotDataDTO generateMZScanCountPlotFromDB( int experimentId ) {
		
		long startTime = System.currentTimeMillis();
		
		double[] preMZArray  = daoFactory.getMsScanDAO().getPreMZArrayForExperimentId(experimentId);
		
		int numScans = preMZArray.length;

		double precursorMZMinDouble = Double.MAX_VALUE;
		double precursorMZMaxDouble =  -Double.MAX_VALUE;
		
		//  Find max and min values
		
		for ( double preMZ : preMZArray ) {
			
			if ( preMZ < precursorMZMinDouble ) {
				precursorMZMinDouble = preMZ;
			}
			
			if ( preMZ > precursorMZMaxDouble ) {
				precursorMZMaxDouble = preMZ;
			}

		}
		
		double precursorMZMaxMinusMin = precursorMZMaxDouble - precursorMZMinDouble;

		
		double binSizeAsDouble = ( precursorMZMaxMinusMin ) / BIN_COUNT_PREMZ_VALUES;

		
		int[] scanCounts = new int[ BIN_COUNT_PREMZ_VALUES ];
		
		for ( double preMZ : preMZArray ) {
			
			int bin = (int) ( (  ( preMZ - precursorMZMinDouble )  / precursorMZMaxMinusMin ) * BIN_COUNT_PREMZ_VALUES );
			
			if ( bin < 0 ) {
				
				bin = 0;
			} else if ( bin >= BIN_COUNT_PREMZ_VALUES ) {
				
				bin = BIN_COUNT_PREMZ_VALUES - 1;
			} 
			
			scanCounts[ bin ]++;
		}
		
		
		
		//  Generate JSON   All labels/object member names must be in " (double quotes)
		
		StringBuilder plotDataSB = new StringBuilder( 10000 );
		
		plotDataSB.append("{\"experimentId\":");
		plotDataSB.append(experimentId);
		plotDataSB.append(",\"numScans\":");
		plotDataSB.append(numScans);
		plotDataSB.append(",\"precursorMZMax\":");
//		outputDataSB.append(precursorMZMax);
		plotDataSB.append(precursorMZMaxDouble);
		plotDataSB.append(",\"precursorMZMin\":");
//		outputDataSB.append(precursorMZMin);
		plotDataSB.append(precursorMZMinDouble);
		
		
		//  change to output the general data that will be re-formatted in the Javascript
		//  to be what Google chart can use

		plotDataSB.append(",\"chartBuckets\":");		
		
		//  start of array
		plotDataSB.append("[");
		
		int counter = 0;
		for ( int scanCount : scanCounts ) {

			if ( counter > 0 ) {
				plotDataSB.append(",");
			}

			// start of next entry
			plotDataSB.append("{\"preMZ\":");
			// start/left side of bin
			plotDataSB.append( ( ( counter * binSizeAsDouble ) + precursorMZMinDouble ) );
			plotDataSB.append(",");
			// count
			plotDataSB.append("\"scanCount\":");
			plotDataSB.append(scanCount);
			//  end of entry
			plotDataSB.append("}");
			
			counter++;
		}
		
		//  end of array
		plotDataSB.append("]}");		
		

		String plotData = plotDataSB.toString();
		
		
		MZScanCountPlotDataDTO mzScanCountPlotDataDTO = new MZScanCountPlotDataDTO();
		
		mzScanCountPlotDataDTO.setExperimentId( experimentId );
		mzScanCountPlotDataDTO.setPlotData( plotData );
		mzScanCountPlotDataDTO.setScanCount( numScans );
		
		long endTime = System.currentTimeMillis();

		int createTimeInSeconds = Math.round( ( (float)( endTime - startTime ) ) / 1000 );
		
		mzScanCountPlotDataDTO.setCreateTimeInSeconds( createTimeInSeconds );
		
		return mzScanCountPlotDataDTO;
	}
}
