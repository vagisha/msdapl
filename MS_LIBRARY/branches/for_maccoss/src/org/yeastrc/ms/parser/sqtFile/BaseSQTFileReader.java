/**
 * BaseSQTFileReader.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;

/**
 * 
 */
public class BaseSQTFileReader extends SQTFileReader<SQTSearchScanIn<MsSearchResultIn>> {

    private final PeptideResultBuilder peptideResultBuilder;
    
    private boolean doPercolatorMLineCheck = false;
    
    public BaseSQTFileReader(PeptideResultBuilder peptideResultBuilder) {
        this.peptideResultBuilder = peptideResultBuilder;
    }
    
    public void doPercolatorMLineCheck() {
        this.doPercolatorMLineCheck = true;
    }
    
    @Override
    public SQTSearchScanIn<MsSearchResultIn> getNextSearchScan() throws DataProviderException {
        
        BaseSearchScan scan = new BaseSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                MsSearchResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge());
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
    private MsSearchResultIn parsePeptideResult(int scanNumber, int charge) throws DataProviderException {

        BaseSQTResult result = parsePeptideResult(currentLine, scanNumber, charge);

        advanceLine();

        
        boolean isPlaceholder = false;
        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                locus = parseLocus(currentLine);
                if (locus != null) {
                    result.addMatchingLocus(locus);

                    if(doPercolatorMLineCheck) {
                        // NOTE: IGNORE ALL 'M' LINES FOLLOWED BY THE FOLLOWING 'L' LINE
                        // L       Placeholder satisfying DTASelect
                        // This is not a valid result and we will return null
                        if(locus.getAccession().startsWith("Placeholder"))
                            isPlaceholder = true;
                    }
                }
            }
            else
                break;
            advanceLine();
        }
//        if (result.getProteinMatchList().size() == 0)
//            throw new DataProviderException(currentLineNum-1, "Invalid 'M' line.  No locus matches found." , null);
        if(isPlaceholder)   result = null;
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
    BaseSQTResult parsePeptideResult(String line, int scanNumber, int charge) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        BaseSQTResult result = new BaseSQTResult(peptideResultBuilder, getDynamicResidueMods(), getDynamicTerminalMods());
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
    
    private static final class BaseSearchScan implements SQTSearchScanIn<MsSearchResultIn> {

        private SearchScan scan;
        private List<MsSearchResultIn> resultList;

        public BaseSearchScan(SearchScan scan) {
            this.scan = scan;
            resultList = new ArrayList<MsSearchResultIn>();
        }
        public void addSearchResult(MsSearchResultIn result) {
            resultList.add(result);
        }
        public List<MsSearchResultIn> getScanResults() {
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
