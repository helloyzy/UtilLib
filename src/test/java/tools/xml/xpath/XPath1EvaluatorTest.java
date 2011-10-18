package tools.xml.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Test;
import org.w3c.dom.Document;

import tools.xml.XmlUtils;

public class XPath1EvaluatorTest {

	private static Document document = null;

	private static Document document_NamespaceNotAware = null;
	
	private final static String RES_WITHNS = "/msg_namespace.xml";
	
	private final static String RES_WITHOUTNS = "/msg_nonamespace.xml";

	private static void xmlSetup(String resPath)
			throws Exception {
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
	public void testResWithNS_XPathNSAware() throws Exception {
		xmlSetup(RES_WITHNS);
		
		String xpath;
		String messageId = "message id";

		// name space aware - full path
		xpath = "/NS1:GALTestMessage/NS1:messageId";
		assertEquals(messageId, XPath1Evaluator.evaluate(document, xpath));
		
		// name space aware - short path
		xpath = "//NS1:messageId";
		assertEquals(messageId, XPath1Evaluator.evaluate(document, xpath));
	}
	
	@Test
	public void testResWithNS_XPathNSAware_Exceptions() throws Exception {
		xmlSetup(RES_WITHNS);
		
		String xpath;

		// If some part of the XPath contains name space while other parts does not, then the result can not be retrieved 
		xpath = "/NS1:GALTestMessage/receiverInfo/applicationName"; // full path
		try {
			XPath1Evaluator.evaluate(document, xpath);
			fail("This case should throw exception out");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("evaluates an empty string"));
		}
		
		xpath = "//applicationName"; // short path
		try {
			XPath1Evaluator.evaluate(document, xpath);
			fail("This case should throw exception out");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("evaluates an empty string"));
		}
	}
	
	@Test
	public void testResWithNS_XPathNSUnaware() throws Exception {
		xmlSetup(RES_WITHNS);
		
		String xpath;
		String appName = "application";

        // the case which can not retrieve result (testResWithNS_XPathNSAware_Exceptions) is now OK
		// name space aware - full path
		xpath = "/GALTestMessage/receiverInfo/applicationName";
		assertEquals(appName, XPath1Evaluator.evaluate(document_NamespaceNotAware, xpath));
		
		// name space aware - short path
		xpath = "//applicationName";
		assertEquals(appName, XPath1Evaluator.evaluate(document_NamespaceNotAware, xpath));
	}

}
