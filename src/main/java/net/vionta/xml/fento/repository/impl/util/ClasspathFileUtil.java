package net.vionta.xml.fento.repository.impl.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

import net.sf.saxon.resource.ResourceLoader;


/**
 * A utility class that reads and writes content to classpath files. 
 * Classpath files must exist in order to be writen. 
 */
public class ClasspathFileUtil {

	
	static Logger LOGGER = getLogger(FileManager.class);
	
	 /**
     * Checks if a file exists in the defined path.
     *
     * @param path
     * @return
	 * @throws URISyntaxException 
     */
    public static boolean fileExists(String path)  {
    	LOGGER.debug("Checking Path "+path);
    	try {
    	URL url = ResourceLoader.class.getClassLoader().getResource(path); 
    	File file = new File(url.getPath());
    	return new File(path).exists();
    	} catch (Exception e) {
    		LOGGER.error("File  "+path+ " does not exist in classpath ");
    		LOGGER.debug(e.getCause() +" - "+ e.getMessage() );
    		return false;
		}
    }
	
    /**
     * Writes a file, from within the classpath entries. 
     * Ths method can not write if the file does not exist 
     * previously on the classpath.
     *
     * @param path file relative path.
     * @param contents File contents.
     * @throws IOException Write file exception (permissions, etc.).
     */
    public static void writeFile(String path, String contents) throws IOException, URISyntaxException  {
    	LOGGER.debug("Writng File "+path);
    	URL url = ResourceLoader.class.getClassLoader().getResource(path);
    	LOGGER.debug("To  "+url);
    	PrintWriter writer = new PrintWriter(new File(url.getPath()));
    	writer.println(contents);
    	writer.close();
    }
    
    /**
     * Reads a file from the classpath entries
     * 
     * @param path
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws URISyntaxException
     */
    public static String readFile(String path) throws IOException, SAXException, ParserConfigurationException, URISyntaxException  {
    	LOGGER.debug("Reading file "+path);
    	ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        
        byte[] resourceBytes = inputStream.readAllBytes();
        
//        InputStream inputStream;
//    	String contents = ""; 
//		try {
//			InputStream inputStream = classLoader.getResourceAsStream(path);
//			inputStream = Files.newInputStream(path);
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//			String line = null;
//			boolean firstLine= true;
//			while ((line = bufferedReader.readLine()) != null) {
//				if(firstLine)	contents+=line.substring(1);
//				else {
//					contents+=line.substring(1);
//					firstLine=false;
//				}
//				contents+="\n";
//			}
//			return contents; 
//		} catch (IOException e) {
//			LOGGER.error("Read file failed with cause:");
//			LOGGER.error(""+e.getCause());
//			return null; 
//		}
        
        
        
        
        
        
        inputStream.close();
        // Convert the byte array to a String and print it
        String resourceString = new String(resourceBytes, StandardCharsets.UTF_8);
        if(resourceString.indexOf("?")==0) resourceString = resourceString.substring(1);
        LOGGER.debug(" Returning:"+resourceString);
        return resourceString;
//		return contents;
	}        
	
    
    
}
