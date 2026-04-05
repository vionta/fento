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

public class Serializer {
		
		private static Logger log  = LoggerFactory.getLogger(Serializer.class);
		
		public Document serialize(Serializable mainObject, Document document) throws MappingException, BindingException, XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, ClassNotFoundException {
			
			log.info("Serialzing Document  "+document);
			 ObjectDocumentMapping mapping = BindMapExtractor.analyze(mainObject);
			 
			 log.info("With Mapping "+mapping);
				
//			mainObject = Deserialzer.getObjectInstance(mapping.getPropertyClass());
//			log.debug("Main Object:  "+mainObject);
			
			String mainMappingExpression = mapping.getMappingExpression();
			log.debug(" Mapping Expresion "+mainMappingExpression);
			
			Node mainNode =	Deserialzer.getClassNode(document,  mainMappingExpression);
			log.debug(" Main Node  : "+mainNode);
			
			return serializeSubproperties(mainObject, mainNode,  mapping.getMappings(), document);
						
		}
		
	protected Document serializeSubproperties(Serializable parentObject, Node mainNode, ArrayList<Mapping> mappings, Document document) throws XPathExpressionException, 
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, 
			BindingException {
			
		log.info(" Subproperties. Node  : "+ mainNode+ " Parent Object : "+parentObject);
		if(parentObject!=null)
		
		for(Mapping currentMapping : mappings) {
			log.info(" Subproperty, expression : "+ currentMapping.getMappingExpression() +" ->  property: "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			log.debug(" Mapping Property Class : "+currentMapping.getPropertyClass());
			
			Serializable objectInstance = (Serializable) Deserialzer.getObjectInstance(currentMapping.getPropertyClass());
			log.debug(" Mapping class Instance : "+objectInstance.getClass().getName());
			
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

//							Serializable singelObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
							
							Serializable deserializeSubproperties = (Serializable) serializeSubproperties(objectInstance, node, currentMapping.getMappings(), document);
//							lista.add(deserializeSubproperties);
//							PropertyUtils.setNestedProperty(parentObject, propertyName,  deserializeSubproperties);
							
							
							
						}
						
						
					}  else if(isAttributeMapping(mappingExpression) || (parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
						log.info(" Setting attribute: "+ propertyName);
						log.info(" *** Step review pending : *****" );
						log.info(" *** Step review pending : *****" );
						log.info(" *** Step review pending : *****" );
						
						Object nestedProperty = PropertyUtils.getNestedProperty(parentObject,propertyName);
						Node attributeNode= (Node) 
								getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODE);
						log.info(" Setting value: "+ nestedProperty.toString());
						attributeNode.setNodeValue(nestedProperty.toString());
				//TODO:Ver el tipo de nodo y el tipo de resultado. 
				
			}	else if( parentObject.getClass().getDeclaredField(propertyName).getClass().equals(Vector.class) || 
					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(ArrayList.class) ||
					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(List.class)) {
				log.info(" Getting List: "+ propertyName);
		
//				PropertyUtils.setNestedProperty(appender,appenderNameMapping.getPropertyName(), appenderName);
				NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODESET);
				//TODO: Falta por hacer el binding de listas
				
			} else {
				// Nos queda el nodo single
				log.debug(" Trying to serialize single node for "+ propertyName);
				
				Serializable candidateObject ;
				try {
//					Object nestedProperty = PropertyUtils.getNestedProperty(parentObject, propertyName);
					candidateObject = (Serializable) PropertyUtils.getNestedProperty( parentObject,propertyName);
					log.debug(" Gotten value : "+ candidateObject);
					
//					if(singelObject==null ) singelObject = (Serializable) Deserialzer.getObjectInstance( currentMapping.getPropertyClass());
					Node currentNode = (Node) getXPath().evaluate(mappingExpression, mainNode, XPathConstants.NODE);
					if(candidateObject!=null) {
						if (currentNode!=null)
							if ( MappingHelper.isMappedClass(candidateObject)
							|| ( currentMapping.getMappings() != null && currentMapping.getMappings().size() > 0)) serializeSubproperties(candidateObject, currentNode, currentMapping.getMappings(), document);
							else currentNode.setTextContent(candidateObject.toString());
						else log.warn("*** TODO: Create subnodes ***");
					}
					
				} catch (Exception e) {
					log.error("Could not set "+ propertyName+" property on "+parentObject );
					MappingException mappingException = new MappingException();
					mappingException.setSourceClassName((parentObject!= null) ? parentObject.getClass().getName(): null);
					mappingException.setTargetPropertyName(propertyName);
					mappingException.setException(e);
					log.error(mappingExpression);
					throw mappingException;
				}
				
				
			}
			
		}
		return document;
	}

}
