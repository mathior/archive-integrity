package de.cbraeutigam.archint.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

import de.cbraeutigam.archint.hashforest.InvalidInputException;

/**
 * Interface for classes that must support writing to and reading from character
 * streams.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2014-12-12
 * 
 */
public interface TextSerializable extends Serializable {
	
	/**
	 * Writes this object to w.
	 * @param w Writer
	 */
	public void writeTo(Writer w) throws IOException;
	
	/**
	 * Reads this object from r.
	 * @param r Reader
	 */
	public void readFrom(Reader r) throws IOException, InvalidInputException;

}
