/**
 * Ms2FileToDbUploader.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dbuploader;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;
import org.yeastrc.ms.parser.ms2File.Header;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.ms2File.Scan;
import org.yeastrc.ms.util.Sha1SumCalculator;


/**
 * 
 */
public class Ms2FileToDbConverter {

    private static final Logger log = Logger.getLogger(Ms2FileToDbConverter.class);
    
    /**
     * @param filePath
     * @param experimentId
     * @return true if file was uploaded to the database; false otherwise
     * @throws Ms2FileReaderException if an error occurs while parsing the file
     */
    public boolean convertMs2File(String filePath, int experimentId) throws Exception {
        
        String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        
        String justFileName = new File(filePath).getName();
        if (fileIsInDb(justFileName, sha1Sum)) {
            log.warn("Aborting upload of file: "+filePath+". This run already exists in the database");
            return false;
        }
        
        Ms2FileReader reader = new Ms2FileReader();
        reader.open(filePath);
        
        convertMs2File(filePath, reader, experimentId, sha1Sum);
        return true;
    }

    public boolean convertMs2File(InputStream inStream, String fileName, int experimentId) throws Exception {
        
        String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(inStream);
        
        if (fileIsInDb(fileName, sha1Sum)) {
            log.warn("Aborting upload of file: "+fileName+". This run already exists in the database");
            return false;
        }
        
        Ms2FileReader reader = new Ms2FileReader();
        reader.open(inStream);
        
        convertMs2File(fileName, reader, experimentId, sha1Sum);
        return true;  
    }
    
    boolean fileIsInDb(String fileName, String sha1Sum) {
        
        MsRunDAO<MS2Run, MS2RunDb> runDao = DAOFactory.instance().getMS2FileRunDAO();
        List <Integer> runsIds = runDao.runIdsFor(fileName, sha1Sum);
        // if a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload this run
        
        if (runsIds.size() > 0)
            return true;
        return false;
        
    }

    private void convertMs2File(String file, Ms2FileReader reader, int experimentId, String sha1Sum)
            throws Exception {
        Header header = reader.getHeader();
        header.setFilePath(file);
        header.setSha1Sum(sha1Sum);
        // insert a MS2Run into the database and get the run Id
        MsRunDAO<MS2Run, MS2RunDb> rundao = DAOFactory.instance().getMS2FileRunDAO();
        int runId = rundao.saveRun(header, experimentId);
        
        MsScanDAO<MS2Scan, MS2ScanDb> scanDAO = DAOFactory.instance().getMS2FileScanDAO();
        while (reader.hasScans()) {
            Scan scan = reader.getNextScan();
            // save the scan
            scanDAO.save(scan, runId);
        }
    }
  
    
    public static void main(String[] args) {
       Ms2FileToDbConverter uploader = new Ms2FileToDbConverter();
//       String file = "./resources/sample.ms2";
       String file = "./resources/PARC_p75_01_itms.ms2";
//       String file = "/Users/vagisha/WORK/MS_LIBRARY/sample_MS2_data/p75/p75_01_itms.ms2";
//       String file = "./resources/NE063005ph8s01.ms2";
       long start = System.currentTimeMillis();
       try {
           uploader.convertMs2File(file, 18);
       }
       catch (Exception e) {
           e.printStackTrace();
       }
       long end = System.currentTimeMillis();
       long timeElapsed = (end - start)/1000;
       System.out.println("Seconds to upload: "+timeElapsed);
       
    }
}
