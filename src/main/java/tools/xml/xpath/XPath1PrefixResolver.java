package tools.xml.xpath;

import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class implements a generic PrefixResolver that can be used to perform
 * prefix-to-namespace lookup for the XPath object.
 */
public class XPath1PrefixResolver implements PrefixResolver {

	/**
	 * The context to resolve the prefix from, if the context is not given.
	 */
	Node nodeContext;

	public XPath1PrefixResolver(Node nodeContext) {
		this.nodeContext = nodeContext;
	}

	public String getBaseIdentifier() {
		return null;
	}

	public String getNamespaceForPrefix(String prefix) {
		return getNamespaceForPrefix(prefix, nodeContext);
	}

	public String getNamespaceForPrefix(String prefix, Node context) {
		// only search current node's attributes for any prefix information
		if (prefix == null || prefix.trim().length() == 0 || context == null) {
			return null;
		}
		if (prefix.equals("xml")) {
			return Constants.S_XMLNAMESPACEURI;
		}
		if (context.getNodeType() == Node.ELEMENT_NODE) {
			NamedNodeMap attrbutes = context.getAttributes();
			for (int i = 0; i < attrbutes.getLength(); i++) {
				Node attr = attrbutes.item(i);
				String attrName = attr.getNodeName();
				if (!attrName.startsWith("xmlns:")) {
					// not a namespace attribute
					continue;
				}
				String p;
				if (attrName.equals("xmlns")) {
					p = "";
				}else {
					int index = attrName.indexOf(':');
					p = attrName.substring(index + 1);
				}
				if (p.equals(prefix)) {
					return attr.getNodeValue();
				}
			}
		}
		return null;
	}

	public boolean handlesNullPrefixes() {
		return false;
	}

}
