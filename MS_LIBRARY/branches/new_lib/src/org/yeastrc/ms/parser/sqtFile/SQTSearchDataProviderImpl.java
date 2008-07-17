/**
 * SQTSearchDataProviderImpl.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.io.File;
import java.io.InputStream;
import java.util.zip.DataFormatException;

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
    private ScanResultIterator iterator;
    
    public SQTSearchDataProviderImpl() {
        reader = new SQTFileReader();
    }
    
    public void setSQTSearch(String filePath) throws ParserException {
        fileName = new File(filePath).getName();
        reader.open(filePath);
    }
    
    public void setSQT(String fileName, InputStream inputStream) throws ParserException {
        this.fileName = fileName;
        reader.open(inputStream);
    }
    
    public String getFileName() {
        return fileName;
    }

    public SQTSearch getSearchData() throws DataFormatException {
        try {
            return reader.getHeader();
        }
        catch(ParserException e) {
            throw new DataFormatException(e.getMessage());
        }
    }
    
    public ScanResultIterator scanResultIterator() {
        if (iterator == null)
            iterator = new ScanResultIterator();
        else
            throw new IllegalStateException("Cannot make a second request for scan iterator");
        return iterator;
    }
   
    public class ScanResultIterator {

        public boolean hasNext() {
            if (reader.hasScans()) return true;
            reader.close();
            return false;
        }

        public SQTSearchScan next() throws DataFormatException {
            try {
                return reader.getNextScan();
            }
            catch (ParserException e) {
               throw new DataFormatException(e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (reader != null) reader.close();
    }

}
