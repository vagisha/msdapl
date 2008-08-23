/**
 * SequestParamsParser.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sequestParams;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.sqtFile.Database;


/**
 * 
 */
public class SequestParamsParser implements SearchParamsDataProvider {

    private String remoteServer;

    private List<SequestParam> paramList;

    private boolean reportEvalue = false;
    private Database database;
    private String enzymeCode;
    private MsEnzyme enzyme;
    private List<MsResidueModification> staticResidueModifications;
    private List<MsTerminalModification> staticTerminalModifications;
    private List<MsResidueModification> dynamicResidueModifications;

    private int currentLineNum = 0;
    private String currentLine;

    static final Pattern paramLinePattern = Pattern.compile("^([\\S&&[^=]]+)\\s*=\\s*([^;]*)\\s*;?(.*)");
    static final Pattern staticTermModPattern = Pattern.compile("^add\\_([N|C])\\_terminus$");
    static final Pattern staticResidueModPattern = Pattern.compile("add\\_([A-Z])\\_[\\w]+");
    static final Pattern enzymePattern = Pattern.compile("^(\\d+)\\.\\s+(\\S+)\\s+([0|1])\\s+([[\\-]|[A-Z]]+)\\s+([[\\-]|[A-Z]]+)$");


    private static final char[] modChars = {'*', '#', '@'};

    public SequestParamsParser(String remoteServer) {
        this.remoteServer = remoteServer;
        paramList = new ArrayList<SequestParam>();
        staticResidueModifications = new ArrayList<MsResidueModification>();
        staticTerminalModifications = new ArrayList<MsTerminalModification>();
        dynamicResidueModifications = new ArrayList<MsResidueModification>();
    }

    public Database getSearchDatabase() {
        return database;
    }

    public MsEnzyme getSearchEnzyme() {
        return enzyme;
    }

    public List<MsResidueModification> getDynamicResidueMods() {
        return dynamicResidueModifications;
    }

    public List<MsResidueModification> getStaticResidueMods() {
        return staticResidueModifications;
    }

    public List<MsTerminalModification> getStaticTerminalMods() {
        return staticTerminalModifications;
    }

    public List<MsTerminalModification> getDynamicTerminalMods() {
        return new ArrayList<MsTerminalModification>(0);
    }
    
    public boolean reportEvalue() {
        return reportEvalue;
    }

    public void parseParamsFile(String filePath) throws DataProviderException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((currentLine = reader.readLine())!= null) {
                currentLineNum++;
                currentLine = currentLine.trim();

                // ignore comment lines
                if (currentLine.startsWith("#")) // comment line
                    continue;
                
                // match a param = value pair, if we can
                SequestParam param = matchParamValuePair(currentLine);
                if (param != null) {
                    addParam(param);
                }
                // look for the sequest enzyme info section so that we can get the details for the enzyme
                // used for the search.
                else if (currentLine.startsWith("[SEQUEST_ENZYME_INFO]")){
                    System.out.println("Found enzyme section");
                    parseEnzymes(reader);
                }
            }
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("Cannot find file: "+filePath, e);
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        finally{
            if (reader != null) try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (database == null || enzyme == null)
            throw new DataProviderException("No database of enzyme information found in file: "+filePath);
    }

    void parseEnzymes(BufferedReader reader) throws IOException {
        while ((currentLine = reader.readLine())!= null) {
            currentLineNum++;
            Matcher m = enzymePattern.matcher(currentLine);
            // if we don't get a match it means we are no longer looking at enzymes. 
            if (!m.matches())
                break;
            MsEnzyme enz = matchEnzyme(m, this.enzymeCode);
            if (enz != null) {
                this.enzyme = enz;
                break;
            }
        }
    }
    
    MsEnzyme matchEnzyme(Matcher m, String enzymeCode) {
        String enzCode = m.group(1);
        if (!enzCode.equals(enzymeCode))
            return null;
        final String enzName = m.group(2);
        final String sense = m.group(3);
        final String cut = m.group(4);
        final String noCut = m.group(5);
        return new MsEnzyme() {
            public String getCut() {return cut;}
            public String getDescription() {return null;}
            public String getName() {return enzName;}
            public String getNocut() {return noCut;}
            public Sense getSense() {return Sense.instance(Short.parseShort(sense));
            }};
    }

    private void addParam(SequestParam param) throws DataProviderException {
        paramList.add(param);
        // e-value
        if (param.getParamName().equalsIgnoreCase("print_expect_score")) {
            if (param.getParamValue().equals("1"))
                reportEvalue = true;
        }
        // database
        else if (param.getParamName().equalsIgnoreCase("database_name")) {
            database = new Database();
            database.setServerAddress(remoteServer);
            database.setServerPath(param.getParamValue());
        }
        // enzyme number (actual enzyme information will be parsed later in the file
        else if (param.getParamName().equalsIgnoreCase("enzyme_number")) {
            enzymeCode = param.getParamValue();
        }
        else if (param.getParamName().equalsIgnoreCase("diff_search_options")) {
            parseDynamicResidueMods(param.getParamValue());
        }
        else if (parsedStaticTerminalModParam(param.getParamName(), param.getParamValue())) return;
        else if (parsedStaticResidueModParam(param.getParamName(), param.getParamValue()))  return;
    }

