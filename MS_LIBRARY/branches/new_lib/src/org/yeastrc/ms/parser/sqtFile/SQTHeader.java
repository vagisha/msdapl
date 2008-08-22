package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;


public class SQTHeader implements SQTRunSearch {

    // required headers 
    private static final String DATABASE = "Database";
    private static final String SQTGENERATOR_VERSION = "SQTGeneratorVersion";
    private static final String SQTGENERATOR = "SQTGenerator";
    private static final String DYNAMIC_MOD = "DiffMod";
    private static final String STATIC_MOD = "StaticMod";
    private static final String PRECURSOR_MASS_TYPE = "PrecursorMasses";
    private static final String FRAGMENT_MASS_TYPE = "FragmentMasses";
    
    
    public static final String SEQUEST = "SEQUEST";
    public static final String SEQUEST_NORM = "EE-normalized SEQUEST";
    public static final String PERCOLATOR = "Percolator";
    public static final String PROLUCID = "ProLuCID";
    public static final String PEPPROBE = "PEP_PROBE";
    
    private static final Pattern multiDbPattern = Pattern.compile(".*[,:;\\s]+.*");
    private static final Pattern staticModPattern = Pattern.compile("[A-Z]+");
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm a"); // Example: 01/29/2008, 03:34 AM
    
    private String sqtGenerator;
    private String sqtGeneratorVersion;
    private String fragmentMassType;
    private BigDecimal fragmentMassTolerance;
    private String precursorMassType;
    private BigDecimal precursorMassTolerance;
    
    private String startTimeString;
    private String endTimeString;
    private Date startDate;
    private Date endDate;
    private int searchDuration = -1;
    
    private Database database;
    private String serverAddress;
    
    private List<SQTField> headerItems;
    private List<MsResidueModification> staticMods;
    private List<MsResidueModification> dynaMods;
    private List<MsEnzyme> enzymes;
    
    
    public SQTHeader() {
        headerItems = new ArrayList<SQTField>();
        enzymes = new ArrayList<MsEnzyme>();
    }
   
    public boolean isValid() {
        if (sqtGenerator == null)           return false;
        if (sqtGeneratorVersion == null)    return false;
        if (database == null)               return false;
        if (fragmentMassType == null)       return false;
        if (precursorMassType == null)      return false;
//        if (startTimeString == null)        return false;
//        if (staticMods == null)             return false;
//        if (dynaMods == null)               return false;
        
        return true;
    }
    
    public static final String requiredHeaders() {
       StringBuilder buf = new StringBuilder();
       buf.append(SQTGENERATOR);
       buf.append(", ");
       buf.append(SQTGENERATOR_VERSION);
       buf.append(", ");
       buf.append(DATABASE);
       buf.append(", ");
       buf.append(FRAGMENT_MASS_TYPE);
       buf.append(", ");
       buf.append(PRECURSOR_MASS_TYPE);
//       buf.append(", ");
//       buf.append(STATIC_MOD);
//       buf.append(", ");
//       buf.append(DYNAMIC_MOD);
       return buf.toString();
    }
    
    /**
     * @param name
     * @param value
     * @throws SQTParseException if header name is null or if the header value is invalid
     */
    public void addHeaderItem(String name, String value) throws SQTParseException {
        
        if (name == null)
            throw new SQTParseException("name for Header cannot be null.");
        
        headerItems.add(new HeaderItem(name, value));
        
        // if there is no value for this header ignore it; It will still get added to the 
        // headerItems list. 
        if (value == null || value.trim().length() == 0)
            return;
        
        if (isSqtGenerator(name))
            sqtGenerator = value;
        else if (isSqtGeneratorVersion(name))
            sqtGeneratorVersion = value;
        else if (isDatabase(name))
            setDatabasePath(value);
        else if (isDatabaseLength(name))
            setDatabaseLength(value);
        else if (isDatabaseLocusCount(name))
            setDatabaseLocusCount(value);
        else if (isFragmentMassType(name))
            fragmentMassType = value;
        else if (isFragmentMassTolerance(name))
            setFragmentMassTolerance(value);
        else if (isPrecursorMassType(name))
            precursorMassType = value;
        else if (isPrecursorMassTolerance(name))
            setPrecursorMassTolerance(value);
        else if (isStartTime(name))
            setStartTime(value);
        else if (isEndTime(name))
            setEndTime(value);
        else if (isStaticModification(name))
            addStaticMods(value);
        else if (isDynamicModification(name))
            addDynamicMods(value);
        else if (isEnzyme(name))
            addEnzyme(value);
    }

