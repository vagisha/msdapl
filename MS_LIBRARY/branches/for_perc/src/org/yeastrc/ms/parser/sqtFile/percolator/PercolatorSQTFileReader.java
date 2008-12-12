/**
 * PercolatorSQTFileReader.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.percolator;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public class PercolatorSQTFileReader extends SQTFileReader {

    private float percolatorVersion;
    private SearchProgram searchProgram;

    public PercolatorSQTFileReader() {
        super();
    }

    public void open(String filePath, float percolatorVersion, SearchProgram searchProgram) throws DataProviderException{
        super.open(filePath);
        this.percolatorVersion = percolatorVersion;
        this.searchProgram = searchProgram;
    }

    public void open(String fileName, Reader input, float percolatorVersion, SearchProgram searchProgram) throws DataProviderException  {
        super.open(fileName, input);
        this.percolatorVersion = percolatorVersion;
        this.searchProgram = searchProgram;
    }

    public void init() {
        super.init();
    }
    
    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    @Override
    public PercSearchScan getNextSearchScan() throws DataProviderException {
        PercSearchScan scan = new PercSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                PercolatorResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge());
                if (result != null) 
                    scan.addSearchResult(result);
            }
            else {
                break;
            }
        }
        return scan;
    }

    /**
     * Parses a 'M' line and any associated 'L' lines
     * @param scanNumber
     * @param charge
     * @return
     * @throws DataProviderException 
     */
    private PercolatorResultIn parsePeptideResult(int scanNumber, int charge) throws DataProviderException {

        PercolatorResult result = parsePeptideResult(currentLine, scanNumber, charge);

        advanceLine();

        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                locus = parseLocus(currentLine);
                
                // NOTE: IGNORE ALL 'M' LINES FOLLOWED BY THE FOLLOWING 'L' LINE
                // L       Placeholder satisfying DTASelect
                // This is not a valid result and we will return null
                if(locus.getAccession().startsWith("Placeholder satisfying DTASelect"))
                    return null;
                
                if (locus != null)
                    result.addMatchingLocus(locus);
            }
            else
                break;
            advanceLine();
        }
//        if (result.getProteinMatchList().size() == 0)
//            throw new DataProviderException(currentLineNum-1, "Invalid 'M' line.  No locus matches found." , null);
        return result;
    }

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
    PercolatorResult parsePeptideResult(String line, int scanNumber, int charge) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        PercolatorResult result = new PercolatorResult(getDynamicResidueMods(), getDynamicTerminalMods(), searchProgram);
        try {
            result.setxCorrRank(Integer.parseInt(tokens[1]));
            result.setSpRank(Integer.parseInt(tokens[2]));
            result.setMass(new BigDecimal(tokens[3]));
            result.setDeltaCN(new BigDecimal(tokens[4]));
            
            if(percolatorVersion >= 1.07)
                result.setPosteriorErrorProbability(Double.parseDouble(tokens[5]));
            else
                result.setDiscriminantScore(Double.parseDouble(tokens[5]));
            result.setQvalue(Double.parseDouble(tokens[6]));
            result.setNumMatchingIons(Integer.parseInt(tokens[7]));
            result.setNumPredictedIons(Integer.parseInt(tokens[8]));
        }
        catch(NumberFormatException e) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Error parsing number(s). "+e.getMessage(), line);
        }

        result.setOriginalPeptideSequence(tokens[9]);
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

    private static final class PercSearchScan implements SQTSearchScanIn {

        private SQTSearchScanIn scan;
        private List<PercolatorResultIn> resultList;

        public PercSearchScan(SQTSearchScanIn scan) {
            this.scan = scan;
            resultList = new ArrayList<PercolatorResultIn>();
        }
        public void addSearchResult(PercolatorResultIn result) {
            resultList.add(result);
        }
        public List<PercolatorResultIn> getScanResults() {
            return resultList;
        }
        public int getScanNumber() {
            return scan.getScanNumber();
        }
        public int getCharge() {
            return scan.getCharge();
        }
        public BigDecimal getLowestSp() {
            return scan.getLowestSp();
        }
        public BigDecimal getObservedMass() {
            return scan.getObservedMass();
        }
        public int getProcessTime() {
            return scan.getProcessTime();
        }
        public int getSequenceMatches() {
            return scan.getSequenceMatches();
        }
        public String getServerName() {
            return scan.getServerName();
        }
        public BigDecimal getTotalIntensity() {
            return scan.getTotalIntensity();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
