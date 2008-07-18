/**
 * SQTFileValidator.java
 * @author Vagisha Sharma
 * Jul 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchDataProviderImpl;

/**
 * 
 */
public class SQTFileValidator {

    private static final Logger log = Logger.getLogger(SQTFileValidator.class);
    
    private boolean headerValid = true;
    private int numValidScans = 0;
    private int numScans = 0;
    private int numWarnings = 0;
    
    private void validateFile(String filePath) {
        
        
        SQTSearchDataProviderImpl dataProvider = new SQTSearchDataProviderImpl();
        
        // open the file
        try {
            dataProvider.setSQTSearch(filePath);
        }
        catch (ParserException e) {
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }
        
        // read the header
        try {
            dataProvider.getSearchHeader();
        }
        catch (DataFormatException e) {
            headerValid = false;
            log.error(e.getMessage(), e);
        }
        catch (IOException e) {
            headerValid = false;
            log.error(e.getMessage(), e);
            dataProvider.close();
            return;
        }
        
        // read the scans
        while (dataProvider.hasNextSearchScan()) {
            numScans++;
            try {
                dataProvider.getNextSearchScan();
                numValidScans++;
            }
            catch (DataFormatException e) {
                log.error(e.getMessage(), e);
            }
            catch (IOException e) {
                log.error(e.getMessage(), e);
                dataProvider.close();
                return;
            }
        }
        numWarnings = dataProvider.numWarnings();
        dataProvider.close();
    }
    
    public boolean validate(String filePath) {
        validateFile(filePath);
        log.info("Ran validator on: "+filePath);
        log.info("# warnings: "+numWarnings);
        log.info("Valid SQT Header: "+headerValid);
        log.info("# scans in file: "+numScans+"; # valid scans: "+numValidScans);
        return (headerValid && numValidScans > 0);
    }
    
    public static void main(String[] args) {
        String filePath = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/test/21251_PARC_meth_async_05_itms.sqt";
        SQTFileValidator validator = new SQTFileValidator();
        validator.validate(filePath);
    }
}
