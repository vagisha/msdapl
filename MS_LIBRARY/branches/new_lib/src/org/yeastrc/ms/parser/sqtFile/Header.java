package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class Header {

    private List<HeaderItem> headerItems;
    
    private String sqtGenerator;
    private String sqtGeneratorVersion;
    private String databases;
    private int databaseLength;
    private int databaseLocusCount;
    private String fragmentMassType;
    private BigDecimal fragmentMassTolerance;
    private String precursorMassType;
    private BigDecimal precursorMassTolerance;
    private String startTime;
    private String endTime;
    private List<StaticModification> staticMods;
    private List<DynamicModification> dynaMods;
    private String enzyme;
    
    public Header() {
        headerItems = new ArrayList<HeaderItem>();
        staticMods = new ArrayList<StaticModification>();
        dynaMods = new ArrayList<DynamicModification>();
    }
   
    public boolean isHeaderValid() {
        if (sqtGenerator == null)   return false;
        if (sqtGeneratorVersion == null)    return false;
        if (databases == null)  return false;
        if (fragmentMassType == null)   return false;
        if (precursorMassType == null)  return false;
        if (startTime == null)  return false;
        if (endTime == null)    return false;
        return true;
    }
    
    public void addHeaderItem(String name, String value) {
        headerItems.add(new HeaderItem(name, value));
        
        if (isSqtGenerator(name))
            sqtGenerator = value;
        else if (isSqtGeneratorVersion(name))
            sqtGeneratorVersion = value;
        else if (isDatabase(name))
            databases = value;
        else if (isDatabaseLength(name))
            databaseLength = Integer.parseInt(value);
        else if (isDatabaseLocusCount(name))
            databaseLocusCount = Integer.parseInt(value);
        else if (isFragmentMassType(name))
            fragmentMassType = value;
        else if (isFragmentMassTolerance(name))
            fragmentMassTolerance = new BigDecimal(value);
        else if (isPrecursorMassType(name))
            precursorMassType = value;
        else if (isPrecursorMassTolerance(name))
            precursorMassTolerance = new BigDecimal(value);
        else if (isStartTime(name))
            startTime = value;
        else if (isEndTime(name))
            endTime = value;
        else if (isStaticModification(name))
            addStaticMod(value);
        else if (isDynamicModification(name))
            addDynamicMod(value);
        else if (isEnzyme(name))
            enzyme = value; // TODO: can there be multiple enzyme headers????
    }
    
    private void addStaticMod(String value) {
        String[] tokens = value.split("=");
        if (tokens.length < 2 || tokens.length > 2)
            throw new RuntimeException("Cannot parse static modification: "+value);
        if (tokens[0].length() < 1)
            throw new RuntimeException("No residues for static modification: "+value);
        if (tokens[1].length() < 1)
            throw new RuntimeException("No mass for static modification: "+value);
        
        BigDecimal mass = new BigDecimal(tokens[1]);
        
        // this modification may be for multiple residue characters; 
        // add one StaticModification for each residue character
        for (int i = 0; i < tokens[0].length(); i++) {
            if (Character.isUpperCase(tokens[0].charAt(i))) {
                staticMods.add(new StaticModification(tokens[0].charAt(i), mass));
            }
            else {
                throw new RuntimeException("Modified residue is not uppercase: "+value);
            }
        }
    }
    
    private void addDynamicMod(String value) {
        String[] tokens = value.split("=");
        if (tokens.length < 2 || tokens.length > 2)
            throw new RuntimeException("Cannot parse dynamic modification: "+value);
        if (tokens[0].length() < 2)
            throw new RuntimeException("No residues for dynamic modification: "+value);
        if (tokens[1].length() < 1)
            throw new RuntimeException("No mass for dynamic modification: "+value);
        
        String massStr = tokens[1];
        if (massStr.charAt(0) == '+' || massStr.charAt(0) == '-')
            massStr = massStr.substring(1);
        massStr = massStr.replaceAll("\\s", ""); // remove any spaces
        
        BigDecimal mass = new BigDecimal(massStr);
        
        // this modification may be for multiple residue characters; 
        // add one DynamicModification for each residue character
        // BUT FIRST, the last character is the modification symbol
        char modSymbol = tokens[0].charAt(tokens[0].length() -1);
        for (int i = 0; i < tokens[0].length() -1; i++) {
            if (Character.isUpperCase(tokens[0].charAt(i))) {
                dynaMods.add(new DynamicModification(tokens[0].charAt(i), mass, modSymbol));
            }
            else {
                throw new RuntimeException("Modified residue is not uppercase: "+value);
            }
        }
    }

    private boolean isSqtGenerator(String name) {
        return name.equalsIgnoreCase("SQTGenerator");
    }
    
    private boolean isSqtGeneratorVersion(String name) {
        return name.equalsIgnoreCase("SQTGeneratorVersion");
    }
    
    private boolean isDatabase(String name) {
        return name.equalsIgnoreCase("Database");
    }
    
    private boolean isDatabaseLength(String name) {
        return name.equalsIgnoreCase("DBSeqLength");
    }
    
    private boolean isDatabaseLocusCount(String name) {
        return name.equalsIgnoreCase("DBLocusCount");
    }
    
    private boolean isFragmentMassType(String name) {
        return name.equalsIgnoreCase("FragmentMasses");
    }
    
    private boolean isFragmentMassTolerance(String name) {
        return name.equalsIgnoreCase("Alg-PreMassTol");
    }
    
    private boolean isPrecursorMassType(String name) {
        return name.equalsIgnoreCase("PrecursorMasses");
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
        return name.equalsIgnoreCase("StaticMod");
    }
    
    private boolean isDynamicModification(String name) {
        return name.equalsIgnoreCase("DiffMod");
    }
    
    private boolean isEnzyme(String name) {
        return name.equalsIgnoreCase("EnzymeSpec");
    }
    
    
    

    
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (HeaderItem h: headerItems) {
            buf.append(h.toString());
            buf.append("\n");
        }
        if (buf.length() > 0)
            buf.deleteCharAt(buf.length() -1);
        return buf.toString();
    }
    
    //----------------------------------------------------------------------------------------------
    // BEGIN class HeaderItem
    //----------------------------------------------------------------------------------------------
    public static class HeaderItem {
        
        private String name;
        private String value;
        
        public HeaderItem(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("H\t");
            buf.append(name);
            if (value != null) {
                buf.append("\t");
                buf.append(value);
            }
            return buf.toString();
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    //----------------------------------------------------------------------------------------------
    // END class HeaderItem
    //----------------------------------------------------------------------------------------------
    
    /**
     * @return the headerItems
     */
    public List<HeaderItem> getHeaderItems() {
        return headerItems;
    }

    /**
     * @return the sqtGenerator
     */
    public String getSqtGenerator() {
        return sqtGenerator;
    }

    /**
     * @return the sqtGeneratorVersion
     */
    public String getSqtGeneratorVersion() {
        return sqtGeneratorVersion;
    }

    /**
     * @return the databases
     */
    public String getDatabases() {
        return databases;
    }

    /**
     * @return the databaseLength
     */
    public int getDatabaseLength() {
        return databaseLength;
    }

    /**
     * @return the databaseLocusCount
     */
    public int getDatabaseLocusCount() {
        return databaseLocusCount;
    }

    /**
     * @return the fragmentMassType
     */
    public String getFragmentMassType() {
        return fragmentMassType;
    }

    /**
     * @return the fragmentMassTolerance
     */
    public BigDecimal getFragmentMassTolerance() {
        return fragmentMassTolerance;
    }

    /**
     * @return the precursorMassType
     */
    public String getPrecursorMassType() {
        return precursorMassType;
    }

    /**
     * @return the precursorMassTolerance
     */
    public BigDecimal getPrecursorMassTolerance() {
        return precursorMassTolerance;
    }

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @return the staticMods
     */
    public List<StaticModification> getStaticMods() {
        return staticMods;
    }

    /**
     * @return the dynaMods
     */
    public List<DynamicModification> getDynaMods() {
        return dynaMods;
    }

    /**
     * @return the enzyme
     */
    public String getEnzyme() {
        return enzyme;
    }
}
