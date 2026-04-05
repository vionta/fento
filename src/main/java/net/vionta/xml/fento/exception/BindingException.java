package net.vionta.xml.fento.exception;

public class BindingException extends Exception {

	private String sourceClassName;
	private String targetClassName;
	private String value;
	private String mappingExpression;
	@Override
	public String toString() {
		return "Could not get a bind operation [sourceClassName=" + sourceClassName + ", targetClassName=" + targetClassName
				+ ", value=" + value + ", mappingExpression=" + mappingExpression + "]";
	}
	
}
