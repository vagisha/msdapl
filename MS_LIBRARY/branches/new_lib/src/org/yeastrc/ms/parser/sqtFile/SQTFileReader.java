package org.yeastrc.ms.parser.sqtFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.parser.AbstractReader;
import org.yeastrc.ms.parser.ParserException;
import org.yeastrc.ms.service.SQTSearchDataProvider;


public class SQTFileReader extends AbstractReader implements SQTSearchDataProvider {

    private List<MsSearchModification> searchDynamicMods;

    private static final Logger log = Logger.getLogger(SQTFileReader.class);

    private static final Pattern headerPattern = Pattern.compile("^H\\s+([\\S]+)\\s*(.*)");
    private static final Pattern locusPattern = Pattern.compile("^L\\s+([\\S]+)\\s*(.*)");
    private static final Pattern sqtGenPattern = Pattern.compile("^H\\s+SQTGenerator\\s+(.*)");
    
    public static boolean isSequestSQT(String filePath) throws FileNotFoundException, IOException {
        return isSequestSQT(new FileReader(filePath));
    }
    
    static boolean isSequestSQT(Reader reader) throws IOException {
        Matcher match = null;
        BufferedReader bReader = null;
        bReader = new BufferedReader(reader);
        String line;
        try {
            line = bReader.readLine();
            if (line != null)   line = line.trim();
            while(line != null && isHeaderLine(line)) {
                if (line.contains("Percolator"))    {
                    log.warn("Percolator sqt.");
                    return false;
                }
                match = sqtGenPattern.matcher(line);
                if (match.matches()) {
                    String genProg = match.group(1);
                    if (genProg != null && 
                            (genProg.equalsIgnoreCase(SQTHeader.SEQUEST) ||
                             genProg.equalsIgnoreCase(SQTHeader.SEQUEST_NORM)))
                        return true;
                    else {
                        log.warn("Non-Sequest sqt. Generating program is: "+genProg);
                        return false;
                    }
                }
                line = bReader.readLine();
                if (line != null)   line = line.trim();
            }
        }
        finally {
            if (bReader != null) {
                try {bReader.close();}
                catch (IOException e) {}
            }
        }
        log.warn("No sqt generating program found");
        return false;
    }
    
    public SQTHeader getSearchHeader()  throws IOException {

        SQTHeader header = new SQTHeader();
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            if (nameAndVal.length == 2) {
                header.addHeaderItem(nameAndVal[0], nameAndVal[1]);
            }
            else {
                // ignore if both label and value for this header item are missing
                log.warn("!!!LINE# "+currentLineNum+" Invalid 'H' line; ignoring...: -- "+currentLine);
            }
            advanceLine();
        }
        this.searchDynamicMods = header.getDynamicModifications();
        return header;
    }


    String[] parseHeader(String line) {
        Matcher match = headerPattern.matcher(line);
        if (match.matches())
            return new String[]{match.group(1), match.group(2)};
        else
            return new String[0];
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
//            throw e;
        }
        return scan;
    }

    private void skipScan() throws IOException {
        advanceLine();
        while(isResultLine(currentLine) || isLocusLine(currentLine))
            advanceLine();
    }

    ScanResult parseScan(String line) throws ParserException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            warnings++;
            throw new ParserException(currentLineNum, "Error parsing scan. Expected line starting with 'S'. Found -- ", line);
        }

        String[] tokens = line.split("\\s+");
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
            scan.setSequenceMatches(Integer.parseInt(tokens[9]));
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
     * @throws ParserException if the line did not contain the expected number of fields OR
     *                         there was an error parsing numbers in the line OR
     *                         there was an error parsing the peptide sequence in this 'M' line.
     */
    PeptideResult parsePeptideResult(String line, int scanNumber, int charge) throws ParserException {

        String[] tokens = line.split("\\s+");
        if (tokens.length < 11 || tokens.length > 11) {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

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
        
        // parse the peptide sequence
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
    DbLocus parseLocus(String line) throws ParserException {
        
        Matcher match = locusPattern.matcher(line);
        if (match.matches()) {
            return new DbLocus(match.group(1), match.group(2));
        }
        else {
            warnings++;
            throw new ParserException(currentLineNum, "Invalid 'L' line. Expected >= 2 fields", line);
        }
    }


    private static boolean isScanLine(String line) {
        if (line == null)   return false;
        return line.startsWith("S");
    }

    private static boolean isHeaderLine(String line) {
        if (line == null)   return false;
        return line.startsWith("H");
    }

    private static boolean isResultLine(String line) {
        if (line == null)   return false;
        return line.startsWith("M");
    }

    private static boolean isLocusLine(String line) {
        if (line == null)   return false;
        return line.startsWith("L");
    }

    protected boolean isValidLine(String line) {
        if (line.trim().length() == 0)  return false;
        return( line.charAt(0) == 'L'   || 
                line.charAt(0) == 'M'   || 
                line.charAt(0) == 'S'   ||
                line.charAt(0) == 'H');
    }
}
