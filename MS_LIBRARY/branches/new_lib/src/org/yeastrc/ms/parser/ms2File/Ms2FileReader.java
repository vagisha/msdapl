/**
 * Ms2FileParser.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.yeastrc.ms.parser.ParserException;


/**
 * 
 */
public class Ms2FileReader {

    private BufferedReader reader;
    private String currentLine;
    private int currentLineNum = 0;

    public void open(String filePath) {
        try {
            reader = new BufferedReader(new FileReader(filePath));
            advanceLine();
        }
        catch (FileNotFoundException e) {
            closeAndThrowException("File does not exist: "+filePath, e);
        }
        catch (IOException e) {
            closeAndThrowException("Error reading file: "+filePath, e);
        }
    }

    
    public void open(InputStream inStream) {
        try {
            reader = new BufferedReader(new InputStreamReader(inStream));
            advanceLine();
        }
        catch (IOException e) {
            closeAndThrowException("Error reading file from input stream", e);
        }
    }
    
    private void advanceLine() throws IOException {
        currentLineNum++;
        currentLine = reader.readLine(); // advance first
        // skip over blank lines
        while(currentLine != null && currentLine.trim().length() == 0) {
            currentLineNum++;
            currentLine = reader.readLine();
        }
    }
    
    public Header getHeader() {
        
        Header header = new Header();
        while (isHeaderLine(currentLine)) {
            String[] tokens = currentLine.split("\\t");
            if (tokens.length >= 3) {
                //header.addHeaderItem(tokens[1], tokens[2]);
                StringBuilder val = new StringBuilder();
                // the value for the header may be a tab separated list; get the entire string
                if (tokens.length > 3) {
                    int idx = currentLine.indexOf('\t',currentLine.indexOf('\t')+1);
                    tokens[2] = currentLine.substring(idx+1, currentLine.length());
                }
                header.addHeaderItem(tokens[1], tokens[2]);
            }
            else if (tokens.length >= 2){
                // if the value for this header is missing, add the header
                // with an empty string as the value
                header.addHeaderItem(tokens[1], "");
            }
            else {
             // ignore if both label and value for this header item are missing
             //throw new Exception("Invalid header: "+currentLine);
            }
            
            try {
                advanceLine();
            }
            catch (IOException e) {
                closeAndThrowException(e);
            }
        }
        return header;
    }
    
    public boolean hasScans() {
        return currentLine != null;
    }
    
    public Scan getNextScan() {
        
        Scan scan = parseScan();
        
        try {
            advanceLine(); // go to the next line
            
            while(currentLine != null) {
                // is this one of the charge states of the scan?
                if (isChargeLine(currentLine)) {
                    parseScanCharge(scan);
                }
                // is this one of the charge independent analysis for this scan?
                else if (isChargeIndAnalysisLine(currentLine)) {
                    parseIAnalysis(scan);
                }
                // it is neither so must be peak data
                else {
                    parsePeaks(scan);
                    break; // done parsing this scan!
                }
            }
        }
        catch (IOException e) {
            closeAndThrowException(e);
        }
        
        return scan;
    }

    private void parseIAnalysis(Scan scan) {
        String[] tokens = currentLine.split("\\t");
        if (tokens.length < 3)
            closeAndThrowException("2 fields expected for line: "+currentLine);
        scan.addAnalysisItem(tokens[1], tokens[2]);
        // advance to next line
        try {
            advanceLine();
        }
        catch (IOException e) {
            closeAndThrowException(e);
        }
    }
    
