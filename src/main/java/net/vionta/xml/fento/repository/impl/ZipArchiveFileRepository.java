package net.vionta.xml.fento.repository.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import net.vionta.xml.fento.bind.serialize.Deserialzer;
import net.vionta.xml.fento.bind.serialize.Serializer;
import net.vionta.xml.fento.repository.DocumentRepository;
import net.vionta.xml.fento.repository.exception.PersistException;
import net.vionta.xml.fento.repository.exception.RetrieveException;
import net.vionta.xml.fento.repository.impl.util.DocumentUtils;
import net.vionta.xml.fento.repository.impl.util.PathAdjust;
import net.vionta.xml.fento.repository.impl.util.ZipFileUtil;

public class ZipArchiveFileRepository implements DocumentRepository {

	static Logger log  = getLogger(ZipArchiveFileRepository.class);

	private String zipFilePattern ;
	private String fileNamePattern ; 

	public ZipArchiveFileRepository() {}

	public ZipArchiveFileRepository(String zipFilePattern, String fileNamePattern) {
		this.zipFilePattern = zipFilePattern;
		this.fileNamePattern = fileNamePattern;
	}
	
	@Override
	public Object template() {
		throw new IllegalStateException("This method has not been developped yet.");
	}

	@Override
	public Object load(Serializable object) throws RetrieveException {
		try {
			String readTextFileInZip = ZipFileUtil.readTextFileInZip(zipFilePattern, fileNamePattern); 
			return  object = new Deserialzer().deserialize(object,DocumentUtils.stringToDocument(readTextFileInZip));
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( fileNamePattern);
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
			Document serialize = new Serializer().serialize(object, document);
			ZipFileUtil.writeTextFileInZip(zipFilePattern, PathAdjust.adjustedPath(fileNamePattern, object), DocumentUtils.documentToString(document)); 
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(fileNamePattern);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	public void persist(Serializable object) throws PersistException {
		try {
			String readTextFileInZip = ZipFileUtil.readTextFileInZip(zipFilePattern, fileNamePattern); 
			
			Document document = new Serializer().serialize(object, DocumentUtils.stringToDocument(  readTextFileInZip));
			ZipFileUtil.writeTextFileInZip(zipFilePattern, PathAdjust.adjustedPath(fileNamePattern, object), DocumentUtils.documentToString(document)); 
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(fileNamePattern);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	public String getZipFilePattern() {
		return zipFilePattern;
	}

	public void setZipFilePattern(String zipFilePattern) {
		this.zipFilePattern = zipFilePattern;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

}