    //-------------------------------------------------------------------------------------------------------
    // Mass tolerance used for the search
    //-------------------------------------------------------------------------------------------------------
    private void setPrecursorMassTolerance(String value) throws SQTParseException {
        try {
            precursorMassTolerance = new BigDecimal(value);
        }
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing precursor mass tolerance: "+value);
        }
    }

    private void setFragmentMassTolerance(String value) throws SQTParseException {
        try {fragmentMassTolerance = new BigDecimal(value);}
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing fragment mass tolerance: "+value);
        }
    }

    
    //-------------------------------------------------------------------------------------------------------
    // Search database
    //-------------------------------------------------------------------------------------------------------
    /**
     * @throws SQTParseException
     */
    private void setDatabasePath(String filePath) throws SQTParseException {
        if (database == null)
            database = new Database();
        // we should have at least one database
        if (filePath.trim().length() == 0)
            throw new SQTParseException("No database path found in header");
        // check is there are multiple databases (look for commas, semicolons, colons and spaces)
        if (multipleDatabases(filePath))
            throw new SQTParseException("Multiple databases found in header");
        database.setServerPath(filePath);
        database.setServerAddress(this.serverAddress);
    }
    
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    boolean multipleDatabases(String filePath) {
        // remove any spaces at the beginning and end
        filePath = filePath.trim();
        Matcher matcher = multiDbPattern.matcher(filePath);
        return matcher.matches(); 
    }

    private void setDatabaseLength(String lengthStr) throws SQTParseException {
        long length = 0;
        try {
            length = Long.parseLong(lengthStr);
        }
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing database length: "+lengthStr);
        }
        if (database == null)
            database = new Database();
        database.setSequenceLength(length);
    }
    
    private void setDatabaseLocusCount(String countStr) throws SQTParseException {
        int count = 0;
        try {
            count = Integer.parseInt(countStr);
        }
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing database locus count: "+countStr);
        }
        if (database == null)
            database = new Database();
        database.setProteinCount(count);
    }
    
    //-------------------------------------------------------------------------------------------------------
    // Static Modifications
    //-------------------------------------------------------------------------------------------------------
    /**
     * Example of a valid static modification String: C=160.139
     * Multiple static modifications should be present on separate StaticMod lines in a SQT file
     * @param value
     * @return
     * @throws SQTParseException if an error occurs while parsing the static modification
     */
    void addStaticMods(String value) throws SQTParseException {
        
        if (staticMods == null) staticMods = new ArrayList<MsResidueModification>();
        
        // if there were no modifications we will get a empty string
        value = value.trim();
        if (value.length() == 0)
            return;
        
        String[] tokens = value.split("=");
        // The split should create exactly two tokens
        if (tokens.length < 2)
            throw new SQTParseException("Invalid static modification string: "+value);
        if (tokens.length > 2)
            throw new SQTParseException("Invalid static modification string (appears to have > 1 static modification): "+value);
        
        // convert modification chars to upper case 
        String modChars = tokens[0].trim().toUpperCase();
        if (modChars.length() < 1)
            throw new SQTParseException("No residues found for static modification: "+value);
        if (!isValidModCharString(modChars))
            throw new SQTParseException("Invalid residues found found for static modification"+value);
        
        
        String modMass = tokens[1].trim();
        if (modMass.length() < 1)
            throw new SQTParseException("No mass found for static modification: "+value);
        
        BigDecimal mass = null;
        try {
            mass = new BigDecimal(modMass);
        }
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing modification mass: "+value);
        }
        
        // this modification may be for multiple residues; 
        // add one StaticModification for each residue character
        for (int i = 0; i < modChars.length(); i++) {
            staticMods.add(new StaticResidueModification(modChars.charAt(i), mass));
        }
    }
    
    boolean isValidModCharString(String staticModStr) {
        return staticModPattern.matcher(staticModStr).matches();
    }
    
    //-------------------------------------------------------------------------------------------------------
    // Dynamic Modifications
    //-------------------------------------------------------------------------------------------------------
    /**
     * Example of a valid dynamic modification String: STY*=+80.000
     * Multiple dynamic modifications should be present on separate DiffMod lines in a SQT file
     * @param value
     * @return
     * @throws SQTParseException if an error occurs while parsing the dynamic modification
     */
    void addDynamicMods(String value) throws SQTParseException {
        
        if (dynaMods == null)   dynaMods = new ArrayList<MsResidueModification>();
        
        // if there were no modifications we will get a empty string
        value = value.trim();
        if (value.length() == 0)
            return;
        
        
        String[] tokens = value.split("=");
        // The split should create exactly two tokens
        if (tokens.length < 2)
            throw new SQTParseException("Invalid dynamic modification string: "+value);
        if (tokens.length > 2)
            throw new SQTParseException("Invalid dynamic modification string (appears to have > 1 dynamic modification): "+value);
        
        String modChars = tokens[0].trim();
        // get the modification symbol (this character should follow the modification residue characters)
        if (modChars.length() < 2)
            throw new SQTParseException("No modification symbol found: "+value);
        char modSymbol = modChars.charAt(modChars.length() - 1);
        if (!isValidDynamicModificationSymbol(modSymbol))
            throw new SQTParseException("Invalid modification symbol: "+value);
        
        // remove the modification symbol and convert modification chars to upper case 
        modChars = modChars.substring(0, modChars.length()-1).toUpperCase();
        if (modChars.length() < 1)
            throw new SQTParseException("No residues found for dynamic modification: "+value);
        if (!isValidModCharString(modChars))
            throw new SQTParseException("Invalid residues found found for dynamic modification"+value);
        
        
        String modMass = tokens[1].trim();
        if (modMass.length() < 1)
            throw new SQTParseException("No mass found for dynamic modification: "+value);
        
        
        BigDecimal mass = null;
        try { mass = new BigDecimal(modMass);}
        catch(NumberFormatException e) {
            throw new SQTParseException("Error parsing modification mass: "+value);
        }
        
        // this modification may be for multiple residues; 
        // add one StaticModification for each residue character
        for (int i = 0; i < modChars.length(); i++) {
            dynaMods.add(new DynamicResidueModification(modChars.charAt(i), mass, modSymbol));
        }
    }

    boolean isValidDynamicModificationSymbol(char modSymbol) {
        modSymbol = Character.toUpperCase(modSymbol);  
        return (modSymbol < 'A' || modSymbol > 'Z');
    }
    
    //-------------------------------------------------------------------------------------------------------
    // Enzyme(s) used for the search
    //-------------------------------------------------------------------------------------------------------
    private void addEnzyme(String enzyme) {
        if (enzyme.equalsIgnoreCase("No_Enzyme"))
            return;
        enzymes.add(new EnzymeNameHolder(enzyme));
    }
    
    private static final class EnzymeNameHolder implements MsEnzyme {

        private String name;
        public EnzymeNameHolder(String name) { this.name = name;}
        public String getCut() {return null;}
        public String getDescription() {return null;}
        public String getName() {return name;}
        public String getNocut() {return null;}
        public Sense getSense() {return Sense.UNKNOWN;}
        
    }
    
    //-------------------------------------------------------------------------------------------------------
    // Start and end times of the search
    //-------------------------------------------------------------------------------------------------------
    private void setEndTime(String value) throws SQTParseException {
        endTimeString = value;
        if (endTimeString != null) {
            try {
                endDate = new Date(getTime(endTimeString));
            }
            catch (ParseException e) {
                throw new SQTParseException("Error parsing end time: "+value+"\n"+e.getMessage(),
                        SQTParseException.NON_FATAL,
                        e);
            }
        }
    }

    private void setStartTime(String value) throws SQTParseException {
        startTimeString = value;
        if (startTimeString != null) {
            try {
                startDate = new Date(getTime(startTimeString));
            }
            catch (ParseException e) {
                throw new SQTParseException("Error parsing start time: "+value+"\n"+e.getMessage(),
                        SQTParseException.NON_FATAL,
                        e);
            }
        }
    }
    
    //-------------------------------------------------------------------------------------------------------
    // These are the header names we know
    //-------------------------------------------------------------------------------------------------------
    private boolean isSqtGenerator(String name) {
        return name.equalsIgnoreCase(SQTGENERATOR);
    }
    
    private boolean isSqtGeneratorVersion(String name) {
        return name.equalsIgnoreCase(SQTGENERATOR_VERSION);
    }
    
    private boolean isDatabase(String name) {
        return name.equalsIgnoreCase(DATABASE);
    }
    
    private boolean isDatabaseLength(String name) {
        return name.equalsIgnoreCase("DBSeqLength");
    }
    
    private boolean isDatabaseLocusCount(String name) {
        return name.equalsIgnoreCase("DBLocusCount");
    }
    
    private boolean isFragmentMassType(String name) {
        return name.equalsIgnoreCase(FRAGMENT_MASS_TYPE);
    }
    
    private boolean isFragmentMassTolerance(String name) {
        return name.equalsIgnoreCase("Alg-PreMassTol");
    }
    
    private boolean isPrecursorMassType(String name) {
        return name.equalsIgnoreCase(PRECURSOR_MASS_TYPE);
    }
    
    private boolean isPrecursorMassTolerance(String name) {
        return name.equalsIgnoreCase("Alg-FragMassTol");
    }
    
    private boolean isStartTime(String name) {
        return name.equalsIgnoreCase("StartTime");
    }
    
    private boolean isEndTime(String name) {
        return name.equalsIgnoreCase("EndTime");
    }
    
    private boolean isStaticModification(String name) {
        return name.equalsIgnoreCase(STATIC_MOD);
    }
    
    private boolean isDynamicModification(String name) {
        return name.equalsIgnoreCase(DYNAMIC_MOD);
    }
    
    private boolean isEnzyme(String name) {
        return name.equalsIgnoreCase("EnzymeSpec");
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (SQTField h: headerItems) {
            buf.append(h.toString());
            buf.append("\n");
        }
        if (buf.length() > 0)
            buf.deleteCharAt(buf.length() -1);
        return buf.toString();
    }
    

