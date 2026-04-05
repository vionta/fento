package net.vionta.xml.fento.repository;

import java.io.Serializable;

import org.w3c.dom.Document;

import net.vionta.xml.fento.repository.exception.PersistException;
import net.vionta.xml.fento.repository.exception.RetrieveException;

public interface DocumentRepository {
	
	Object load(Serializable object) throws RetrieveException;

	void persist(Serializable object, Document document) throws PersistException;


	Object template();


}
