/**
 * MSXMLSequentialParser.java
 * @author Vagisha Sharma
 * Sep 17, 2009
 * @version 1.0
 */
package org.systemsbiology.jrap.stax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * 
 */
public class MSXMLSequentialParser {

    /** The file we are in charge of reading */
    private String fileName = null;
    private XMLStreamReader xmlSR = null;
    private InputStream inputStr = null;


    private MZXMLFileInfo fileHeader = null;

    /** The indexes */
    private Map<Integer, Long> offsets;
    private int maxScan;
    private long chrogramIndex;


    private boolean isXML = false;
    private boolean isML = false;

    private int currentScan = 0; // current scan number being read
    
    public MSXMLSequentialParser() {}

    public void open(String fileName) throws FileNotFoundException, XMLStreamException {
        this.fileName = fileName;

        if(fileName.indexOf("mzXML") != -1)
            isXML = true;
        else {
            // don't know how to parse
            isML = true;
        }

        //using IndexParser get indexes
        IndexParser indexParser = new IndexParser(fileName); // this will open and close the file once. 
        indexParser.parseIndexes();
        offsets = indexParser.getOffsetMap();
        maxScan = indexParser.getMaxScan();
        chrogramIndex = indexParser.getChrogramIndex();

        inputStr = new FileInputStream(fileName);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        xmlSR = inputFactory.createXMLStreamReader(inputStr);

        // read the file header 
        readFileHeader(xmlSR);
    }

    public void close() {
        if(this.xmlSR != null) {
            try {xmlSR.close();}
            catch (XMLStreamException e) {}
        }
        if(this.inputStr != null) {
            try {inputStr.close();}
            catch(IOException e) {}
        }
    }

    /**this gives back the file header (info before scan)
     *@return the file header info (MZXMLFileInfo)
     * @throws XMLStreamException 
     */
    private void readFileHeader(XMLStreamReader reader) throws XMLStreamException {
        FileHeaderParser fileHeaderParser = new FileHeaderParser(fileName);
        fileHeaderParser.parseFileHeader(reader);
        this.fileHeader = fileHeaderParser.getInfo();
    }
    
    /**
     * This gives back the file header (info before scan)
     *@return the file header info (MZXMLFileInfo)
     */
    public MZXMLFileInfo getFileHeader() {
        return this.fileHeader;
    }


    private void closeFile(FileInputStream fileIN) {
        if(fileIN != null) {
            try {
                fileIN.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns true if there are more scans to be parsed in the file
     * @return
     */
    public boolean hasNextScan() {
        return currentScan != maxScan;
    }
    
    // Note: scanNumbers are 1-based, so scanNumber must be atleast 1
    // and be not greater than getScanCount() + 1
//    private int getNextScanNmber() {
//        Long offset = null;
//        while(offset == null) {
//            currentScan++;
//            offset = this.offsets.get(currentScan);
//        }
//        return currentScan;
//    }
    
    /**
     * Returns a Scan object with its peaks and header information
     * @return
     * @throws XMLStreamException 
     */
    public Scan getNextScan() throws XMLStreamException {
        if(isXML)
        {
            ScanAndHeaderParser scanParser = new ScanAndHeaderParser();
            scanParser.setIsScan(true);
            scanParser.parseScanAndHeader(xmlSR);
            this.currentScan = scanParser.getScan().getHeader().getNum();
            return scanParser.getScan();
        }
        else
        {
            MLScanAndHeaderParser scanParser = new MLScanAndHeaderParser();
            scanParser.setIsScan(true);
            scanParser.parseMLScanAndHeader(xmlSR);
            this.currentScan = scanParser.getScan().getHeader().getNum();
            return (scanParser.getScan());
        }
    }
    

    /**
     * Get the total number of scans in the mzXMLfile handled by this parser.
     *
     * @return The number of scans.
     */
    public int getScanCount()
    {
        return offsets.size();
    }

    public int getMaxScanNumber()
    {
        return maxScan;
    }
   
}