//    /**
//     * @return the sqtGenerator
//     */
//    public String getSearchEngineName() {
//        return sqtGenerator;
//    }
//
//    /**
//     * @return the sqtGeneratorVersion
//     */
//    public String getSearchEngineVersion() {
//        return sqtGeneratorVersion;
//    }
    
//    /**
//     * @return the fragmentMassType
//     */
//    public String getFragmentMassType() {
//        return fragmentMassType;
//    }
//
//    /**
//     * @return the fragmentMassTolerance
//     */
//    public BigDecimal getFragmentMassTolerance() {
//        return fragmentMassTolerance;
//    }
//
//    /**
//     * @return the precursorMassType
//     */
//    public String getPrecursorMassType() {
//        return precursorMassType;
//    }
//
//    /**
//     * @return the precursorMassTolerance
//     */
//    public BigDecimal getPrecursorMassTolerance() {
//        return precursorMassTolerance;
//    }

//    /**
//     * @return the staticMods
//     */
//    public List<MsResidueModification> getStaticResidueMods() {
//        if (staticMods == null)
//            return new ArrayList<MsResidueModification>(0);
//        return staticMods;
//    }

    /**
     * @return the dynaMods
     */
    public List<MsResidueModification> getDynamicModifications() {
        if (dynaMods == null)
            return new ArrayList<MsResidueModification>(0);
        return dynaMods;
    }

    public List<SQTField> getHeaders() {
       return headerItems;
    }

    public SearchFileFormat getSearchFileFormat() {
        if (sqtGenerator.equalsIgnoreCase(SEQUEST))
            return SearchFileFormat.SQT_SEQ;
        else if (sqtGenerator.equalsIgnoreCase(SEQUEST_NORM))
            return SearchFileFormat.SQT_NSEQ;
        else if (sqtGenerator.equalsIgnoreCase(PERCOLATOR))
            return SearchFileFormat.SQT_PERC;
        else if (sqtGenerator.equalsIgnoreCase(PROLUCID))
            return SearchFileFormat.SQT_PLUCID;
        else if (sqtGenerator.equalsIgnoreCase(PEPPROBE))
            return SearchFileFormat.SQT_PPROBE;
        else
            return SearchFileFormat.UNKNOWN;
    }

//    public List<MsSearchDatabase> getSearchDatabases() {
//        List<MsSearchDatabase> dbList = new ArrayList<MsSearchDatabase>(1);
//        dbList.add(database);
//        return dbList;
//    }

    public Date getSearchDate() {
        return this.startDate;
    }
    
    public int getSearchDuration() {
        
        // if we don't have start or end time return 0
        if (endDate == null || startDate == null) {
            searchDuration = 0;
        }
        // calculating for the first time
        else if (searchDuration == -1) {
            long start = startDate.getTime();
            long end = endDate.getTime();
            searchDuration = (int)((end - start)/(1000*60));
        }
        return searchDuration;
    }

    /**
     * Example of a valid time string: 01/29/2008, 03:34 AM
     * @param timeStr
     * @return
     * @throws ParseException 
     */
    long getTime(String timeStr) throws ParseException {
        return dateFormat.parse(timeStr).getTime();
    }
    
//    @Override
//    public List<MsEnzyme> getEnzymeList() {
//        return enzymes;
//    }
}
