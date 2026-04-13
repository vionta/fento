package net.vionta.xml.fento.bind.serialize;


import static net.vionta.xml.fento.bind.serialize.MappingHelper.isMapped;

import java.awt.SecondaryLoop;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.fento.bind.analyze.map.Mapping;
import net.vionta.xml.fento.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.fento.bind.annotation.Bind;
import net.vionta.xml.fento.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.fento.bind.serialize.util.XPathManager;
import net.vionta.xml.fento.exception.BindingException;
import net.vionta.xml.fento.exception.MappingException;

/**
 * Main deserializer class using Single and 
 * multiple collections. 
 */
public class CollectionDeserializeHelper {

	private static Logger log = LoggerFactory.getLogger(CollectionDeserializeHelper.class);

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return
	 */
	public static boolean isSingleCollection(Serializable parentObject, String propertyName) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		if(!isCollection(parentObject, propertyName)  || !isMapped(parentObject, propertyName)) return false;
		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
		if(annotation.classNames() == null || annotation.classNames().length <= 1) return true;
		return false;
	}

	/**
	 * Returns true if the collection is mapped with the fento annotation. 
	 * 
	 * @param parentObject
	 * @param parentNode
	 * @param mappings
	 * @param propertyName
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static boolean isMappedCollection(Serializable parentObject, String propertyName) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		return  isCollection(parentObject, propertyName)  && isMapped(parentObject, propertyName);
	}



	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean isCollection(Serializable parentObject, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException  {
		Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
		return (declaredField.getType().equals(Vector.class) || 
				declaredField.getType().equals(ArrayList.class) ||
				declaredField.getType().equals(List.class));
	}

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean isSimpleValueType(Serializable parentObject, String propertyName, Mapping mapping) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException  {
		Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
		return (declaredField.getType().equals(String.class) || 
				declaredField.getType().equals(Integer.class) ||
				declaredField.getType().equals(Float.class));
	}

	/**
	 * @param parentObject
	 * @param parentNode
	 * @param mapping
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws BindingException 
	 * @throws MappingException 
	 */
	protected Serializable deserializeSingleCollection(Serializable parentObject, Node parentNode, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		
		log.debug(" Deserializincing Singe Collection: "+ mapping);
		String propertyName = mapping.getPropertyName();
		Class propertyClass = mapping.getPropertyClass();
		log.debug(" Property : "+propertyName+" - "+propertyClass);
		List targetCollection = (List) PropertyUtils.getNestedProperty( parentObject, propertyName);
		log.debug(" targetCollection : "+targetCollection);
		
		
		NodeList targetNodeList = (NodeList) XPathManager.buildXPath().evaluate(mapping.getMappingExpression(), parentNode, XPathConstants.NODESET);

		for(int i =0 ; i<targetNodeList.getLength() ; i++) {
			Node node = targetNodeList.item(i);
			Serializable collectionElement = getCollectionTypeInstance( parentObject, propertyClass, mapping);
			 
			
			
			Bind elementAnnotation = collectionElement.getClass().getAnnotation(Bind.class); 
			String elementExpression = (elementAnnotation==null) ? null : elementAnnotation.expression() ; 
			if(elementExpression==null ) {
				Serializable deserializeSingleElement = new Deserializer().deserializeSubproperties(collectionElement , node, mapping.getMappings());
				targetCollection.add(deserializeSingleElement);
			} else {
				NodeList elementLevelNodeList = (NodeList) XPathManager.buildXPath().evaluate(elementExpression, node, XPathConstants.NODESET);
				for(int e =0 ; e<elementLevelNodeList.getLength() ; e++) {
					Node secondaryElementNode = elementLevelNodeList.item(e);
					Serializable deserializeSingleElement = new Deserializer().deserializeSubproperties(collectionElement , secondaryElementNode, mapping.getMappings());
					targetCollection.add(deserializeSingleElement);
				}
			}
		}
		return (Serializable) targetCollection;
	}

	/**
	 * Multiple element collection, a collection that has more than one possible sub-element.
	 * 
	 * @param parentObject
	 * @param parentNode
	 * @param collectionMapping
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws BindingException 
	 * @throws MappingException 
	 */
	protected Serializable deserializeMultipleCollection(Serializable parentObject, Node parentNode, Mapping collectionMapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		
		String propertyName = collectionMapping.getPropertyName();
		log.debug(" Deserializincing Multiple Collection: "+ collectionMapping);
		List targetCollection = (List) PropertyUtils.getNestedProperty( parentObject, propertyName);
		NodeList listNodes =  (NodeList) XPathManager.buildXPath().evaluate(collectionMapping.getMappingExpression(), parentNode, XPathConstants.NODESET);
		
		ArrayList<Mapping>  collectionClassesMappings = collectionMapping.getMappings();
		
		for(int n =0 ; n<  listNodes.getLength() ; n++) {
			Node currentNode = listNodes.item(n);
			
			for(int c =0 ; c <  collectionClassesMappings.size() ; c++) {
				Mapping elementMapping = collectionClassesMappings.get(n);
				Class propertyClass = elementMapping.getPropertyClass();
				String elementMappinExpression = elementMapping.getMappingExpression();
				
				NodeList collectionElementNodes =  (NodeList) XPathManager.buildXPath().evaluate(elementMappinExpression, currentNode, XPathConstants.NODESET);
				for(int e= 0 ; e< collectionElementNodes.getLength() ; e++) {
					Serializable deserializedSingleElement = new Deserializer().deserializeSubproperties((Serializable)propertyClass.newInstance(), collectionElementNodes.item(e), elementMapping.getMappings());
					targetCollection.add(deserializedSingleElement);
				}
				}
		}
		return (Serializable) targetCollection;
	}

	/**
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws InstantiationException 
	 * @throws BindingException 
	 */
	public static Serializable getCollectionTypeInstance(Class clazz) throws BindingException  {
		Type type = clazz.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	public static Serializable getCollectionTypeInstance(Serializable parentObject, Class elementClass, Mapping mapping) throws BindingException  {
		if(mapping.getClass()!=null) return DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		Type type = elementClass.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	
//	protected Serializable deserializeSingleElement(Serializable parentObject, Node mainNode, ArrayList<Mapping> mappings) throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {
//		log.info(" Subproperties : "+ mainNode+ " Into "+parentObject);
//		if(parentObject!=null)
//		log.debug(" Parent Object Class: "+ parentObject.getClass().getName());
//		log.debug(" Iterating overr subproperties : ------------------------------------- ");
//
//		for(Mapping currentMapping : mappings) {
//
//			log.info(" Subproperty : "+ currentMapping.getMappingExpression() +" -> "+currentMapping.getPropertyName());
//			String mappingExpression = currentMapping.getMappingExpression();
//			String propertyName = currentMapping.getPropertyName();
//			log.debug(" Subproperty Class : "+currentMapping.getPropertyClass());
//
//			Serializable objectInstance = (Serializable) DeserializerHelper.getObjectInstance(currentMapping.getPropertyClass());
//			log.debug(" Object Instances: "+objectInstance.getClass().getName());
//
//			//Single collection
//			if (isCollection(parentObject, propertyName))  {
//				if(isSingleCollection(parentObject, propertyName)) {
//					log.debug(" Getting Singe Node: "+ propertyName);
//					PropertyUtils.setNestedProperty(parentObject, propertyName, deserializeSingleCollection(parentObject, mainNode, currentMapping));
//				} else {
//					log.debug(" Getting Singe Node: "+ propertyName);
//					PropertyUtils.setNestedProperty(parentObject, propertyName, deserializeMultipleCollection(parentObject, mainNode, currentMapping));
//				}
//			} //Attributes
//			else if(isAttributeMapping(mappingExpression) || (parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
//				log.info(" Getting attribute: "+ propertyName);
//				String attributeValue= (String) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.STRING);
//
//				log.info(" Getting value: "+ attributeValue);
//				PropertyUtils.setNestedProperty(parentObject,propertyName,attributeValue);
//				//TODO:Ver el tipo de nodo y el tipo de resultado. 
//				//Si colleccion: Node List
//				//TODO:Revisar class por instance
//			}	else if( parentObject.getClass().getDeclaredField(propertyName).getClass().equals(Vector.class) || 
//					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(ArrayList.class) ||
//					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(List.class)) {
//				log.info(" Getting List: "+ propertyName);
//		
////				PropertyUtils.setNestedProperty(appender,appenderNameMapping.getPropertyName(), appenderName);
//				NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODESET);
//				//TODO: Falta por hacer el binding de listas
//				
//				//Single object 
//			} else {
////				// Nos queda el nodo single
////				log.debug(" Getting Single Node: "+ propertyName);
////				Node currentNode = (Node) XPathManager.buildXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
////				Serializable singleObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
////				
////				Serializable deserializeSubproperties = (Serializable) new Deserializer().deserializeSubproperties(singleObject, currentNode, currentMapping.getMappings());
////				PropertyUtils.setNestedProperty(parentObject, propertyName,  deserializeSubproperties);
//			
//				// Nos queda el nodo single
//				log.debug(" Getting Single Node for: "+ propertyName);
//				Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
//				Serializable singleObject ;
//				try {
//					singleObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
//					log.debug(" Candidate Object: "+singleObject);
//					if(singleObject==null ) {
//						log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
//						singleObject = (Serializable) DeserializerHelper.getObjectInstance( currentMapping.getPropertyClass());
//					}
//					if(currentMapping.getPropertyClass()!=null && !currentMapping.getPropertyClass().equals(java.lang.String.class)) {
//						Serializable deserializeSubproperties = (Serializable) new Deserializer().deserializeSubproperties(singleObject, currentNode, currentMapping.getMappings());
//						log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
//						PropertyUtils.setNestedProperty(parentObject, propertyName, deserializeSubproperties);
//					} else if(currentNode!=null && currentNode.getTextContent()!=null) PropertyUtils.setNestedProperty(parentObject, propertyName,  currentNode.getTextContent());
//					
//				} catch (Exception e) {
//					log.error("Could not get  "+ propertyName+" property from "+parentObject );
//					MappingException mappingException = new MappingException();
//					mappingException.setSourceClassName((parentObject!= null) ? parentObject.getClass().getName(): null);
//					mappingException.setTargetPropertyName(propertyName);
//					mappingException.setException(e);
//					log.error(mappingExpression);
//					throw mappingException;
//				}
//			
//			}
//
//		}
//		return parentObject;
//	}


	protected static Serializable deserializeCollection(Serializable parentObject, Node mainNode, Mapping currentMapping,
			String propertyName) throws XPathExpressionException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NoSuchFieldException, MappingException, BindingException {
		if(CollectionDeserializeHelper.isSingleCollection(parentObject, propertyName)) {
			log.debug(" Getting Singe Node: "+ propertyName);
			Serializable deserializeSingleCollection = new CollectionDeserializeHelper().deserializeSingleCollection(parentObject, mainNode, currentMapping);
			return deserializeSingleCollection;
		} else {
			log.debug(" Getting Singe Node: "+ propertyName);
			Serializable deserializeMultipleCollection = new CollectionDeserializeHelper().deserializeMultipleCollection(parentObject, mainNode, currentMapping);
			return deserializeMultipleCollection;
		}
	}
	
}
