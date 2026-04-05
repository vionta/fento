package net.vionta.xml.fento.repository.exception;

public class RetrieveException extends PersistException {

	public String toString() {
		return " A error has ocurred ["+sourceExpeption+"] while retrieving a document ("+ objectName +") at path=" + path ;
		}
	
}
