/**
 * ProlucidSQTFileReader.java
 * @author Vagisha Sharma
 * Aug 30, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.prolucid;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.PrimaryScore;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.SecondaryScore;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public class ProlucidSQTFileReader extends SQTFileReader {

    
    private PrimaryScore primaryScoreType;
    private SecondaryScore secondaryScoreType;
    
    public void init() {
        super.init();
        primaryScoreType = null;
        secondaryScoreType = null;
    }
    
    public void open(String filePath, String serverAddress, PrimaryScore primaryScore, SecondaryScore secondaryScore) throws DataProviderException{
        super.open(filePath, serverAddress);
        this.primaryScoreType = primaryScore;
        this.secondaryScoreType = secondaryScore;
    }
    
    public void open(String fileName, Reader input, String serverAddress, PrimaryScore primaryScore, SecondaryScore secondaryScore) throws DataProviderException  {
        super.open(fileName, input, serverAddress);
        this.primaryScoreType = primaryScore;
        this.secondaryScoreType = secondaryScore;
    }
    
    
    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    @Override
    public ProlucidSearchScan getNextSearchScan() throws DataProviderException {
        PlucidSearchScan scan = new PlucidSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                ProlucidSearchResult result = parsePeptideResult(scan.getScanNumber(), scan.getCharge());
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
    private ProlucidSearchResult parsePeptideResult(int scanNumber, int charge) throws DataProviderException {

        ProlucidResult result = parsePeptideResult(currentLine, scanNumber, charge);

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
    ProlucidResult parsePeptideResult(String line, int scanNumber, int charge) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        ProlucidResult result = new ProlucidResult(getDynamicResidueMods(), getDynamicTerminalMods());
        try {
            result.setxCorrRank(Integer.parseInt(tokens[1]));
            result.setSpRank(Integer.parseInt(tokens[2]));
            result.setMass(new BigDecimal(tokens[3]));
//            result.setDeltaCN(new BigDecimal(tokens[4]));
            result.setBinomialScore(Double.valueOf(tokens[4]));
            result.setXcorr(new BigDecimal(tokens[5]));
//            result.setSp(new BigDecimal(tokens[6]));
            result.setZscore(Double.valueOf(tokens[6]));
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


    private static final class PlucidSearchScan implements ProlucidSearchScan {

        private SQTSearchScan scan;
        private List<ProlucidSearchResult> resultList;

        public PlucidSearchScan(SQTSearchScan scan) {
            this.scan = scan;
            resultList = new ArrayList<ProlucidSearchResult>();
        }
        public void addSearchResult(ProlucidSearchResult result) {
            resultList.add(result);
        }
        public List<ProlucidSearchResult> getScanResults() {
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
}
