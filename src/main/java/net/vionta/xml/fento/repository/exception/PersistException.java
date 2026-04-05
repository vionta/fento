package net.vionta.xml.fento.repository.exception;

public class PersistException extends Exception {

	protected String path;
	protected String objectName;
	protected Exception sourceExpeption;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public Exception getSourceExpeption() {
		return sourceExpeption;
	}
	public void setSourceExpeption(Exception sourceExpeption) {
		this.sourceExpeption = sourceExpeption;
	}
	@Override
	public String toString() {
		return " A error has ocurred ["+sourceExpeption+"] while storing a document ("+ objectName +") at path=" + path ;
		}
	
}
