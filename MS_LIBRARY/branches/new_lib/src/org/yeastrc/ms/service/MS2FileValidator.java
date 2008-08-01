/**
 * MS2FileValidator.java
 * @author Vagisha Sharma
 * Jul 23, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MS2FileValidator {

    private static final Logger log = Logger.getLogger(MS2FileValidator.class);

    private boolean headerValid = false;
    private boolean scansValid = false;

    private void validateFile(String filePath) {


        Ms2FileReader dataProvider = new Ms2FileReader();

        // open the file
        try {
            String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
            dataProvider.open(filePath, sha1Sum);
        }
        catch (DataProviderException e) {
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }
        catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }

        // read the header
        try {
            dataProvider.getRunHeader();
            headerValid = true;
        }
        catch (DataProviderException e) {
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }

        // read the scans
        while (dataProvider.hasNextScan()) {
            try {
                MS2Scan scan = dataProvider.getNextScan();
            }
            catch (DataProviderException e) {
                log.error(e.getMessage(), e);
                dataProvider.close();
                return;
            }
        }
        dataProvider.close();
    }

    public boolean validate(String filePath) {
        validateFile(filePath);
        StringBuilder buf = new StringBuilder();
        buf.append("Ran validator on: "+filePath);
        if (headerValid) {
            buf.append("Header: valid");
            if (scansValid)
                buf.append("Scans: valid");
            else
                buf.append("Scans: invalid");
        }
        else {
            buf.append("Header: invalid");
        }
        log.info(buf.toString());
        return (headerValid && scansValid);
    }

    public static void main(String[] args) {
//        String downloadDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/SQTParserTest";
//        Map<Integer, String> cycleIds = getCycleIdList();
//        int found = 0;
//        int valid = 0;
//        try {
//            for (Integer cycleId: cycleIds.keySet()) {
//                //if (found > 208)
//                 //   break;
//                String fileName = cycleIds.get(cycleId);
//                if (fileName == null || fileName.trim().length() == 0)
//                    continue;
//                fileName = fileName+".ms2";
//                YatesCycleDownloader downloader = new YatesCycleDownloader();
//                if (downloader.downloadFile(cycleId, downloadDir, fileName, DATA_TYPE.MS2)) {
//                    found++;
//                    SQTFileValidator validator = new SQTFileValidator();
//                    if (validator.validate(downloadDir+File.separator+fileName)){
//                        valid++;
//                    }
//                    else {
//                        log.error("!!!!!!!!!!INVALID FILE: "+fileName);
//                    }
//                    new File(downloadDir+File.separator+fileName).delete();
//                }
//            }
//        }
//        catch(Exception e) {e.printStackTrace();}
//        finally {
//            log.info("Num files found: "+(found-1)+"; Valid files: "+valid);
//        }
      String filePath = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/UploadTest/PARC_orbplusphos-05.ms2";
      MS2FileValidator validator = new MS2FileValidator();
      validator.validate(filePath);
    }

    public static Map<Integer, String> getCycleIdList() {
        Connection conn = null;
        Statement s = null;
        ResultSet rs = null;
        Map<Integer, String> cycleIdList = new HashMap<Integer, String>();
        try {
            conn = getConnection();
            s = conn.createStatement();
            rs = s.executeQuery("SELECT cycleID, cycleFileName FROM tblYatesCycles ORDER BY runID DESC limit 500");
            while (rs.next()) {
                cycleIdList.put(rs.getInt("cycleID"), rs.getString("cycleFileName"));
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                conn.close();
                s.close();
                rs.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cycleIdList;
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/yrc";
        return DriverManager.getConnection( URL, "root", "");
    }
}
