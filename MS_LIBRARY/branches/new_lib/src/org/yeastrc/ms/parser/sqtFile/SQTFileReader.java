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
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.parser.AbstractReader;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.service.SQTSearchDataProvider;


public class SQTFileReader extends AbstractReader implements SQTSearchDataProvider {

    private List<MsSearchModification> searchDynamicMods;
    
    private String serverAddress;

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
    
    public SQTFileReader(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public SQTHeader getSearchHeader()  throws DataProviderException {

        SQTHeader header = new SQTHeader();
        // if any search databases are found; set the server path
        header.setServerAddress(serverAddress);
        
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            addHeaderItem(header, nameAndVal);
            advanceLine();
        }
        
        if (!header.isValid())
            throw new DataProviderException("Invalid SQT Header. One or more required headers is missing. "+
                    "Required headers:\n\t"+SQTHeader.requiredHeaders());
        
        this.searchDynamicMods = header.getDynamicModifications();
        return header;
    }

    private void addHeaderItem(SQTHeader header, String[] nameAndVal) throws DataProviderException {
        
        if (nameAndVal.length == 0)
            throw new DataProviderException(currentLineNum, "Invalid Header line.", currentLine);
        
        String name = nameAndVal[0];
        String val = nameAndVal.length > 1 ? nameAndVal[1] : null;
        
        try { 
            header.addHeaderItem(name, val);
        }
        catch(SQTParseException e) {
            DataProviderException ex = new DataProviderException(currentLineNum, e.getMessage(), currentLine, e);
            if (e.isFatal())
                throw ex;
            else
                log.warn(ex.getMessage());
        }
    }
    
    String[] parseHeader(String line) {
        return parseNameValueLine(line, headerPattern);
    }
    
    public boolean hasNextSearchScan()  {
        return currentLine != null;
    }

    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    public ScanResult getNextSearchScan() throws DataProviderException {

        ScanResult scan = parseScan(currentLine);
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                PeptideResult result = parsePeptideResult(scan.getStartScan(), scan.getCharge());
                if (result != null) 
                    scan.addPeptideResult(result);
            }
            else {
                break;
            }
        }
        
        return scan;
    }

    ScanResult parseScan(String line) throws DataProviderException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            throw new DataProviderException(currentLineNum, "Error parsing scan. Expected line starting with 'S'.", line);
        }

        String[] tokens = line.split("\\s+");
        if (tokens.length != 10) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Expected 10 fields", line);
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
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Error parsing number(s). "+e.getMessage(), line);
        }
        scan.setServer(tokens[5]);

        return scan;
    }

    /**
     * Parses a 'M' line and any associated 'L' lines
     * @param scanNumber
     * @param charge
     * @return
     * @throws DataProviderException 
     */
    private PeptideResult parsePeptideResult(int scanNumber, int charge) throws DataProviderException {

        PeptideResult result = parsePeptideResult(currentLine, scanNumber, charge);

        advanceLine();
        
        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                locus = parseLocus(currentLine);
                if (locus != null)
                    result.addMatchingLocus(locus);
            }
            else
                break;
            advanceLine();
        }
        return result;
    }

//    private void skipPeptideResult() throws DataProviderException {
//        advanceLine();
//        while(isLocusLine(currentLine))
//            advanceLine();
//    }

    /**
     * Parses a 'M' line in the sqt file.
     * @param line
     * @param scanNumber
     * @param charge
     * @return
     * @throws DataProviderException if the line did not contain the expected number of fields OR
     *                         there was an error parsing numbers in the line OR
     *                         there was an error parsing the peptide sequence in this 'M' line.
     */
    PeptideResult parsePeptideResult(String line, int scanNumber, int charge) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
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
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Error parsing number(s). "+e.getMessage(), line);
        }
        
        result.setResultSequence(tokens[9]);
        result.setValidationStatus(tokens[10].charAt(0));
        result.setCharge(charge);
        result.setScanNumber(scanNumber);
        
        // parse the peptide sequence
        try {
            result.buildPeptideResult();
        }
        catch(SQTParseException e) {
            throw new DataProviderException(currentLineNum, "Invalid peptide sequence in 'M'. "+e.getMessage(), line);
        }
        return result;
    }

    /**
     * Parses a 'L' line in the sqt file
     * @param line
     * @return
     * @throws DataProviderException
     */
    DbLocus parseLocus(String line) throws DataProviderException {
        
        String[] nameAndVal = super.parseNameValueLine(line, locusPattern);
        if (nameAndVal.length == 2) {
            return new DbLocus(nameAndVal[0], nameAndVal[1]);
        }
        else if (nameAndVal.length == 1) {
            return new DbLocus(nameAndVal[0], null);
        }
        
        else {
            throw new DataProviderException(currentLineNum, "Invalid 'L' line. Expected 2 fields", line);
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
