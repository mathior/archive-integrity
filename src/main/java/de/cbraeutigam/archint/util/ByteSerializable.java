package de.cbraeutigam.archint.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Interface for classes that must be byte serializable.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2014-12-12
 *
 */
public interface ByteSerializable extends Serializable {
	
	/**
	 * Write this object to os.
	 * @param os
	 */
	public void writeTo(OutputStream os) throws IOException;
	
	/**
	 * Read this object from is.
	 * @param is
	 */
	void readFrom(InputStream is)  throws IOException;

}
