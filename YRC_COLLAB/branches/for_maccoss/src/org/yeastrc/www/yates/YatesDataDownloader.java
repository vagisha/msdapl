/*
 * YatesDataDownloader.java
 * Created on Oct 12, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import org.yeastrc.project.*;

/**
 * Download all MS2 data in a given location.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 12, 2004
 */

public class YatesDataDownloader {

	// ATTRIBUTES FOR OUR SSH CONNECTION
	private final String YATES_HOSTNAME = "fields.scripps.edu";
	private final String YATES_USERNAME = "yrc";
	private final String YATES_PASSWORD = "m9s244";
	
	private final String MACCOSS_HOSTNAME = "proteome.gs.washington.edu";
	private final String MACCOSS_USERNAME = "mriffle";
	private final String MACCOSS_PASSWORD = "Nohkile4";

	private String HOSTNAME = null;
	private String USERNAME = null;
	private String PASSWORD = null;
	
	public void setGroup (String group) {
		if (group.equals( Projects.YATES )) {
			HOSTNAME = YATES_HOSTNAME;
			USERNAME = YATES_USERNAME;
			PASSWORD = YATES_PASSWORD;
			return;
		}
		
		if (group.equals( Projects.MACCOSS )) {
			HOSTNAME = MACCOSS_HOSTNAME;
			USERNAME = MACCOSS_USERNAME;
			PASSWORD = MACCOSS_PASSWORD;
			return;
		}
		
	}
	
	/**
	 * Download all the Yates MS data in the given directory
	 * @param directory The directory to check
	 * @return The full path name of the directory to which we downloaded data
	 * @throws IOException If there is an IO problem
	 * @throws Exception If there is a general problem
	 */
	public String downloadData(String directory) throws IOException, Exception {
		if (this.HOSTNAME == null)
			throw new Exception ("No HOSTNAME set in DataDownloader.");
		
		String DOWNLOAD_DIR = "/a/scratch/ms_data/" + String.valueOf((new Date()).getTime());		// UNIX version
		//DOWNLOAD_DIR = DOWNLOAD_DIR.replace('/', File.separatorChar);
		
		//String DOWNLOAD_DIR = "c:\\a\\scratch\\ms_data\\" + String.valueOf((new Date()).getTime());	// WINDOWS version
		
		// Connect to the SSH server
		SshClient ssh = new SshClient();
		ssh.connect(HOSTNAME, new IgnoreHostKeyVerification());

		try {
			// Authenticate on our connection
			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
			pwd.setUsername(USERNAME);
			pwd.setPassword(PASSWORD);
			int result = ssh.authenticate(pwd);

			// Make sure we authenticated
			if(result==AuthenticationProtocolState.FAILED || result==AuthenticationProtocolState.PARTIAL) {
				throw new Exception("Authentication to " + this.HOSTNAME + " failed.");
			}

			// Create our SFTP client
			SftpClient sftp = ssh.openSftpClient();
			sftp.cd(directory);
			File file = new File(DOWNLOAD_DIR);
			file.mkdir();
			sftp.lcd(DOWNLOAD_DIR);

			List fileList = sftp.ls();

			// Make sure the critical file is present!
			//if (!fileList.contains("DTASelect-filter.txt")) {
			//	throw new Exception ("DTASelect-filter.txt WAS NOT FOUND IN " + directory);
			//}
		
			// Iterate through and download all the files
			Iterator iter = fileList.iterator();
			while (iter.hasNext()) {
				SftpFile sfile = (SftpFile)(iter.next());
				String fname = sfile.getFilename();

				// Files to skip
				if (fname.equals(".")) continue;
				if (fname.equals("..")) continue;
				if ( !fname.endsWith(".txt") &&
						!fname.endsWith(".ms2") &&
						!fname.endsWith(".sqt") &&
						!fname.endsWith(".params") &&
						!fname.endsWith(".html"))
					continue;
				
				// Get the file
				sftp.get(fname, fname);
			}
				

			// Try to disconnect
			ssh.disconnect();
			ssh = null;
		}
		finally {

			// Make sure we disconnect.
			try {
				if (ssh != null) {
					ssh.disconnect();
					ssh = null;
				}
			} catch (Exception e) { ; }
		}

		// We succeeded, kick back the place of deposit
		return DOWNLOAD_DIR;
	}
	
	
}
