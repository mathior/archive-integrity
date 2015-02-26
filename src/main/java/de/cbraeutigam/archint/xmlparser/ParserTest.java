package de.cbraeutigam.archint.xmlparser;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.stream.XMLStreamException;


/**
 * Test driver for a HT.diVAS xml file parser.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-26
 *
 */
public class ParserTest {

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		String manifestFileName = "dip-example/7551b9b4-2c90-4207-9ecf-d8a2ebbb08a7/manifest.xml";
		ManifestStAXParser read = new ManifestStAXParser();
		List<FileItem> files = read.readManifest(manifestFileName);
		for (FileItem fileItem : files) {
			System.out.println(fileItem);
		}
	}

}
