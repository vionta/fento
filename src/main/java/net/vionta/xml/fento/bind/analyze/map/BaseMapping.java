package net.vionta.xml.fento.bind.analyze.map;

import java.util.ArrayList;

/**
 * Base Mapping class, that provides the list of attributes.
 */
class BaseMapping {

	/**
	 * The name of the java property that the mapping 
	 * points to. It should be a valid existing property of 
	 * a Serializable class.
	 */
	protected String propertyName; 
	/**
	 * The xpath 3.x expression that points to the 
	 * location of the document.
	 */
	protected String mappingExpression;
	/**
	 * Used on lists to identify the property that identifies 
	 * the node. If it is true, a property with a value that
	 * matches the node value will be replaced instead of added.
	 */
	protected boolean key = false;
	
	/**
	 * The class the node will be mapped to. In most cases it could
	 * be inferred and in those cases it is taken directly from the 
	 * mapped java property.
	 */
	protected Class propertyClass;
	
	/**
	 * Identifies if a collection may have more than one 
	 * type of elements.
	 */
	protected Boolean isMultilple = Boolean.FALSE;
	protected Class propertyFormatter;	
	/**
	 * Sub mappings of the current instance object, 
	 * represent the subelements from the current 
	 * element.
	 */
	protected ArrayList<Mapping> mappings;
	protected Object value;
	/**
	 * A list of the namespaces of the current mapping
	 * expression. 
	 * NOT YET IN USE
	 * Namespaces can be added using Q{<alias>,<uri>} 
	 * saxonica syntax.
	 */
	protected ArrayList<Namespace>[] namespaces ; 

	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getMappingExpression() {
		return mappingExpression;
	}

	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}

	public ArrayList<Mapping> getMappings() {
		if(mappings==null) return new ArrayList<Mapping>();
		return mappings;
	}

	public void setMappings(ArrayList<Mapping> mappings) {
		this.mappings = mappings;
	}

	public Class getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(Class propertyClass) {
		this.propertyClass = propertyClass;
	}

	public Class getPropertyFormatter() {
		return propertyFormatter;
	}

	public void setPropertyFormatter(Class propertyFormatter) {
		this.propertyFormatter = propertyFormatter;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean getIsMultilple() {
		return isMultilple;
	}

	public void setIsMultilple(Boolean isMultilple) {
		this.isMultilple = isMultilple;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public ArrayList<Namespace>[] getNamespace() {
		return namespaces;
	}

	public void setNamespace(ArrayList<Namespace>[] namespaces) {
		this.namespaces = namespaces;
	}
	
	
	
}
