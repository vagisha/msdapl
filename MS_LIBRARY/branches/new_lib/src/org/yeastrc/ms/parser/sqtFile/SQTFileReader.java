package org.yeastrc.ms.parser.sqtFile;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;


public abstract class SQTFileReader extends AbstractReader 
    implements SQTSearchDataProvider  {

    private List<MsResidueModificationIn> searchDynamicResidueMods;
    private List<MsTerminalModificationIn> searchDynamicTerminalMods;
    

    private static final Logger log = Logger.getLogger(SQTFileReader.class);

    private static final Pattern headerPattern = Pattern.compile("^H\\s+([\\S]+)\\s*(.*)");
    private static final Pattern locusPattern = Pattern.compile("^L\\s+([\\S]+)\\s*(.*)");
//    private static final Pattern sqtGenPattern = Pattern.compile("^H\\s+SQTGenerator\\s+(.*)");
    
    public void init() {
        searchDynamicResidueMods.clear();
        searchDynamicTerminalMods.clear();
    }
    
    public static SearchFileFormat getSearchFileType(String filePath) {
        
        SQTFileReader reader = new SQTFileReader(){
            @Override
            public SQTSearchScan getNextSearchScan()
            throws DataProviderException {
                throw new UnsupportedOperationException("");
            }};
            
            try {
                reader.open(filePath);
                SQTHeader header = reader.getSearchHeader();
                return header.getSearchFileFormat();
            }
            catch (DataProviderException e) {
                e.printStackTrace();
            }
            finally {
                reader.close();
            }
            return SearchFileFormat.UNKNOWN;
    }
    
    public static SearchFileFormat getSearchFileType(String fileName, Reader input) {
        
        SQTFileReader reader = new SQTFileReader(){
            @Override
            public SQTSearchScan getNextSearchScan()
                    throws DataProviderException {
                throw new UnsupportedOperationException("");
            }};
            
            try {
                reader.open(fileName, input);
                SQTHeader header = reader.getSearchHeader();
                return header.getSearchFileFormat();
            }
            catch (DataProviderException e) {
                e.printStackTrace();
            }
            finally {
                reader.close();
            }
            return SearchFileFormat.UNKNOWN;
    }
    
    public SQTFileReader() {
        searchDynamicResidueMods = new ArrayList<MsResidueModificationIn>();
        searchDynamicTerminalMods = new ArrayList<MsTerminalModificationIn>();
    }
    
    public void setDynamicResidueMods(List<MsResidueModificationIn> dynaResidueMods) {
        if (dynaResidueMods != null)
            this.searchDynamicResidueMods = dynaResidueMods;
    }
    
    protected List<MsResidueModificationIn> getDynamicResidueMods() {
        return this.searchDynamicResidueMods;
    }
    
    public void setDynamicTerminalMods(List<MsTerminalModificationIn> dynaTerminalMods) {
        if (dynaTerminalMods != null)
            this.searchDynamicTerminalMods = dynaTerminalMods;
    }
    
    protected List<MsTerminalModificationIn> getDynamicTerminalMods() {
        return this.searchDynamicTerminalMods;
    }
    
    public SQTHeader getSearchHeader()  throws DataProviderException {

        SQTHeader header = new SQTHeader();
        
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            addHeaderItem(header, nameAndVal);
            advanceLine();
        }
        
        if (!header.isValid())
            throw new DataProviderException("Invalid SQT Header. One or more required headers is missing. "+
                    "Required headers:\n\t"+SQTHeader.requiredHeaders());
        
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

    protected SearchScan parseScan(String line) throws DataProviderException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            throw new DataProviderException(currentLineNum, "Error parsing scan. Expected line starting with 'S'.", line);
        }

        String[] tokens = line.split("\\s+");
        if (tokens.length != 10) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Expected 10 fields", line);
        }

        SearchScan scan = new SearchScan();
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
     * Parses a 'L' line in the sqt file
     * @param line
     * @return
     * @throws DataProviderException
     */
    protected DbLocus parseLocus(String line) throws DataProviderException {
        
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

    protected static boolean isResultLine(String line) {
        if (line == null)   return false;
        return line.startsWith("M");
    }

    protected static boolean isLocusLine(String line) {
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
