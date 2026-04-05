package net.vionta.xml.fento.bind.analyze;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vionta.xml.fento.bind.analyze.map.Mapping;
import net.vionta.xml.fento.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.fento.bind.annotation.Bind;
import net.vionta.xml.fento.bind.serialize.CollectionDeserializeHelper;

/**
 * Exptracts the object structure and associated 
 * mapping. 
 */
public class BindMapExtractor {

	private static Logger LOGGER = LoggerFactory.getLogger(BindMapExtractor.class);

	/**
	 * Extracts the object mapping from 
	 * the object mapping.
	 * @param serializable
	 * @return
	 */
	public static ObjectDocumentMapping analyze(Serializable serializable) throws ClassNotFoundException {
		LOGGER.debug(" Analyzing "+serializable.getClass().getName());
		int recursionFuse = 1; 
		Bind mainBindAnnotation = serializable.getClass().getAnnotation(Bind.class); 
		if(serializable == null 
				|| mainBindAnnotation == null ) return null;
		ObjectDocumentMapping mainMapping = new ObjectDocumentMapping();
		mainMapping.setMappingExpression(mainBindAnnotation.expression());
		mainMapping.setPropertyClass(serializable.getClass());
		mainMapping.setKey(mainBindAnnotation.key());
		mainMapping.setPropertyName(serializable.getClass().getName());
		mainMapping.setMappings(extractMappings(mainMapping.getMappings(), serializable.getClass(), recursionFuse));
		LOGGER.debug(" Analyzed "+mainMapping);
		return mainMapping;
		
	}

	/**
	 * Iterative method to extract child object mappings. 
	 * 
	 * @param mappings
	 * @param clazzy
	 * @param recursionFuse
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static ArrayList<Mapping> extractMappings(ArrayList<Mapping> mappings, 
											Class clazzy,  int recursionFuse) throws ClassNotFoundException  {
		LOGGER.debug(" Analyzing "+clazzy.getName());
		if(recursionFuse> 500) throw new IllegalStateException("Too much recursion, probable mapping cycle");
		Field[] declaredFields = clazzy.getDeclaredFields();
		for (Field field: declaredFields) {
			LOGGER.debug(" Feld "+field);
			Bind bindAnnotation = field.getAnnotation(Bind.class);
			if(bindAnnotation != null){
				Class<?> clazz ;
				 if (field.getGenericType() instanceof ParameterizedType) {
						 clazz = (Class<?>) field.getType();
			            ParameterizedType pt = (ParameterizedType) field.getGenericType() ;
			            Type[] typeArgs = pt.getActualTypeArguments();
			            LOGGER.debug("Generic Type: " + typeArgs[0]);
			            if(typeArgs[0] != null)   {
			            	LOGGER.debug("-Class: " + typeArgs[0].getClass());
			            	clazz  = Class.forName(typeArgs[0].getTypeName());
			            }
			            //TODO: Eliminar duplicidad codigo
			        	Mapping mapping = new Mapping(field.getName(), bindAnnotation.expression());
						mapping.setPropertyName(field.getName());
						mapping.setPropertyClass(clazz);
						mapping.setKey(bindAnnotation.key());
						mapping.setMappingExpression(bindAnnotation.expression());
						mapping.setMappings(extractMappings(mapping.getMappings(), clazz, recursionFuse + 1));
						mappings.add(mapping);
			        } else  if (field.getType() instanceof Class) {
			        	clazz = (Class<?>) field.getType();
			        	//TODO: Revisar esto cuando =class java.lang.Class,
			        	//String.class.getGenericSuperclass() != field.getGenericType()
			        	if(field.getGenericType() != null && 1==2 )   {
			        		clazz = field.getGenericType().getClass(); 
			        	}				
//						LOGGER.debug("- q"+serializable.getClass().getName()+" -| field.name:"+field.getName()+" > "+field.getAnnotatedType()+ ":::: "+field.getGenericType());
						Mapping mapping = new Mapping(field.getName(), bindAnnotation.expression());
						mapping.setPropertyName(field.getName());
						mapping.setPropertyClass(clazz);
						mapping.setKey(bindAnnotation.key());
						mapping.setMappingExpression(bindAnnotation.expression());
						mapping.setMappings(extractMappings(mapping.getMappings(), clazz, recursionFuse + 1));
						mappings.add(mapping);
				}
			}		
		}
		return mappings;
	}

}
