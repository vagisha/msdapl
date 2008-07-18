package org.yeastrc.ms.parser.sqtFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.service.SQTSearchDataProvider;


public class SQTFileReader implements SQTSearchDataProvider {

    private BufferedReader reader;
    private String currentLine;
    private int currentLineNum = 0;
    private String fileName;
    private List<MsSearchModification> searchDynamicMods;

    private int warnings = 0;

    private static final Logger log = Logger.getLogger(SQTFileReader.class);

    public int getWarningCount() {
        return warnings;
    }

    public void open(String filePath) throws IOException{
        reader = new BufferedReader(new FileReader(filePath));
        fileName = new File(filePath).getName();
        advanceLine();
    }


    public void open(String fileName, InputStream inStream) throws IOException {
        this.fileName = fileName;
        reader = new BufferedReader(new InputStreamReader(inStream));
        advanceLine();
    }

    public String getFileName() {
        return fileName;
    }
    
    private void advanceLine() throws IOException {

        currentLineNum++;
        currentLine = reader.readLine(); // advance first
        // skip over blank lines and line that don't start with a H, S, M or L
        while(currentLine != null && !isValidLine(currentLine)) {
            log.warn("!!!LINE# "+currentLineNum+" Lines should begin with H, S, M, or L. Invalid line: -- "+currentLine);
            currentLineNum++;
            currentLine = reader.readLine();
        }
    }

    private boolean isValidLine(String line) {
        if (line.trim().length() == 0)  return false;
        return( line.charAt(0) == 'L'   || 
                line.charAt(0) == 'M'   || 
                line.charAt(0) == 'S'   ||
                line.charAt(0) == 'H');
    }

    public SQTHeader getSearchHeader()  throws IOException, ParserException {

        SQTHeader header = new SQTHeader();
        while (isHeaderLine(currentLine)) {
            String[] tokens = currentLine.split("\\t");
            if (tokens.length >= 3) {
                // the value for the header may be a tab separated list; get the entire string
                // e.g. H       Alg-MaxDiffMod  3H      Alg-DisplayTop  5
                if (tokens.length > 3) {
                    int idx = currentLine.indexOf('\t',currentLine.indexOf('\t')+1);
                    tokens[2] = currentLine.substring(idx+1, currentLine.length());
                }
                header.addHeaderItem(tokens[1], tokens[2]);
            }
            else if (tokens.length >= 2){
                // maybe the header and value are separated by a space rather than a tab
                String temp = tokens[1].trim(); // remove any trailing space first
                int i = temp.indexOf(' '); // look for the first space character
                if (i != -1)
                    header.addHeaderItem(temp.substring(0, i), temp.substring(i+1));
                else
                    // if the value for this header is missing, add the header with a empty String
                    header.addHeaderItem(tokens[1], "");
            }
            // ignore if both label and value for this header item are missing

            advanceLine();
        }
        this.searchDynamicMods = header.getDynamicModifications();
        if (!header.isValid()) {
            warnings++;
            ParserException e = new ParserException(currentLineNum-1, "Invalid header.  Required fields are missing", "");
            log.warn(e.getMessage());
            throw e;
        }
        return header;
    }


    public boolean hasNextSearchScan()  {
        return currentLine != null;
    }

    /**
     * Returns the next scan in the file. 
     * Returns null if the scan line (beginning with 'S') was invalid
     * @return
     * @throws IOException 
     * @throws ParserException 
     */
    public ScanResult getNextSearchScan() throws IOException, ParserException {

        ScanResult scan = null;
        try {
            scan = parseScan(currentLine);
        }
        catch(ParserException e) {
            // there was an error parsing the 'S' line. We will ignore this scan
            // if there are any 'M' or 'L' lines after the offending 'S' line skip over them.
            skipScan();
            log.warn(e.getMessage());
            throw e;
        }

        advanceLine();

        while(currentLine != null) {

            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                PeptideResult result = null;
                try {
                    result = parsePeptideResult(scan.getStartScan(), scan.getCharge());
                }
                catch (ParserException e) {
                    log.warn(e.getMessage());
                }
                if (result != null) 
                    scan.addPeptideResult(result);
            }
            else {
                break;
            }
        }

