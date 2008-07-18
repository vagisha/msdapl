/**
 * SQTSearchDataProviderImpl.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.io.File;
import java.io.IOException;
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

    public SQTSearch getSearchHeader() throws DataFormatException, IOException {
        Header header = reader.getHeader();
        if (header == null || !header.isValid())
            throw new DataFormatException("Invalid header for SQT file: "+fileName);
        return header;
    }

    @Override
    public boolean hasNextSearchScan() {
        if (reader.hasScans()) return true;
        return false;
    }
    
    @Override
    public void close() {
        if (reader != null) reader.close();
    }

    @Override
    public SQTSearchScan getNextSearchScan() throws DataFormatException, IOException {
        try {
            ScanResult scan = reader.getNextScan();
            if (scan == null || !scan.isValid())
                throw new DataFormatException("Invalid scan for SQT file: "+fileName);
            return scan;
        }
        catch (ParserException e) {
            throw new DataFormatException(e.getMessage());
        }
    }
    
    public int numWarnings() {
        return reader.getWarningCount();
    }
}