    boolean parsedStaticTerminalModParam(String paramName, String paramValue) throws DataProviderException {
        Matcher m = staticTermModPattern.matcher(paramName);
        if (!m.matches())
            return false;

        Terminal term = Terminal.instance(m.group(1).charAt(0));

        BigDecimal modMass = null;
        try {modMass = new BigDecimal(paramValue);}
        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+paramValue);}

        if (modMass.doubleValue() > 0.0)
            staticTerminalModifications.add(new TerminalModification(term, modMass));
        return true;
    }

    boolean parsedStaticResidueModParam (String paramName, String paramValue) throws DataProviderException {
        Matcher m = staticResidueModPattern.matcher(paramName);
        if (!m.matches())
            return false;

        char modResidue = m.group(1).charAt(0);
        BigDecimal modMass = null;
        try {modMass = new BigDecimal(paramValue);}
        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+paramValue);}

        if (modMass.doubleValue() > 0.0)
            staticResidueModifications.add(new ResidueModification(modResidue, modMass, '\u0000'));
        return true;
    }

    void parseDynamicResidueMods(String modString) throws DataProviderException {
        // e.g. diff_search_options = 0.0000 S 9.0 C 16.0 M 0.0000 X 0.0000 T 0.0000 Y
        // modString is: 0.0000 S 9.0 C 16.0 M 0.0000 X 0.0000 T 0.0000 Y
        // SEQUEST assigns 3 symbols (*, #, @) to the first, second, and third modification, respectively
        final String[] tokens = modString.split("\\s+");

        // expect an even number of tokens.
        if (tokens.length % 2 != 0) {
            throw new DataProviderException(currentLineNum, "Error parsing dynamic residue modification string", currentLine);
        }

        int modCharIdx = 0;
        for (int i = 0; i < tokens.length; i+=2) {

            BigDecimal mass = null;
            try {mass = new BigDecimal((tokens[i]));}
            catch(NumberFormatException e) {throw new DataProviderException(currentLineNum, "Error parsing modification mass: "+tokens[i], currentLine, e);}

            // don't consider modifications with mass-shift of 0;
            if (mass.doubleValue() <= 0.0) continue; 

            // modified residue should be a single character. 
            char modResidue;
            if (tokens[i+1].length() != 1)
                throw new DataProviderException(currentLineNum, "Invalid char for modified residue: "+tokens[i+1], currentLine);
            modResidue = tokens[i+1].charAt(0);

            // if we have already used up all the modifications characters throw an exception.
            // We should have had only 3 dynamic residue modifications
            if (modCharIdx == modChars.length)
                throw new DataProviderException(currentLineNum, "Only three modifications are supported", currentLine);

            dynamicResidueModifications.add(new ResidueModification(modResidue, mass, modChars[modCharIdx++]));
        }
    }

    // parameter_name = parameter_value ; parameter_description
    // e.g. create_output_files = 1                ; 0=no, 1=yes
    // e.g. database_name = /net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta
    SequestParam matchParamValuePair(String line) {
        Matcher m = paramLinePattern.matcher(line);
        if (m.matches()) {
            final String paramName = m.group(1).trim();
            final String paramVal = m.group(2).trim();
            return new SequestParam(){
                public String getParamName() {return paramName;}
                public String getParamValue() {return paramVal;}
            };
        }
        else {
            return null;
        }
    }

    private static final class ResidueModification implements MsResidueModification {

        private char modifiedResidue;
        private char modSymbol;
        private BigDecimal modMass;
        public ResidueModification(char modifiedResidue, BigDecimal modMass, char modSymbol) {
            this.modifiedResidue = modifiedResidue;
            this.modMass = modMass;
            this.modSymbol = modSymbol;
        }
        public char getModifiedResidue() {return modifiedResidue;}
        public BigDecimal getModificationMass() {return modMass;}
        public char getModificationSymbol() {return modSymbol;}
    }

    private static final class TerminalModification implements MsTerminalModification {

        private BigDecimal modMass;
        private Terminal terminal;

        public TerminalModification(Terminal modTerminal, BigDecimal modMass) {
            this.modMass = modMass;
            this.terminal = modTerminal;
        }
        public BigDecimal getModificationMass() {return modMass;}
        public char getModificationSymbol() {return '\u0000';}
        public Terminal getModifiedTerminal() {return terminal;}
    }

    /**
     * @param args
     * @throws DataProviderException 
     */
    public static void main(String[] args) throws DataProviderException {
        String paramFile = "resources/sequest.params";
        SequestParamsParser parser = new SequestParamsParser("remote.server");
        parser.parseParamsFile(paramFile);
    }

}
