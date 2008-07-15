/**
 * SQTSearchDataProviderImpl.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.service.SQTSearchDataProvider;

/**
 * 
 */
public class SQTSearchDataProviderImpl implements SQTSearchDataProvider {

    private String fileName;
    private SQTFileReader reader;
    private Iterator<SQTSearchScan> iterator;
    
    public SQTSearchDataProviderImpl() {
        reader = new SQTFileReader();
    }
    
    public void setSQTSearch(String filePath) {
        fileName = new File(filePath).getName();
        reader.open(filePath);
    }
    
    public void setSQT(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        reader.open(inputStream);
    }
    
    public String getFileName() {
        return fileName;
    }

    public SQTSearch getSearchData() {
        Header header = reader.getHeader();
        if (!header.isHeaderValid())
            throw new RuntimeException("Invalid SQT Header");
        return header;
    }
    
    public Iterator<SQTSearchScan> scanResultIterator() {
        if (iterator == null)
            iterator = new ScanResultIterator();
        else
            throw new IllegalStateException("Cannot make a second request for scan iterator");
        return iterator;
    }
   
    private class ScanResultIterator implements Iterator<SQTSearchScan> {

        public boolean hasNext() {
            if (reader.hasScans()) return true;
            reader.close();
            return false;
        }

        public SQTSearchScan next() {
            return reader.getNextScan();
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not permitted on ScanResultIterator");
        }
    }

}
