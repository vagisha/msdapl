/**
 * Ms2FileParser.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.parser.AbstractReader;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.service.MS2RunDataProvider;


/**
 * 
 */
public class Ms2FileReader extends AbstractReader implements MS2RunDataProvider {

    private String sha1Sum;

    private static final Logger log = Logger.getLogger(Ms2FileReader.class);

    private static final Pattern headerPattern = Pattern.compile("^H\\s+([\\S]+)\\s*(.*)");
    private static final Pattern dAnalysisPattern = Pattern.compile("^D\\s+([\\S]+)\\s*(.*)");
    private static final Pattern iAnalysisPattern = Pattern.compile("^I\\s+([\\S]+)\\s*(.*)");
    private static final Pattern chargeStatePattern = Pattern.compile("^Z\\s+(\\d+)\\s+(\\d+\\.?\\d*)\\s*$");


    public void open(String filePath, String sha1Sum) throws DataProviderException {
        this.sha1Sum = sha1Sum;
        super.open(filePath);
    }

    public void open(String fileName, InputStream inStream, String sha1Sum) throws DataProviderException {
        this.sha1Sum = sha1Sum;
        super.open(fileName, inStream);
    }

    public MS2Header getRunHeader() throws DataProviderException {

        MS2Header header = new MS2Header();
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            if (nameAndVal.length == 2) {
                header.addHeaderItem(nameAndVal[0], nameAndVal[1]);
            }
            else if (nameAndVal.length == 1) {
                DataProviderException e = new DataProviderException(currentLineNum, "Missing value in 'H' line.Setting value to null.", currentLine);
                log.warn(e.getMessage());
                header.addHeaderItem(nameAndVal[0], null);
            }
            else {
                // ignore if both label and value for this header item are missing
                DataProviderException e = new DataProviderException(currentLineNum, "Invalid 'H' line; Ignoring.", currentLine);
                log.warn(e.getMessage());
            }
            advanceLine();
        }
        
