package de.cbraeutigam.archint.application;

/**
 * Exception that signals a missing integrity file.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-27
 *
 */
public class MissingIntegrityFileException extends Exception {

	private static final long serialVersionUID = 9004646738034860997L;
	
	public MissingIntegrityFileException(String message) {
		super(message);
	}

}
