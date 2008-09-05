package org.yeastrc.ms.parser.prolucidParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yeastrc.ms.domain.general.MsEnzymeI;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.MsEnzymeInImpl;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.Database;
import org.yeastrc.ms.parser.ResidueModification;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.TerminalModification;

public class ProlucidParamsParser implements SearchParamsDataProvider {

    public static enum Score {SP, BIN_PROB, XCORR, DELTA_CN, ZSCORE, BLANK};

    private String remoteServer;

    private List<ProlucidParam> parentParams; // normally we should have only one parent (the <parameter> element)

    private Score xcorrColumnScore;
    private Score spColumnScore;
    private Score deltaCNColumnScore;

    private Database database;
    private MsEnzymeI enzyme;
    private List<MsResidueModification> staticResidueModifications;
    private List<MsTerminalModification> staticTerminalModifications;
    private List<MsResidueModification> dynamicResidueModifications;
    private List<MsTerminalModification> dynamicTerminalModifications;

    public List<ProlucidParam> getParentParamElement() {
        return parentParams;
    }

    public MsSearchDatabase getSearchDatabase() {
        return database;
    }

    public MsEnzymeI getSearchEnzyme() {
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
        return dynamicTerminalModifications;
    }
    
    public SearchProgram getSearchProgram() {
        return SearchProgram.PROLUCID;
    }
    
    public Score getXcorrColumnScore() {
        return xcorrColumnScore;
    }
    
    public Score getSpColumnScore() {
        return spColumnScore;
    }

    public Score getDeltaCNColumnScore() {
        return deltaCNColumnScore;
    }
    
    public List<ProlucidParam> getParamList() {
        return this.parentParams;
    }
    
    public boolean isEnzymeUsedForSearch() {
        if (enzyme == null || enzyme.getName().equalsIgnoreCase("No_Enzyme"))
            return false;
        return true;
    }
    
    private void init(String remoteServer) {
        this.remoteServer = remoteServer;
        parentParams.clear();
        staticResidueModifications.clear();
        staticTerminalModifications.clear();
        dynamicResidueModifications.clear();
        dynamicTerminalModifications.clear();
        this.database = null;
        this.enzyme = null;
        this.xcorrColumnScore = Score.XCORR;
        this.spColumnScore = Score.SP;
        this.deltaCNColumnScore = Score.DELTA_CN;
    }

    public ProlucidParamsParser() {
        parentParams = new ArrayList<ProlucidParam>();
        staticResidueModifications = new ArrayList<MsResidueModification>();
        staticTerminalModifications = new ArrayList<MsTerminalModification>();
        dynamicResidueModifications = new ArrayList<MsResidueModification>();
        dynamicTerminalModifications = new ArrayList<MsTerminalModification>();
    }
    
