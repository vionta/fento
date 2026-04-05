package net.vionta.xml.fento.exception;

public class MappingException extends Exception {

	private String sourceClassName;
	private String targetPropertyName;
	private String value;
	private String mappingExpression;
	private Exception exception;
	
	@Override
	public String toString() {
		return "Could not get a mapping [sourceClassName=" + sourceClassName + ", targetPropertyName=" + targetPropertyName
				+ ", value=" + value + ", mappingExpression=" + mappingExpression + ", exception=" + exception + "]";
	}


	public String getSourceClassName() {
		return sourceClassName;
	}
	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}
	public String getTargetPropertyName() {
		return targetPropertyName;
	}
	public void setTargetPropertyName(String targetClassName) {
		this.targetPropertyName = targetClassName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMappingExpression() {
		return mappingExpression;
	}
	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	

}
