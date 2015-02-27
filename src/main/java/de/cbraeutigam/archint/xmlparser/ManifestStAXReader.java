package de.cbraeutigam.archint.xmlparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Parser for a HT.diVAS xml file.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-26
 *
 */
public class ManifestStAXReader {
	
	// cf. http://www.vogella.com/tutorials/JavaXML/article.html
	
	static final String FILES = "files";
	static final String FILE = "file";
	static final String ORIGINALNAME = "originalName";
	static final String FILENAME = "fileName";
	
	public List<FileItem> readManifest(String manifestFile)
			throws FileNotFoundException, XMLStreamException {
		
		List<FileItem> fileItems = new ArrayList<FileItem>();
		
		// First, create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		
		// Setup a new eventReader
		InputStream in = new FileInputStream(manifestFile);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		
		// read the XML document
		FileItem fileItem = null;
		
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				// If we have an fileIitem element, we create a new fileItem
				if (startElement.getName().getLocalPart().equals(FILE)) {
		            fileItem = new FileItem();
		        }
			}
			
			
			if (event.isStartElement()) {
				if (event.asStartElement().getName().getLocalPart().equals(ORIGINALNAME)) {
					event = eventReader.nextEvent();
					fileItem.setOriginalName(event.asCharacters().getData());
					continue;
				}
			}
			
			if (event.isStartElement()) {
				if (event.asStartElement().getName().getLocalPart().equals(FILENAME)) {
					event = eventReader.nextEvent();
					fileItem.setFileName(event.asCharacters().getData());
					continue;
				}
			}
			
			
			// If we reach the end of an item element, we add it to the list
	        if (event.isEndElement()) {
	        	EndElement endElement = event.asEndElement();
	        	if (endElement.getName().getLocalPart().equals(FILE)) {
	        		fileItems.add(fileItem);
	        	}
	        }
			
		}
		
		return fileItems;
	}

}
