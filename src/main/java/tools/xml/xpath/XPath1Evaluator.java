package tools.xml.xpath;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;

public class XPath1Evaluator {
	
	/**
	 * Evaluates XPath based on the supplied XML document and XPath query
	 * string.
	 * 
	 * @param document
	 *            XML document to be searched.
	 * @param elementXPath
	 *            XPath query string to be applied on the XML document
	 * @return The XPath evaluated string
	 * @throws XPathProcessorException
	 *             The exception is thrown in the case of generic XML exception
	 *             or when the evaluated value is null or empty string
	 
	public static String evaluate(Document document, String elementXPath)
			throws XPathProcessorException {
		Node documentNode = document.getDocumentElement();
		String namespaceURI = documentNode.getNamespaceURI();
		if (namespaceURI == null) { 
			return evaluate(document, elementXPath, false);
		} else {
			return evaluate(document, elementXPath, true);
		}
	}
	*/
	
	/**
	 * Evaluates XPath based on the supplied XML document and XPath query
	 * string.
	 * 
	 * @param document
	 *            XML document to be searched.
	 * @param elementXPath
	 *            XPath query string to be applied on the XML document
	 * @return The XPath evaluated string
	 * @throws Exception
	 *             The exception is thrown in the case of generic XML exception
	 *             or when the evaluated value is null or empty string
	 */
	public static String evaluate(Document document, String elementXPath)
			throws Exception {
		String result = null;
		try {
			XObject xObj;
			xObj = XPathAPI.eval(document, elementXPath);
			result = xObj.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		if (result == null || result.length() == 0) {
			if (result == null) {
				throw new Exception(
						"The XPath expression "
								+ elementXPath
								+ " did not target an element");
			}
			if (result.length() == 0)
				throw new Exception(
						"The XPath expression "
								+ elementXPath
								+ " evaluates an empty string!");
		}
		return result;
	}
	
}