        header.setFileName(fileName);
        header.setSha1Sum(sha1Sum);
        return header;
    }

    public boolean hasNextScan() {
        return currentLine != null;
    }

    public MS2Scan getNextScan() throws DataProviderException {

        Scan scan = parseScan(currentLine);

        advanceLine(); // go to the next line

        while(currentLine != null) {
            // is this one of the charge states of the scan?
            if (isChargeLine(currentLine)) {
                ScanCharge sc = parseScanCharge();
                scan.addChargeState(sc);
            }
            // is this one of the charge independent analysis for this scan?
            else if (isChargeIndAnalysisLine(currentLine)) {
                parseChargeIndependentAnalysis(currentLine, scan);
                advanceLine();
            }
            // it is neither so must be peak data
            else {
                parsePeaks(scan);
                break; // done parsing this scan!
            }
        }
        if (!scan.isValid()) {
            throw new DataProviderException(currentLineNum-1, "Invalid MS2 scan -- no valid peaks and/or charge states found", "");
        }

        return scan;
    }
   

    private Scan parseScan(String line) throws DataProviderException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            throw new DataProviderException(currentLineNum, "Error parsing scan. Expected line starting with 'S'.", line);
        }

        String[] tokens = line.split("\\s+");
        if (tokens.length != 4) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Expected 4 fields.", line);
        }

        Scan scan = new Scan();
        try {
            scan.setStartScan(Integer.parseInt(tokens[1]));
            scan.setEndScan(Integer.parseInt(tokens[2]));
            scan.setPrecursorMz(tokens[3]);
        }
        catch(NumberFormatException e) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Error parsing number(s). "+e.getMessage(), line);
        }
        return scan;
    }

    private ScanCharge parseScanCharge() throws DataProviderException {
        
        ScanCharge scanCharge = parseScanCharge(currentLine);

        // parse any charge dependent analysis associated with this charge state
        advanceLine();
        while (isChargeDepAnalysisLine((currentLine))) {
            parseChargeDependentAnalysis(currentLine, scanCharge);
            advanceLine();
        }
        return scanCharge;
    }

    ScanCharge parseScanCharge(String line) throws DataProviderException {
        
        Matcher match = chargeStatePattern.matcher(line);
        if (match.matches()) {
            ScanCharge scanCharge = new ScanCharge();
            scanCharge.setCharge(Integer.parseInt(match.group(1)));
            scanCharge.setMass(new BigDecimal(match.group(2)));
            return scanCharge;
        }
        else {
            throw new DataProviderException(currentLineNum, "Invalid 'Z' line.", line);
        }
    }

    void parseChargeDependentAnalysis(String line, ScanCharge scanCharge) {
        String[] nameAndVal = parseChargeDependentAnalysis(line);
        if (nameAndVal.length == 2) {
            scanCharge.addAnalysisItem(nameAndVal[0], nameAndVal[1]);
        }
        else if (nameAndVal.length == 2) {
            scanCharge.addAnalysisItem(nameAndVal[0], null);
            DataProviderException e = new DataProviderException(currentLineNum, "Missing value in 'D' line. Setting value to null.", line);
            log.warn(e.getMessage());
        }
        else {
            // ignore if both label and value for this analysis item are missing
            DataProviderException e = new DataProviderException(currentLineNum, "Invalid 'D' line. Expected 2 fields. Ignoring.", line);
            log.warn(e.getMessage());
        }
    }

    void parseChargeIndependentAnalysis(String line, Scan scan) {
        String[] nameAndVal = parseChargeIndependentAnalysis(line);
        if (nameAndVal.length == 2) {
            scan.addAnalysisItem(nameAndVal[0], nameAndVal[1]);
        }
        else if (nameAndVal.length == 1) {
            scan.addAnalysisItem(nameAndVal[0], null);
            DataProviderException e = new DataProviderException(currentLineNum, "Missing value in 'I' line. Setting value to null.", line);
            log.warn(e.getMessage());
        }
        else {
            // ignore if both label and value for this analysis item are missing
            DataProviderException e = new DataProviderException(currentLineNum, "Invalid 'I' line. Expected 2 fields. Ignoring.", line);
            log.warn(e.getMessage());
        }
    }
    
    public void parsePeaks(Scan scan) throws DataProviderException {

        while (isPeakDataLine(currentLine)) {
            String[] tokens = currentLine.split("\\s+");
            
            if (tokens.length == 0) {
                throw new DataProviderException(currentLineNum, "Missing peak m/z and intensity for scan: "+scan.getStartScanNum(), currentLine);
            }
            
            if (tokens.length == 1) {
                throw new DataProviderException(currentLineNum, "Missing peak intensity for scan: "+scan.getStartScanNum(), currentLine);
            }
            
            
            if (!isValidDouble(tokens[0])) {
                throw new DataProviderException(currentLineNum, "Invalid m/z value found for peak in scan: "+scan.getStartScanNum(), currentLine);
            }
            
            if (!isValidDouble(tokens[1])) {
                throw new DataProviderException(currentLineNum, "Invalid intensity value found for peak in scan: "+scan.getStartScanNum(), currentLine);
            }
            
            scan.addPeak(tokens[0], tokens[1]); 
            advanceLine();
        }
    }

    String[] parseHeader(String line) {
        return parseNameValueLine(line, headerPattern);
    }

    String[] parseChargeIndependentAnalysis(String line) {
        return parseNameValueLine(line, iAnalysisPattern);
    }

    String[] parseChargeDependentAnalysis(String line) {
        return parseNameValueLine(line, dAnalysisPattern);
    }

    private boolean isValidDouble(String doubleStr) {
        try {Double.parseDouble(doubleStr);}
        catch(NumberFormatException e) {
            return false;
        }
        return true;
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

    protected boolean isValidLine(String line) {
        if (line.trim().length() == 0)  return false;
        return true;
    }
}
