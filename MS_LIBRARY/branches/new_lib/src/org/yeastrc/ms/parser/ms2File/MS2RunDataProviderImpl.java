/**
 * MS2RunDataProviderImpl.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.service.MS2RunDataProvider;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MS2RunDataProviderImpl implements MS2RunDataProvider {

    private String fileName;
    private String sha1Sum;
    private Ms2FileReader reader;
    private Iterator<MS2Scan> iterator;
    
    public MS2RunDataProviderImpl() {
        reader = new Ms2FileReader();
    }
    
    public void setMS2Run(String filePath) throws NoSuchAlgorithmException, IOException {
        sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        fileName = new File(filePath).getName();
        reader.open(filePath);
    }
    
    public void setMS2Run(String fileName, InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        sha1Sum = Sha1SumCalculator.instance().sha1SumFor(inputStream);
        this.fileName = fileName;
        reader.open(inputStream);
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public MS2Run getRunData() {
        Header header = reader.getHeader();
        if (!header.isHeaderValid())
            throw new IllegalArgumentException("Invalid MS2 Header");
        header.setFileName(fileName);
        header.setSha1Sum(sha1Sum);
        return header;
    }

    @Override
    public String getSha1Sum() {
        return sha1Sum;
    }
    
    @Override
    public Iterator<MS2Scan> scanIterator() {
        if (iterator == null)
            iterator = new ScanIterator();
        else
            throw new IllegalStateException("Cannot make a second request for scan iterator");
        return iterator;
    }
   
    private class ScanIterator implements Iterator<MS2Scan> {

        public boolean hasNext() {
            if (reader.hasScans()) return true;
            reader.close();
            return false;
        }

        public MS2Scan next() {
            return reader.getNextScan();
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not permitted on ScanIterator");
        }
    }
}
