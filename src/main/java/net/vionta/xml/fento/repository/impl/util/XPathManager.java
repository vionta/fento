package net.vionta.xml.fento.repository.impl.util;

import javax.xml.xpath.XPath;

/**
 * XPath management utils.
 */
public class XPathManager {

	/**
	 * @return XPath implementation.
	 */
	public static XPath buildXPath()  {
		XPath xPath = (new net.sf.saxon.xpath.XPathFactoryImpl()).newXPath();
		return xPath;
	}
	
}
	
	
	
