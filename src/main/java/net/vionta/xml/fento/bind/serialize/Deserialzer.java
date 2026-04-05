package net.vionta.xml.fento.bind.serialize;


import static net.vionta.xml.fento.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.fento.bind.serialize.XPathHelper.getXPath;

import java.awt.List;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

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
import net.vionta.xml.fento.exception.BindingException;
import net.vionta.xml.fento.exception.MappingException;

public class Deserialzer {
	
	private static Logger log = LoggerFactory.getLogger(Deserialzer.class);
	
	public Serializable deserialize(Serializable mainObject, Document document) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, XPathExpressionException, NoSuchFieldException, ClassNotFoundException, MappingException, BindingException {
		
		ObjectDocumentMapping mapping = BindMapExtractor.analyze(mainObject);
		
		 log.info("Deserialzing Document  "+document);
		log.info("With Mapping:  "+mapping);
			
		mainObject = getObjectInstance(mapping.getPropertyClass());
		log.info("Main Object:  "+mainObject);
		
			// Getting main nodeset (if provided)h
		String mainMappingExpression = mapping.getMappingExpression();
		log.info(" Mapping Expresion "+mainMappingExpression);
		Node mainNode =	getClassNode(document,  mainMappingExpression);
		log.info(" Main Node  : "+mainNode);
		//We start with the iterative exploraton.
		ArrayList<Mapping> mappings = mapping.getMappings();
		log.info("Main Mappings : "+mappings);
		mainObject =(Serializable) deserializeSubproperties(mainObject, mainNode, mappings);
		return mainObject;
					
	}
	
	public Object deserialize(ObjectDocumentMapping mapping, Document document) throws InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, XPathExpressionException, NoSuchFieldException, MappingException, BindingException {

		log.info("Deserialzing Document  "+document);
		log.info("With Mapping:  "+mapping);
		
		Serializable mainObject = getObjectInstance(mapping.getPropertyClass());
		log.info("Main Object:  "+mainObject);
	
		// Getting main nodeset (if provided)h
		String mainMappingExpression = mapping.getMappingExpression();
		log.info(" Mapping Expresion "+mainMappingExpression);
		Node mainNode =	getClassNode(document,  mainMappingExpression);
		log.info(" Main Node  : "+mainNode);
		//We start with the iterative exploraton.
		ArrayList<Mapping> mappings = mapping.getMappings();
		log.info("Main Mappings : "+mappings);
		mainObject =(Serializable) deserializeSubproperties(mainObject, mainNode, mappings);
		return mainObject;
	}

	protected static Serializable getObjectInstance(Class clazz)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(clazz == null ||  clazz.equals(java.lang.Class.class) ) return null;
		log.debug("Getting instance of "+clazz.getName());
		Serializable mainObject =(Serializable) clazz.getDeclaredConstructor().newInstance();
		return mainObject;
	}
	
	
	
	
	private Serializable deserializeCollection(Serializable parentObject, Node parentNode, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, NoSuchFieldException, SecurityException {	
			
		//Iis the class single or multiple 
		
			// Single 
				// Is tthe mapping on the list 
					// Get the nodes from the list
					// Add it to the collection 
		
				// Is the mapping on the class 
					// Get the nodes from the class 
					// Add it to the collection 
				// Is it in both
					//Get the list nodeset 
					// get the class nodeset 
					// Add it to the collection
		
		// Multiple 
			//the collection has a list mappping
			
			// It is only on the class 
		
		
		//TODO: retrieve the mapping from the multiple classes.
//		mapping.get

//		LOGGER.debug(" Deserializincing Multiple Collection: "+ propertyName);
//		NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode, XPathConstants.NODESET);
//		for(int i =0 ; i<nodeList.getLength() ; i++) {
//			Node node = nodeList.item(i);
//		 //TODO: Falta por hacer el binding de listas
//		//Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
//			ArrayList  lista= (ArrayList) PropertyUtils.getNestedProperty( parentObject, propertyName);
//
//			Serializable singelObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
//		
//			Serializable deserializeSubproperties = (Serializable) deserializeSubproperties(objectInstance, node, currentMapping.getMappings());
//			lista.add(deserializeSubproperties);
//			//PropertyUtils.setNestedProperty(parentObject, propertyName,  deserializeSubproperties);
//		
//		
	return null;
	}

		protected Serializable deserializeSubproperties(Serializable parentObject, Node mainNode, ArrayList<Mapping> mappings) throws XPathExpressionException, 
		InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, 
		BindingException {
			
		log.info(" Subproperties : "+ mainNode+ " Into "+parentObject);
		if(parentObject!=null)
		log.debug(" Parent Object Class: "+ parentObject.getClass().getName());
		log.debug(" Iterating overr subproperties : ------------------------------------- ");
		
		for(Mapping currentMapping : mappings) {
			
			log.info(" Subproperty evaluated to : "+ currentMapping.getMappingExpression() +" -> "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			log.debug(" Subproperty Class : "+currentMapping.getPropertyClass());
			
			Serializable objectInstance = (Serializable) getObjectInstance(currentMapping.getPropertyClass());
			log.debug(" Object Instances: "+objectInstance.getClass().getName());
			
			//Si attribute : String 
			
			if (
					parentObject.getClass().equals(Vector.class) || 
					parentObject.getClass().equals(ArrayList.class) ||
					parentObject.getClass().equals(List.class))  {
						
						log.debug(" Getting Singe Node: "+ propertyName);
						NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODESET);
						for(int i =0 ; i<nodeList.getLength() ; i++) {
							Node node = nodeList.item(i);
							//TODO: Falta por hacer el binding de listas
//							Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
							ArrayList  lista= (ArrayList) PropertyUtils.getNestedProperty( parentObject, propertyName);

							Serializable singelObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
							
							Serializable deserializeSubproperties = (Serializable) deserializeSubproperties(objectInstance, node, currentMapping.getMappings());
							lista.add(deserializeSubproperties);
//							PropertyUtils.setNestedProperty(parentObject, propertyName,  deserializeSubproperties);
							
							
							
						}
						
						
					}  else if(isAttributeMapping(mappingExpression) || (parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
				log.info(" Getting attribute: "+ propertyName);
				String attributeValue= (String) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.STRING);
				
				log.info(" Getting value: "+ attributeValue);
				PropertyUtils.setNestedProperty(parentObject,propertyName,attributeValue);
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
				log.debug(" Getting Node for: "+ propertyName);
				Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
				Serializable singleObject ;
				try {
					singleObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
					log.debug(" Candidate Object: "+singleObject);
					if(singleObject==null ) {
						log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
						singleObject = (Serializable) getObjectInstance( currentMapping.getPropertyClass());
					}
					if(currentMapping.getPropertyClass()!=null) {
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
		// TODO Auto-generated method stub
		return parentObject;
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
