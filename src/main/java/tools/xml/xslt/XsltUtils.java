package tools.xml.xslt;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class XsltUtils {

	private static TransformerFactory transformerFactory;
	private static Transformer defaultTransformer;
	private static Map<String, Transformer> transformersMap = Collections
			.synchronizedMap(new HashMap<String, Transformer>());

	private static void checkTransformFactory()
			throws TransformerConfigurationException {
		if (transformerFactory == null) {
			transformerFactory = TransformerFactory.newInstance();
		}
	}

	private static Transformer getDefaultTransformer()
			throws TransformerConfigurationException {
		checkTransformFactory();
		if (defaultTransformer == null) {
			defaultTransformer = transformerFactory.newTransformer();
		}
		return defaultTransformer;
	}

	private static Transformer getTransformer(String xsltResourcePath)
			throws TransformerConfigurationException {
		if (xsltResourcePath == null) {
			return getDefaultTransformer();
		} else {
			checkTransformFactory();
			Transformer transformer = (Transformer) transformersMap
					.get(xsltResourcePath);
			if (transformer == null) {
				transformer = transformerFactory
						.newTransformer(new StreamSource(XsltUtils.class
								.getResourceAsStream(xsltResourcePath)));
				transformersMap.put(xsltResourcePath, transformer);
			}
			return transformer;
		}
	}

	private static Transformer getTransformerFromText(String xsltContent)
			throws TransformerConfigurationException {
		if (xsltContent == null) {
			return getDefaultTransformer();
		} else {
			checkTransformFactory();
			return transformerFactory.newTransformer(new StreamSource(
					new StringReader(xsltContent)));
		}
	}

	private static String transformToStringResult(Document document,
			Transformer transformer) throws TransformerConfigurationException,
			TransformerException {
		StringWriter stringWriter = new StringWriter();
		synchronized (transformer) {
			transformer.transform(new DOMSource(document), new StreamResult(
					stringWriter));
		}
		return stringWriter.toString();
	}

	private static Document transformToDomResult(Document document,
			Transformer transformer) throws TransformerConfigurationException,
			TransformerException {
		DOMResult domResult = new DOMResult();
		synchronized (transformer) {
			transformer.transform(new DOMSource(document), domResult);
		}
		Document transformedDocument = (Document) domResult.getNode();
		return transformedDocument;
	}

	/**
	 * Transforms the input XML document using the XSLT template specified.
	 * 
	 * @param document
	 *            XML document to be transformed
	 * @param xsltResourcePath
	 *            Resource path to the XSLT template
	 * @return XML string representing the transformed XML document
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public static String transformDocumentToString(Document document,
			String xsltResourcePath) throws TransformerConfigurationException,
			TransformerException {
		Transformer transformer = getTransformer(xsltResourcePath);
		return transformToStringResult(document, transformer);
	}

	/**
	 * Transforms the input XML document using the XSLT template specified.
	 * 
	 * @param document
	 *            XML document to be transformed
	 * @param xsltResourcePath
	 *            Resource path to the XSLT template
	 * @return Transformed XML document
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public static Document transformDocument(Document document,
			String xsltResourcePath) throws TransformerConfigurationException,
			TransformerException {
		Transformer transformer = getTransformer(xsltResourcePath);
		return transformToDomResult(document, transformer);
	}

	/**
	 * Transforms the input XML document using the given XSLT content.
	 */
	public static Document transformDocWithXslt(Document document,
			String xsltContent) throws TransformerConfigurationException,
			TransformerException {
		Transformer transformer = getTransformerFromText(xsltContent);
		return transformToDomResult(document, transformer);
	}

	public static String transformDocToTextWithXslt(
			Document document, String xsltContent)
			throws TransformerConfigurationException, TransformerException {
		Transformer transformer = getTransformerFromText(xsltContent);
		return transformToStringResult(document, transformer);
	}

	/**
	 * Renders the supplied XML document to string.
	 * 
	 * @param document
	 *            XML document to be rendered
	 * @return String representing rendered XML document
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public static String renderDocumentToText(Document document)
			throws TransformerConfigurationException, TransformerException {
		return transformDocumentToString(document, null);
	}

}
