package tools.xml.xpath;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;
import org.w3c.dom.Document;

import tools.xml.XmlUtils;


public class XPath2EvaluatorTest {
	
	private static Document document = null;

	private static Document document_NamespaceNotAware = null;

	private final static String DEFAULT_RES_DIR = "/";

	private final static String RES_DEFAULT = "msg.xml";
	
	private final static String RES_NONAMESPACE = "msg_nonamespace.xml";

	private final static String RES_NAMESPACE = "msg_namespace.xml";
	
	private final static String RES_NORMALTEXT = "msg_normaltext.xml";
	
	private static void xmlSetup() throws Exception {
		xmlSetup(DEFAULT_RES_DIR, RES_DEFAULT);
	}

	private static void xmlSetup(String resDir, String resName)
			throws Exception {
		String resPath = resDir + resName;
		document = XmlUtils.getDocumentFromFile(resPath);
		// documentStr = XsltUtils.renderDocumentToString(document);
		document_NamespaceNotAware = XmlUtils.getDocumentFromFile(resPath,
				false);
	}

	private static void xmlTeardown() {
		document = null;
		document_NamespaceNotAware = null;
	}

	@AfterClass
	public static void tearDown() {
		xmlTeardown();
	}

	@Test
	public void testXPathNamespacesUnaware() throws Exception {
		xmlSetup(DEFAULT_RES_DIR, RES_NONAMESPACE);
		String timeStamp = "20100125150709";
		String xpath = "/KpCanonicalLogMessage/logTimestamp";
		
		// with name space unaware document
		assertEquals(
				timeStamp,
				XPath2Evaluator.evaluate(document_NamespaceNotAware, xpath));

		// with name space aware document
		assertEquals(
				timeStamp,
				XPath2Evaluator.evaluate(document, xpath));
		
	}
	
	@Test
	public void testXPathNamespacesAware() throws Exception {
		xmlSetup();
		String exceptionCode = "0x00d30002";
		String xpath = "/ns0:KpCanonicalLogMessage/ns0:ExceptionInfo/spftns:exception/spftns:exceptionCode";
		
		// name space aware xpath with name space unaware document
		assertEquals(
				exceptionCode,
				XPath2Evaluator.evaluate(document_NamespaceNotAware, xpath));

		// name space aware xpath with name space aware document
		assertEquals(
				exceptionCode,
				XPath2Evaluator.evaluate(document, xpath));
		
	}
	
	@Test
	public void testXPath2Function_Matches() throws Exception {
		xmlSetup(DEFAULT_RES_DIR, RES_NAMESPACE);
		// match IP address
		String xpath = "matches(/NS1:GALTestMessage/NS1:senderInfo/NS1:ipAddress, '(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)')";
		assertEquals("true", XPath2Evaluator.evaluate(document, xpath));
	}
	
	@Test
	public void testXPath2Func_Tokenize() throws Exception {
		xmlSetup(DEFAULT_RES_DIR, RES_NORMALTEXT);
		// "|" needs to be escaped in regular expression -> "\|"
		String xpath = "tokenize(/, '\\|')[1]";
		assertEquals("mapper1", XPath2Evaluator.evaluate(document_NamespaceNotAware, xpath));
	}
	
	@Test
	public void testXPath2Function1() throws Exception {
		xmlSetup(DEFAULT_RES_DIR, RES_NAMESPACE);
		String xpath = "count(//namespace::*)";
		// String xpath = "//namespace-uri-for-prefix('NS1', /NS1:GALTestMessage/NS1:senderInfo/NS1:ipAddress)";
		System.out.println(XPath2Evaluator.evaluate(document, xpath));
//		xpath = "/NS1:GALTestMessage/NS1:ExceptionInfo/NS2:ExceptionName";
//		System.out.println(XPath2Evaluator.evaluate(document, xpath));
	}
}
