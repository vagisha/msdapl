package org.yeastrc.mz_scan_count_plot.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.mz_scan_count_plot.dao.MZScanCountPlotDataDAO;
import org.yeastrc.mz_scan_count_plot.dto.MZScanCountPlotDataDTO;



public class MZScanCountPlotDataDAOImpl implements MZScanCountPlotDataDAO {

    private static final Logger log = Logger.getLogger(MZScanCountPlotDataDAOImpl.class);

	
	private static final String insertSQL 
	= "INSERT INTO mz_scan_count_plot_data "
	+ 		"(experiment_id, plot_data, scan_count, create_time_in_seconds, data_version ) "
	+ 		"VALUES ( ?, ?, ?, ?, ? )"
	
    + " ON DUPLICATE KEY UPDATE "
    + "     plot_data = ?, scan_count = ?, create_time_in_seconds = ?, data_version = ?";

	
    @Override
    public int save(MZScanCountPlotDataDTO mzScanCountPlotDataDTO) {
    	

//    	CREATE TABLE mz_scan_count_plot_data (
//		  experiment_id int(10) unsigned NOT NULL,
//		  plot_data varchar(4000) NOT NULL,
//		  scan_count int(10) unsigned NOT NULL,
//		  create_time_in_seconds int(10) unsigned NOT NULL,
//		  data_version int(10) unsigned NOT NULL,
//		  PRIMARY KEY (experiment_id)
//		) ENGINE=MyISAM DEFAULT CHARSET=latin1;

		Connection connection = null;

		PreparedStatement pstmt = null;

		try {
			
			connection = DAOFactory.instance().getConnection();

			pstmt = connection.prepareStatement( insertSQL );
			
			int paramCounter = 0;
			
			//  For insert portion of statement
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getExperimentId() );
			paramCounter++;
			pstmt.setString( paramCounter, mzScanCountPlotDataDTO.getPlotData() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getScanCount() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getCreateTimeInSeconds() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getDataVersion() );
			
			//  For update portion of statement
			paramCounter++;
			pstmt.setString( paramCounter, mzScanCountPlotDataDTO.getPlotData() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getScanCount() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getCreateTimeInSeconds() );
			paramCounter++;
			pstmt.setInt( paramCounter, mzScanCountPlotDataDTO.getDataVersion() );
			
			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {
				
			}

		} catch (Exception sqlEx) {
			String msg = "save :Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL ;
			log.error( msg, sqlEx);
			throw new RuntimeException( msg, sqlEx );

		} finally {

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
		return mzScanCountPlotDataDTO.getExperimentId() ;
    }
    
    @Override
    public MZScanCountPlotDataDTO load(int experimentId) {
    	{
    		MZScanCountPlotDataDTO mzScanCountPlotDataDTO = null;

    		Connection connection = null;

    		PreparedStatement pstmt = null;

    		ResultSet rs = null;

    		try {

    			connection = DAOFactory.instance().getConnection();

    			String querySqlStringComplete = "SELECT plot_data, data_version FROM mz_scan_count_plot_data WHERE experiment_id = ?";

    			pstmt = connection.prepareStatement( querySqlStringComplete );

    			pstmt.setInt( 1, experimentId );

    			rs = pstmt.executeQuery();

    			if ( rs.next() ) {

    				mzScanCountPlotDataDTO = new MZScanCountPlotDataDTO();
    				
    				mzScanCountPlotDataDTO.setExperimentId( experimentId );
    				
    				mzScanCountPlotDataDTO.setPlotData( rs.getString( "plot_data" ) );
    				mzScanCountPlotDataDTO.setDataVersion( rs.getInt( "data_version" ) );
    			}


    		} catch (Exception sqlEx) {

    			String msg = "load :Exception '" + sqlEx.toString() + '.';
    			log.error( msg, sqlEx);
    			throw new RuntimeException( msg, sqlEx );

    		} finally {

    			if (rs != null) {
    				try {
    					rs.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}

    			if (pstmt != null) {
    				try {
    					pstmt.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}

    			if (connection != null) {
    				try {
    					connection.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}
    		}

    		return mzScanCountPlotDataDTO;
    	}
    }
    
    
    @Override
    public MZScanCountPlotDataDTO loadFromExperimentIdAndDataVersion(int experimentId, int dataVersion) {
    	{
    		MZScanCountPlotDataDTO mzScanCountPlotDataDTO = null;

    		Connection connection = null;

    		PreparedStatement pstmt = null;

    		ResultSet rs = null;

    		try {

    			connection = DAOFactory.instance().getConnection();

    			String querySqlStringComplete = "SELECT plot_data FROM mz_scan_count_plot_data WHERE experiment_id = ? AND data_version = ?";

    			pstmt = connection.prepareStatement( querySqlStringComplete );

    			pstmt.setInt( 1, experimentId );
    			pstmt.setInt( 2, dataVersion );

    			rs = pstmt.executeQuery();

    			if ( rs.next() ) {

    				mzScanCountPlotDataDTO = new MZScanCountPlotDataDTO();
    				
    				mzScanCountPlotDataDTO.setExperimentId( experimentId );
    				
    				mzScanCountPlotDataDTO.setPlotData( rs.getString( "plot_data" ) );
    				mzScanCountPlotDataDTO.setDataVersion( dataVersion );
    			}


    		} catch (Exception sqlEx) {

    			String msg = "load :Exception '" + sqlEx.toString() + '.';
    			log.error( msg, sqlEx);
    			throw new RuntimeException( msg, sqlEx );

    		} finally {

    			if (rs != null) {
    				try {
    					rs.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}

    			if (pstmt != null) {
    				try {
    					pstmt.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}

    			if (connection != null) {
    				try {
    					connection.close();
    				} catch (SQLException ex) {
    					// ignore
    				}
    			}
    		}

    		return mzScanCountPlotDataDTO;
    	}
    }


}