    public void parseParamsFile(String remoteServer, String filePath) throws DataProviderException {

        init(remoteServer);

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setIgnoringComments(true);
        DocumentBuilder b = null;
        Document doc = null;
        try {
            b = f.newDocumentBuilder();
            doc = b.parse(filePath);
            parseDocument(doc);
        }
        catch (ParserConfigurationException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        catch (SAXException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        
        // make sure the three score types are different
        if (spColumnScore == xcorrColumnScore)
            throw new DataProviderException("Score types for Sp and XCorr columns cannot be the same: "+spColumnScore);
        if (spColumnScore == deltaCNColumnScore)
            throw new DataProviderException("Score types for Sp and DeltaCN columns cannot be the same: "+spColumnScore);
        if (xcorrColumnScore == deltaCNColumnScore)
            throw new DataProviderException("Score types for the XCorr and DeltaCN columns cannot be the same: "+xcorrColumnScore);
    }

    private void parseDocument(Document doc) throws DataProviderException {
        NodeList nodes = doc.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) == null)  continue;
            ProlucidParamNode pn = parseNode(nodes.item(i), null);
            if (pn != null)
                this.parentParams.add(pn);
        }
        printParams();
    }

    private void printParams() {
        for (ProlucidParam n: this.parentParams) {
            printParam(n, 0);
        }
    }

    private void printParam(ProlucidParam param, int indent) {
        String tab = "";
        for (int i = 0; i < indent; i++) {
            tab += "\t";
        }
        System.out.print(tab+"<"+param.getParamElementName()+">");
        if (param.getParamElementValue() != null)
            System.out.print(param.getParamElementValue());
        System.out.println("");

        List<ProlucidParam> childNodes = param.getChildParamElements();
        for (ProlucidParam child: childNodes) {
            printParam(child, indent+1);
        }

        System.out.println(tab+"</"+param.getParamElementName()+">");
    }

    private ProlucidParamNode parseNode(Node node, ProlucidParamNode parent) throws DataProviderException {

        if (node.getNodeType() != Node.ELEMENT_NODE)
            return null;

        ProlucidParamNode thisNode = new ProlucidParamNode();
        thisNode.elName = node.getNodeName();

        NodeList nodes = node.getChildNodes();
        String nodeVal = "";
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                nodeVal += n.getNodeValue().trim();
            }
        }
        if (nodeVal.length() > 0)
            thisNode.elValue = nodeVal;

        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n == null)  continue;
            ProlucidParamNode childNode = parseNode(n, thisNode);
            if (childNode != null)
                thisNode.addChildParamElement(childNode);
        }
        extractRequiredInfo(thisNode);
        return thisNode;
    }

    private void extractRequiredInfo(ProlucidParamNode node) throws DataProviderException {
        if (node.getParamElementName().equalsIgnoreCase("database"))
            parseDatabaseInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("enzyme_info"))
            parseEnzymeInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("modifications"))
            parseModificationInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("primary_score_type"))
            parsePrimaryScoreTypeFormat2(node);
        else if (node.getParamElementName().equalsIgnoreCase("secondary_score_type"))
            parseSecondaryScoreTypeFormat2(node);
        else if (node.getParamElementName().equalsIgnoreCase("additional_estimate"))
            parsePrimaryScoreTypeFormat1(node);
        else if (node.getParamElementName().equalsIgnoreCase("confidence"))
            parseSecondaryScoreTypeFormat1(node);
    }

    /**
     * <!--ADDITIONAL_ESTIMATE 
     *      0 - default Binomial Probability 
     *      1 - XCORRR
     *  -->
     * @param node
     * @throws DataProviderException 
     */
    private void parsePrimaryScoreTypeFormat1(ProlucidParamNode node) throws DataProviderException {
        String val = node.getParamElementValue();
        if (val == null)
            throw new DataProviderException("Value of additional_estimate cannot be null");
        int ival = 0;
        try {ival = Integer.parseInt(val);}
        catch(NumberFormatException e) {
            throw new DataProviderException("Invalid value for additional_estimate: "+val+". Allowed values: 0,1"); 
        }
        switch(ival) {
            case 0:
                xcorrColumnScore = Score.BIN_PROB;
                break;
            case 1:
                xcorrColumnScore = Score.XCORR;
                break;
            default:
                throw new DataProviderException("Invalid value for additional_estimate: "+val+". Allowed values: 0,1"); 
        }
    }

    
    /**
     * From the annotated search.xml (http://bart.scripps.edu/wiki/index.php/ProLuCID)
     * <!--Primary score type
     *      0 - default Binomial Probability This is not pep-prob probability score (by Rovshan), but similar. In Sequest, Sp score is the preliminary score. In Prolucid, the binomial probability score is the preliminary score.
     *      1 - XCorr
     *  -->
     *  FROM DANIEL COCIORVA
     *  - XCorr column will contain the "primary_score_type" (which is usually XCorr, but not always)
     *  - DeltCN column will contain DeltCN, which is derived from the primary score. Its value is always between 0 and 1, regardless on which primary score type it is based. So you needn't mess with this one.
     *  - Sp column will contain the "secondary_score_type" (which is usually ZScore, but not always).
     * @param node
     * @throws DataProviderException
     */
    private void parsePrimaryScoreTypeFormat2(ProlucidParamNode node) throws DataProviderException {
        String val = node.getParamElementValue();
        if (val == null)
            throw new DataProviderException("Value of primary_score_type cannot be null");
        int ival = 0;
        try {ival = Integer.parseInt(val);}
        catch(NumberFormatException e) {
            throw new DataProviderException("Invalid value for primary_score_type: "+val+". Allowed values: 0,1"); 
        }
        switch(ival) {
            case 0:
                xcorrColumnScore = Score.BIN_PROB;
                break;
            case 1:
                xcorrColumnScore = Score.XCORR;
                break;
            default:
                throw new DataProviderException("Invalid value for primary_score_type: "+val+". Allowed values: 0,1"); 
        }
    }
    
    
    /**
     * <!--CONFIDENCE
     *      0 - deltaCn 
     *      1 - T Score 
     * -->
     * NOTE: Assuming "T Score" is the same as "Z score"
     * @param node
     * @throws DataProviderException
     */
    private void parseSecondaryScoreTypeFormat1(ProlucidParamNode node) throws DataProviderException {
        String val = node.getParamElementValue();
        if (val == null)
            throw new DataProviderException("Value of confidence cannot be null");
        int ival = 0;
        try {ival = Integer.parseInt(val);}
        catch(NumberFormatException e) {
            throw new DataProviderException("Invalid value for confidence: "+val+". Allowed values: 0,1"); 
        }
        switch(ival) {
            case 0:
                deltaCNColumnScore = Score.DELTA_CN;
                break;
            case 1:
                deltaCNColumnScore = Score.ZSCORE;
                break;
            default:
                throw new DataProviderException("Invalid value for confidence: "+val+". Allowed values: 0,1"); 
        }
    }
    
    
    /**
     * From the annotated search.xml (http://bart.scripps.edu/wiki/index.php/ProLuCID)
     * <!--Secondary score type
     *      0 - Binomial Probability
     *      1 - XCorr
     *      2  Zscore Zscore is Prolucid's answer to Sequest's delCN. delCN =(XCorr of the top hit - XCorr of the second hit)/XCorr of the top hit.
     *      Zscore = (XCorr of the top hit - average XCorr of the 2nd , 3rd, and 500th hits)/standard deviation of the XCorr's of the 2nd , 3rd, and 500th hits. A Zscore of 5 means that the top hit is 5 STD away from the average. A good Zscore cutoff is 4.5, and 5 is even better.
     *  -->
     *  
     *  FROM DANIEL COCIORVA
     *  - XCorr column will contain the "primary_score_type" (which is usually XCorr, but not always)
     *  - DeltCN column will contain DeltCN, which is derived from the primary score. Its value is always between 0 and 1, regardless on which primary score type it is based. So you needn't mess with this one.
     *  - Sp column will contain the "secondary_score_type" (which is usually ZScore, but not always).
     * @param node
     * @throws DataProviderException
     */
    private void parseSecondaryScoreTypeFormat2(ProlucidParamNode node) throws DataProviderException {
        String val = node.getParamElementValue();
        if (val == null)
            throw new DataProviderException("Value of secondary_score_type cannot be null");
        int ival = 0;
        try {ival = Integer.parseInt(val);}
        catch(NumberFormatException e) {
            throw new DataProviderException("Invalid value for secondary_score_type: "+val+". Allowed values: 0,1,2"); 
        }
        switch(ival) {
            case 0:
                spColumnScore = Score.BIN_PROB;
                break;
            case 1:
                spColumnScore = Score.XCORR;
                break;
            case 2:
                spColumnScore = Score.ZSCORE;
                break;
            default:
                throw new DataProviderException("Invalid value for primary_score_type: "+val+". Allowed values: 0,1,2"); 
        }
    }


    private void parseDatabaseInfo(ProlucidParam node) throws DataProviderException {
        String dbPath = null;
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("database_name")) {
                // there should only be one database.
                if (dbPath != null)
                    throw new DataProviderException("Cannot handle more than one search databases");
                dbPath = child.getParamElementValue();
            }
        }
        if (dbPath == null) {
            throw new DataProviderException("No search database found");
        }
        this.database = new Database();
        database.setServerAddress(remoteServer);
        database.setServerPath(dbPath);
    }

    private void parseEnzymeInfo(ProlucidParam node) throws DataProviderException {
        String name = null;
        Sense sense = null;
        String cut = "";
        for (ProlucidParam child: node.getChildParamElements()) {
            // enzyme name
            if (child.getParamElementName().equalsIgnoreCase("name"))
                name = child.getParamElementValue();
            // residues at which this enzyme cuts
            else if (child.getParamElementName().equalsIgnoreCase("residues")) {
                for (ProlucidParam cr: child.getChildParamElements()) {
                    if (cr.getParamElementName().equals("residue"))
                        cut+= cr.getParamElementValue();
                }
            }
            // where does the enzyme cut: C-Term or N-Term
            else if (child.getParamElementName().equalsIgnoreCase("type")) {
                if (child.getParamElementValue().equalsIgnoreCase("true"))
                    sense = Sense.CTERM;
                else
                    sense = Sense.NTERM;
            }
        }
        if (name == null || sense == null || cut.length() == 0) {
            throw new DataProviderException("Invalid enzyme information. One or more required values (name, type, residue) are missing");
        }
        MsEnzymeInImpl e = new MsEnzymeInImpl();
        e.setName(name);
        e.setCut(cut);
        e.setSense(sense);
        this.enzyme = e;
    }

    // parse <modifications> element
    private void parseModificationInfo(ProlucidParam node) throws DataProviderException {
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("n_term"))
                parseNtermMod(child);
            else if (child.getParamElementName().equalsIgnoreCase("c_term"))
                parseCtermMod(child);
            else if (child.getParamElementName().equalsIgnoreCase("static_mods"))
                parseStaticResidueMods(child);
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods"))
                parseDynamicResidueMods(child);
        }
    }

    // parse <n_term> element
    private void parseNtermMod(ProlucidParam node) throws DataProviderException {

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("static_mod")) {
                parseNtermModFormat2(node);
                return;
            }
        }
        parseNtermModFormat1(node);
    }

    /**
     * FORMAT 2 Example: 
     * <n_term>
     *      <static_mod>
     *          <symbol>*</symbol>
     *          <mass_shift>0</mass_shift>
     *      </static_mod>
     *      <diff_mods>
     *          <diff_mod>
     *              <symbol>*</symbol>
     *              <mass_shift>0</mass_shift>
     *          </diff_mod>
     *      </diff_mods>
     * </n_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseNtermModFormat2(ProlucidParam node) throws DataProviderException {

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod")) {
                parseStaticTermModFormat2(Terminal.NTERM, child);
            }
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods")) {
                for (ProlucidParam c: child.getChildParamElements()) {
                    if (c.getParamElementName().equalsIgnoreCase("diff_mod")) {
                        parseDynamicTermModFormat2(Terminal.NTERM, c);
                    }
                }
            }
        }
    }

    private void parseStaticTermModFormat2(Terminal term, ProlucidParam node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for terminal modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for terminal modification: "+child.getParamElementValue(), e);
                }
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for terminal modification");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification(term, mass, symbol);
        this.staticTerminalModifications.add(mod);
    }

    private void parseDynamicTermModFormat2(Terminal term, ProlucidParam node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for terminal modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for terminal modification: "+child.getParamElementValue(), e);
                }
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for terminal modification");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification(term, mass, symbol);
        this.dynamicTerminalModifications.add(mod);
    }

    /**
     * Example: 
     * <n_term>
     *      <symbol>*</symbol>
     *      <mass_shift>156.1011</mass_shift>
     *      <is_static_mod>false</is_static_mod>
     * </n_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseNtermModFormat1(ProlucidParam node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        Boolean isStatic = null;

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for n_term modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for n_term modification: "+child.getParamElementValue(), e);
                }
            }
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod"))
                isStatic = Boolean.valueOf(child.getParamElementValue());
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for n_term modification");
        if (isStatic == null)
            throw new DataProviderException("Missing information if n-term modification is static or terminal");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification(Terminal.NTERM, mass, symbol);

        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }

    // parse <c_term> element
    private void parseCtermMod(ProlucidParam node) throws DataProviderException {

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("static_mod")) {
                parseCtermModFormat2(node);
                return;
            }
        }
        parseCtermModFormat1(node);
    }

    /**
     * Example: 
     * <c_term>
     *      <static_mod>
     *          <symbol>*</symbol>
     *          <mass_shift>0</mass_shift>
     *      </static_mod>
     *      <diff_mods>
     *          <diff_mod>
     *              <symbol>*</symbol>
     *              <mass_shift>0</mass_shift>
     *          </diff_mod>
     *      </diff_mods>
     * </c_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseCtermModFormat2(ProlucidParam node) throws DataProviderException {

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod")) {
                parseStaticTermModFormat2(Terminal.CTERM, child);
            }
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods")) {
                for (ProlucidParam c: child.getChildParamElements()) {
                    if (c.getParamElementName().equalsIgnoreCase("diff_mod")) {
                        parseDynamicTermModFormat2(Terminal.CTERM, c);
                    }
                }
            }
        }
    }

    /**
     * Example: 
     * <c_term>
     *      <symbol>*</symbol>
     *      <mass_shift>156.1011</mass_shift>
     *      <is_static_mod>false</is_static_mod>
     * </c_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseCtermModFormat1(ProlucidParam node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        Boolean isStatic = null;

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for c_term modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")){
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for c_term modification: "+child.getParamElementValue(), e);
                }
            }
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod")) {
                String s = child.getParamElementValue();
                if (!s.equalsIgnoreCase("true") && !s.equalsIgnoreCase("false"))
                    throw new DataProviderException("Invalid value for is_static_mod element");
                isStatic = Boolean.valueOf(child.getParamElementValue());
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for c_term modification");
        if (isStatic == null)
            throw new DataProviderException("Missing information if c-term modification is static or terminal");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification(Terminal.CTERM, mass, symbol);

        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }

    // parse <static_mods> element
    private void parseStaticResidueMods(ProlucidParam node) throws DataProviderException {
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod"))
                parseStaticResidueMod(child);
        }
    }

    // parse <static_mod> element
    private void parseStaticResidueMod(ProlucidParam node) throws DataProviderException {
        BigDecimal mass = null;
        char residue = 0;
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("residue")) {
                if (residue != 0)
                    throw new DataProviderException("Error parsing static residue modification. More than one residue found for static modification");
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid residue for static modification: "+s); 
                residue = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for static residue modification: "+child.getParamElementValue(), e);
                }
                // if mass shift is 0, ignore this modification
                if (mass.doubleValue() == 0)
                    return;
            }
        }

        ResidueModification mod = new ResidueModification(residue, mass);
        this.staticResidueModifications.add(mod);
    }

    // parse <diff_mods> element
    private void parseDynamicResidueMods(ProlucidParam node) throws DataProviderException {
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("diff_mod"))
                parseDynamicMod(child);
        }
    }

    // parse <diff_mod> element
    private void parseDynamicMod(ProlucidParam node) throws DataProviderException {
        char modSymbol = 0;
        BigDecimal mass = null;
        List<Character> modResidues = new ArrayList<Character>();

        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for static residue modification: "+child.getParamElementValue(), e);
                }
                // if mass shift is 0, ignore this modification
                if (mass.doubleValue() == 0)
                    return;
            }
            else if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                // TODO  <symbol> can have a string value. e.g. "phosporylation"
//              if (s == null || s.length() != 1)
                if (s == null || s.length() < 1)
                    throw new DataProviderException("Invalid modification symbol for dynamic residue modification: "+s); 
                modSymbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("residues")) {
                for (ProlucidParam cr: child.getChildParamElements()) {
                    if (cr.getParamElementName().equals("residue")) {
                        String s = cr.getParamElementValue();
                        if (s == null || s.length() != 1)
                            throw new DataProviderException("Invalid residue for dynamic residue modification: "+s); 
                        modResidues.add(s.charAt(0));
                    }
                }
            }
        }
        for (Character res: modResidues) {
            ResidueModification mod = new ResidueModification(res, mass, modSymbol);
            dynamicResidueModifications.add(mod);
        }
    }

    private static final class ProlucidParamNode implements ProlucidParam {

        private String elName;
        private String elValue;
        private List<ProlucidParam> childElList = new ArrayList<ProlucidParam>();

        @Override
        public String getParamElementName() {
            return elName;
        }
        @Override
        public String getParamElementValue() {
            return elValue;
        }
        @Override
        public List<ProlucidParam> getChildParamElements() {
            return childElList;
        }

        public void addChildParamElement(ProlucidParam param) {
            childElList.add(param);
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Name: "+elName);
            buf.append("\n");
            buf.append("Value: "+elValue);
            return buf.toString();
        }
    }


    public static void main(String[] args) throws DataProviderException {
        String file = "resources/prolucid_search_format1.xml";
        ProlucidParamsParser parser = new ProlucidParamsParser();
        parser.parseParamsFile("remote.server", file);
    }
}
