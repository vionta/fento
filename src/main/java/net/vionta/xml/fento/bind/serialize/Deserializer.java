package net.vionta.xml.fento.bind.serialize;


import static net.vionta.xml.fento.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.fento.bind.serialize.util.XPathHelper.getXPath;

import java.awt.List;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.fento.bind.analyze.BindMapExtractor;
import net.vionta.xml.fento.bind.analyze.map.Mapping;
import net.vionta.xml.fento.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.fento.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.fento.exception.BindingException;
import net.vionta.xml.fento.exception.MappingException;
import net.vionta.xml.fento.repository.impl.util.DocumentUtils;

/**
 * Main class that takes the document information and 
 * populates the java beans.
 */
public class Deserializer {
	
	private static Logger log = LoggerFactory.getLogger(Deserializer.class);
	
	/**
	 * @param mainObject
	 * @param document
	 * @return The object that 
	 * @throws MappingException
	 * @throws BindingException
	 * @throws ClassNotFoundException 
	 * @throws XPathExpressionException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public <T extends Serializable> T deserialize(T mainObject, Document document) throws MappingException, BindingException, ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException {
		ObjectDocumentMapping mapping = BindMapExtractor.analyze(mainObject);
		return (T) deserialize(mapping, document);
	}
	
	public Object deserialize(ObjectDocumentMapping mapping, Document document) throws  MappingException, BindingException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException {

		log.info("Deserialzing Document  "+document);
		log.info("With Mapping:  "+mapping);
		
		Serializable mainObject = DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		log.info("Main Object:  "+mainObject);
	
		// Getting main nodeset (if provided)h
		String mainMappingExpression = mapping.getMappingExpression();
		log.info(" Mapping Expresion "+mainMappingExpression);
		Node mainNode =	DeserializerHelper.getClassNode(document,  mainMappingExpression);
		log.info(" Main Node  : "+mainNode);
		//We start with the iterative exploraton.
		ArrayList<Mapping> mappings = mapping.getMappings();
		log.info("Main Mappings : "+mappings);
		mainObject =(Serializable) deserializeSubproperties(mainObject, mainNode, mappings);
		return mainObject;
	}

	
	protected <T extends Serializable> T deserializeSubproperties(T parentObject, Node mainNode, ArrayList<Mapping> mappings) throws  MappingException, 
		BindingException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, XPathExpressionException, InstantiationException {
			
		log.info(" Subproperties : "+ mainNode+ " Into "+parentObject);
		if(parentObject!=null)
		log.debug(" Parent Object Class: "+ parentObject.getClass().getName());
		log.debug(" Iterating overr subproperties : ------------------------------------- ");
		
		for(Mapping currentMapping : mappings) {
			
			log.info(" Subproperty evaluated to : "+ currentMapping.getMappingExpression() +" -> "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			log.debug(" Subproperty Class : "+currentMapping.getPropertyClass());
			
			Serializable objectInstance = (Serializable) DeserializerHelper.getObjectInstance(currentMapping.getPropertyClass());
			log.debug(" Object Instances: "+objectInstance.getClass().getName());
			
	
				//Deserialize collection.
			if (CollectionDeserializeHelper.isCollection(parentObject, propertyName))  {
				Serializable deserializedCollection = CollectionDeserializeHelper.deserializeCollection(parentObject, mainNode, currentMapping, propertyName);
				PropertyUtils.setNestedProperty(parentObject, propertyName, deserializedCollection);	
				} 
				//Deserialize Attribute
				else if(isAttributeMapping(mappingExpression) || (parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
					deserializeAttribute(parentObject, mainNode, mappingExpression, propertyName);
				//TODO:Ver el tipo de nodo y el tipo de resultado. 
				//Si colleccion: Node List
				//TODO:Revisar class por instance
			}	else if( parentObject.getClass().getDeclaredField(propertyName).getClass().equals(Vector.class) || 
					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(ArrayList.class) ||
					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(List.class)) {
				log.info(" Getting List: "+ propertyName);
		
//				PropertyUtils.setNestedProperty(appender,appenderNameMapping.getPropertyName(), appenderName);
				NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODESET);
				//TODO: Falta por hacer el binding de listas
				
			} else {
				// Nos queda el nodo single
				log.debug(" Getting Single Node for: "+ propertyName);
				Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
				Serializable singleObject ;
				try {
					singleObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
					log.debug(" Candidate Object: "+singleObject);
					if(singleObject==null ) {
						log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
						singleObject = (Serializable) DeserializerHelper.getObjectInstance( currentMapping.getPropertyClass());
					}
					if(currentMapping.getPropertyClass()!=null && !currentMapping.getPropertyClass().equals(java.lang.String.class)) {
						Serializable deserializeSubproperties = (Serializable) deserializeSubproperties(singleObject, currentNode, currentMapping.getMappings());
						log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
						PropertyUtils.setNestedProperty(parentObject, propertyName, deserializeSubproperties);
					} else if(currentNode!=null && currentNode.getTextContent()!=null) PropertyUtils.setNestedProperty(parentObject, propertyName,  currentNode.getTextContent());
					
				} catch (Exception e) {
					log.error("Could not get  "+ propertyName+" property from "+parentObject );
					MappingException mappingException = new MappingException();
					mappingException.setSourceClassName((parentObject!= null) ? parentObject.getClass().getName(): null);
					mappingException.setTargetPropertyName(propertyName);
					mappingException.setException(e);
					log.error(mappingExpression);
					throw mappingException;
				}
			}
			
		}
		return parentObject;
	}

	private void deserializeAttribute(Serializable parentObject, Node mainNode, String mappingExpression,
			String propertyName)
			throws XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		log.info(" Getting attribute: "+ propertyName);
		String attributeValue= (String) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.STRING);
		
		log.info(" Getting value: "+ attributeValue);
		PropertyUtils.setNestedProperty(parentObject,propertyName,attributeValue);
	}
	



}
