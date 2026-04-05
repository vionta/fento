package net.vionta.xml.fento.bind.serialize;

import javax.xml.xpath.XPath;

import net.vionta.xml.fento.repository.impl.util.XPathManager;

public class XPathHelper {

	/**
	 * Returns the default XPath interpreter.
	 * @return
	 */
	public static XPath getXPath() {
		return XPathManager.buildXPath();
	}
	
}
