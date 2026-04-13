package net.vionta.xml.fento.bind.serialize.util;

import java.util.ArrayList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;

import net.sf.saxon.pull.NamespaceContextImpl;
import net.vionta.xml.fento.bind.analyze.map.Namespace;

public class XPathHelper {

	/**
	 * Returns the default XPath interpreter, a Saxonica 
	 * www.saxonica.com, based XPath 3.1 implementation.
	 * @return
	 */
	public static XPath getXPath() {
		return XPathManager.buildXPath();
	}
	
}
