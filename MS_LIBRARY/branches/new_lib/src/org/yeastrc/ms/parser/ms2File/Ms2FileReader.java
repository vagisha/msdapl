/**
 * Ms2FileParser.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.service.MS2RunDataProvider;


/**
 * 
 */
public class Ms2FileReader implements MS2RunDataProvider {

    private BufferedReader reader;
    private String currentLine;
    private String fileName;
    private String sha1Sum;
    private int currentLineNum = 0;
    private int warnings = 0;

    private static final Logger log = Logger.getLogger(Ms2FileReader.class);
    
    private static final Pattern headerPattern = Pattern.compile("^H\\s+([\\S]+)\\s*(.*)");

    public int getWarningCount() {
        return warnings;
    }

    public void open(String filePath, String sha1Sum) throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        fileName = new File(filePath).getName();
        this.sha1Sum = sha1Sum;
        advanceLine();
    }

    public void open(String fileName, InputStream inStream, String sha1Sum) throws IOException {
        this.fileName = fileName;
        this.sha1Sum = sha1Sum;
        reader = new BufferedReader(new InputStreamReader(inStream));
        advanceLine();
    }

    @Override
    public String getFileName() {
        return this.fileName;
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

    public MS2Header getRunHeader() throws IOException {

        MS2Header header = new MS2Header();
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            if (nameAndVal.length == 2) {
                header.addHeaderItem(nameAndVal[0], nameAndVal[1]);
            }
            else {
                // ignore if both label and value for this header item are missing
                //throw new Exception("Invalid header: "+currentLine);
                log.warn("!!!LINE# "+currentLineNum+" Invalid 'H' line; ignoring...: -- "+currentLine);
            }
            advanceLine();
        }
//        if (!header.isValid()) {
//            warnings++;
//            ParserException e = new ParserException(currentLineNum-1, "Invalid header.  Required fields are missing", "");
//            log.warn(e.getMessage());
//            throw e;
//        }
        header.setFileName(fileName);
        header.setSha1Sum(sha1Sum);
        return header;
    }

    String[] parseHeader(String line) {
        Matcher match = headerPattern.matcher(line);
        if (match.matches())
            return new String[]{match.group(1), match.group(2)};
        else
            return new String[0];
    }

    public boolean hasNextScan() {
        return currentLine != null;
    }

    public MS2Scan getNextScan() throws IOException, ParserException {

        Scan scan;
        try {
            scan = parseScan();
        }
        catch (ParserException e) {
            log.warn(e.getMessage());
            skipScan();
            throw e;
        }

        advanceLine(); // go to the next line
        
        while(currentLine != null) {
            // is this one of the charge states of the scan?
            if (isChargeLine(currentLine)) {
                try {
                    ScanCharge sc = parseScanCharge();
                    scan.addChargeState(sc);
                }
                catch (ParserException e) {
                    log.warn(e.getMessage());
                }
            }
            // is this one of the charge independent analysis for this scan?
            else if (isChargeIndAnalysisLine(currentLine)) {
                try {
                    MS2Field iAnalysis = parseIAnalysis(currentLine);
                    scan.addAnalysisItem(iAnalysis.getName(), iAnalysis.getValue());
                }
                catch (ParserException e) {
                    log.warn(e.getMessage());
                }
                catch (DataFormatException e) {
                    log.warn(e.getMessage());
                }
                advanceLine();
            }
            // it is neither so must be peak data
            else {
                parsePeaks(scan);
                break; // done parsing this scan!
            }
        }
        if (!scan.isValid()) {
            warnings++;
            ParserException e = new ParserException(currentLineNum-1, "Invalid MS2 scan -- no peaks found", "");
            log.warn(e.getMessage());
            throw e;
        }
        
        return scan;
    }

    private void skipScan() throws IOException {
        while (currentLine != null && !isScanLine(currentLine))
            advanceLine();
    }

    private MS2Field parseIAnalysis(String line) throws ParserException {
        String[] tokens = line.split("\\s+");
        if (tokens.length < 3)
            throw new ParserException(currentLineNum, "Invalid 'I' line. Expected 3 fields.", line);
        return new HeaderItem(tokens[1], tokens[2]);
    }

    private Scan parseScan() throws ParserException {

        // make sure we have a scan line
        if (!isScanLine(currentLine)) {
            warnings++;
            throw new ParserException(currentLineNum, "Error parsing scan. Expected line starting with 'S'.", currentLine);
        }

        String[] tokens = currentLine.split("\\s+");
        if (tokens.length < 4) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'S' line. Expected 4 fields.", currentLine);
        }

        Scan scan = new Scan();
        try {
            scan.setStartScan(Integer.parseInt(tokens[1]));
            scan.setEndScan(Integer.parseInt(tokens[2]));
            scan.setPrecursorMz(tokens[3]);
        }
        catch(NumberFormatException e) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'S' line. Error parsing number(s). "+e.getMessage(), currentLine);
        }
        return scan;
    }

    private ScanCharge parseScanCharge() throws IOException, ParserException {
        String tokens[] = currentLine.split("\\s+");
        if (tokens.length < 3) {
            skipScanCharge();
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'Z' line. Expected 3 fields.", currentLine);
        }

        // get the charge and mass
        ScanCharge scanCharge = new ScanCharge();
        try {
            scanCharge.setCharge(Integer.parseInt(tokens[1]));
            scanCharge.setMass(new BigDecimal(tokens[2]));
        }
        catch(NumberFormatException e) {
            skipScanCharge();
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'Z' line. Error parsing number(s). "+e.getMessage(), currentLine);
        }

        // parse any charge dependent analysis associated with this charge state
        advanceLine();
        while (isChargeDepAnalysisLine((currentLine))) {
            tokens = currentLine.split("\\s+");
            if (tokens.length < 3) {
                warnings++;
                ParserException e = new ParserException(currentLineNum, "Invalid 'D' line. Expected 2 fields.", currentLine);
                log.warn(e.getMessage());
            }
            else {
                scanCharge.addAnalysisItem(tokens[1], tokens[2]);
            }
            advanceLine();
        }
        return scanCharge;
    }

    private void skipScanCharge() throws IOException {
        advanceLine();
        while(isChargeDepAnalysisLine(currentLine))
            advanceLine();
    }

    public void parsePeaks(Scan scan) throws IOException {

        while (isPeakDataLine(currentLine)) {
            String[] tokens = currentLine.split("\\s+");
            if (tokens.length >= 2) {
                // add peak m/z and intensity values
                if (!isValidDouble(tokens[0])) {
                    warnings++;
                    log.warn( new ParserException(currentLineNum, "Invalid m/z value. Ignoring peak...", currentLine).getMessage());
                    continue;
                }
                if (!isValidDouble(tokens[1])) {
                    warnings++;
                    log.warn( new ParserException(currentLineNum, "Invalid intensity value. Ignoring peak...", currentLine).getMessage());
                    continue;
                }
                scan.addPeak(tokens[0], tokens[1]); 
            }
            else if (tokens.length == 1) {
                warnings++;
                log.warn( new ParserException(currentLineNum, 
                        "missing peak intensity in line. Setting peak intensity to 0.", currentLine).getMessage());
                if (!isValidDouble(tokens[0])) {
                    warnings++;
                    log.warn( new ParserException(currentLineNum, "Invalid m/z value. Ignoring peak...", currentLine).getMessage());
                    continue;
                }
                scan.addPeak(tokens[0], "0");
            }
            advanceLine();
        }
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


    public void close() {
        if (reader != null) 
            try {reader.close();}
        catch (IOException e) {}
    }
}
