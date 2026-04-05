package net.vionta.xml.fento.bind.analyze.map;

import java.util.ArrayList;

public class Mapping extends BaseMapping {

	public Mapping(String propertyName, String mappingExpression) {
		super();
		this.propertyName = propertyName;
		this.mappingExpression = mappingExpression;
	}

	public Mapping(String propertyName, String mappingExpression, ArrayList<Mapping> mappings) {
		super();
		this.propertyName = propertyName;
		this.mappingExpression = mappingExpression;
		this.mappings = mappings;
	}

	public Mapping(ArrayList<Mapping> mappings) {
		super();
		this.mappings = mappings;
	}


	@Override
	public String toString() {
		return "Mapping [propertyName=" + propertyName + ", mappingExpression=" + mappingExpression + ", propertyClass="
				+ propertyClass + ", propertyFormatter=" + propertyFormatter + ",\n     mappings=" + mappings + "]";
	}
	
}
