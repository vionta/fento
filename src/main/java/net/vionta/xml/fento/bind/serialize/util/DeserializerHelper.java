package net.vionta.xml.fento.bind.serialize.util;

import static net.vionta.xml.fento.bind.serialize.util.XPathHelper.getXPath;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.fento.bind.serialize.Deserializer;
import net.vionta.xml.fento.exception.BindingException;

public class DeserializerHelper {

	private static Logger log = LoggerFactory.getLogger(Deserializer.class);

	public static Serializable getObjectInstance(Class clazz)
			throws BindingException {
		try {
		if(clazz == null ||  clazz.equals(java.lang.Class.class) ) return null;
		log.debug("Getting instance of "+clazz.getName());
		Serializable mainObject =(Serializable) clazz.getDeclaredConstructor().newInstance();
		return mainObject;
		} catch (Exception e) {
			BindingException bindException = new BindingException();
			log.error("Error getting class instance: "+e);
			if(clazz != null) {
				log.error("Getting instance of "+clazz.getName());
				bindException.setTargetClassName(clazz.getName());
			}
			throw bindException;
		}
	}
	

	/**
	 * Get the main node of the base class. Deppends on the mainMapping, that may 
	 * exist or not.
	 * It does not accept nodecollections as a result, since a single class should map to
	 *  a unique node. 
	 *  
	 * @param document
	 * @param mainMappingExpression
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Node getClassNode(Document document,  String mainMappingExpression)
		throws XPathExpressionException {
		if(document==null) return null;
		NodeList mainNodeset;
		Node mainNode = null; 
		if (mainMappingExpression != null && mainMappingExpression != "" ) {
			log.debug("Getting main Nodeset at :"+mainMappingExpression);
			mainNodeset= (NodeList)  getXPath().evaluate(mainMappingExpression, document , XPathConstants.NODESET);
			if(mainNodeset!=null && mainNodeset.getLength()==1) {
			  mainNode = mainNodeset.item(0 );
			} else if (mainNodeset!=null && mainNodeset.getLength()>1 ) {
				throw new IllegalStateException("More than one node expected to map to one class, refine mappingExpression");
			}
		}
		return mainNode;
	}
	
}
