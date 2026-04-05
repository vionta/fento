package net.vionta.xml.fento.repository.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URI;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import net.vionta.xml.fento.bind.analyze.BindMapExtractor;
import net.vionta.xml.fento.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.fento.bind.serialize.Deserialzer;
import net.vionta.xml.fento.bind.serialize.Serializer;
import net.vionta.xml.fento.exception.BindingException;
import net.vionta.xml.fento.exception.MappingException;
import net.vionta.xml.fento.repository.DocumentRepository;
import net.vionta.xml.fento.repository.exception.PersistException;
import net.vionta.xml.fento.repository.exception.RetrieveException;
import net.vionta.xml.fento.repository.impl.util.DocumentUtils;
import net.vionta.xml.fento.repository.impl.util.FileManager;
import net.vionta.xml.fento.repository.impl.util.HttpUtil;
import net.vionta.xml.fento.repository.impl.util.PathAdjust;
import net.vionta.xml.fento.repository.impl.util.ZipFileUtil;

public class HttpRepository implements DocumentRepository {

	static Logger log = getLogger(HttpRepository.class);
	
	/**
	 * Specific document path part. It is intended for paths 
	 * that need to be adjusted based on the object/documnet 
	 * properties.  
	 */
	private String documentPath = "" ; 
	/**
	 * The base path,intended for the collection path.
	 */
	private String basePath = "" ; 
	
	@Override
	public Object load(Serializable object) throws RetrieveException {
		try {
			String textFromResource = HttpUtil.readHttpContent(PathAdjust.adjustedPath(getPath(), object));
			return  object = new Deserialzer().deserialize(object,DocumentUtils.stringToDocument(textFromResource));
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( getPath());
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
		
//		try {
//			HttpClient client = HttpClient.newHttpClient();
//			HttpRequest request = HttpRequest.newBuilder()
//					.uri(URI.create(PathAdjust.adjustedPath(getPath(), object)))
//					.GET()
//					.build();
//			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
//			response.statusCode();
//			String body = response.body();
//			ObjectDocumentMapping mapping = BindMapExtractor.analyze((Serializable)object);
//			new Deserialzer().deserialize(mapping, DocumentUtils.stringToDocument(body));
//			return body;
//		} catch (Exception e) {
//			log.error(e.toString());
//			RetrieveException re = new RetrieveException();
//			re.setPath(documentPath);
//			re.setSourceExpeption(e);
//			e.printStackTrace();
//			log.error("Error retrieving object from Http repository");
//			log.error(re.toString());
//			throw re;
//		} 
	}


	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			Document serializedDocument = new Serializer().serialize(object, document);
			HttpUtil.sendHttpContent(PathAdjust.adjustedPath(getPath(), object), DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(getPath());
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
		
//		//TODO: Serializer
//		try {
////			ObjectDocumentMapping mapping = BindMapExtractor.analyze((Serializable)object);
//			new Serializer().serialize(object, document);
//			HttpClient client = HttpClient.newHttpClient();
//			HttpRequest request = HttpRequest.newBuilder()
//				  .uri(URI.create(PathAdjust.adjustedPath(getPath(), object)))
////				  .POST(HttpRequest.BodyPublishers.ofString(""))
//				  .build();
//
//			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
////			response.statusCode();
//			response.body();
//		} catch (Exception e) {
//			log.error(e.toString());
//			PersistException re = new PersistException();
//			re.setPath(documentPath);
//			re.setSourceExpeption(e);
//			e.printStackTrace();
//			log.error("Error retrieving object from Http repository");
//			log.error(re.toString());
//			throw re;
//		}
	}
	
	@Override
	public Object template() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getPath() {
		return getBasePath() + getDocumentPath();
	}

}
