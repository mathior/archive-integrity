package de.cbraeutigam.archint.application;

/**
 * Exception that signals a missing ordering file.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-27
 *
 */
public class MissingOrderingFileException extends Exception {

	private static final long serialVersionUID = 5994625156398461520L;
	
	public MissingOrderingFileException(String message) {
		super(message);
	}

}
