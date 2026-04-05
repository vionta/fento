package net.vionta.xml.fento.repository.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.vionta.xml.fento.bind.serialize.Deserialzer;
import net.vionta.xml.fento.bind.serialize.Serializer;
import net.vionta.xml.fento.repository.DocumentRepository;
import net.vionta.xml.fento.repository.exception.PersistException;
import net.vionta.xml.fento.repository.exception.RetrieveException;
import net.vionta.xml.fento.repository.impl.util.DocumentUtils;
import net.vionta.xml.fento.repository.impl.util.FileManager;
import net.vionta.xml.fento.repository.impl.util.PathAdjust;

public class FilePathRepository implements DocumentRepository {

	static Logger log  = getLogger(FilePathRepository.class);

	private String path = "" ; 
	private String basePath = "" ; 

	public FilePathRepository(String path) {
		super();
		if (path == null) throw new IllegalStateException("The paths can not be null");
		this.path = path;
	}

	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);
			// Load 
			Document serializedDocument = new Serializer().serialize(object, document);
			FileManager.writeFile(adjustedPath,DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(basePath+path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from File repository");
			log .error(re.toString());
			throw re;
		}
	}

	@Override
	public Object load(Serializable object) throws RetrieveException  {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);

			// Load 
			Document documentContents = FileManager.readDocument(adjustedPath);
			return  new Deserialzer().deserialize(object, documentContents);
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( basePath + path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from File repository");
			log .error(re.toString());
			throw re;
		}
	}


	public String getFullPath() {
		return basePath + path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public Object template() {
		return null;
	}



}
