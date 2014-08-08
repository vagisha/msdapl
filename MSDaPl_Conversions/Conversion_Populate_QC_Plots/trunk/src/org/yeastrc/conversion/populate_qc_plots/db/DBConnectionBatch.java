package org.yeastrc.conversion.populate_qc_plots.db;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManagerIF;

public class DBConnectionBatch implements DBConnectionManagerIF {

	private static final Logger log = Logger.getLogger(DBConnectionBatch.class);

	public static final String JOB_QUEUE_DATABASE_NAME = "jobQueue";

	public static final String DATABASE_HOST = "localhost";

	public static final String MS_DATA_DB_PROPERTIES_FILE = "msDataDB.properties";

	private static boolean firstCall = true;

	private static String userid;

	private static String password;


	@Override
	public Connection getConnection(String db) throws SQLException {

		if ( db == null ) {

			throw new IllegalArgumentException( "db param cannot be null" );
		}

		if ( ! JOB_QUEUE_DATABASE_NAME.equals(db) ) {

			throw new IllegalArgumentException( "db param must be = '" + JOB_QUEUE_DATABASE_NAME + "'.  '" + db + "' was passed in." );
		}

		try {
			Class.forName( "com.mysql.jdbc.Driver" );
		} catch (ClassNotFoundException e) {

			System.out.println( "Driver not found for database " + db );
			e.printStackTrace();

			throw new SQLException( "Driver Not Found" , e );
		}

		if ( userid == null ) {

			getUserIdAndPasswordFromPropertiesFile();
		}

		String url = "jdbc:mysql://" + DATABASE_HOST + "/" + db +
				"?autoReconnect=true";

		if ( firstCall ) {
			log.warn( "connecting to database at |" + url + "|, using userid " + userid );
		}


		firstCall = false;

		return DriverManager.getConnection( url, userid, password );

	}

	/**
	 * @throws Exception 
	 * 
	 */
	private void getUserIdAndPasswordFromPropertiesFile()  {

		try {

			ClassLoader thisClassLoader = this.getClass().getClassLoader();

			URL configPropFile = thisClassLoader.getResource( MS_DATA_DB_PROPERTIES_FILE );


			if ( configPropFile == null ) {

				log.error( "Properties file '" + MS_DATA_DB_PROPERTIES_FILE + "' not found " );
			} else {

				log.info( "Properties file '" + MS_DATA_DB_PROPERTIES_FILE + "' load path = " + configPropFile.getFile() );
			}

			InputStream props = thisClassLoader.getResourceAsStream( MS_DATA_DB_PROPERTIES_FILE );

			if ( props == null ) {

				log.error( "file " + MS_DATA_DB_PROPERTIES_FILE + " not found " );

			} else {

				URL propURL = thisClassLoader.getResource( MS_DATA_DB_PROPERTIES_FILE );

				if ( propURL != null ) {

					log.info( "file " + MS_DATA_DB_PROPERTIES_FILE + "  propURL.getFile() = " + propURL.getFile() );
				}

				Properties configProps = new Properties();

				configProps.load(props);


				userid = configProps.getProperty( "db.user" );

				password = configProps.getProperty( "db.password" );

				if ( userid == null || userid.length() == 0 ) {

					String msg = "ERROR: db.user value is missing or empty";

					log.error( msg );
					throw new Exception( msg );
				}

				if ( password == null || password.length() == 0 ) {

					String msg = "ERROR: db.password value is missing or empty";

					log.error( msg );
					throw new RuntimeException( msg );
				}
			}

		} catch ( Exception ex ) {
			
			throw new RuntimeException( "Exception retrieving config from " + MS_DATA_DB_PROPERTIES_FILE, ex );
		}
	}
}
