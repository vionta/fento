package net.vionta.xml.fento.bind.serialize;

import static net.vionta.xml.fento.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.fento.bind.serialize.MappingHelper.isMapped;

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
import net.vionta.xml.fento.bind.annotation.Bind;
import net.vionta.xml.fento.exception.BindingException;
import net.vionta.xml.fento.exception.MappingException;
import net.vionta.xml.fento.repository.impl.util.XPathManager;

/**
 * Main deserializer class using Single and 
 * multiple collections. 
 */
public class CollectionDeserializeHelper {

	private static Logger LOGGER = LoggerFactory.getLogger(CollectionDeserializeHelper.class);

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
	private Serializable deserializeSingleCollection(Serializable parentObject, Node parentNode, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		String propertyName = mapping.getPropertyName();
		LOGGER.debug(" Deserializincing Singe Collection: "+ mapping);
		List targetCollection = (List) PropertyUtils.getNestedProperty( parentObject, propertyName);
		NodeList nodeList = (NodeList) XPathManager.buildXPath().evaluate(mapping.getMappingExpression(), parentNode, XPathConstants.NODESET);

		for(int i =0 ; i<nodeList.getLength() ; i++) {
			Node node = nodeList.item(i);
			Serializable collectionElement = getCollectionTypeInstance(mapping.getPropertyClass());
			Serializable deserializeSingleElement = deserializeSingleElement(collectionElement , node, mapping.getMappings());
			targetCollection.add((Serializable)deserializeSingleElement);
		}
//		PropertyUtils.setNestedProperty(parentObject, propertyName, targetCollection);
//		return parentObject;
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
	private Serializable deserializeMultipleCollection(Serializable parentObject, Node parentNode, Mapping collectionMapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		
		String propertyName = collectionMapping.getPropertyName();
		LOGGER.debug(" Deserializincing Multiple Collection: "+ collectionMapping);
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
					Serializable deserializedSingleElement = deserializeSingleElement((Serializable)propertyClass.newInstance(), collectionElementNodes.item(e), elementMapping.getMappings());
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
	 */
	public static Serializable getCollectionTypeInstance(Class clazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException  {
		Type type = clazz.getGenericInterfaces()[0];
		return (Serializable) type.getClass().newInstance();
	}

	
	protected Serializable deserializeSingleElement(Serializable object, Node mainNode, ArrayList<Mapping> mappings) throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {
		LOGGER.info(" Subproperties : "+ mainNode+ " Into "+object);
		LOGGER.debug(" Parent Object Class: "+ object.getClass().getName());
		LOGGER.debug(" Iterating overr subproperties : ------------------------------------- ");

		for(Mapping currentMapping : mappings) {

			LOGGER.info(" Subproperty : "+ currentMapping.getMappingExpression() +" -> "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			LOGGER.debug(" Subproperty Class : "+currentMapping.getPropertyClass());

			Serializable objectInstance = (Serializable) new Deserialzer().getObjectInstance(currentMapping.getPropertyClass());
			LOGGER.debug(" Object Instances: "+objectInstance.getClass().getName());

			//Single collection
			if (isCollection(object, propertyName))  {
				if(isSingleCollection(object, propertyName)) {
					LOGGER.debug(" Getting Singe Node: "+ propertyName);
					PropertyUtils.setNestedProperty(object, propertyName, deserializeSingleCollection(object, mainNode, currentMapping));
				} else {
					LOGGER.debug(" Getting Singe Node: "+ propertyName);
					PropertyUtils.setNestedProperty(object, propertyName, deserializeMultipleCollection(object, mainNode, currentMapping));
				}
			} //Attributes
			else if(isAttributeMapping(mappingExpression) || (object.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
				LOGGER.info(" Getting attribute: "+ propertyName);
				String attributeValue= (String) XPathManager.buildXPath().evaluate(mappingExpression, mainNode,XPathConstants.STRING);

				LOGGER.info(" Getting value: "+ attributeValue);
				PropertyUtils.setNestedProperty(object,propertyName,attributeValue);
				//TODO:Ver el tipo de nodo y el tipo de resultado. 
				//Si colleccion: Node List
				//TODO:Revisar class por instance
			}	//Single object 
			   else {
				// Nos queda el nodo single
				LOGGER.debug(" Getting Singe Node: "+ propertyName);
				Node currentNode = (Node) XPathManager.buildXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
				Serializable singleObject = (Serializable) PropertyUtils.getNestedProperty( object, propertyName);
				Serializable deserializeSubproperties = (Serializable) new Deserialzer().deserializeSubproperties(singleObject, currentNode, currentMapping.getMappings());
				PropertyUtils.setNestedProperty(object, propertyName,  deserializeSubproperties);
			}

		}
		// TODO Auto-generated method stub
		return object;
	}


}
