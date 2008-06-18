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


/**
 * 
 */
public class Ms2FileReader {

    private BufferedReader reader;
    private String currentLine;
    

    public void open(String filePath) throws Ms2FileReaderException {
        try {
            reader = new BufferedReader(new FileReader(filePath));
            currentLine = reader.readLine();
        }
        catch (FileNotFoundException e) {
            close();
            throw new Ms2FileReaderException("File not found: "+filePath);
        }
        catch (IOException e) {
            close();
            throw new Ms2FileReaderException(e.getMessage());
        }
    }
    
    public Header getHeader() throws Ms2FileReaderException {
        
        Header header = new Header();
        while (isHeaderLine(currentLine)) {
            String[] tokens = currentLine.split("\\t");
            if (tokens.length >= 3) {
                header.addHeaderItem(tokens[1], tokens[2]);
            }
            else {
                // ignore if either label or value for this header item is missing
                //throw new Ms2FileReaderException("Invalid header: "+currentLine);
            }
            
            try {
                currentLine = reader.readLine();
            }
            catch (IOException e) {
                close();
                throw new Ms2FileReaderException(e.getMessage());
            }
        }
        return header;
    }
    
    public boolean hasScans() {
        return currentLine != null;
    }
    
    public Scan getNextScan() throws Ms2FileReaderException {
        
        Scan scan = parseScan();
        
        try {
            currentLine = reader.readLine(); // go to the next line
            
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
            close();
            throw new Ms2FileReaderException(e.getMessage());
        }
        
        return scan;
    }

    private void parseIAnalysis(Scan scan)
            throws Ms2FileReaderException, IOException {
        String[] tokens = currentLine.split("\\t");
        if (tokens.length < 3)
            throw new Ms2FileReaderException("2 fields expected for line: "+currentLine);
        scan.addAnalysisItem(tokens[1], tokens[2]);
        // advance to next line
        currentLine = reader.readLine();
    }
    
    private Scan parseScan() throws Ms2FileReaderException {
        
        // make sure we have a scan line
        if (!isScanLine(currentLine))
            throw new Ms2FileReaderException("Error parsing scan. Expected line starting with \"S\"");
        
        String[] tokens = currentLine.split("\\t");
        if (tokens.length < 4)
            throw new Ms2FileReaderException("Expected 3 fields in scan line: "+currentLine);
        
        int firstScan;
        int lastScan;
        float precursorMz;
        try {firstScan = Integer.parseInt(tokens[1]);}
        catch(NumberFormatException e) {throw new Ms2FileReaderException("Invalid first scan num in scan line: "+currentLine);}
        try {lastScan = Integer.parseInt(tokens[2]);}
        catch(NumberFormatException e) {throw new Ms2FileReaderException("Invalid last scan num in scan line: "+currentLine);}
        try {precursorMz = Float.parseFloat(tokens[3]);}
        catch(NumberFormatException e) {throw new Ms2FileReaderException("Invalid precursor m/z in scan line: "+currentLine);}
        
        Scan scan = new Scan();
        scan.setStartScan(firstScan);
        scan.setEndScan(lastScan);
        scan.setPrecursorMz(precursorMz);
        
        return scan;
    }
    
    private void parseScanCharge(Scan scan) throws Ms2FileReaderException {
        String tokens[] = currentLine.split("\\s");
        if (tokens.length < 3)
            throw new Ms2FileReaderException("2 fields expected for charge line: "+currentLine);
        
        // get the charge and mass
        int charge;
        float mass;
        try {charge = Integer.parseInt(tokens[1]);}
        catch(NumberFormatException e) {throw new Ms2FileReaderException("Invalid charge in line: "+currentLine);}
        try {mass = Float.parseFloat(tokens[2]);}
        catch(NumberFormatException e) {throw new Ms2FileReaderException("Invalid mass in line: "+currentLine);}
        
        ScanCharge scanCharge = new ScanCharge();
        scanCharge.setCharge(charge);
        scanCharge.setMass(mass);
        
        // parse any charge dependent analysis associated with this charge state
        try {
            while (isChargeDepAnalysisLine((currentLine = reader.readLine()))) {
                tokens = currentLine.split("\\t");
                if (tokens.length < 3)
                    throw new Ms2FileReaderException("2 fields expected for line: "+currentLine);
                scanCharge.addAnalysisItem(tokens[1], tokens[2]);
            }
        }
        catch (IOException e) {
            close();
            throw new Ms2FileReaderException(e.getMessage());
        }
        scan.addChargeState(scanCharge);
    }
    
    public void parsePeaks(Scan scan) throws Ms2FileReaderException {
        
        while (isPeakDataLine(currentLine)) {
            String[] tokens = currentLine.split("\\s");
            if (tokens.length < 2)
                throw new Ms2FileReaderException("missing charge and/or mass in line: "+currentLine);
            float mz;
            double intensity;
            try {mz = Float.parseFloat(tokens[0]);}
            catch (NumberFormatException e) {throw new Ms2FileReaderException("Invalid m/z in line: "+currentLine);}
            try {intensity = Double.parseDouble(tokens[1]);}
            catch (NumberFormatException e){throw new Ms2FileReaderException("Invalid intensity in line: "+currentLine);}
            
            scan.addPeak(mz, intensity);
            
            try {
                currentLine = reader.readLine();
            }
            catch (IOException e) {
                close();
                throw new Ms2FileReaderException(e.getMessage());
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
    
    
    private void close() {
        if (reader != null) 
            try {reader.close();}
            catch (IOException e) {}
    }
    
}
