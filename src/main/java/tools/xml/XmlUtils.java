package tools.xml;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import tools.file.FileUtils;
import tools.xml.xslt.XsltUtils;

public class XmlUtils {

	private static DocumentBuilder documentBuilder;
	
	private static DocumentBuilder docBuilder_NS_Aware;
	
	private static DocumentBuilder docBuilder_NS_Unaware;

	/**
	 * Initialize the document builder
	 * 
	 * @param namespaceAware - indicates which document builder to be initialized
	 * @throws Exception
	 */
	private static void initializeDocumentBuilder(
			boolean namespaceAware) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(namespaceAware);
		if (namespaceAware) {
			docBuilder_NS_Aware = documentBuilderFactory.newDocumentBuilder();
		} else {
			docBuilder_NS_Unaware = documentBuilderFactory.newDocumentBuilder();
		}
	}
	
	private synchronized static void pickupDocBuilder(boolean namespaceAware) throws Exception {
		if (namespaceAware) {
			if (docBuilder_NS_Aware == null) {
				initializeDocumentBuilder(true);
			}
			documentBuilder = docBuilder_NS_Aware;
		} else {
			if (docBuilder_NS_Unaware == null) {
				initializeDocumentBuilder(false);
			}
			documentBuilder = docBuilder_NS_Unaware;
		}
	}

	/**
	 * Generates XML document from the supplied string XML content.
	 * 
	 * @param xmlText
	 *            XML text to be parsed
	 * @return Parsed XML document
	 * @throws Exception
	 */
	public static Document getDocument(String xmlText) throws Exception {
		return getDocument(xmlText, true);
	}

	public static Document getDocument(String xmlText, boolean namespaceAware)
			throws Exception {
		pickupDocBuilder(namespaceAware);
		Document document = null;
		synchronized (documentBuilder) {
			document = documentBuilder.parse(new InputSource(new StringReader(
					xmlText)));
		}
		return document;

	}

	/**
	 * Generates XML document from the given file
	 * 
	 * @param fileName
	 * @return Parsed XML document
	 * @throws Exception
	 */
	public static Document getDocumentFromFile(String fileName)
			throws Exception {
		return getDocumentFromFile(fileName, true);
	}

	public static Document getDocumentFromFile(String fileName,
			boolean namespaceAware) throws Exception {
		pickupDocBuilder(namespaceAware);
		Document document = null;
		InputStream inputStream = null;
		try {
			inputStream = FileUtils.getInputStreamFromFilePath(fileName);
			synchronized (documentBuilder) {
				document = documentBuilder.parse(new InputSource(inputStream));
			}
		} finally {
			FileUtils.closeInputStream(inputStream);
		}
		return document;
	}

	public static Document createDocument(boolean namespaceAware)
			throws Exception {
		pickupDocBuilder(namespaceAware);
		Document document = null;
		synchronized (documentBuilder) {
			document = documentBuilder.newDocument();
		}
		return document;
	}
	
	/**
	 * convert the document to string
	 * @param document
	 * @return String format for this document
	 * @throws Exception
	 */
	public static String renderDocumentToText(Document document) throws Exception {
		return XsltUtils.renderDocumentToText(document);
	}
	
	/**
	 * Serialize an object using simple framework
	 * @param obj
	 * @param fileName
	 * @throws Exception
	 */
	public static void serializeToFile(Object obj, String filePath) throws Exception {
		Serializer serializer = new Persister();
		File result = new File(filePath);
		serializer.write(obj, result);
	}
	
	/**
	 * Deserialize to an object using simple framework
	 */
	public static <T> T deserialize(String filePath, Class<T> info) throws Exception {
		 Serializer serializer = new Persister();
         File source = new File(filePath);
         return serializer.read(info, source);
	}
}