    private Scan parseScan() {
        
        // make sure we have a scan line
        if (!isScanLine(currentLine))
            closeAndThrowException("Error parsing scan. Expected line starting with \"S\"");
        
        String[] tokens = currentLine.split("\\t");
        if (tokens.length < 4)
            closeAndThrowException("Expected 3 fields in scan line: "+currentLine);
        
        int firstScan = -1;
        int lastScan = -1;
        try {firstScan = Integer.parseInt(tokens[1]);}
        catch(NumberFormatException e) {closeAndThrowException("Invalid first scan num in scan line: "+currentLine, e);}
        try {lastScan = Integer.parseInt(tokens[2]);}
        catch(NumberFormatException e) {closeAndThrowException("Invalid last scan num in scan line: "+currentLine, e);}
        
        Scan scan = new Scan();
        scan.setStartScan(firstScan);
        scan.setEndScan(lastScan);
        try {
            scan.setPrecursorMz(tokens[3]);
        }
        catch (NumberFormatException e) {
            closeAndThrowException("Invalid precursor m/z in scan line: "+currentLine);
        }
        
        return scan;
    }
    
    private void parseScanCharge(Scan scan) {
        String tokens[] = currentLine.split("\\s");
        if (tokens.length < 3)
            closeAndThrowException("2 fields expected for charge line: "+currentLine);
        
        // get the charge and mass
        int charge = -1;
        try {charge = Integer.parseInt(tokens[1]);}
        catch(NumberFormatException e) {closeAndThrowException("Invalid charge in line: "+currentLine, e);}
        
        ScanCharge scanCharge = new ScanCharge();
        scanCharge.setCharge(charge);
        try {
            scanCharge.setMass(tokens[2]);
        }
        catch(NumberFormatException e) {
            closeAndThrowException("Invalid mass in line: "+currentLine, e);
        }
        
        // parse any charge dependent analysis associated with this charge state
        try {
            advanceLine();
            while (isChargeDepAnalysisLine((currentLine))) {
                tokens = currentLine.split("\\t");
                if (tokens.length < 3)
                    closeAndThrowException("2 fields expected for line: "+currentLine);
                scanCharge.addAnalysisItem(tokens[1], tokens[2]);
                advanceLine();
            }
        }
        catch (IOException e) {
            closeAndThrowException(e);
        }
        scan.addChargeState(scanCharge);
    }
    
    public void parsePeaks(Scan scan) {
        
        while (isPeakDataLine(currentLine)) {
            String[] tokens = currentLine.split("\\s");
            if (tokens.length < 2)
                closeAndThrowException("missing charge and/or mass in line: "+currentLine);
            
            // add peak m/z and intensity values
            scan.addPeak(tokens[0], tokens[1]);
            
            try {
                advanceLine();
            }
            catch (IOException e) {
                closeAndThrowException(e);
            }
        }
    }
    
    private boolean isScanLine(String line) {
        if (line == null)   return false;
        return line.startsWith("S");
    }
    
    private boolean isHeaderLine(String line) {
        if (line == null)   return false;
        return line.startsWith("H");
    }
    
    private boolean isChargeLine(String line) {
        if (line == null)   return false;
        return line.startsWith("Z");
    }
    
    private boolean isChargeIndAnalysisLine(String line) {
        if (line == null)   return false;
        return line.startsWith("I");
    }
    
    private boolean isChargeDepAnalysisLine(String line) {
        if (line == null)   return false;
        return line.startsWith("D");
    }
    
    private boolean isPeakDataLine(String line) {
        if (line == null)
            return false;
        if (isScanLine(line) ||
            isChargeLine(line) ||
            isChargeDepAnalysisLine(line) ||
            isChargeIndAnalysisLine(line) ||
            isHeaderLine(line))
            return false;
        return true;
    }
    
    
    public void close() {
        if (reader != null) 
            try {reader.close();}
            catch (IOException e) {}
    }
    
    private void closeAndThrowException(Exception e) {
        closeAndThrowException("Error reading file.", e);
    }
    
    private void closeAndThrowException(String message, Exception e) {
        close();
        throw new ParserException(currentLineNum, message, e);
    }
    
    private void closeAndThrowException(String message) {
        close();
        throw new ParserException(currentLineNum, message);
    }
}
