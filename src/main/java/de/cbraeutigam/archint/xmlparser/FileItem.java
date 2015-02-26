package de.cbraeutigam.archint.xmlparser;


/**
 * 
 * Helper class to store the item information from a HT.diVAS xml file.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-26
 *
 */
public class FileItem {
	
	private String originalName;
	private String fileName;
	
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return "File [originalName=" + originalName + ", fileName=" + fileName + "]";
	}

}
