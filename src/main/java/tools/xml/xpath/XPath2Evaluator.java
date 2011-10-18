package tools.xml.xpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XPath2Evaluator  {

	/**
	 * using Xpath2.0 tokenize
	 * 
	 * @param doc
	 *            a document against the XPath query is executed
	 * @param xPath
	 *            XPath expression
	 * @return value obtained by executing the supplied XPath query
	 * @exception DataLackException
	 */
	public static String evaluate(Document doc, String xPath)
			throws Exception {
		return evaluate(doc, xPath, null);
	}

	/**
	 * using Xpath2.0 tokenize
	 * 
	 * @param doc
	 *            a document against the XPath query is executed
	 * @param xPath
	 *            XPath expression
	 * @param namespaceUri
	 *            namespace that has to be added
	 * @return value obtained by executing the supplied XPath query
	 * @exception DataLackException
	 */
	public static String evaluate(Document doc, String xPath,
			Map<String, String> namespaceUri) throws Exception {
		String retValue = null;
		try {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPathObj = xPathFactory.newXPath();

			if (namespaceUri == null) {
				// try to process the name spaces of the root element
				Map<String, String> namespaces = new HashMap<String, String>();
				addNamespaces(doc.getDocumentElement(), namespaces);
				if (namespaces.size() > 0) {
					xPathObj.setNamespaceContext(new SimpleNamespaceContext(
							namespaces));
				}
			} else {
				xPathObj.setNamespaceContext(new SimpleNamespaceContext(
						namespaceUri));
			}

			retValue = (String) xPathObj.evaluate(xPath, doc,
					XPathConstants.STRING);
			if (retValue == null) {
				throw new Exception(
						"The XPath expression "
								+ xPath
								+ " did not target an element");
			}
			if (retValue.length() == 0)
				throw new Exception(
						"The XPath expression "
								+ xPath
								+ " evaluates an empty string!");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return retValue;
	}

	/**
     * Add the name space property of the element which starts with "xmlns:" to the namespaces
     * @param element
     * @param namespaces
     * @throws Exception
     */
	static void addNamespaces(Element element,
			Map<String, String> namespaces) {
		NamedNodeMap nodeMap = element.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node attrNode = nodeMap.item(i);
			String nsAttrNodeName = attrNode.getNodeName(); // will get the name such as 
			if (nsAttrNodeName.startsWith("xmlns:")) {
				int nsIndex = nsAttrNodeName.indexOf(':');
				String nsPrefix = nsAttrNodeName.substring(nsIndex + 1);
				String namespace = element.getAttribute(nsAttrNodeName);
				namespaces.put(nsPrefix, namespace);
			}
		}

	}

	static class SimpleNamespaceContext implements NamespaceContext {

		private Map<String, String> namespaceUri;

		public SimpleNamespaceContext(Map<String, String> namespaceUri) {
			this.namespaceUri = namespaceUri;
		}

		public String getNamespaceURI(String prefix) {
			return namespaceUri.get(prefix);
		}

		public String getPrefix(String namespaceURI) {
			return null;
		}

		public Iterator<String> getPrefixes(String namespaceURI) {
			return null;
		}

	}

}
