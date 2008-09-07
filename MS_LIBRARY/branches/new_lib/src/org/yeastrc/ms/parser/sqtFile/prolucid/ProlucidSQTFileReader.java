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

import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.Score;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public class ProlucidSQTFileReader extends SQTFileReader {

    
    private ScoreSetter xcorrColScoreSetter;
    private ScoreSetter spColScoreSetter;
    private ScoreSetter deltaCNColScoreSetter;
    
    public void init() {
        super.init();
    }
    
    public void open(String filePath, Score spColumnScore, Score xcorrColumnScore, Score deltaCNColumnScore) throws DataProviderException{
        super.open(filePath);
        initScoreSetters(spColumnScore, xcorrColumnScore, deltaCNColumnScore);
    }
    
    public void open(String fileName, Reader input, Score spColumnScore, Score xcorrColumnScore, Score deltaCNColumnScore) throws DataProviderException  {
        super.open(fileName, input);
        initScoreSetters(spColumnScore, xcorrColumnScore, deltaCNColumnScore);
    }
    
    private void initScoreSetters(Score spColumnScore, Score xcorrColumnScore, Score deltaCNColumnScore) {
        
        // what score will we find in the sp score column
        if (spColumnScore == Score.SP)
            spColScoreSetter = new SpScoreSetter();
        else if (spColumnScore == Score.BIN_PROB)
            spColScoreSetter = new BinProbScoreSetter();
        else if (spColumnScore == Score.XCORR)
            spColScoreSetter = new XcorrScoreSetter();
        else if (spColumnScore == Score.DELTA_CN)
            spColScoreSetter = new DeltaCNScoreSetter();
        else if (spColumnScore == Score.ZSCORE)
            spColScoreSetter = new ZscoreScoreSetter();
        else if (spColumnScore == Score.BLANK)
            spColScoreSetter = new BlankScoreSetter();
            
        
        // what score will we find in the xcorr score column
        if (xcorrColumnScore == Score.SP)
            xcorrColScoreSetter = new SpScoreSetter();
        else if (xcorrColumnScore == Score.BIN_PROB)
            xcorrColScoreSetter = new BinProbScoreSetter();
        else if (xcorrColumnScore == Score.XCORR)
            xcorrColScoreSetter = new XcorrScoreSetter();
        else if (xcorrColumnScore == Score.DELTA_CN)
            xcorrColScoreSetter = new DeltaCNScoreSetter();
        else if (xcorrColumnScore == Score.ZSCORE)
            xcorrColScoreSetter = new ZscoreScoreSetter();
        else if (xcorrColumnScore == Score.BLANK)
            xcorrColScoreSetter = new BlankScoreSetter();
        
        // what score will we find in the deltacn score column
        if (deltaCNColumnScore == Score.SP)
            deltaCNColScoreSetter = new SpScoreSetter();
        else if (deltaCNColumnScore == Score.BIN_PROB)
            deltaCNColScoreSetter = new BinProbScoreSetter();
        else if (deltaCNColumnScore == Score.XCORR)
            deltaCNColScoreSetter = new XcorrScoreSetter();
        else if (deltaCNColumnScore == Score.DELTA_CN)
            deltaCNColScoreSetter = new DeltaCNScoreSetter();
        else if (deltaCNColumnScore == Score.ZSCORE)
            deltaCNColScoreSetter = new ZscoreScoreSetter();
        else if (deltaCNColumnScore == Score.BLANK)
            deltaCNColScoreSetter = new BlankScoreSetter();
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
                ProlucidSearchResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge());
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
    private ProlucidSearchResultIn parsePeptideResult(int scanNumber, int charge) throws DataProviderException {

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
            
            // parsing deltaCN column; what score do we have in this column
            deltaCNColScoreSetter.setResultScore(result, tokens[4]);
            
            // parsing the xcorr column; what score do we have in this column
            xcorrColScoreSetter.setResultScore(result, tokens[5]);
            
            // parsing the sp column; what score do we have in this column
            spColScoreSetter.setResultScore(result, tokens[6]);
            
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

    static interface ScoreSetter {
        public abstract void setResultScore(ProlucidResult result, String scoreString);
    }
    
    private static final class SpScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            result.setSp(new BigDecimal(scoreString));
        }
    }
    
    private static final class BinProbScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            result.setBinomialScore(Double.valueOf(scoreString));
        }
    }
    
    private static final class XcorrScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            result.setXcorr(new BigDecimal(scoreString));
        }
    }
    
    private static final class DeltaCNScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            result.setDeltaCN(new BigDecimal(scoreString));
        }
    }
    
    private static final class ZscoreScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            result.setZscore(Double.valueOf(scoreString));
        }
    }
    
    private static final class BlankScoreSetter implements ScoreSetter {
        public void setResultScore(ProlucidResult result, String scoreString) {
            // this does nothing; Allows for columns that should be ignored.
        }
    }

    private static final class PlucidSearchScan implements ProlucidSearchScan {

        private SQTSearchScanIn scan;
        private List<ProlucidSearchResultIn> resultList;

        public PlucidSearchScan(SQTSearchScanIn scan) {
            this.scan = scan;
            resultList = new ArrayList<ProlucidSearchResultIn>();
        }
        public void addSearchResult(ProlucidSearchResultIn result) {
            resultList.add(result);
        }
        public List<ProlucidSearchResultIn> getScanResults() {
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
