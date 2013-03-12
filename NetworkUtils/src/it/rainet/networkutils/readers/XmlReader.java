package it.rainet.networkutils.readers;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


public class XmlReader {

	private XPath xPath;
	private Document document;

	public XmlReader(String response) {
		xPath = XPathFactory.newInstance().newXPath();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(response)));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getNodeValue(String xPathExpression) {
		try {
			Node node = (Node) xPath.evaluate(xPathExpression, document, XPathConstants.NODE);
			return node.getTextContent();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return "";
		}
	}
}
