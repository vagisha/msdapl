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
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.Database;
import org.yeastrc.ms.parser.Enzyme;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.TerminalModification;

public class ProlucidParamsParser implements SearchParamsDataProvider {

    private String remoteServer;

    private List<ProlucidParam> parentParams; // normally we should have only one parent (the <parameter> element)


    private Database database;
    private MsEnzyme enzyme;
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
        return dynamicTerminalModifications;
    }

    public ProlucidParamsParser(String remoteServer) {
        this.remoteServer = remoteServer;
        parentParams = new ArrayList<ProlucidParam>();
        staticResidueModifications = new ArrayList<MsResidueModification>();
        staticTerminalModifications = new ArrayList<MsTerminalModification>();
        dynamicResidueModifications = new ArrayList<MsResidueModification>();
        dynamicTerminalModifications = new ArrayList<MsTerminalModification>();
    }

    public void parseParamsFile(String filePath) throws DataProviderException {
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
        extractUsefulInfo(thisNode);
        return thisNode;
    }

    private void extractUsefulInfo(ProlucidParamNode node) throws DataProviderException {
        if (node.getParamElementName().equals("database"))
            parseDatabaseInfo(node);
        else if (node.getParamElementName().equals("enzyme_info"))
            parseEnzymeInfo(node);
        else if (node.getParamElementName().equals("modifications"))
            parseModificationInfo(node);
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
        Enzyme e = new Enzyme();
        e.setName(name);
        e.setCut(cut);
        e.setSense(sense);
    }

    private void parseModificationInfo(ProlucidParam node) throws DataProviderException {
        for (ProlucidParam child: node.getChildParamElements()) {
            if (node.getParamElementName().equalsIgnoreCase("n_term"))
                parseNtermMod(child);
            else if (node.getParamElementName().equalsIgnoreCase("c_term"))
                parseCtermMod(child);
            else if (node.getParamElementName().equalsIgnoreCase("static_mods"))
                parseStaticResidueMod(child);
            else if (node.getParamElementName().equalsIgnoreCase("diff_mods"))
                parseDynamicResidueMods(child);
        }
    }

    // parse <n_term> element
    private void parseNtermMod(ProlucidParam node) throws DataProviderException {
        String symbol = null;
        String massShift = null;
        boolean isStatic = false;
        
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol"))
                symbol = child.getParamElementValue();
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift"))
                massShift = child.getParamElementValue();
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod"))
                isStatic = Boolean.valueOf(child.getParamElementValue());
        }
        if (symbol == null || symbol.length() != 1)
            throw new DataProviderException("Invalid modification symbol for n_term modification: "+symbol);
        
        BigDecimal mass = null;
        try {mass = new BigDecimal(massShift);}
        catch(NumberFormatException e) {throw new DataProviderException("Invalid mass_shift for n_term modification: "+massShift, e);
        
        }
        TerminalModification mod = new TerminalModification(Terminal.NTERM, mass, symbol.charAt(0));
        
        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }

    // parse <c_term> element
    private void parseCtermMod(ProlucidParam node) throws DataProviderException {
        String symbol = null;
        String massShift = null;
        boolean isStatic = false;
        
        for (ProlucidParam child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol"))
                symbol = child.getParamElementValue();
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift"))
                massShift = child.getParamElementValue();
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod"))
                isStatic = Boolean.valueOf(child.getParamElementValue());
        }
        if (symbol == null || symbol.length() != 1)
            throw new DataProviderException("Invalid modification symbol for c_term modification: "+symbol);
        
        BigDecimal mass = null;
        try {mass = new BigDecimal(massShift);}
        catch(NumberFormatException e) {throw new DataProviderException("Invalid mass_shift for c_term modification: "+massShift, e);
        
        }
        TerminalModification mod = new TerminalModification(Terminal.CTERM, mass, symbol.charAt(0));
        
        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }
   
    // parse <static_mods> element
    private void parseStaticResidueMod(ProlucidParam child) {

    }

    // parse <diff_mods> element
    private void parseDynamicResidueMods(ProlucidParam child) {

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
        ProlucidParamsParser parser = new ProlucidParamsParser("remote.server");
        parser.parseParamsFile(file);
    }

}
