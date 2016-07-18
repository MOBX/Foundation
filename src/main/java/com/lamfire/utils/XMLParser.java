package com.lamfire.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

    private Document document_;
    private XPath    xpath_;

    public XMLParser(String uri) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        document_ = db.parse(uri);

        XPathFactory xpathFactory = XPathFactory.newInstance();
        xpath_ = xpathFactory.newXPath();
    }

    public XMLParser(InputStream input) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        document_ = db.parse(input);

        XPathFactory xpathFactory = XPathFactory.newInstance();
        xpath_ = xpathFactory.newXPath();
    }

    public XMLParser(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        document_ = db.parse(xmlFile);

        XPathFactory xpathFactory = XPathFactory.newInstance();
        xpath_ = xpathFactory.newXPath();
    }

    public String getNodeValue(String xql) throws XPathExpressionException {
        String value = xpath_.compile(xql).evaluate(document_);
        return "".equals(value) ? null : value;
    }

    public String[] getNodeValues(String xql) throws XPathExpressionException {
        XPathExpression expr = xpath_.compile(xql);
        NodeList nl = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        int size = nl.getLength();
        String[] values = new String[size];

        for (int i = 0; i < size; ++i) {
            Node node = nl.item(i);
            node = node.getFirstChild();
            values[i] = node.getNodeValue();
        }
        return values;
    }

    public NodeList getNodeList(String xql) throws XPathExpressionException {
        XPathExpression expr = xpath_.compile(xql);
        NodeList nodeList = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        return nodeList;
    }

    public Node getNode(String xql) throws XPathExpressionException {
        XPathExpression expr = xpath_.compile(xql);
        NodeList nodeList = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        return nodeList.getLength() < 0 ? null : nodeList.item(0);
    }

    public Node getNode(String xql, int index) throws XPathExpressionException {
        XPathExpression expr = xpath_.compile(xql);
        NodeList nodeList = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        if (nodeList.getLength() == 0 || nodeList.getLength() <= index) {
            return null;
        }
        return nodeList.item(index);
    }

    public List<Node> getNodes(String xpath, String nodeName) throws XPathExpressionException {
        List<Node> nodes = new ArrayList<Node>();
        XPathExpression expr = xpath_.compile(xpath);
        NodeList nodeList = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        if (nodeList.getLength() == 0) {
            return nodes;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (StringUtils.equalsIgnoreCase(child.getNodeName(), nodeName)) {
                nodes.add(child);
            }
        }
        return nodes;
    }

    public Node getNode(String xpath, String nodeName) throws XPathExpressionException {
        XPathExpression expr = xpath_.compile(xpath);
        NodeList nodeList = (NodeList) expr.evaluate(document_, XPathConstants.NODESET);
        if (nodeList.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (StringUtils.equalsIgnoreCase(node.getNodeName(), nodeName)) {
                return node;
            }
        }
        return null;
    }

    public List<Node> getChildNodes(Node node, String nodeName) {
        List<Node> childs = new ArrayList<Node>();
        NodeList nodeList = node.getChildNodes();
        if (nodeList.getLength() == 0) {
            return childs;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (StringUtils.equalsIgnoreCase(child.getNodeName(), nodeName)) {
                childs.add(child);
            }
        }
        return childs;
    }

    public Node getChildNode(Node node, String nodeName) {
        NodeList nodeList = node.getChildNodes();
        if (nodeList.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (StringUtils.equalsIgnoreCase(child.getNodeName(), nodeName)) {
                return child;
            }
        }
        return null;
    }

    public String getNodeAttribute(String xpath, String attrName) throws XPathExpressionException {
        String value = null;
        NodeList list = getNodeList(xpath);
        if (list.getLength() > 0) {
            Node node = list.item(0);
            return getAttributeValue(node, attrName);
        }
        return value;
    }

    public String getAttributeValue(Node node, String attrName) {
        String value = null;
        node = node.getAttributes().getNamedItem(attrName);
        if (node != null) {
            value = node.getNodeValue();
        }
        return value;
    }

    public static XMLParser load(String resource, Class<?> callingClass) {
        InputStream input = ClassLoaderUtils.getResourceAsStream(resource, callingClass);
        return load(input);
    }

    public static XMLParser load(InputStream input) {
        try {
            XMLParser xml = new XMLParser(input);
            return xml;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException e) {
            }
        }
    }

    public static XMLParser load(File file) {
        try {
            XMLParser xml = new XMLParser(file);
            return xml;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
