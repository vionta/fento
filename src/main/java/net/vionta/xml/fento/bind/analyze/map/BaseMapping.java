package net.vionta.xml.fento.bind.analyze.map;

import java.util.ArrayList;

class BaseMapping {

	protected String propertyName; 
	protected String mappingExpression;
	protected boolean key = false;
	protected Class propertyClass;
	protected Boolean isMultilple = Boolean.FALSE;
	protected Class propertyFormatter;	
	protected ArrayList<Mapping> mappings;
	protected Object value;

	
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
	
}
