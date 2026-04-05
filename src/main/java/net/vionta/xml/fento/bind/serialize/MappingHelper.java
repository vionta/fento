package net.vionta.xml.fento.bind.serialize;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import net.vionta.xml.fento.bind.annotation.Bind;

/**
 * Convenience methods for mapping calculations. 
 */
public class MappingHelper {

	/**
	 * @param mappingExpression
	 * @return
	 */
	public static boolean isAttributeMapping( String mappingExpression) {
		if(mappingExpression == null ||  mappingExpression.isEmpty()) return false;
		Pattern pattern = Pattern.compile("\\/@[\\w\\-]+$|\\/@\\w+:[\\w\\-]+$");
		Matcher matcher = pattern.matcher(mappingExpression);
		return matcher.find();	
	}

	/**
	 * @param parentObject
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
	public static boolean isMapped(Serializable parentObject, String propertyName) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
		return (annotation != null && annotation.expression() != null && !(annotation.expression().isEmpty())); 
	}
	/**
	 * @param parentObject
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
	public static boolean isMappedClass(Serializable parentObject) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		Class<? extends Serializable> testedClass = parentObject.getClass();
		Bind mainAnnotation = testedClass.getAnnotation(Bind.class); 
		return (mainAnnotation != null); 
	}

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

}
