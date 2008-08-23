package org.yeastrc.ms.parser.prolucidParams;

import java.io.IOException;
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
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.Database;

public class ProlucidParamsParser {

    private String remoteServer;

    private List<ProlucidParam> parentParams; // normally we should have only one parent (the <parameter> element)
    
    
    private Database database;
    private MsEnzyme enzyme;
    private List<MsResidueModification> staticResidueModifications;
    private List<MsTerminalModification> staticTerminalModifications;
    private List<MsResidueModification> dynamicResidueModifications;

    public List<ProlucidParam> getParentParamElement() {
        return parentParams;
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
    
    public ProlucidParamsParser(String remoteServer) {
        this.remoteServer = remoteServer;
        parentParams = new ArrayList<ProlucidParam>();
        staticResidueModifications = new ArrayList<MsResidueModification>();
        staticTerminalModifications = new ArrayList<MsTerminalModification>();
        dynamicResidueModifications = new ArrayList<MsResidueModification>();
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
    
    private void parseDocument(Document doc) {
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
    
    private ProlucidParamNode parseNode(Node node, ProlucidParamNode parent) {
        
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
        return thisNode;
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
        String file = "resources/search.xml";
        ProlucidParamsParser parser = new ProlucidParamsParser("remote.server");
        parser.parseParamsFile(file);
    }
    
}
