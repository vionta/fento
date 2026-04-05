package net.vionta.xml.fento.repository.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.Serializable;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import net.vionta.xml.fento.bind.serialize.Deserialzer;
import net.vionta.xml.fento.bind.serialize.Serializer;
import net.vionta.xml.fento.repository.DocumentRepository;
import net.vionta.xml.fento.repository.exception.PersistException;
import net.vionta.xml.fento.repository.exception.RetrieveException;
import net.vionta.xml.fento.repository.impl.util.ClasspathFileUtil;
import net.vionta.xml.fento.repository.impl.util.DocumentUtils;
import net.vionta.xml.fento.repository.impl.util.FileManager;
import net.vionta.xml.fento.repository.impl.util.PathAdjust;

public class ClassPathRepository implements DocumentRepository {
	
	private String path ;
	
	static Logger log  = getLogger(ClassPathRepository.class);

	@Override
	public Object load(Serializable object) throws RetrieveException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			log .debug(" Adjusted Path"+adjustedPath );
			String fileContent = ClasspathFileUtil.readFile(adjustedPath);
			log .debug("File Content:"+fileContent);
			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			return  new Deserialzer().deserialize(object, documentContents);
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}
	
	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			// Load 
			Document serializedDocument = new Serializer().serialize(object, document);
			ClasspathFileUtil.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	public void persist(Serializable object) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			// Load 
			log .debug(" Adjusted Path"+adjustedPath );

			String fileContent = ClasspathFileUtil.readFile(adjustedPath);
			log .debug("File Content:"+fileContent);
			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			Document serializedDocument = new Serializer().serialize(object, documentContents);
			ClasspathFileUtil.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	
	@Override
	public Object template() {
		// m
		return null;
	}

	public String getPath() {
		return path;
	}

	public ClassPathRepository setPath(String path) {
		this.path = path;
		return this;
	}
	
}