        if (!scan.isValid()) {
            warnings++;
            ParserException e = new ParserException(currentLineNum-1, "Invalid SQT scan -- no results found", "");
            log.warn(e.getMessage());
            throw e;
        }
        return scan;
    }

    private void skipScan() throws IOException {
        advanceLine();
        while(isResultLine(currentLine) || isLocusLine(currentLine))
            advanceLine();
    }

    private ScanResult parseScan(String line) throws ParserException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            warnings++;
            throw new ParserException(currentLineNum, "Error parsing scan. Expected line starting with 'S'. Found -- ", line);
        }

        String[] tokens = line.split("\\t");
        if (tokens.length < 10) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'S' line. Expected 10 fields", line);
        }

        ScanResult scan = new ScanResult();
        try {
            scan.setStartScan(Integer.parseInt(tokens[1]));
            scan.setEndScan(Integer.parseInt(tokens[2]));
            scan.setCharge(Integer.parseInt(tokens[3]));
            scan.setProcessingTime(Integer.parseInt(tokens[4]));
            scan.setObservedMass(new BigDecimal(tokens[6]));
            scan.setTotalIntensity(new BigDecimal(tokens[7]));
            scan.setLowestSp(new BigDecimal(tokens[8]));
            scan.setNumMatching(Integer.parseInt(tokens[9]));
        }
        catch(NumberFormatException e) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'S' line. Error parsing number(s): "+e.getMessage(), line);
        }
        scan.setServer(tokens[5]);

        return scan;
    }

    /**
     * Parses a 'M' line and any associated 'L' lines
     * @param scanNumber
     * @param charge
     * @return
     * @throws IOException
     * @throws ParserException 
     */
    private PeptideResult parsePeptideResult(int scanNumber, int charge) throws IOException, ParserException {

        PeptideResult result = null;

        try {
            result = parsePeptideResult(currentLine, scanNumber, charge);
        }
        catch (ParserException e) {
            // there was an error parsing the 'M' line. We will ignore this peptide result
            // if there are any 'L' line after the offending 'M' line skip over them.
            skipPeptideResult();
            throw e;
        }

        advanceLine();
        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                try {locus = parseLocus(currentLine);}
                catch (ParserException e) { log.warn(e.getMessage());}
                if (locus != null)
                    result.addMatchingLocus(locus);
            }
            else
                break;
            advanceLine();
        }
        return result;
    }

    private void skipPeptideResult() throws IOException {
        advanceLine();
        while(isLocusLine(currentLine))
            advanceLine();
    }

    /**
     * Parses a 'M' line in the sqt file.
     * @param line
     * @param scanNumber
     * @param charge
     * @return
     * @throws ParserException
     */
    private PeptideResult parsePeptideResult(String line, int scanNumber, int charge) throws ParserException {

        String[] tokens = line.split("\\t");
        if (tokens.length < 11) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        for (int i = 0; i < tokens.length; i++)
            tokens[i] = tokens[i].replaceAll("\\s+", "");

        PeptideResult result = new PeptideResult(searchDynamicMods);
        try {
            result.setxCorrRank(Integer.parseInt(tokens[1]));
            result.setSpRank(Integer.parseInt(tokens[2]));
            result.setMass(new BigDecimal(tokens[3]));
            result.setDeltaCN(new BigDecimal(tokens[4]));
            result.setXcorr(new BigDecimal(tokens[5]));
            result.setSp(new BigDecimal(tokens[6]));
            result.setNumMatchingIons(Integer.parseInt(tokens[7]));
            result.setNumPredictedIons(Integer.parseInt(tokens[8]));
        }
        catch(NumberFormatException e) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'M' line. Error parsing number(s): "+e.getMessage(), line);
        }
        
        result.setResultSequence(tokens[9]);
        result.setValidationStatus(tokens[10].charAt(0));
        result.setCharge(charge);
        result.setScanNumber(scanNumber);
        
        try {
            result.getResultPeptide();
        }
        catch(IllegalArgumentException e) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid peptide sequence in 'M' line: "+e.getMessage(), line);
        }
        return result;
    }

    /**
     * Parses a 'L' line in the sqt file
     * @param line
     * @return
     * @throws ParserException
     */
    private DbLocus parseLocus(String line) throws ParserException {
        String[] tokens = line.split("\\t");
        if (tokens.length < 2) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'L' line. Expected 2 fields", line);
        }

        if (tokens.length > 2)
            return new DbLocus(tokens[1], tokens[2]);
        else
            return new DbLocus(tokens[1], null);
    }


    private boolean isScanLine(String line) {
        if (line == null)   return false;
        return line.startsWith("S");
    }

    private boolean isHeaderLine(String line) {
        if (line == null)   return false;
        return line.startsWith("H");
    }

    private boolean isResultLine(String line) {
        if (line == null)   return false;
        return line.startsWith("M");
    }

    private boolean isLocusLine(String line) {
        if (line == null)   return false;
        return line.startsWith("L");
    }

    /**
     * This method should be called explicitly after the file has been read.
     */
    public void close() {
        currentLine = null;
        if (reader != null) 
            try {reader.close();}
        catch (IOException e) {}
    }
    
}
